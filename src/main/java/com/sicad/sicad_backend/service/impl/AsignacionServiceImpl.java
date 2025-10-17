package com.sicad.sicad_backend.service.impl;

import com.sicad.sicad_backend.Enum.Modulo;
import com.sicad.sicad_backend.algorithm.service.AlgoritmoAsignacionService;
import com.sicad.sicad_backend.dto.asignacion.AsignacionCreateRequest;
import com.sicad.sicad_backend.dto.asignacion.AsignacionDetalleResponse;
import com.sicad.sicad_backend.dto.asignacion.AsignacionResumenResponse;
import com.sicad.sicad_backend.dto.asignacion.AsignacionUpdateRequest;
import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.dto.carga.CargaDetalleResponse;
import com.sicad.sicad_backend.dto.escuela.EscuelaDetalleResponse;
import com.sicad.sicad_backend.model.*;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import com.sicad.sicad_backend.repository.interfaces.*;
import com.sicad.sicad_backend.service.base.CRUDImpl;
import com.sicad.sicad_backend.service.interfaces.IAsignacionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
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


    @Override
    public BaseObjectResponse<CargaDetalleResponse> asignarConAlgoritmoGeneticoPSO(Integer idCicloAcademico) {
        try {
            // 1. Validaciones iniciales
            CicloAcademico cicloAcademico = cicloAcademicoRepo.findById(idCicloAcademico).orElse(null);
            if (cicloAcademico == null) {
                return new BaseObjectResponse<>(404, "Ciclo académico no encontrado", null);
            }
            Optional<Algoritmo> principalActualOpt = algoritmoRepo.findByPrincipalTrue();
            if (!principalActualOpt.isPresent()) {
                return new BaseObjectResponse<>(400, "no exite un algoritmo principal seleccionado", null);
            }
            Algoritmo algoritmoPrincipal = principalActualOpt.get();

            // 2. Recolección de datos necesarios
            // Obtener docentes que tienen disponibilidad para este cicloa cademico
            List<Docente> docentes = obtenerDocentesDisponibles(idCicloAcademico);
            // obtener cursos de un ciclo academico especifico
            List<Curso> cursos = obtenerCursosPorCiclo(idCicloAcademico);

            if (docentes.isEmpty()) {
                return new BaseObjectResponse<>(400, "No hay docentes disponibles para este ciclo academico", null);
            }

            if (cursos.isEmpty()) {
                return new BaseObjectResponse<>(400, "No hay cursos para el ciclo académico ", null);
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
                return new BaseObjectResponse<>(400, "No se pudieron generar asignaciones válidas con el algoritmo actualizado", null);
            }

            // 7. Persistir las asignaciones
            List<Asignacion> asignacionesGuardadas = asignacionRepo.saveAll(asignacionesOptimas);


            generarResumenAlgoritmoActualizado(asignacionesGuardadas, docentes, cursos,carga);

            Carga obj = cargaRepo.findById(idCicloAcademico).orElse(null);
            if(obj == null) {
                return new BaseObjectResponse<>(400,"carga no encontrada",null);
            }
            return new BaseObjectResponse(200,"Algoritmo hibrido realizado exitosamente", convCargaDetalle(carga));

        } catch (Exception e) {
            System.out.println("=== Error en asignación con algoritmo híbrido actualizado ===");
            return new BaseObjectResponse<>(500, "Error interno en algoritmo híbrido actualizado: " + e.getMessage(), null);
        }
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
            int horas = asignacion.getCurso().getHorario().stream()
                    .mapToInt(Horario::getDuracionHoras)
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


    // Métodos auxiliares para el algoritmo
    private List<Docente> obtenerDocentesDisponibles(Integer idCicloAcademico) {
        // Obtener docentes que tienen disponibilidad para esta carga electiva
        List<Disponibilidad> disponibilidades = disponibilidadRepo.findByCicloAcademico_IdCicloAcademico(idCicloAcademico);
        return disponibilidades.stream()
                .map(Disponibilidad::getDocente)
                .collect(Collectors.toList());
    }

    private List<Curso> obtenerCursosPorCiclo(Integer idCicloAcademico) {
        return cursoRepo.buscarPorCicloAcademico(idCicloAcademico);
    }

    //---------------------------------------------------------------------------------------

    @Override
    public BaseObjectResponse<AsignacionDetalleResponse> buscar(Integer idAsignacion) {
        Optional<Asignacion> asignacionOpt = asignacionRepo.findByIdAndEnabledTrue(idAsignacion);

        if (asignacionOpt.isEmpty()) {
            return new BaseObjectResponse<>(404, Modulo.ASIGNACION.noEncontrado(), null);
        }

        return new BaseObjectResponse<>(200, Modulo.ASIGNACION.encontrado(), convAsignacionDetalle(asignacionOpt.get()));
    }

    @Override
    public BaseObjectResponse<AsignacionDetalleResponse> registrar(AsignacionCreateRequest request) {
        Optional<Docente> docenteOpt = docenteRepo.findByIdAndEnabledTrue(request.getIdDocente());
        if (docenteOpt.isEmpty()) {
            return new BaseObjectResponse<>(404, Modulo.DOCENTE.noEncontrado(), null);
        }
        Optional<Curso> cursoOpt = cursoRepo.findByIdAndEnabledTrue(request.getIdCurso());
        if (cursoOpt.isEmpty()) {
            return new BaseObjectResponse<>(404, Modulo.DOCENTE.noEncontrado(), null);
        }
        Optional<CicloAcademico> cicloAcademicoOpt = cicloAcademicoRepo.findByIdAndEnabledTrue(request.getIdCicloAcademico());
        if (cicloAcademicoOpt.isEmpty()) {
            return new BaseObjectResponse<>(404, Modulo.DOCENTE.noEncontrado(), null);
        }
        Optional<Carga> cargaOpt = cargaRepo.findByIdAndEnabledTrue(request.getIdCarga());
        if (cargaOpt.isEmpty()) {
            return new BaseObjectResponse<>(404, Modulo.CARGA.noEncontrado(), null);
        }

        Asignacion asignacion = Asignacion.builder()
                .docente(docenteOpt.get())
                .curso(cursoOpt.get())
                .cicloAcademico(cicloAcademicoOpt.get())
                .carga(cargaOpt.get())
                .enabled(true)
                .createdAt(LocalDate.now())
                .build();

        asignacionRepo.save(asignacion);
        return new BaseObjectResponse<>(201, Modulo.ASIGNACION.registrado(), convAsignacionDetalle(asignacion));
    }

    @Override
    public BaseListReponse<AsignacionDetalleResponse> registrarAll(List<AsignacionCreateRequest> requests) {
        List<AsignacionDetalleResponse> registrados = new ArrayList<>();
        int errorCount = 0;

        for (AsignacionCreateRequest request : requests) {
            try {
                BaseObjectResponse<AsignacionDetalleResponse> response = registrar(request);
                if (response.status() == 201 && response.data() != null) {
                    registrados.add(response.data());
                } else {
                    errorCount++;
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                errorCount++;
            }
        }
        return new BaseListReponse<>(201,Modulo.ASIGNACION.resumenAllRegistro(registrados.size(), errorCount),registrados);
    }

    @Override
    public BaseObjectResponse<AsignacionDetalleResponse> actualizar(Integer idAsignacion, AsignacionUpdateRequest request) {
        Optional<Asignacion> asignacionOpt = asignacionRepo.findByIdAndEnabledTrue(idAsignacion);
        if (asignacionOpt.isEmpty()) {
            return new BaseObjectResponse<>(404, Modulo.ASIGNACION.noEncontrado(), null);
        }
        Asignacion asignacion = asignacionOpt.get();
        if(request.getIdDocente() != null) {
            Optional<Docente> docenteOpt = docenteRepo.findByIdAndEnabledTrue(request.getIdDocente());
            if (docenteOpt.isEmpty()) {
                return new BaseObjectResponse<>(404, Modulo.DOCENTE.noEncontrado(), null);
            } else {
                asignacion.setDocente(docenteOpt.get());
            }
        }
        if(request.getIdCurso() != null) {
            Optional<Curso> cursoOpt = cursoRepo.findByIdAndEnabledTrue(request.getIdCurso());
            if (cursoOpt.isEmpty()) {
                return new BaseObjectResponse<>(404, Modulo.DOCENTE.noEncontrado(), null);
            } else {
                asignacion.setCurso(cursoOpt.get());
            }
        }
        if(request.getIdCicloAcademico() != null) {
            Optional<CicloAcademico> cicloAcademicoOpt = cicloAcademicoRepo.findByIdAndEnabledTrue(request.getIdCicloAcademico());
            if (cicloAcademicoOpt.isEmpty()) {
                return new BaseObjectResponse<>(404, Modulo.DOCENTE.noEncontrado(), null);
            } else {
                asignacion.setCicloAcademico(cicloAcademicoOpt.get());
            }
        }
        if(request.getIdCarga() != null) {
            Optional<Carga> cargaOpt = cargaRepo.findByIdAndEnabledTrue(request.getIdCarga());
            if (cargaOpt.isEmpty()) {
                return new BaseObjectResponse<>(404, Modulo.CARGA.noEncontrado(), null);
            } else {
                asignacion.setCarga(cargaOpt.get());
            }
        }
        return new BaseObjectResponse<>(201, Modulo.ASIGNACION.actualizado(), convAsignacionDetalle(asignacion));

    }

    @Override
    public BaseObjectResponse<String> eliminar(Integer idAsignacion) {
        Optional<Asignacion> asignacionOpt = asignacionRepo.findByIdAndEnabledTrue(idAsignacion);
        if (asignacionOpt.isEmpty()) {
            return new BaseObjectResponse<>(404, Modulo.ASIGNACION.noEncontrado(), null);
        }
        Asignacion asignacion = asignacionOpt.get();
        asignacion.setEnabled(false);
        asignacionRepo.save(asignacion);
        return new BaseObjectResponse<>(200, Modulo.ASIGNACION.eliminado(), null);
    }

    @Override
    public BaseListReponse<AsignacionResumenResponse> listarPorDocenteCarga(Integer idDocente, Integer idCarga) {
        List<AsignacionResumenResponse> response =asignacionRepo.findByDocenteAndCargaEnabled(idDocente,idCarga)
                .stream()
                .map(this::convAsignacionResumen)
                .toList();

        return new BaseListReponse<>(200,Modulo.ASIGNACION.listado(), response);

    }
    private AsignacionResumenResponse convAsignacionResumen(Asignacion obj) {
        return modelMapper.map(obj, AsignacionResumenResponse.class);
    }
    private AsignacionDetalleResponse convAsignacionDetalle(Asignacion obj) {
        return modelMapper.map(obj, AsignacionDetalleResponse.class);
    }
    private CargaDetalleResponse convCargaDetalle(Carga obj) {
        return modelMapper.map(obj, CargaDetalleResponse.class);
    }

}