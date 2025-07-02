package com.sicad.sicad_backend.service.impl;

import com.sicad.sicad_backend.dto.base.GenericObjectResponse;
import com.sicad.sicad_backend.dto.curso.CursoCreateRequest;
import com.sicad.sicad_backend.dto.curso.CursoDetalleResponse;
import com.sicad.sicad_backend.dto.curso.CursoPeriodoAcademicoResponse;
import com.sicad.sicad_backend.dto.curso.CursoUpdateRequest;
import com.sicad.sicad_backend.model.*;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import com.sicad.sicad_backend.repository.interfaces.*;
import com.sicad.sicad_backend.service.base.CRUDImpl;
import com.sicad.sicad_backend.service.interfaces.ICursoService;
import com.sicad.sicad_backend.utils.CodigoGeneratorUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CursoServiceImpl
        extends CRUDImpl<Curso, Integer>
        implements ICursoService {

    private final ICursoRepo cursoRepo;
    private final IAsignaturaRepo asignaturaRepo;
    private final IPlanDeEstudioRepo planDeEstudioRepo;
    private final IEscuelaRepo escuelaRepo;
    private final ICicloAcademicoRepo cicloAcademicoRepo;
    private final ModelMapper modelMapper;

    @Override
    protected IGenericRepo<Curso, Integer> getRepo() {
        return cursoRepo;
    }

    public GenericObjectResponse<CursoDetalleResponse> registrarCurso(CursoCreateRequest request) {
        // Validar relaciones
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

        String codigoCurso;
        do {
            codigoCurso = CodigoGeneratorUtil.generarCodigoNumerico(6);
        } while (cursoRepo.existsByCodigo(codigoCurso));

        try {
            Time horaInicio = Time.valueOf(request.getHoraInicio() + ":00");
            Time horaFin = Time.valueOf(request.getHoraFin() + ":00");

            long diferenciaMillis = horaFin.getTime() - horaInicio.getTime();
            if (diferenciaMillis <= 0) {
                return new GenericObjectResponse<>(400, "La hora de fin debe ser posterior a la hora de inicio", null);
            }

            int duracionCalculadaMinutos = (int) (diferenciaMillis / (1000 * 60));
            if (duracionCalculadaMinutos != (request.getDuracionHoras())*60) {
                return new GenericObjectResponse<>(400, "La duración indicada no coincide con la diferencia entre hora de inicio y fin en minutos", null);
            }

            Curso curso = Curso.builder()
                    .asignatura(asignatura)
                    .codigo(codigoCurso)
                    .planDeEstudio(plan)
                    .escuela(escuela)
                    .cicloAcademico(ciclo)
                    .grupo(request.getGrupo())
                    .tipoSesion(request.getTipoSesion())
                    .diaSemana(request.getDiaSemana())
                    .horaInicio(horaInicio)
                    .horaFin(horaFin)
                    .aula(request.getAula())
                    .duracionHoras(request.getDuracionHoras())
                    .enabled(true)
                    .build();

            cursoRepo.save(curso);

            CursoDetalleResponse dto = modelMapper.map(curso, CursoDetalleResponse.class);
            return new GenericObjectResponse<>(201, "Curso registrado exitosamente", dto);

        } catch (IllegalArgumentException e) {
            return new GenericObjectResponse<>(400, "Formato de hora inválido (debe ser HH:mm)", null);
        }
    }

    public GenericObjectResponse<CursoDetalleResponse> actualizarCurso(Integer id, CursoUpdateRequest request) {
        Curso curso = cursoRepo.findById(id).orElse(null);
        if (curso == null) {
            return new GenericObjectResponse<>(404, "Curso no encontrado", null);
        }

        if (request.getIdAsignatura() != null) {
            asignaturaRepo.findById(request.getIdAsignatura()).ifPresent(curso::setAsignatura);
        }

        if (request.getIdPlanDeEstudio() != null) {
            planDeEstudioRepo.findById(request.getIdPlanDeEstudio()).ifPresent(curso::setPlanDeEstudio);
        }

        if (request.getIdEscuela() != null) {
            escuelaRepo.findById(request.getIdEscuela()).ifPresent(curso::setEscuela);
        }

        if (request.getIdCicloAcademico() != null) {
            cicloAcademicoRepo.findById(request.getIdCicloAcademico()).ifPresent(curso::setCicloAcademico);
        }

        if (request.getGrupo() != null) curso.setGrupo(request.getGrupo());
        if (request.getTipoSesion() != null) curso.setTipoSesion(request.getTipoSesion());
        if (request.getAula() != null) curso.setAula(request.getAula());

        try {
            Time nuevaHoraInicio = (request.getHoraInicio() != null)
                    ? Time.valueOf(request.getHoraInicio() + ":00")
                    : curso.getHoraInicio();

            Time nuevaHoraFin = (request.getHoraFin() != null)
                    ? Time.valueOf(request.getHoraFin() + ":00")
                    : curso.getHoraFin();

            // Si actualizan horaInicio o horaFin, validar lógica
            if (request.getHoraInicio() != null || request.getHoraFin() != null) {
                if (nuevaHoraFin.getTime() <= nuevaHoraInicio.getTime()) {
                    return new GenericObjectResponse<>(400, "La hora de fin debe ser posterior a la hora de inicio", null);
                }

                // Validar duración en minutos si viene en el request
                if (request.getDuracionHoras() != null) {
                    long diferenciaMillis = nuevaHoraFin.getTime() - nuevaHoraInicio.getTime();
                    int duracionCalculadaMinutos = (int) (diferenciaMillis / (1000 * 60));
                    System.out.println("Duración calculada en minutos: " + duracionCalculadaMinutos);
                    if (duracionCalculadaMinutos != (request.getDuracionHoras()*60)) {
                        return new GenericObjectResponse<>(400,
                                "La duración indicada no coincide con la diferencia entre hora de inicio y fin en minutos", null);
                    }
                }

                curso.setHoraInicio(nuevaHoraInicio);
                curso.setHoraFin(nuevaHoraFin);
            }

        } catch (IllegalArgumentException e) {
            return new GenericObjectResponse<>(400, "Formato de hora inválido (debe ser HH:mm)", null);
        }

        // Actualizar duración sólo si viene (en minutos)
        if (request.getDuracionHoras() != null) {
            curso.setDuracionHoras(request.getDuracionHoras());
        }

        cursoRepo.save(curso);

        CursoDetalleResponse dto = modelMapper.map(curso, CursoDetalleResponse.class);
        return new GenericObjectResponse<>(200, "Curso actualizado exitosamente", dto);
    }
    public GenericObjectResponse<List<CursoPeriodoAcademicoResponse>> listarCursoPeriodoAcademico(Integer idCicloAcademico) {
        List<Curso> cursos = cursoRepo.buscarPorPeriodoAcademico(idCicloAcademico);

        List<CursoPeriodoAcademicoResponse> listaDTO = cursos.stream()
                .map(curso -> modelMapper.map(curso, CursoPeriodoAcademicoResponse.class))
                .toList();

        return new GenericObjectResponse<>(200, "Cursos obtenidos correctamente", listaDTO);
    }


}
