package com.sicad.sicad_backend.service.impl;

import com.sicad.sicad_backend.dto.base.GenericObjectResponse;
import com.sicad.sicad_backend.dto.base.GenericReponse;
import com.sicad.sicad_backend.dto.curso.*;
import com.sicad.sicad_backend.dto.cursoHorario.CursoHorarioCreateRequest;
import com.sicad.sicad_backend.dto.cursoHorario.CursoHorarioDetalleResponse;
import com.sicad.sicad_backend.dto.cursoHorario.HorarioCreateRequest;
import com.sicad.sicad_backend.dto.cursoHorario.HorarioDetalleResponse;
import com.sicad.sicad_backend.model.*;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import com.sicad.sicad_backend.repository.interfaces.*;
import com.sicad.sicad_backend.service.base.CRUDImpl;
import com.sicad.sicad_backend.service.interfaces.ICursoService;
import com.sicad.sicad_backend.utils.CodigoGeneratorUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CursoServiceImpl
        extends CRUDImpl<Curso, Integer>
        implements ICursoService {

    private final ICursoRepo cursoRepo;
    private final ICursoHorarioRepo cursoHorarioRepo;
    private final IAsignaturaRepo asignaturaRepo;
    private final IPlanDeEstudioRepo planDeEstudioRepo;
    private final IEscuelaRepo escuelaRepo;
    private final ICicloAcademicoRepo cicloAcademicoRepo;
    private final ModelMapper modelMapper;

    @Override
    protected IGenericRepo<Curso, Integer> getRepo() {
        return cursoRepo;
    }

    public GenericReponse<CursoDetalleResponse> listarCursosConHorarios() {
        List<Curso> cursos = cursoRepo.findAllWithHorarios();
        if(cursos.isEmpty()) {
            return new GenericReponse<>(200, "No se encontraron cursos", null);
        }

        List<CursoDetalleResponse> responseList = cursos.stream()
                .map(curso -> modelMapper.map(curso, CursoDetalleResponse.class))
                .toList();

        return new GenericReponse<>(200, "Lista de Cursos", responseList);
    }
    public GenericReponse<CursoDetalleResponse> listarCursosPorCicloAcademico(Integer idCicloAcademico) {
        CicloAcademico cicloAcademico = cicloAcademicoRepo.findById(idCicloAcademico).orElse(null);
        if (cicloAcademico == null) {
            return new GenericReponse<>(404, "Ciclo académico no encontrado", null);
        }

        List<Curso> cursos = cursoRepo.buscarPorPeriodoAcademico(idCicloAcademico);

        List<CursoDetalleResponse> responseList = cursos.stream()
                .map(curso -> modelMapper.map(curso, CursoDetalleResponse.class))
                .toList();

        return new GenericReponse<>(200, "Lista de Cursos por ciclo academico", responseList);
    }

    public GenericObjectResponse<CursoDetalleResponse> registrarCurso(CursoCreateRequest request) {
        // Validaciones de entidades relacionadas (sin cambios)
        Asignatura asignatura = asignaturaRepo.findById(request.getIdAsignatura()).orElse(null);
        if (asignatura == null)
            return new GenericObjectResponse<>(404, "Asignatura no encontrada", null);

        PlanDeEstudio plan = planDeEstudioRepo.findById(request.getIdPlanDeEstudio()).orElse(null);
        if (plan == null)
            return new GenericObjectResponse<>(404, "Plan de estudio no encontrado", null);

        Escuela escuela = escuelaRepo.findById(request.getIdEscuela()).orElse(null);
        if (escuela == null)
            return new GenericObjectResponse<>(404, "Escuela no encontrada", null);

        CicloAcademico ciclo = cicloAcademicoRepo.findById(request.getIdCicloAcademico()).orElse(null);
        if (ciclo == null)
            return new GenericObjectResponse<>(404, "Ciclo académico no encontrado", null);

        // Generar código único
        String codigoCurso;
        do {
            codigoCurso = CodigoGeneratorUtil.generarCodigoNumerico(6);
        } while (cursoRepo.existsByCodigo(codigoCurso));

        Curso curso = Curso.builder()
                .asignatura(asignatura)
                .codigo(codigoCurso)
                .planDeEstudio(plan)
                .escuela(escuela)
                .cicloAcademico(ciclo)
                .grupo(request.getGrupo())
                .enabled(true)
                .build();

        cursoRepo.save(curso);

        // Si no se proporcionan horarios
        if (request.getCursoHorario() == null || request.getCursoHorario().isEmpty()) {
            CursoDetalleResponse dto = modelMapper.map(curso, CursoDetalleResponse.class);
            return new GenericObjectResponse<>(201, "Curso registrado sin horarios", dto);
        }

        int registrados = 0;
        int fallidos = 0;

        CursoDetalleResponse cursoResponse = modelMapper.map(curso, CursoDetalleResponse.class);
        if (cursoResponse.getCursoHorario() == null) {
            cursoResponse.setCursoHorario(new ArrayList<>());
        }

        for (CursoHorarioCreateRequest horarioReq : request.getCursoHorario()) {
            try {
                int horaInicio = Integer.parseInt(horarioReq.getHoraInicio().split(":")[0]);
                int horaFin = Integer.parseInt(horarioReq.getHoraFin().split(":")[0]);

                if (horaFin <= horaInicio || (horaFin - horaInicio) != horarioReq.getDuracionHoras()) {
                    fallidos++;
                    continue;
                }

                Time horaInicioTime = Time.valueOf(String.format("%02d:00:00", horaInicio));
                Time horaFinTime = Time.valueOf(String.format("%02d:00:00", horaFin));

                CursoHorario horario = CursoHorario.builder()
                        .tipoSesion(horarioReq.getTipoSesion())
                        .diaSemana(horarioReq.getDiaSemana())
                        .horaInicio(horaInicioTime)
                        .horaFin(horaFinTime)
                        .duracionHoras(horarioReq.getDuracionHoras())
                        .curso(curso)
                        .enabled(true)
                        .build();

                cursoHorarioRepo.save(horario);

                CursoHorarioDetalleResponse horarioDto = modelMapper.map(horario, CursoHorarioDetalleResponse.class);
                cursoResponse.getCursoHorario().add(horarioDto);
                registrados++;
            } catch (Exception e) {
                fallidos++;
            }
        }

        String mensaje = String.format("Curso registrado. Horarios registrados: %d, fallidos: %d", registrados, fallidos);
        return new GenericObjectResponse<>(201, mensaje, cursoResponse);
    }


    public GenericReponse<CursoDetalleResponse> registrarCursosMultiples(List<CursoCreateRequest> requests) {
        List<CursoDetalleResponse> registrados = new ArrayList<>();
        int errorCount = 0;

        for (CursoCreateRequest request : requests) {
            GenericObjectResponse<CursoDetalleResponse> response = registrarCurso(request);
            if (response.status() == 201 && response.data() != null) {
                registrados.add(response.data());
            } else {
                errorCount++;
            }
        }

        String mensaje = String.format("Cursos registrados: %d. Fallidos: %d.", registrados.size(), errorCount);
        return new GenericReponse<>(201, mensaje,registrados);
    }

    public GenericObjectResponse<HorarioDetalleResponse> registrarCursoHorario(Integer idCurso, HorarioCreateRequest request) {
        Curso curso = cursoRepo.findById(idCurso).orElse(null);
        if (curso == null) {
            return new GenericObjectResponse<>(404, "Curso no encontrado", null);
        }

        try {
            int horaInicio = Integer.parseInt(request.getHoraInicio().split(":")[0]);
            int horaFin = Integer.parseInt(request.getHoraFin().split(":")[0]);

            if (horaFin <= horaInicio) {
                return new GenericObjectResponse<>(400, "La hora de fin debe ser posterior a la hora de inicio", null);
            }

            int duracionCalculada = horaFin - horaInicio;
            if (duracionCalculada != request.getDuracionHoras()) {
                return new GenericObjectResponse<>(400,
                        "La duración no coincide con la diferencia entre hora inicio y fin", null);
            }

            Time horaInicioTime = Time.valueOf(request.getHoraInicio());
            Time horaFinTime = Time.valueOf(request.getHoraFin());

            CursoHorario horario = CursoHorario.builder()
                    .curso(curso)
                    .tipoSesion(request.getTipoSesion())
                    .diaSemana(request.getDiaSemana())
                    .horaInicio(horaInicioTime)
                    .horaFin(horaFinTime)
                    .duracionHoras(request.getDuracionHoras())
                    .enabled(true)
                    .build();

            cursoHorarioRepo.save(horario);

            HorarioDetalleResponse dto = modelMapper.map(horario, HorarioDetalleResponse.class);
            return new GenericObjectResponse<>(201, String.format("Horario del idCurso=(%d) registrado exitosamente", idCurso),dto);

        } catch (Exception e) {
            return new GenericObjectResponse<>(400, "Error al procesar las horas o guardar el horario", null);
        }
    }

    public GenericReponse<HorarioDetalleResponse> registrarVariosCursoHorario(Integer idCurso, List<HorarioCreateRequest> requests) {
        Curso curso = cursoRepo.findById(idCurso).orElse(null);
        if (curso == null) {
            return new GenericReponse<>(404, "Curso no encontrado", null);
        }
        List<HorarioDetalleResponse> horariosRegistrados = new ArrayList<>();
        int fallidos = 0;

        for (HorarioCreateRequest req : requests) {
            GenericObjectResponse<HorarioDetalleResponse> response = registrarCursoHorario(idCurso, req);
            if (response.status() == 201 && response.data() != null) {
                horariosRegistrados.add(response.data());
            } else {
                fallidos++;
            }
        }

        String mensaje = String.format("Horarios para idCurso=(%d) registrados: %d, fallidos: %d", idCurso, horariosRegistrados.size(), fallidos);
        return new GenericReponse<>(201, mensaje, horariosRegistrados);
    }

    @Transactional
    public GenericObjectResponse<String> eliminarCursosPorCicloAcademico(Integer idCicloAcademico) {
        // Validación de parámetro
        if (idCicloAcademico == null) {
            return new GenericObjectResponse<>(400, "ID de ciclo académico no proporcionado", null);
        }

        // Validar existencia del ciclo académico
        CicloAcademico ciclo = cicloAcademicoRepo.findById(idCicloAcademico).orElse(null);
        if (ciclo == null) {
            return new GenericObjectResponse<>(404, "Ciclo académico no encontrado", null);
        }

        // Eliminar cursos asociados
        int eliminados;
        try {
            eliminados = cursoRepo.eliminarPorCicloAcademico(idCicloAcademico);
        } catch (Exception e) {
            return new GenericObjectResponse<>(500, "Error al eliminar cursos: " + e.getMessage(), null);
        }

        // Generar respuesta
        String mensaje = eliminados > 0
                ? "Se eliminaron " + eliminados + " curso(s) del ciclo académico ID: " + idCicloAcademico
                : "No se encontraron cursos asociados al ciclo académico ID: " + idCicloAcademico;

        return new GenericObjectResponse<>(200, mensaje, null);
    }



}
