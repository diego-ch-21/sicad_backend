package com.sicad.sicad_backend.service.impl;

import com.sicad.sicad_backend.algorithm.service.AlgoritmoAsignacionService;
import com.sicad.sicad_backend.dto.Especializacion.EspecializacionResumenResponse;
import com.sicad.sicad_backend.dto.algoritmo.AlgoritmoDetalleResponse;
import com.sicad.sicad_backend.dto.asignacion.AsignacionCreateRequest;
import com.sicad.sicad_backend.dto.asignacion.AsignacionDetalleResponse;
import com.sicad.sicad_backend.dto.asignacion.AsignacionResumenResponse;
import com.sicad.sicad_backend.dto.asignacion.AsignacionUpdateRequest;
import com.sicad.sicad_backend.dto.base.GenericObjectResponse;
import com.sicad.sicad_backend.dto.base.GenericReponse;
import com.sicad.sicad_backend.dto.carga.CargaDetalleResponse;
import com.sicad.sicad_backend.model.*;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import com.sicad.sicad_backend.repository.interfaces.*;
import com.sicad.sicad_backend.service.base.CRUDImpl;
import com.sicad.sicad_backend.service.interfaces.IAsignacionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.juli.logging.Log;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class AsignacionServiceImpl
        extends CRUDImpl<Asignacion, Integer>
        implements IAsignacionService {

    private final IAsignacionRepo asignacionRepo;
    private final IDocenteRepo docenteRepo;
    private final ICargaRepo cargaRepo;
    private final ICursoRepo cursoRepo;
    private final ICicloAcademicoRepo cicloAcademicoRepo;
    private final IPreferenciaRepo preferenciaRepo;
    private final IDisponibilidadRepo disponibilidadRepo;
    private final IAlgoritmoRepo algoritmoRepo;
    private final IResultadoRepo resultadoRepo;
    private final ModelMapper modelMapper;
    // Nuevo servicio del algoritmo
    private final AlgoritmoAsignacionService algoritmoService;

    @Override
    protected IGenericRepo<Asignacion, Integer> getRepo() {
        return asignacionRepo;
    }

    public GenericObjectResponse<AsignacionDetalleResponse> registrarAsignacion(AsignacionCreateRequest request) {
        Docente docente = docenteRepo.findById(request.getIdDocente()).orElse(null);
        if (docente == null) {
            return new GenericObjectResponse<>(404, "Docente no encontrado", null);
        }

        Curso curso = cursoRepo.findById(request.getIdCurso()).orElse(null);
        if (curso == null) {
            return new GenericObjectResponse<>(404, "Curso no encontrado", null);
        }

        CicloAcademico cicloAcademico = cicloAcademicoRepo.findById(request.getIdCicloAcademico()).orElse(null);
        if (cicloAcademico == null) {
            return new GenericObjectResponse<>(404, "Ciclo academico no encontrada", null);
        }
        Carga carga = cargaRepo.findById(request.getIdCarga()).orElse(null);
        if(carga == null) {
            return new GenericObjectResponse<>(404, "Carga no encontrada", null);
        }

        Asignacion asignacion = new Asignacion();
        asignacion.setDocente(docente);
        asignacion.setCurso(curso);
        asignacion.setCicloAcademico(cicloAcademico);
        asignacion.setCarga(carga);
        asignacion.setEnabled(true);
        asignacion.setCreatedAt(LocalDate.now());
        asignacionRepo.save(asignacion);
        AsignacionDetalleResponse dto = modelMapper.map(asignacion, AsignacionDetalleResponse.class);
        return new GenericObjectResponse<>(201, "Asignación registrada exitosamente", dto);
    }

    public GenericObjectResponse<AsignacionDetalleResponse> actualizarAsignacion(Integer id, AsignacionUpdateRequest request) {
        Asignacion asignacion = asignacionRepo.findById(id).orElse(null);
        if (asignacion == null) {
            return new GenericObjectResponse<>(404, "Asignación no encontrada", null);
        }

        if (request.getIdDocente() != null) {
            docenteRepo.findById(request.getIdDocente()).ifPresent(asignacion::setDocente);
        }

        if (request.getIdCurso() != null) {
            cursoRepo.findById(request.getIdCurso()).ifPresent(asignacion::setCurso);
        }

        if (request.getIdCicloAcademico() != null) {
            cicloAcademicoRepo.findById(request.getIdCicloAcademico()).ifPresent(asignacion::setCicloAcademico);
        }
        if(request.getIdCarga() != null) {
            cargaRepo.findById(request.getIdCarga()).ifPresent(asignacion::setCarga);
        }


        try {
            asignacionRepo.save(asignacion);
            AsignacionDetalleResponse dto = modelMapper.map(asignacion, AsignacionDetalleResponse.class);
            return new GenericObjectResponse<>(200, "Asignación actualizada exitosamente", dto);
        } catch (DataIntegrityViolationException e) {
            return new GenericObjectResponse<>(400, "Conflicto de unicidad: ya existe una asignación para este docente y horario", null);
        }
    }


    //------------------------------------------------------------------------------------------------------

    /**
     * MÉTODO PRINCIPAL ACTUALIZADO: Ejecuta algoritmo híbrido GA+PSO con nuevo modelo de restricciones
     */
    @Transactional
    public GenericObjectResponse<CargaDetalleResponse> asignarConAlgoritmoGeneticoPSO(Integer idCicloAcademico) {
        try {
            // 1. Validaciones iniciales
            CicloAcademico cicloAcademico = cicloAcademicoRepo.findById(idCicloAcademico).orElse(null);
            if (cicloAcademico == null) {
                return new GenericObjectResponse<>(404, "Ciclo académico no encontrado", null);
            }
            Optional<Algoritmo> principalActualOpt = algoritmoRepo.findByPrincipalTrue();
            if (!principalActualOpt.isPresent()) {
                return new GenericObjectResponse<>(400, "no exite un algoritmo principal seleccionado", null);
            }
            Algoritmo algoritmoPrincipal = principalActualOpt.get();

            // 2. Recolección de datos necesarios
            // Obtener docentes que tienen disponibilidad para este cicloa cademico
            List<Docente> docentes = obtenerDocentesDisponibles(idCicloAcademico);
            // obtener cursos de un ciclo academico especifico
            List<Curso> cursos = obtenerCursosPorCiclo(idCicloAcademico);

            if (docentes.isEmpty()) {
                return new GenericObjectResponse<>(400, "No hay docentes disponibles para este ciclo academico", null);
            }

            if (cursos.isEmpty()) {
                return new GenericObjectResponse<>(400, "No hay cursos para el ciclo académico ", null);
            }

            // 3. Preparar datos para el algoritmo
            //
            Map<Integer, List<Disponibilidad>> disponibilidadPorDocente = prepararDisponibilidad(docentes, idCicloAcademico);
            //
            Map<Integer, List<Preferencia>> preferenciasPorDocente = prepararPreferencias(docentes, idCicloAcademico);
            System.out.println("=== Datos preparados - Disponibilidad y preferencias por docente ===");
            logearLimitesDocentesActualizado(docentes);

            //4. crear carga
            Carga carga = Carga.builder()
                    .algoritmo(algoritmoPrincipal)
                    .cicloAcademico(cicloAcademico)
                    .createdAt(LocalDateTime.now())
                    .principal(false)
                    .enabled(true)
                    .build();
            cargaRepo.save(carga);


            System.out.println("=== Asignaciones anteriores eliminadas ===");

            // 5. Ejecutar algoritmo híbrido actualizado
            List<Asignacion> asignacionesOptimas = algoritmoService.ejecutarAlgoritmoHibrido(
                    docentes, cursos, cicloAcademico, disponibilidadPorDocente, preferenciasPorDocente,algoritmoPrincipal,carga);

            // 6. Validar y guardar resultados
            if (asignacionesOptimas.isEmpty()) {
                return new GenericObjectResponse<>(400, "No se pudieron generar asignaciones válidas con el algoritmo actualizado", null);
            }

            // 7. Persistir las asignaciones
            List<Asignacion> asignacionesGuardadas = asignacionRepo.saveAll(asignacionesOptimas);

            /*
            System.out.println("=== Asignaciones guardadas exitosamente ===");

            // 8. Convertir a DTOs
            List<AsignacionDetalleResponse> response = asignacionesGuardadas.stream()
                    .map(asignacion -> modelMapper.map(asignacion, AsignacionDetalleResponse.class))
                    .collect(Collectors.toList());

            // 9. Generar resumen de resultados actualizado


            return new GenericObjectResponse<>(201, resumen, response);

             */
            generarResumenAlgoritmoActualizado(asignacionesGuardadas, docentes, cursos,carga);

            Carga obj = cargaRepo.findById(idCicloAcademico).orElse(null);
            if(obj == null) {
                return new GenericObjectResponse<>(400,"carga no encontrada",null);
            }
            return new GenericObjectResponse(200,"Algoritmo hibrido realizado exitosamente", convertToDetalle(carga));

        } catch (Exception e) {
            System.out.println("=== Error en asignación con algoritmo híbrido actualizado ===");
            return new GenericObjectResponse<>(500, "Error interno en algoritmo híbrido actualizado: " + e.getMessage(), null);
        }
    }
    private CargaDetalleResponse convertToDetalle(Carga obj) {
        return modelMapper.map(obj, CargaDetalleResponse.class);
    }

    /**
     * Prepara el mapa de disponibilidad por docente
     */
    private Map<Integer, List<Disponibilidad>> prepararDisponibilidad(List<Docente> docentes, Integer idCicloAcademico) {
        Map<Integer, List<Disponibilidad>> mapa = new HashMap<>();

        for (Docente docente : docentes) {
            List<Disponibilidad> disponibilidades = disponibilidadRepo.buscarPorDocenteYCicloAcademico(
                    docente.getIdDocente(), idCicloAcademico);
            mapa.put(docente.getIdDocente(), disponibilidades);
            System.out.println("Docente " + docente.getCodigo() + ": " + disponibilidades.size() + " disponibilidades registradas");

        }

        return mapa;
    }

    /**
     * Prepara el mapa de preferencias por docente
     */
    private Map<Integer, List<Preferencia>> prepararPreferencias(List<Docente> docentes, Integer idCargaElectiva) {
        Map<Integer, List<Preferencia>> mapa = new HashMap<>();

        for (Docente docente : docentes) {
            List<Preferencia> preferencias = preferenciaRepo.buscarPorDocenteYCicloAcademico(
                    docente.getIdDocente(), idCargaElectiva);
            mapa.put(docente.getIdDocente(), preferencias);
            System.out.println("Docente " + docente.getCodigo() + ": " + preferencias.size() + " preferencias registradas (restricción blanda)");
        }

        return mapa;
    }

    /**
     * ACTUALIZADO: Loggea solo los límites de horasMaxLectivas por docente
     */
    private void logearLimitesDocentesActualizado(List<Docente> docentes) {
        System.out.println("=== LÍMITES DE HORAS POR DOCENTE (SOLO horasMaxLectivas) ===");
        for (Docente docente : docentes) {
            int horasMaximas = docente.getDedicacion().getHorasMaxLectivas() != null ?
                    docente.getDedicacion().getHorasMaxLectivas() : 12;
            System.out.println("Docente " + docente.getCodigo() + ": máximo " + horasMaximas + " horas (horasMaxLectivas)");
        }
        System.out.println("=== FIN DE LÍMITES ===");
    }

    /**
     * ACTUALIZADO: Genera resumen detallado del algoritmo con nuevo modelo
     */
    private void generarResumenAlgoritmoActualizado(List<Asignacion> asignaciones, List<Docente> docentes, List<Curso> cursos,Carga carga) {
        int cursosAsignados = asignaciones.size();
        int cursosSinAsignar = cursos.size() - cursosAsignados;

        // Calcular docentes utilizados
        Set<Integer> docentesUtilizados = asignaciones.stream()
                .map(a -> a.getDocente().getIdDocente())
                .collect(Collectors.toSet());

        // Calcular distribución de horas
        Map<Integer, Integer> horasPorDocente = new HashMap<>();
        for (Asignacion asignacion : asignaciones) {
            Integer idDocente = asignacion.getDocente().getIdDocente();
            int horas = asignacion.getCurso().getCursoHorario().stream()
                    .mapToInt(CursoHorario::getDuracionHoras)
                    .sum();
            horasPorDocente.merge(idDocente, horas, Integer::sum);
        }

        // Calcular estadísticas de carga
        OptionalDouble promedioHoras = horasPorDocente.values().stream()
                .mapToInt(Integer::intValue)
                .average();

        int maxHoras = horasPorDocente.values().stream()
                .mapToInt(Integer::intValue)
                .max().orElse(0);

        int minHoras = horasPorDocente.values().stream()
                .mapToInt(Integer::intValue)
                .min().orElse(0);

        // Contar preferencias satisfechas (RESTRICCIÓN BLANDA)
        long preferenciasSatisfechas = asignaciones.stream()
                .filter(this::verificarPreferenciaSatisfecha)
                .count();

        double porcentajePreferencias = asignaciones.isEmpty() ? 0.0 :
                (double) preferenciasSatisfechas / asignaciones.size() * 100.0;

        // Verificar docentes que exceden límites de horasMaxLectivas
        long docentesExcedidos = horasPorDocente.entrySet().stream()
                .filter(entry -> {
                    Docente docente = docentes.stream()
                            .filter(d -> d.getIdDocente().equals(entry.getKey()))
                            .findFirst().orElse(null);
                    if (docente != null) {
                        int limite = docente.getDedicacion().getHorasMaxLectivas() != null ?
                                docente.getDedicacion().getHorasMaxLectivas() : 12;
                        return entry.getValue() > limite;
                    }
                    return false;
                })
                .count();
        Resultado resultado = Resultado.builder()
                .cursosAsignados(cursosAsignados)
                .totalCursos(cursos.size())
                .porcentajeCursosAsignados((double) cursosAsignados / cursos.size() * 100.0)
                .cursosSinAsignar(cursosSinAsignar)
                .docentesUtilizados(docentesUtilizados.size())
                .totalDocentes(docentes.size())
                .porcentajeDocentesUtilizados((double) docentesUtilizados.size() / docentes.size() * 100.0)
                .porcentajePreferenciasSatisfechas(porcentajePreferencias)
                .promedioHoras(promedioHoras.orElse(0.0))
                .maxHoras(maxHoras)
                .minHoras(minHoras)
                .docentesExcedidos((int) docentesExcedidos)
                .enabled(true)
                .build();
        resultadoRepo.save(resultado);
        carga.setResultado(resultado);
        cargaRepo.save(carga);


    }

    /**
     * Verifica si una asignación satisface las preferencias del docente (RESTRICCIÓN BLANDA)
     */
    private boolean verificarPreferenciaSatisfecha(Asignacion asignacion) {
        List<Preferencia> preferencias = preferenciaRepo.buscarPorDocenteYCicloAcademico(
                asignacion.getDocente().getIdDocente(),
                asignacion.getCicloAcademico().getIdCicloAcademico());

        return preferencias.stream()
                .anyMatch(pref -> pref.getAsignatura().getIdAsignatura()
                        .equals(asignacion.getCurso().getAsignatura().getIdAsignatura()));
    }

    //---------------------------------------------------------------------------------------

    // Métodos auxiliares para el algoritmo
    private List<Docente> obtenerDocentesDisponibles(Integer idCicloAcademico) {
        // Obtener docentes que tienen disponibilidad para esta carga electiva
        List<Disponibilidad> disponibilidades = disponibilidadRepo.findByCicloAcademico_IdCicloAcademico(idCicloAcademico);
        return disponibilidades.stream()
                .map(Disponibilidad::getDocente)
                .collect(Collectors.toList());
    }

    private List<Curso> obtenerCursosPorCiclo(Integer idCicloAcademico) {
        return cursoRepo.buscarPorPeriodoAcademico(idCicloAcademico);
    }
    /*
    private List<Asignacion> ejecutarAlgoritmoHibrido(List<Docente> docentes, List<Curso> cursos, CicloAcademico cicloAcademico) {
        // Usar el nuevo servicio de algoritmo híbrido actualizado
        try {
            // Preparar datos para el algoritmo
            Map<Integer, List<Disponibilidad>> disponibilidadPorDocente = prepararDisponibilidad(docentes, cicloAcademico.getIdCicloAcademico());
            Map<Integer, List<Preferencia>> preferenciasPorDocente = prepararPreferencias(docentes, cicloAcademico.getIdCicloAcademico());

            // Ejecutar algoritmo híbrido actualizado
            return algoritmoService.ejecutarAlgoritmoHibrido(
                    docentes, cursos, cicloAcademico, disponibilidadPorDocente, preferenciasPorDocente);

        } catch (Exception e) {
            System.out.println("=== Error en algoritmo híbrido actualizado, usando algoritmo simplificado ===");

            // Fallback: algoritmo simplificado si falla el actualizado
            return ejecutarAlgoritmoSimplificado(docentes, cursos, cicloAcademico);
        }
    }

     */

    /**
     * ACTUALIZADO: Algoritmo simplificado como fallback con nuevo modelo
     */
    private List<Asignacion> ejecutarAlgoritmoSimplificado(List<Docente> docentes, List<Curso> cursos, CicloAcademico cicloAcademico) {
        System.out.println("=== Ejecutando algoritmo simplificado actualizado como fallback ===");
        System.out.println("RESTRICCIONES: Solo disponibilidad + horasMaxLectivas");

        List<Asignacion> asignaciones = new ArrayList<>();
        Map<Integer, Integer> asignacionesTemporales = new HashMap<>();

        // Versión básica: asignación por disponibilidad y límites de horasMaxLectivas
        for (Curso curso : cursos) {
            Docente docenteAsignado = encontrarMejorDocenteParaCursoActualizado(
                    curso, docentes, cicloAcademico, asignacionesTemporales);

            if (docenteAsignado != null) {
                asignacionesTemporales.put(curso.getIdCurso(), docenteAsignado.getIdDocente());

                Asignacion asignacion = new Asignacion();
                asignacion.setDocente(docenteAsignado);
                asignacion.setCurso(curso);
                asignacion.setCicloAcademico(cicloAcademico);
                asignacion.setEnabled(true);
                asignacion.setCreatedAt(LocalDate.now());

                asignaciones.add(asignacion);
                System.out.println("Asignado curso " + curso.getCodigo() +
                        " al docente " + docenteAsignado.getCodigo() + " (algoritmo simplificado)");
            }
        }

        return asignaciones;
    }

    /**
     * ACTUALIZADO: Busca el mejor docente para un curso con nuevo modelo de restricciones
     */
    private Docente encontrarMejorDocenteParaCursoActualizado(Curso curso, List<Docente> docentes,
                                                              CicloAcademico cicloAcademico,
                                                              Map<Integer, Integer> asignacionesTemporales) {
        // 1. Buscar docentes con preferencia por la asignatura del curso (RESTRICCIÓN BLANDA)
        List<Preferencia> preferencias = preferenciaRepo.findByCicloAcademico_IdCicloAcademico(cicloAcademico.getIdCicloAcademico());

        List<Docente> docentesConPreferencia = preferencias.stream()
                .filter(pref -> pref.getAsignatura().getIdAsignatura().equals(curso.getAsignatura().getIdAsignatura()))
                .map(Preferencia::getDocente)
                .filter(docente -> docentes.contains(docente))
                .collect(Collectors.toList());

        // 2. Priorizar docentes con preferencia, pero no es obligatorio
        if (!docentesConPreferencia.isEmpty()) {
            for (Docente docente : docentesConPreferencia) {
                if (verificarDisponibilidadHoraria(docente, curso, cicloAcademico) &&
                        puedeTomarCursoActualizado(docente, curso, asignacionesTemporales)) {
                    System.out.println("Docente " + docente.getCodigo() +
                            " asignado por preferencia a curso " + curso.getCodigo());
                    return docente;
                }
            }
        }

        // 3. Si no hay preferencias válidas, buscar cualquier docente disponible
        for (Docente docente : docentes) {
            if (verificarDisponibilidadHoraria(docente, curso, cicloAcademico) &&
                    puedeTomarCursoActualizado(docente, curso, asignacionesTemporales)) {
                System.out.println("Docente " + docente.getCodigo() +
                        " asignado sin preferencia a curso " + curso.getCodigo());
                return docente;
            }
        }
        System.out.println("=== No se encontró docente disponible para curso " + curso.getCodigo() + " ===");
        return null;
    }

    /**
     * Verifica disponibilidad horaria (RESTRICCIÓN DURA - OBLIGATORIA)
     */
    private boolean verificarDisponibilidadHoraria(Docente docente, Curso curso, CicloAcademico cicloAcademico) {
        List<Disponibilidad> disponibilidades = disponibilidadRepo.buscarPorDocenteYCicloAcademico(
                docente.getIdDocente(), cicloAcademico.getIdCicloAcademico());

        if (disponibilidades.isEmpty()) {
            System.out.println("=== Docente " + docente.getCodigo() + " no tiene disponibilidad registrada ===");
            return false;
        }

        // Verificar si el docente está disponible en todos los horarios del curso
        for (CursoHorario horario : curso.getCursoHorario()) {
            boolean tieneDisponibilidad = disponibilidades.stream()
                    .anyMatch(disp ->
                            disp.getDiaSemana().equalsIgnoreCase(horario.getDiaSemana()) &&
                                    !disp.getHoraInicio().after(horario.getHoraInicio()) &&
                                    !disp.getHoraFin().before(horario.getHoraFin())
                    );

            if (!tieneDisponibilidad) {
                System.out.println("=== Docente " + docente.getCodigo() +
                        " no disponible para horario " + horario.getDiaSemana() +
                        " " + horario.getHoraInicio() + "-" + horario.getHoraFin());
                return false;
            }
        }

        return true;
    }

    /**
     * ACTUALIZADO: Solo considera horasMaxLectivas como restricción dura
     */
    private boolean puedeTomarCursoActualizado(Docente docente, Curso curso, Map<Integer, Integer> asignacionesTemporales) {
        // Calcular horas actuales del docente en asignaciones temporales
        int horasActuales = 0;
        for (Map.Entry<Integer, Integer> entry : asignacionesTemporales.entrySet()) {
            if (entry.getValue().equals(docente.getIdDocente())) {
                // Buscar el curso y sumar sus horas
                Curso cursoAsignado = obtenerCursosPorCiclo(curso.getCicloAcademico().getIdCicloAcademico())
                        .stream()
                        .filter(c -> c.getIdCurso().equals(entry.getKey()))
                        .findFirst().orElse(null);

                if (cursoAsignado != null) {
                    horasActuales += cursoAsignado.getCursoHorario().stream()
                            .mapToInt(CursoHorario::getDuracionHoras)
                            .sum();
                }
            }
        }

        // Sumar horas del curso que se quiere asignar
        int horasCurso = curso.getCursoHorario().stream()
                .mapToInt(CursoHorario::getDuracionHoras)
                .sum();

        // Verificar SOLO límite de horasMaxLectivas (ÚNICA RESTRICCIÓN DE HORAS)
        int horasMaximas = docente.getDedicacion().getHorasMaxLectivas() != null ?
                docente.getDedicacion().getHorasMaxLectivas() : 12;

        boolean puedeAsignar = horasActuales + horasCurso <= horasMaximas;

        if (!puedeAsignar) {
            System.out.println("=== Docente " + docente.getCodigo() +
                    " no puede tomar curso " + curso.getCodigo() +
                    " - Horas actuales: " + horasActuales + ", Curso horas: " + horasCurso +
                    ", Límite horasMaxLectivas: " + horasMaximas);
        }

        return puedeAsignar;
    }
    public GenericReponse<AsignacionResumenResponse> obtenerAsignacionesPorDocenteCarga(
            Integer idDocente,
            Integer idCarga
    ){
        Boolean isDocente = docenteRepo.existsByIdDocente(idDocente);
        if(!isDocente) {
            return new GenericReponse<>(200, "No se encontraron docente", null);
        }
        Boolean isCarga = cargaRepo.existsByIdCarga(idCarga);
        if(!isCarga) {
            return new GenericReponse<>(200, "No se encontraron carga", null);
        }
        List<Asignacion> asignaciones = asignacionRepo.findByDocenteAndCargaEnabled(idDocente, idCarga);
        if(asignaciones.isEmpty()) {
            return new GenericReponse<>(200, "No se encontraron asignaciones para este docente", null);
        }
        // Convertir a DTOs
        List<AsignacionResumenResponse> listaDTO = asignaciones.stream()
                .map(this:: convertResumenToDTO)
                .toList();
        return new GenericReponse<>(200, "Asignaciones de docente obtenida correctamente", listaDTO);

    }
    public GenericObjectResponse<String> eliminarAsignacion(Integer idAsignacion) {
        // Validación de parámetro
        if (idAsignacion == null) {
            return new GenericObjectResponse<>(400, "IdAsignacion no proporcionado", null);
        }

        // Validar existencia del curso
        Asignacion asignacion = asignacionRepo.findById(idAsignacion).orElse(null);
        if (asignacion == null) {
            return new GenericObjectResponse<>(404, "Asignación  no encontrado", null);
        }

        // desabilitar
        asignacion.setEnabled(false);
        asignacionRepo.save(asignacion);
        return new GenericObjectResponse<>(200, "se elimino la asignación exitosamente", null);
    }


    @Override
    public List<Asignacion> findByEnabledTrue() {
        return asignacionRepo.findByEnabledTrue();
    }
    private AsignacionDetalleResponse convertToDTO(Asignacion obj) {
        return modelMapper.map(obj, AsignacionDetalleResponse.class);
    }
    private AsignacionResumenResponse convertResumenToDTO(Asignacion obj) {
        return modelMapper.map(obj, AsignacionResumenResponse.class);
    }

}