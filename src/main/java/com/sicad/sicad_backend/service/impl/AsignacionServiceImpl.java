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
    // NUEVO MÉTODO PARA EL ALGORITMO GENÉTICO + PSO
    @Transactional
    public GenericObjectResponse<List<AsignacionDetalleResponse>> asignarConAlgoritmoGeneticoPSO(Integer idCargaElectiva) {
        System.out.println("Iniciando asignación con algoritmo genético + PSO para carga electiva: " + idCargaElectiva);
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
            System.out.println("Datos recolectados - Docentes: " + docentes.size() + ", Cursos: " + cursos.size());

            // 3. Eliminar asignaciones anteriores para esta carga electiva
            asignacionRepo.deleteByCargaElectiva_IdCargaElectiva(idCargaElectiva);
            System.out.println("Asignaciones anteriores eliminadas");

            // 4. Ejecutar algoritmo híbrido
            List<Asignacion> asignacionesOptimas = ejecutarAlgoritmoHibrido(docentes, cursos, cargaElectiva);

            // 5. Validar y guardar resultados
            if (asignacionesOptimas.isEmpty()) {
                return new GenericObjectResponse<>(400, "No se pudieron generar asignaciones válidas", null);
            }

            // 6. Persistir las asignaciones
            List<Asignacion> asignacionesGuardadas = asignacionRepo.saveAll(asignacionesOptimas);
            System.out.println("Asignaciones guardadas: " + asignacionesGuardadas.size());

            // 7. Convertir a DTOs
            List<AsignacionDetalleResponse> response = asignacionesGuardadas.stream()
                    .map(asignacion -> modelMapper.map(asignacion, AsignacionDetalleResponse.class))
                    .collect(Collectors.toList());

            // 8. Generar resumen de resultados
            String resumen = generarResumenAsignaciones(asignacionesGuardadas, docentes, cursos);

            return new GenericObjectResponse<>(201, resumen, response);

        } catch (Exception e) {
            System.out.println("Error en asignación con algoritmo: " + e.getMessage());
            return new GenericObjectResponse<>(500, "Error interno: " + e.getMessage(), null);
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
        // TODO: Por ahora implementamos una versión simplificada
        // En los siguientes pasos implementaremos el algoritmo genético y PSO completo

        System.out.println("Ejecutando algoritmo híbrido simplificado...");

        List<Asignacion> asignaciones = new ArrayList<>();

        // Versión básica: asignación por disponibilidad y preferencias
        for (Curso curso : cursos) {
            Docente docenteAsignado = encontrarMejorDocenteParaCurso(curso, docentes, cargaElectiva);

            if (docenteAsignado != null) {
                Asignacion asignacion = new Asignacion();
                asignacion.setDocente(docenteAsignado);
                asignacion.setCurso(curso);
                asignacion.setCargaElectiva(cargaElectiva);
                asignacion.setTipoAsignacion("LECTIVO");
                asignacion.setEnabled(true);
                asignacion.setCreatedAt(LocalDate.now());

                asignaciones.add(asignacion);
                System.out.println("Asignado curso " + curso.getCodigo() + " al docente " + docenteAsignado.getCodigo());
            }
        }

        return asignaciones;
    }

    private Docente encontrarMejorDocenteParaCurso(Curso curso, List<Docente> docentes, CargaElectiva cargaElectiva) {
        // Buscar docentes con preferencia por la asignatura del curso
        List<Preferencia> preferencias = preferenciaRepo.findByCargaElectiva_IdCargaElectiva(cargaElectiva.getIdCargaElectiva());

        // Filtrar por asignatura del curso
        List<Docente> docentesConPreferencia = preferencias.stream()
                .filter(pref -> pref.getAsignatura().getIdAsignatura().equals(curso.getAsignatura().getIdAsignatura()))
                .map(Preferencia::getDocente)
                .filter(docente -> docentes.contains(docente))
                .collect(Collectors.toList());

        if (!docentesConPreferencia.isEmpty()) {
            // Verificar disponibilidad horaria
            for (Docente docente : docentesConPreferencia) {
                if (verificarDisponibilidadHoraria(docente, curso, cargaElectiva)) {
                    return docente;
                }
            }
        }

        // Si no hay preferencias, buscar cualquier docente disponible
        for (Docente docente : docentes) {
            if (verificarDisponibilidadHoraria(docente, curso, cargaElectiva)) {
                return docente;
            }
        }

        return null;
    }

    private boolean verificarDisponibilidadHoraria(Docente docente, Curso curso, CargaElectiva cargaElectiva) {
        List<Disponibilidad> disponibilidades = disponibilidadRepo.buscarPorDocenteYCargaElectiva(
                docente.getIdDocente(), cargaElectiva.getIdCargaElectiva());

        // Verificar si el docente está disponible en los horarios del curso
        for (CursoHorario horario : curso.getCursoHorario()) {
            boolean tieneDisponibilidad = disponibilidades.stream()
                    .anyMatch(disp ->
                            disp.getDiaSemana().equalsIgnoreCase(horario.getDiaSemana()) &&
                                    !disp.getHoraInicio().after(horario.getHoraInicio()) &&
                                    !disp.getHoraFin().before(horario.getHoraFin())
                    );

            if (!tieneDisponibilidad) {
                return false;
            }
        }

        return true;
    }

    private String generarResumenAsignaciones(List<Asignacion> asignaciones, List<Docente> docentes, List<Curso> cursos) {
        int cursosAsignados = asignaciones.size();
        int cursosSinAsignar = cursos.size() - cursosAsignados;
        int docentesUtilizados = (int) asignaciones.stream()
                .map(a -> a.getDocente().getIdDocente())
                .distinct()
                .count();

        return String.format(
                "Algoritmo completado exitosamente. Cursos asignados: %d/%d. Docentes utilizados: %d/%d. Cursos sin asignar: %d",
                cursosAsignados, cursos.size(), docentesUtilizados, docentes.size(), cursosSinAsignar
        );
    }



}