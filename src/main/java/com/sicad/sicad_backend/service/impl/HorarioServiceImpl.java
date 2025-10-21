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

// import java.sql.Time; // <-- CAMBIO: Eliminado
import java.time.Duration;
import java.time.LocalTime; // <-- CAMBIO: Ya estaba, pero ahora es el único que se usa
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

        // --- CAMBIO AQUÍ ---
        // Se usa LocalTime.parse() en lugar de Time.valueOf()
        LocalTime horaInicioTime = LocalTime.parse(request.getHoraInicio());
        LocalTime horaFinTime = LocalTime.parse(request.getHoraFin());
        // --- FIN DEL CAMBIO ---

        Aula aula = null;
        if (request.getIdAula() != null) {
            Optional<Aula> aulaOpt = aulaRepo.findByIdAndEnabledTrue(request.getIdAula());
            if (aulaOpt.isEmpty()) {
                return new BaseObjectResponse<>(404, Modulo.AULA.noEncontrado(), null);
            } else {
                aula = aulaOpt.get();
            }
        }

        Horario horario = Horario.builder()
                .curso(cursoOpt.get())
                .tipoSesion(request.getTipoSesion())
                .diaSemana(request.getDiaSemana())
                .horaInicio(horaInicioTime) // <-- Ya es LocalTime
                .horaFin(horaFinTime)       // <-- Ya es LocalTime
                .duracionHoras(request.getDuracionHoras())
                .aula(aula)
                .enabled(true)
                .build();

        horarioRepo.save(horario);

        return new BaseObjectResponse<>(201, Modulo.HORARIO.registrado(), convHorarioDetalle(horario));
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
                    System.out.println("true");
                    registrados.add(response.data());
                } else {
                    System.out.println("false");
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

        if (request.getIdAula() != null) {
            Optional<Aula> aulaOpt = aulaRepo.findByIdAndEnabledTrue(request.getIdAula());
            if (aulaOpt.isEmpty()) {
                return new BaseObjectResponse<>(404, Modulo.AULA.noEncontrado(), null);
            }
            if (horario.getAula() == null || !horario.getAula().getIdAula().equals(request.getIdAula())) {
                horario.setAula(aulaOpt.get());
            }
        }

        // --- CAMBIO AQUÍ ---
        // Se usa LocalTime y LocalTime.parse()
        LocalTime horaInicioTime = (request.getHoraInicio() != null)
                ? LocalTime.parse(request.getHoraInicio())
                : horario.getHoraInicio(); // <-- horario.getHoraInicio() ya devuelve LocalTime

        LocalTime horaFinTime = (request.getHoraFin() != null)
                ? LocalTime.parse(request.getHoraFin())
                : horario.getHoraFin(); // <-- horario.getHoraFin() ya devuelve LocalTime
        // --- FIN DEL CAMBIO ---


        Integer duracionHoras = (request.getDuracionHoras() != null)
                ? request.getDuracionHoras()
                : horario.getDuracionHoras();

        // --- CAMBIO AQUÍ ---
        // Se eliminan las variables 'inicioLT' y 'finLT' porque horaInicioTime y horaFinTime ya son LocalTime
        // LocalTime inicioLT = horaInicioTime.toLocalTime(); // <-- Eliminado
        // LocalTime finLT = horaFinTime.toLocalTime(); // <-- Eliminado

        // Se usan las variables 'horaInicioTime' y 'horaFinTime' directamente
        if (horaFinTime.isBefore(horaInicioTime) || horaFinTime.equals(horaInicioTime)) {
            return new BaseObjectResponse<>(400, HORA_FIN_INICIO_ERROR.toString(), null);
        }

        int duracionCalculada = (int) Duration.between(horaInicioTime, horaFinTime).toHours();
        // --- FIN DEL CAMBIO ---

        if (duracionCalculada != duracionHoras) {
            return new BaseObjectResponse<>(400, DURACION_DIFERENCIA_HORA_ERROR.toString(), null);
        }

        horario.setHoraInicio(horaInicioTime);
        horario.setHoraFin(horaFinTime);
        horario.setDuracionHoras(duracionHoras);

        if (request.getTipoSesion() != null) {
            horario.setTipoSesion(request.getTipoSesion());
        }

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