package com.sicad.sicad_backend.service.impl;


import com.sicad.sicad_backend.Enum.Modulo;
import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.dto.Horario.HorarioCreateRequest;
import com.sicad.sicad_backend.dto.Horario.HorarioDetalleResponse;
import com.sicad.sicad_backend.dto.Horario.HorarioUpdateRequest;
import com.sicad.sicad_backend.model.Aula;
import com.sicad.sicad_backend.model.Curso;
import com.sicad.sicad_backend.model.Horario;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import com.sicad.sicad_backend.repository.interfaces.IAulaRepo;
import com.sicad.sicad_backend.repository.interfaces.IHorarioRepo;
import com.sicad.sicad_backend.repository.interfaces.ICursoRepo;
import com.sicad.sicad_backend.service.base.CRUDImpl;
import com.sicad.sicad_backend.service.interfaces.IHorarioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.sicad.sicad_backend.Enum.Message.DURACION_DIFERENCIA_HORA_ERROR;
import static com.sicad.sicad_backend.Enum.Message.HORA_FIN_INICIO_ERROR;

@Slf4j
@Service
@RequiredArgsConstructor
public class HorarioServiceImpl
        extends CRUDImpl<Horario, Integer>
        implements IHorarioService {

    private final IHorarioRepo horarioRepo;
    private final ICursoRepo cursoRepo;
    private final IAulaRepo aulaRepo;
    private final ModelMapper modelMapper;

    @Override
    protected IGenericRepo<Horario, Integer> getRepo() {
        return horarioRepo;
    }

    @Override
    public BaseListReponse<HorarioDetalleResponse> listarPorCurso(Integer idCurso) {
        Optional<Curso> cursoOpt = cursoRepo.findByIdAndEnabledTrue(idCurso);
        if (cursoOpt.isEmpty()) {
            return new BaseListReponse<>(404, Modulo.CURSO.noEncontrado(), null);
        }

        List<HorarioDetalleResponse> lista = horarioRepo.findHorariosByCurso(idCurso)
                .stream()
                .map(this::convHorarioDetalle)
                .toList();

        return new BaseListReponse<>(200, Modulo.HORARIO.listado(), lista);
    }

    @Override
    public BaseObjectResponse<HorarioDetalleResponse> buscar(Integer idHorario) {
        Optional<Horario> horarioOpt = horarioRepo.findByIdAndEnabledTrue(idHorario);
        if (horarioOpt.isEmpty()) {
            return new BaseObjectResponse<>(404, Modulo.HORARIO.noEncontrado(), null);
        }

        return new BaseObjectResponse<>(200, Modulo.HORARIO.encontrado(), convHorarioDetalle(horarioOpt.get()));
    }

    @Override
    public BaseObjectResponse<HorarioDetalleResponse> registrarPorCurso(Integer idCurso, HorarioCreateRequest request) {
        Optional<Curso> cursoOpt = cursoRepo.findByIdAndEnabledTrue(idCurso);
        if (cursoOpt.isEmpty()) {
            return new BaseObjectResponse<>(404, Modulo.CURSO.noEncontrado(), null);
        }
        int horaInicio = Integer.parseInt(request.getHoraInicio().split(":")[0]);
        int horaFin = Integer.parseInt(request.getHoraFin().split(":")[0]);

        if (horaFin <= horaInicio) {
            return new BaseObjectResponse<>(400, HORA_FIN_INICIO_ERROR.toString(), null);
        }

        int duracionCalculada = horaFin - horaInicio;
        if (duracionCalculada != request.getDuracionHoras()) {
            return new BaseObjectResponse<>(400, DURACION_DIFERENCIA_HORA_ERROR.toString(), null);
        }

        Time horaInicioTime = Time.valueOf(request.getHoraInicio());
        Time horaFinTime = Time.valueOf(request.getHoraFin());

        Aula aula =null;
        if(request.getIdAula() !=null){
            Optional<Aula> aulaOpt= aulaRepo.findByIdAndEnabledTrue(request.getIdAula());
            //VALIDAR el aula que esta siendo usado en una idCicloAcademico y rango de fecha (Todavia no se implementa
            if(aulaOpt.isEmpty()){
                return new BaseObjectResponse<>(404, Modulo.AULA.noEncontrado(), null);
            } else {
                aula =aulaOpt.get();
            }

        }

        Horario horario = Horario.builder()
                .curso(cursoOpt.get())
                .tipoSesion(request.getTipoSesion())
                .diaSemana(request.getDiaSemana())
                .horaInicio(horaInicioTime)
                .horaFin(horaFinTime)
                .duracionHoras(request.getDuracionHoras())
                .aula(aula)
                .enabled(true)
                .build();

        horarioRepo.save(horario);
        return new BaseObjectResponse<>(200, Modulo.HORARIO.registrado(), convHorarioDetalle(horario));

    }

    @Override
    public BaseListReponse<HorarioDetalleResponse> registrarAllPorCurso(Integer idCurso, List<HorarioCreateRequest> requests) {
        Optional<Curso> cursoOpt = cursoRepo.findByIdAndEnabledTrue(idCurso);
        if (cursoOpt.isEmpty()) {
            return new BaseListReponse<>(404, Modulo.CURSO.noEncontrado(), null);
        }

        List<HorarioDetalleResponse> registrados = new ArrayList<>();
        int errorCount = 0;

        for (HorarioCreateRequest request : requests) {
            try {
                BaseObjectResponse<HorarioDetalleResponse> response = registrarPorCurso(idCurso,request);
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
        return new BaseListReponse<>(201,Modulo.HORARIO.resumenAllRegistro(registrados.size(), errorCount),registrados);
    }

    @Override
    public BaseObjectResponse<HorarioDetalleResponse> actualizar(Integer idHorario, HorarioUpdateRequest request) {

        Optional<Horario> horarioOpt = horarioRepo.findByIdAndEnabledTrue(idHorario);
        if (horarioOpt.isEmpty()) {
            return new BaseObjectResponse<>(404, Modulo.HORARIO.noEncontrado(), null);
        }

        Horario horario = horarioOpt.get();


        // Actualizar Aula si viene
        if (request.getIdAula() != null) {
            Optional<Aula> aulaOpt = aulaRepo.findByIdAndEnabledTrue(request.getIdAula());
            if (aulaOpt.isEmpty()) {
                return new BaseObjectResponse<>(404, Modulo.AULA.noEncontrado(), null);
            }
            horario.setAula(aulaOpt.get());
        }

        // Actualizar hora inicio o fin si vienen
        Time horaInicioTime = horario.getHoraInicio();
        Time horaFinTime = horario.getHoraFin();

        if (request.getHoraInicio() != null) {
            horaInicioTime = Time.valueOf(request.getHoraInicio());
            horario.setHoraInicio(horaInicioTime);
        }
        if (request.getHoraFin() != null) {
            horaFinTime = Time.valueOf(request.getHoraFin());
            horario.setHoraFin(horaFinTime);
        }

        // Validar coherencia de horas si alguno de los dos cambió
        if (request.getHoraInicio() != null || request.getHoraFin() != null) {
            int horaInicio = horaInicioTime.toLocalTime().getHour();
            int horaFin = horaFinTime.toLocalTime().getHour();
            if (horaFin <= horaInicio) {
                return new BaseObjectResponse<>(400, HORA_FIN_INICIO_ERROR.toString(), null);
            }
        }

        // Actualizar duración si viene
        if (request.getDuracionHoras() != null) {

            int duracionCalculada = (int) Duration.between(
                    horaInicioTime.toLocalTime(),
                    horaFinTime.toLocalTime()
            ).toHours();

            if (duracionCalculada != request.getDuracionHoras()) {
                return new BaseObjectResponse<>(400, DURACION_DIFERENCIA_HORA_ERROR.toString(), null);
            }

            horario.setDuracionHoras(request.getDuracionHoras());
        }


        // Actualizar tipo de sesión
        if (request.getTipoSesion() != null) {
            horario.setTipoSesion(request.getTipoSesion());
        }

        // Actualizar día de semana
        if (request.getDiaSemana() != null) {
            horario.setDiaSemana(request.getDiaSemana());
        }

        horarioRepo.save(horario);
        return new BaseObjectResponse<>(200, Modulo.HORARIO.actualizado(), convHorarioDetalle(horario));
    }


    @Override
    public BaseObjectResponse<String> eliminar(Integer idHorario) {
        Optional<Horario> horarioOpt = horarioRepo.findByIdAndEnabledTrue(idHorario);
        if (horarioOpt.isEmpty()) {
            return new BaseObjectResponse<>(404, Modulo.HORARIO.noEncontrado(), null);
        }
        Horario horario = horarioOpt.get();
        horario.setEnabled(false);
        horarioRepo.save(horario);
        return new BaseObjectResponse<>(200, Modulo.HORARIO.eliminado(), null);

    }
    private HorarioDetalleResponse convHorarioDetalle(Horario obj) {
        return modelMapper.map(obj, HorarioDetalleResponse.class);
    }
}
