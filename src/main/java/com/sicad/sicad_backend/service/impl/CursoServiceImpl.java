package com.sicad.sicad_backend.service.impl;

import com.sicad.sicad_backend.Enum.Modulo;
import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.dto.curso.*;
import com.sicad.sicad_backend.dto.cursoHorario.*;
import com.sicad.sicad_backend.dto.escuela.EscuelaCreateRequest;
import com.sicad.sicad_backend.dto.escuela.EscuelaDetalleResponse;
import com.sicad.sicad_backend.model.*;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import com.sicad.sicad_backend.repository.interfaces.*;
import com.sicad.sicad_backend.service.base.CRUDImpl;
import com.sicad.sicad_backend.service.interfaces.ICursoService;
import com.sicad.sicad_backend.service.interfaces.IHorarioService;
import com.sicad.sicad_backend.utils.CodigoGeneratorUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
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

    private final IHorarioService horarioService;

    @Override
    protected IGenericRepo<Curso, Integer> getRepo() {
        return cursoRepo;
    }



    @Override
    public BaseListReponse<CursoDetalleResponse> listarPorCicloAcademico(Integer idCicloAcademico) {
        Optional<CicloAcademico> cicloAcademicoOpt = cicloAcademicoRepo.findByIdAndEnabledTrue(idCicloAcademico);
        if(cicloAcademicoOpt.isEmpty()){
            return new BaseListReponse<>(404, Modulo.CICLO_ACADEMICO.noEncontrado(), null);
        }
        List<CursoDetalleResponse> response = cursoRepo.findByEnabledTrueAndCicloAcademico(idCicloAcademico)
                .stream()
                .map(this::convCursoDetalle)
                .toList();

        return new BaseListReponse<>(200, Modulo.CURSO.listado(), response);
    }

    @Override
    public BaseObjectResponse<CursoDetalleResponse> buscar(Integer idCurso) {
        Optional<Curso> cursoOpt = cursoRepo.findByIdAndEnabledTrue(idCurso);
        if (cursoOpt.isEmpty()) {
            return new BaseObjectResponse<>(404, Modulo.CURSO.noEncontrado(), null);
        }
        return new BaseObjectResponse<>(200, Modulo.CURSO.encontrado(), convCursoDetalle(cursoOpt.get()));

    }

    @Override
    public BaseObjectResponse<CursoDetalleResponse> registrar(CursoCreateRequest request) {

        Optional<Asignatura> asignaturaOpt = asignaturaRepo.findByIdAndEnabledTrue(request.getIdAsignatura());
        if (asignaturaOpt.isEmpty()) {
            return new BaseObjectResponse<>(404, Modulo.ASIGNATURA.noEncontrado(), null);
        }
        Optional<PlanDeEstudio> planOpt = planDeEstudioRepo.findByIdAndEnabledTrue(request.getIdPlanDeEstudio());
        if (planOpt.isEmpty()) {
            return new BaseObjectResponse<>(404, Modulo.PLAN_DE_ESTUDIO.noEncontrado(), null);
        }

        Optional<Escuela> escuelaOpt = escuelaRepo.findByIdAndEnabledTrue(request.getIdEscuela());
        if (escuelaOpt.isEmpty()) {
            return new BaseObjectResponse<>(404, Modulo.ESCUELA.noEncontrado(), null);
        }

        Optional<CicloAcademico> cicloAcademicoOpt = cicloAcademicoRepo.findByIdAndEnabledTrue(request.getIdCicloAcademico());
        if (cicloAcademicoOpt.isEmpty()) {
            return new BaseObjectResponse<>(404, Modulo.CICLO_ACADEMICO.noEncontrado(), null);
        }

        String codigoCurso;
        do {
            codigoCurso = CodigoGeneratorUtil.generarCodigoNumerico(6);
        } while (cursoRepo.existsByCodigo(codigoCurso));

        Curso curso = Curso.builder()
                .asignatura(asignaturaOpt.get())
                .codigo(codigoCurso)
                .planDeEstudio(planOpt.get())
                .escuela(escuelaOpt.get())
                .cicloAcademico(cicloAcademicoOpt.get())
                .grupo(request.getGrupo())
                .enabled(true)
                .build();

        cursoRepo.save(curso);


        if (request.getHorario() == null || request.getHorario().isEmpty()) {
            return new BaseObjectResponse<>(201, Modulo.CURSO.registrado(), convCursoDetalle(curso));
        }

        BaseListReponse<HorarioDetalleResponse> response = horarioService.registrarAllPorCurso(curso.getIdCurso(),request.getHorario());
        Optional<Curso> cursoOpt = cursoRepo.findByIdAndEnabledTrue(curso.getIdCurso());
        Curso cursoConHorario = cursoOpt.get();
        return new BaseObjectResponse<>(201, Modulo.CURSO.registrado()+" - "+response.message(), convCursoDetalle(cursoConHorario));
    }

    @Override
    public BaseObjectResponse<CursoDetalleResponse> actualizar(Integer idCurso, CursoUpdateRequest request) {
        Optional<Curso> cursoOpt = cursoRepo.findByIdAndEnabledTrue(idCurso);
        if (cursoOpt.isEmpty()) {
            return new BaseObjectResponse<>(404, Modulo.CURSO.noEncontrado(), null);
        }
        Curso curso = cursoOpt.get();
        // Validaciones de entidades relacionadas si se envían en la request
        if (request.getIdAsignatura() != null) {
            Optional<Asignatura> asignaturaOpt = asignaturaRepo.findByIdAndEnabledTrue(request.getIdAsignatura());
            if (asignaturaOpt.isEmpty()) {
                return new BaseObjectResponse<>(404, Modulo.ASIGNATURA.noEncontrado(), null);
            }
            curso.setAsignatura(asignaturaOpt.get());
        }

        if (request.getIdPlanDeEstudio() != null) {
            Optional<PlanDeEstudio> planOpt = planDeEstudioRepo.findByIdAndEnabledTrue(request.getIdPlanDeEstudio());
            if (planOpt.isEmpty()) {
                return new BaseObjectResponse<>(404, Modulo.PLAN_DE_ESTUDIO.noEncontrado(), null);
            }
            curso.setPlanDeEstudio(planOpt.get());
        }

        if (request.getIdEscuela() != null) {
            Optional<Escuela> escuelaOpt = escuelaRepo.findByIdAndEnabledTrue(request.getIdEscuela());
            if (escuelaOpt.isEmpty()) {
                return new BaseObjectResponse<>(404, Modulo.ESCUELA.noEncontrado(), null);
            }
            curso.setEscuela(escuelaOpt.get());
        }

        if (request.getIdCicloAcademico() != null) {
            Optional<CicloAcademico> cicloAcademicoOpt = cicloAcademicoRepo.findByIdAndEnabledTrue(request.getIdCicloAcademico());
            if (cicloAcademicoOpt.isEmpty()) {
                return new BaseObjectResponse<>(404, Modulo.CICLO_ACADEMICO.noEncontrado(), null);
            }
            curso.setCicloAcademico(cicloAcademicoOpt.get());
        }
        if (request.getGrupo() != null) {
            //VALIDACIONES
            curso.setGrupo(request.getGrupo());
        }

        cursoRepo.save(curso);

        return new BaseObjectResponse<>(200, Modulo.CURSO.actualizado(),convCursoDetalle(curso));
    }

    @Override
    public BaseListReponse<CursoDetalleResponse> registrarAll(List<CursoCreateRequest> requests) {
        List<CursoDetalleResponse> registrados = new ArrayList<>();
        int errorCount = 0;

        for (CursoCreateRequest request : requests) {
            try {
                BaseObjectResponse<CursoDetalleResponse> response = registrar(request);
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
        return new BaseListReponse<>(201,Modulo.CURSO.resumenAllRegistro(registrados.size(), errorCount),registrados);
    }

    @Override
    public BaseObjectResponse<String> eliminar(Integer idCurso) {
        Optional<Curso> cursoOpt = cursoRepo.findByIdAndEnabledTrue(idCurso);
        if (cursoOpt.isEmpty()) {
            return new BaseObjectResponse<>(404, Modulo.CURSO.noEncontrado(), null);
        }
        Curso curso = cursoOpt.get();
        curso.setEnabled(false);
        cursoRepo.save(curso);
        return new BaseObjectResponse<>(200, Modulo.CURSO.eliminado(), null);
    }

    private CursoDetalleResponse convCursoDetalle(Curso obj) {
        return modelMapper.map(obj, CursoDetalleResponse.class);
    }
}
