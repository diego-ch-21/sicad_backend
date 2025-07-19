package com.sicad.sicad_backend.service.impl;

import com.sicad.sicad_backend.algorithm.service.AlgoritmoAsignacionService;
import com.sicad.sicad_backend.dto.asignacion.AsignacionCreateRequest;
import com.sicad.sicad_backend.dto.asignacion.AsignacionDetalleResponse;
import com.sicad.sicad_backend.dto.asignacion.AsignacionUpdateRequest;
import com.sicad.sicad_backend.dto.base.GenericObjectResponse;
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
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class AsignacionServiceImpl
        extends CRUDImpl<Asignacion, Integer>
        implements IAsignacionService {

    private final IAsignacionRepo asignacionRepo;
    private final IDocenteRepo docenteRepo;
    private final ICursoRepo cursoRepo;
    private final ICargaElectivaRepo cargaElectivaRepo;
    private final IPreferenciaRepo preferenciaRepo;
    private final IDisponibilidadRepo disponibilidadRepo;
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

        CargaElectiva carga = cargaElectivaRepo.findById(request.getIdCargaElectiva()).orElse(null);
        if (carga == null) {
            return new GenericObjectResponse<>(404, "Carga Electiva no encontrada", null);
        }

        Asignacion asignacion = new Asignacion();
        asignacion.setDocente(docente);
        asignacion.setCurso(curso);
        asignacion.setCargaElectiva(carga);
        asignacion.setTipoAsignacion(request.getTipoAsignacion());
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

        if (request.getIdCargaElectiva() != null) {
            cargaElectivaRepo.findById(request.getIdCargaElectiva()).ifPresent(asignacion::setCargaElectiva);
        }

        if (request.getTipoAsignacion() != null) {
            asignacion.setTipoAsignacion(request.getTipoAsignacion());
        }

        try {
            asignacionRepo.save(asignacion);
            AsignacionDetalleResponse dto = modelMapper.map(asignacion, AsignacionDetalleResponse.class);
            return new GenericObjectResponse<>(200, "Asignación actualizada exitosamente", dto);
        } catch (DataIntegrityViolationException e) {
            return new GenericObjectResponse<>(400, "Conflicto de unicidad: ya existe una asignación para este docente y horario", null);
        }
    }

    @Transactional
    public GenericObjectResponse<String> eliminarAsignacionesPorCargaElectiva(Integer idCargaElectiva) {
        CargaElectiva carga = cargaElectivaRepo.findById(idCargaElectiva).orElse(null);
        if (carga == null) {
            return new GenericObjectResponse<>(404, "Carga Electiva no encontrada", null);
        }

        int eliminados = asignacionRepo.deleteByCargaElectiva_IdCargaElectiva(idCargaElectiva);

        String mensaje = "Se eliminaron " + eliminados + " asignación(es) para la carga electiva ID: " + idCargaElectiva;
        return new GenericObjectResponse<>(200, mensaje, null);
    }
    //------------------------------------------------------------------------------------------------------

    /**
     * MÉTODO PRINCIPAL ACTUALIZADO: Ejecuta algoritmo híbrido GA+PSO con nuevo modelo de restricciones
     */
    @Transactional
    public GenericObjectResponse<List<AsignacionDetalleResponse>> asignarConAlgoritmoGeneticoPSO(Integer idCargaElectiva) {
        System.out.println("=== Iniciando asignación con algoritmo híbrido actualizado para carga electiva: " + idCargaElectiva + " ===");
        System.out.println("MODELO ACTUALIZADO - RESTRICCIONES DURAS: Disponibilidad + horasMaxLectivas + preferencias");



        try {
            // 1. Validaciones iniciales
            CargaElectiva cargaElectiva = cargaElectivaRepo.findById(idCargaElectiva).orElse(null);
            if (cargaElectiva == null) {
                return new GenericObjectResponse<>(404, "Carga Electiva no encontrada", null);
            }

            // 2. Recolección de datos necesarios
            List<Docente> docentes = obtenerDocentesDisponibles(idCargaElectiva);
            List<Curso> cursos = obtenerCursosPorCiclo(cargaElectiva.getCicloAcademico().getIdCicloAcademico());

            if (docentes.isEmpty()) {
                return new GenericObjectResponse<>(400, "No hay docentes disponibles para esta carga electiva", null);
            }

            if (cursos.isEmpty()) {
                return new GenericObjectResponse<>(400, "No hay cursos para el ciclo académico de esta carga electiva", null);
            }

            // 3. Preparar datos para el algoritmo
            Map<Integer, List<Disponibilidad>> disponibilidadPorDocente = prepararDisponibilidad(docentes, idCargaElectiva);
            Map<Integer, List<Preferencia>> preferenciasPorDocente = prepararPreferencias(docentes, idCargaElectiva);
            System.out.println("=== Datos preparados - Disponibilidad y preferencias por docente ===");
            logearLimitesDocentesActualizado(docentes);

            // 4. Eliminar asignaciones anteriores para esta carga electiva
            asignacionRepo.deleteByCargaElectiva_IdCargaElectiva(idCargaElectiva);
            System.out.println("=== Asignaciones anteriores eliminadas ===");

            // 5. Ejecutar algoritmo híbrido actualizado
            List<Asignacion> asignacionesOptimas = algoritmoService.ejecutarAlgoritmoHibrido(
                    docentes, cursos, cargaElectiva, disponibilidadPorDocente, preferenciasPorDocente);

            // 6. Validar y guardar resultados
            if (asignacionesOptimas.isEmpty()) {
                return new GenericObjectResponse<>(400, "No se pudieron generar asignaciones válidas con el algoritmo actualizado", null);
            }

            // 7. Persistir las asignaciones
            List<Asignacion> asignacionesGuardadas = asignacionRepo.saveAll(asignacionesOptimas);
            System.out.println("=== Asignaciones guardadas exitosamente ===");

            // 8. Convertir a DTOs
            List<AsignacionDetalleResponse> response = asignacionesGuardadas.stream()
                    .map(asignacion -> modelMapper.map(asignacion, AsignacionDetalleResponse.class))
                    .collect(Collectors.toList());

            // 9. Generar resumen de resultados actualizado
            String resumen = generarResumenAlgoritmoActualizado(asignacionesGuardadas, docentes, cursos);

            return new GenericObjectResponse<>(201, resumen, response);

        } catch (Exception e) {
            System.out.println("=== Error en asignación con algoritmo híbrido actualizado ===");
            return new GenericObjectResponse<>(500, "Error interno en algoritmo híbrido actualizado: " + e.getMessage(), null);
        }
    }

    /**
     * Prepara el mapa de disponibilidad por docente
     */
    private Map<Integer, List<Disponibilidad>> prepararDisponibilidad(List<Docente> docentes, Integer idCargaElectiva) {
        Map<Integer, List<Disponibilidad>> mapa = new HashMap<>();

        for (Docente docente : docentes) {
            List<Disponibilidad> disponibilidades = disponibilidadRepo.buscarPorDocenteYCargaElectiva(
                    docente.getIdDocente(), idCargaElectiva);
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
            List<Preferencia> preferencias = preferenciaRepo.buscarPorDocenteYCargaElectiva(
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
            int horasMaximas = docente.getHorasMaxLectivas() != null ?
                    docente.getHorasMaxLectivas() : 12;
            System.out.println("Docente " + docente.getCodigo() + ": máximo " + horasMaximas + " horas (horasMaxLectivas)");
        }
        System.out.println("=== FIN DE LÍMITES ===");
    }

    /**
     * ACTUALIZADO: Genera resumen detallado del algoritmo con nuevo modelo
     */
    private String generarResumenAlgoritmoActualizado(List<Asignacion> asignaciones, List<Docente> docentes, List<Curso> cursos) {
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
                        int limite = docente.getHorasMaxLectivas() != null ?
                                docente.getHorasMaxLectivas() : 12;
                        return entry.getValue() > limite;
                    }
                    return false;
                })
                .count();

        return String.format(
                " ALGORITMO HÍBRIDO GA+PSO COMPLETADO (MODELO ACTUALIZADO) \n" +
                        "RESTRICCIONES DURAS: Disponibilidad + horasMaxLectivas + preferencias\n" +
                        //"RESTRICCIONES DURAS: Disponibilidad + horasMaxLectivas\n" +
                        //"RESTRICCIONES BLANDAS: Solo preferencias (opcionales)\n" +
                        "ELIMINADO: Consideración de dedicación y categoría\n" +
                        "RESULTADOS:\n" +
                        "  • Cursos asignados: %d/%d (%.1f%%)\n" +
                        "  • Cursos sin asignar: %d\n" +
                        "  • Docentes utilizados: %d/%d (%.1f%%)\n" +
                        "  • Preferencias satisfechas: %.1f%% (opcional)\n" +
                        "DISTRIBUCIÓN DE CARGA:\n" +
                        "  • Promedio horas/docente: %.1f\n" +
                        "  • Máximo horas: %d\n" +
                        "  • Mínimo horas: %d\n" +
                        "    • Docentes que exceden horasMaxLectivas: %d\n" +
                        "  Optimización completada con nuevo modelo de restricciones",
                cursosAsignados, cursos.size(), (double) cursosAsignados / cursos.size() * 100.0,
                cursosSinAsignar,
                docentesUtilizados.size(), docentes.size(), (double) docentesUtilizados.size() / docentes.size() * 100.0,
                porcentajePreferencias,
                promedioHoras.orElse(0.0),
                maxHoras,
                minHoras,
                docentesExcedidos
        );
    }

    /**
     * Verifica si una asignación satisface las preferencias del docente (RESTRICCIÓN BLANDA)
     */
    private boolean verificarPreferenciaSatisfecha(Asignacion asignacion) {
        List<Preferencia> preferencias = preferenciaRepo.buscarPorDocenteYCargaElectiva(
                asignacion.getDocente().getIdDocente(),
                asignacion.getCargaElectiva().getIdCargaElectiva());

        return preferencias.stream()
                .anyMatch(pref -> pref.getAsignatura().getIdAsignatura()
                        .equals(asignacion.getCurso().getAsignatura().getIdAsignatura()));
    }

    /**
     * ACTUALIZADO: Obtiene estadísticas detalladas con nuevo modelo de restricciones
     */
    public GenericObjectResponse<Map<String, Object>> obtenerEstadisticasAsignacion(Integer idCargaElectiva) {
        try {
            // Validar carga electiva
            CargaElectiva cargaElectiva = cargaElectivaRepo.findById(idCargaElectiva).orElse(null);
            if (cargaElectiva == null) {
                return new GenericObjectResponse<>(404, "Carga Electiva no encontrada", null);
            }

            // Obtener asignaciones actuales
            List<Asignacion> asignaciones = asignacionRepo.findAll().stream()
                    .filter(a -> a.getCargaElectiva().getIdCargaElectiva().equals(idCargaElectiva))
                    .filter(Asignacion::getEnabled)
                    .collect(Collectors.toList());

            // Obtener datos de contexto
            List<Docente> docentesDisponibles = obtenerDocentesDisponibles(idCargaElectiva);
            List<Curso> cursosDelCiclo = obtenerCursosPorCiclo(cargaElectiva.getCicloAcademico().getIdCicloAcademico());

            // Calcular estadísticas
            Map<String, Object> estadisticas = new HashMap<>();

            // Estadísticas básicas
            estadisticas.put("totalCursos", cursosDelCiclo.size());
            estadisticas.put("cursosAsignados", asignaciones.size());
            estadisticas.put("cursosSinAsignar", cursosDelCiclo.size() - asignaciones.size());
            estadisticas.put("porcentajeCobertura", cursosDelCiclo.isEmpty() ? 0.0 :
                    (double) asignaciones.size() / cursosDelCiclo.size() * 100.0);

            // Estadísticas de docentes
            Set<Integer> docentesUtilizados = asignaciones.stream()
                    .map(a -> a.getDocente().getIdDocente())
                    .collect(Collectors.toSet());

            estadisticas.put("totalDocentesDisponibles", docentesDisponibles.size());
            estadisticas.put("docentesUtilizados", docentesUtilizados.size());
            estadisticas.put("docentesSinAsignar", docentesDisponibles.size() - docentesUtilizados.size());

            // Distribución de carga por docente
            Map<String, Integer> cargaPorDocente = new HashMap<>();
            Map<Integer, Integer> horasPorDocente = new HashMap<>();
            Map<String, String> limitesDocente = new HashMap<>();

            for (Asignacion asignacion : asignaciones) {
                String codigoDocente = asignacion.getDocente().getCodigo();
                Integer idDocente = asignacion.getDocente().getIdDocente();

                int horas = asignacion.getCurso().getCursoHorario().stream()
                        .mapToInt(CursoHorario::getDuracionHoras)
                        .sum();

                cargaPorDocente.merge(codigoDocente, horas, Integer::sum);
                horasPorDocente.merge(idDocente, horas, Integer::sum);

                // ACTUALIZADO: Solo mostrar límite de horasMaxLectivas
                int limite = asignacion.getDocente().getHorasMaxLectivas() != null ?
                        asignacion.getDocente().getHorasMaxLectivas() : 12;
                limitesDocente.put(codigoDocente, horas + "/" + limite + " horas (horasMaxLectivas)");
            }

            estadisticas.put("distribucionCarga", cargaPorDocente);
            estadisticas.put("limitesDocentes", limitesDocente);

            // Estadísticas de horas
            if (!horasPorDocente.isEmpty()) {
                OptionalDouble promedioHoras = horasPorDocente.values().stream()
                        .mapToInt(Integer::intValue)
                        .average();

                int maxHoras = horasPorDocente.values().stream()
                        .mapToInt(Integer::intValue)
                        .max().orElse(0);

                int minHoras = horasPorDocente.values().stream()
                        .mapToInt(Integer::intValue)
                        .min().orElse(0);

                estadisticas.put("promedioHorasPorDocente", promedioHoras.orElse(0.0));
                estadisticas.put("maximoHoras", maxHoras);
                estadisticas.put("minimoHoras", minHoras);
            }

            // ACTUALIZADO: Análisis de violaciones solo de horasMaxLectivas
            List<String> docentesExcedidos = new ArrayList<>();
            for (Map.Entry<Integer, Integer> entry : horasPorDocente.entrySet()) {
                Docente docente = docentesDisponibles.stream()
                        .filter(d -> d.getIdDocente().equals(entry.getKey()))
                        .findFirst().orElse(null);

                if (docente != null) {
                    int limite = docente.getHorasMaxLectivas() != null ?
                            docente.getHorasMaxLectivas() : 12;
                    if (entry.getValue() > limite) {
                        docentesExcedidos.add(String.format("%s: %d/%d horas (excede horasMaxLectivas)",
                                docente.getCodigo(), entry.getValue(), limite));
                    }
                }
            }
            estadisticas.put("docentesQueExcedenLimite", docentesExcedidos);

            // ACTUALIZADO: Análisis de preferencias como restricción blanda
            long preferenciasSatisfechas = asignaciones.stream()
                    .filter(this::verificarPreferenciaSatisfecha)
                    .count();

            double porcentajePreferencias = asignaciones.isEmpty() ? 0.0 :
                    (double) preferenciasSatisfechas / asignaciones.size() * 100.0;

            estadisticas.put("preferenciasSatisfechas", preferenciasSatisfechas);
            estadisticas.put("porcentajePreferencias", porcentajePreferencias);
            estadisticas.put("notaPreferencias", "Las preferencias son restricciones blandas (opcionales)");

            // Cursos sin asignar
            List<String> cursosSinAsignar = cursosDelCiclo.stream()
                    .filter(curso -> asignaciones.stream()
                            .noneMatch(a -> a.getCurso().getIdCurso().equals(curso.getIdCurso())))
                    .map(curso -> curso.getCodigo() + " - " + curso.getAsignatura().getNombre())
                    .collect(Collectors.toList());

            estadisticas.put("cursosSinAsignarDetalle", cursosSinAsignar);

            // ACTUALIZADO: Metadata con nuevo modelo
            estadisticas.put("cargaElectiva", cargaElectiva.getNombre());
            estadisticas.put("fechaAnalisis", LocalDate.now().toString());
            estadisticas.put("totalAsignaciones", asignaciones.size());
            //estadisticas.put("modeloRestriccion", "ACTUALIZADO: Duras = Disponibilidad + horasMaxLectivas | Blandas = Solo preferencias");
            estadisticas.put("restriccionesDuras", "Disponibilidad horaria + horasMaxLectivas");
            estadisticas.put("restriccionesBlandas", "Solo preferencias de docentes (opcionales)");
            estadisticas.put("eliminado", "Consideración de dedicación y categoría");

            return new GenericObjectResponse<>(200, "Estadísticas generadas con modelo actualizado de restricciones", estadisticas);

        } catch (Exception e) {
            System.out.println("=== Error al generar estadísticas para carga electiva " + idCargaElectiva + " ===");
            return new GenericObjectResponse<>(500, "Error al generar estadísticas: " + e.getMessage(), null);
        }
    }

    // Métodos auxiliares para el algoritmo

    private List<Docente> obtenerDocentesDisponibles(Integer idCargaElectiva) {
        // Obtener docentes que tienen disponibilidad para esta carga electiva
        List<Disponibilidad> disponibilidades = disponibilidadRepo.findByCargaElectiva_IdCargaElectiva(idCargaElectiva);
        return disponibilidades.stream()
                .map(Disponibilidad::getDocente)
                .distinct()
                .filter(docente -> docente.isEnabled())
                .collect(Collectors.toList());
    }

    private List<Curso> obtenerCursosPorCiclo(Integer idCicloAcademico) {
        return cursoRepo.buscarPorPeriodoAcademico(idCicloAcademico);
    }

    private List<Asignacion> ejecutarAlgoritmoHibrido(List<Docente> docentes, List<Curso> cursos, CargaElectiva cargaElectiva) {
        // Usar el nuevo servicio de algoritmo híbrido actualizado
        try {
            // Preparar datos para el algoritmo
            Map<Integer, List<Disponibilidad>> disponibilidadPorDocente = prepararDisponibilidad(docentes, cargaElectiva.getIdCargaElectiva());
            Map<Integer, List<Preferencia>> preferenciasPorDocente = prepararPreferencias(docentes, cargaElectiva.getIdCargaElectiva());

            // Ejecutar algoritmo híbrido actualizado
            return algoritmoService.ejecutarAlgoritmoHibrido(
                    docentes, cursos, cargaElectiva, disponibilidadPorDocente, preferenciasPorDocente);

        } catch (Exception e) {
            System.out.println("=== Error en algoritmo híbrido actualizado, usando algoritmo simplificado ===");

            // Fallback: algoritmo simplificado si falla el actualizado
            return ejecutarAlgoritmoSimplificado(docentes, cursos, cargaElectiva);
        }
    }

    /**
     * ACTUALIZADO: Algoritmo simplificado como fallback con nuevo modelo
     */
    private List<Asignacion> ejecutarAlgoritmoSimplificado(List<Docente> docentes, List<Curso> cursos, CargaElectiva cargaElectiva) {
        System.out.println("=== Ejecutando algoritmo simplificado actualizado como fallback ===");
        System.out.println("RESTRICCIONES: Solo disponibilidad + horasMaxLectivas");

        List<Asignacion> asignaciones = new ArrayList<>();
        Map<Integer, Integer> asignacionesTemporales = new HashMap<>();

        // Versión básica: asignación por disponibilidad y límites de horasMaxLectivas
        for (Curso curso : cursos) {
            Docente docenteAsignado = encontrarMejorDocenteParaCursoActualizado(
                    curso, docentes, cargaElectiva, asignacionesTemporales);

            if (docenteAsignado != null) {
                asignacionesTemporales.put(curso.getIdCurso(), docenteAsignado.getIdDocente());

                Asignacion asignacion = new Asignacion();
                asignacion.setDocente(docenteAsignado);
                asignacion.setCurso(curso);
                asignacion.setCargaElectiva(cargaElectiva);
                asignacion.setTipoAsignacion("LECTIVO");
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
                                                              CargaElectiva cargaElectiva,
                                                              Map<Integer, Integer> asignacionesTemporales) {
        // 1. Buscar docentes con preferencia por la asignatura del curso (RESTRICCIÓN BLANDA)
        List<Preferencia> preferencias = preferenciaRepo.findByCargaElectiva_IdCargaElectiva(cargaElectiva.getIdCargaElectiva());

        List<Docente> docentesConPreferencia = preferencias.stream()
                .filter(pref -> pref.getAsignatura().getIdAsignatura().equals(curso.getAsignatura().getIdAsignatura()))
                .map(Preferencia::getDocente)
                .filter(docente -> docentes.contains(docente))
                .collect(Collectors.toList());

        // 2. Priorizar docentes con preferencia, pero no es obligatorio
        if (!docentesConPreferencia.isEmpty()) {
            for (Docente docente : docentesConPreferencia) {
                if (verificarDisponibilidadHoraria(docente, curso, cargaElectiva) &&
                        puedeTomarCursoActualizado(docente, curso, asignacionesTemporales)) {
                    System.out.println("Docente " + docente.getCodigo() +
                            " asignado por preferencia a curso " + curso.getCodigo());
                    return docente;
                }
            }
        }

        // 3. Si no hay preferencias válidas, buscar cualquier docente disponible
        for (Docente docente : docentes) {
            if (verificarDisponibilidadHoraria(docente, curso, cargaElectiva) &&
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
    private boolean verificarDisponibilidadHoraria(Docente docente, Curso curso, CargaElectiva cargaElectiva) {
        List<Disponibilidad> disponibilidades = disponibilidadRepo.buscarPorDocenteYCargaElectiva(
                docente.getIdDocente(), cargaElectiva.getIdCargaElectiva());

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
        int horasMaximas = docente.getHorasMaxLectivas() != null ?
                docente.getHorasMaxLectivas() : 12;

        boolean puedeAsignar = horasActuales + horasCurso <= horasMaximas;

        if (!puedeAsignar) {
            System.out.println("=== Docente " + docente.getCodigo() +
                    " no puede tomar curso " + curso.getCodigo() +
                    " - Horas actuales: " + horasActuales + ", Curso horas: " + horasCurso +
                    ", Límite horasMaxLectivas: " + horasMaximas);
        }

        return puedeAsignar;
    }

    /**
     * ACTUALIZADO: Genera resumen con nuevo modelo de restricciones
     */
    private String generarResumenAsignaciones(List<Asignacion> asignaciones, List<Docente> docentes, List<Curso> cursos) {
        int cursosAsignados = asignaciones.size();
        int cursosSinAsignar = cursos.size() - cursosAsignados;
        int docentesUtilizados = (int) asignaciones.stream()
                .map(a -> a.getDocente().getIdDocente())
                .distinct()
                .count();

        // Contar preferencias satisfechas
        long preferenciasSatisfechas = asignaciones.stream()
                .filter(this::verificarPreferenciaSatisfecha)
                .count();

        double porcentajePreferencias = asignaciones.isEmpty() ? 0.0 :
                (double) preferenciasSatisfechas / asignaciones.size() * 100.0;

        return String.format(
                "Algoritmo simplificado completado exitosamente.\n" +
                        "MODELO ACTUALIZADO - RESTRICCIONES DURAS: Disponibilidad + horasMaxLectivas\n" +
                        "MODELO ACTUALIZADO - RESTRICCIONES BLANDAS: Solo preferencias (%.1f%% satisfechas)\n" +
                        "ELIMINADO: Consideración de dedicación y categoría\n" +
                        "Cursos asignados: %d/%d. Docentes utilizados: %d/%d. Cursos sin asignar: %d.",
                porcentajePreferencias, cursosAsignados, cursos.size(),
                docentesUtilizados, docentes.size(), cursosSinAsignar
        );
    }
}