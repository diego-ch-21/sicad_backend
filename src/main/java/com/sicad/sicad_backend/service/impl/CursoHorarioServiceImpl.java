package com.sicad.sicad_backend.service.impl;


import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.dto.cursoHorario.HorarioCreateRequest;
import com.sicad.sicad_backend.dto.cursoHorario.HorarioDetalleResponse;
import com.sicad.sicad_backend.dto.cursoHorario.HorarioUpdateRequest;
import com.sicad.sicad_backend.model.Aula;
import com.sicad.sicad_backend.model.Curso;
import com.sicad.sicad_backend.model.CursoHorario;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import com.sicad.sicad_backend.repository.interfaces.IAulaRepo;
import com.sicad.sicad_backend.repository.interfaces.ICursoHorarioRepo;
import com.sicad.sicad_backend.repository.interfaces.ICursoRepo;
import com.sicad.sicad_backend.service.base.CRUDImpl;
import com.sicad.sicad_backend.service.interfaces.ICursoHorarioService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CursoHorarioServiceImpl
        extends CRUDImpl<CursoHorario, Integer>
        implements ICursoHorarioService {

    private final ICursoHorarioRepo cursoHorarioRepo;
    private final ICursoRepo cursoRepo;
    private final IAulaRepo aulaRepo;
    private final ModelMapper modelMapper;

    @Override
    protected IGenericRepo<CursoHorario, Integer> getRepo() {
        return cursoHorarioRepo;
    }

    @Override
    public BaseListReponse<HorarioDetalleResponse> listarPorCursoHabilitado(Integer idCurso) {
        List<CursoHorario> horarios = cursoHorarioRepo.listarPorCursoHabilitado(idCurso);
        List<HorarioDetalleResponse> response = horarios
                .stream()
                .map(this::convertToHorario)
                .toList();

        return new BaseListReponse<>(200,"Lista del horario del curso",response);
    }

    public BaseObjectResponse<HorarioDetalleResponse> registrarHorario(Integer idCurso, HorarioCreateRequest request) {
        Curso curso = cursoRepo.findById(idCurso).orElse(null);
        if (curso == null) {
            return new BaseObjectResponse<>(404, "Curso no encontrado", null);
        }

        try {
            int horaInicio = Integer.parseInt(request.getHoraInicio().split(":")[0]);
            int horaFin = Integer.parseInt(request.getHoraFin().split(":")[0]);

            if (horaFin <= horaInicio) {
                return new BaseObjectResponse<>(400, "La hora de fin debe ser posterior a la hora de inicio", null);
            }

            int duracionCalculada = horaFin - horaInicio;
            if (duracionCalculada != request.getDuracionHoras()) {
                return new BaseObjectResponse<>(400,
                        "La duración no coincide con la diferencia entre hora inicio y fin", null);
            }

            Time horaInicioTime = Time.valueOf(request.getHoraInicio());
            Time horaFinTime = Time.valueOf(request.getHoraFin());

            Aula aulaObj =null;
            if(request.getIdAula() !=null){
                aulaObj = aulaRepo.getById(request.getIdAula());
                if(aulaObj == null) {
                    return new BaseObjectResponse<>(400, "Aula no encontrada", null);
                }
            }

            CursoHorario horario = CursoHorario.builder()
                    .curso(curso)
                    .tipoSesion(request.getTipoSesion())
                    .diaSemana(request.getDiaSemana())
                    .horaInicio(horaInicioTime)
                    .horaFin(horaFinTime)
                    .duracionHoras(request.getDuracionHoras())
                    .aula(aulaObj)
                    .enabled(true)
                    .build();

            cursoHorarioRepo.save(horario);

            HorarioDetalleResponse dto = modelMapper.map(horario, HorarioDetalleResponse.class);
            return new BaseObjectResponse<>(201, String.format("Horario del idCurso=(%d) registrado exitosamente", idCurso),dto);

        } catch (Exception e) {
            System.out.println("error: "+e.getMessage());
            return new BaseObjectResponse<>(400, "Error al procesar las horas o guardar el horario", null);
        }
    }

    public BaseListReponse<HorarioDetalleResponse> registrarVariosHorario(Integer idCurso, List<HorarioCreateRequest> requests) {
        Curso curso = cursoRepo.findById(idCurso).orElse(null);
        if (curso == null) {
            return new BaseListReponse<>(404, "Curso no encontrado", null);
        }
        List<HorarioDetalleResponse> horariosRegistrados = new ArrayList<>();
        int fallidos = 0;

        for (HorarioCreateRequest req : requests) {
            BaseObjectResponse<HorarioDetalleResponse> response = registrarHorario(idCurso, req);
            if (response.status() == 201 && response.data() != null) {
                horariosRegistrados.add(response.data());
            } else {
                fallidos++;
            }
        }

        String mensaje = String.format("Horarios para idCurso=(%d) registrados: %d, fallidos: %d", idCurso, horariosRegistrados.size(), fallidos);
        return new BaseListReponse<>(201, mensaje, horariosRegistrados);
    }


    public BaseObjectResponse<String> eliminarHorario(Integer idCursoHorario) {
        // Validación de parámetro
        if (idCursoHorario == null) {
            return new BaseObjectResponse<>(400, "idCursoHorario no proporcionado", null);
        }

        // Validar existencia del curso
        CursoHorario cursoHorario = cursoHorarioRepo.findById(idCursoHorario).orElse(null);
        if (cursoHorario == null) {
            return new BaseObjectResponse<>(404, "CursoHorario  no encontrado", null);
        }

        // desabilitar
        cursoHorario.setEnabled(false);
        cursoHorarioRepo.save(cursoHorario);
        return new BaseObjectResponse<>(200, "se elimino el CursoHorario exitosamente", null);
    }
    public BaseObjectResponse<HorarioDetalleResponse> actualizarHorario(Integer idCursoHorario, HorarioUpdateRequest request) {
        // Validar existencia del horario
        CursoHorario horario = cursoHorarioRepo.findById(idCursoHorario).orElse(null);
        if (horario == null) {
            return new BaseObjectResponse<>(404, "CursoHorario no encontrado", null);
        }

        try {
            // Actualizar tipo de sesión si se envía
            if (request.getTipoSesion() != null && !request.getTipoSesion().isBlank()) {
                horario.setTipoSesion(request.getTipoSesion());
            }

            // Actualizar día de semana
            if (request.getDiaSemana() != null && !request.getDiaSemana().isBlank()) {
                horario.setDiaSemana(request.getDiaSemana());
            }

            // Actualizar horas si se envían
            if (request.getHoraInicio() != null && request.getHoraFin() != null) {
                Time horaInicioTime = Time.valueOf(request.getHoraInicio());
                Time horaFinTime = Time.valueOf(request.getHoraFin());

                if (horaFinTime.before(horaInicioTime)) {
                    return new BaseObjectResponse<>(400, "La hora de fin debe ser posterior a la de inicio", null);
                }

                int duracionCalculada = (horaFinTime.getHours() - horaInicioTime.getHours());
                if (request.getDuracionHoras() != null && !request.getDuracionHoras().equals(duracionCalculada)) {
                    return new BaseObjectResponse<>(400, "La duración no coincide con la diferencia entre hora inicio y fin", null);
                }

                horario.setHoraInicio(horaInicioTime);
                horario.setHoraFin(horaFinTime);

                // Si no envían duración, se calcula automáticamente
                if (request.getDuracionHoras() == null) {
                    horario.setDuracionHoras(duracionCalculada);
                } else {
                    horario.setDuracionHoras(request.getDuracionHoras());
                }
            } else if (request.getDuracionHoras() != null) {
                // Si solo envían duración, actualizarla directamente
                horario.setDuracionHoras(request.getDuracionHoras());
            }

            if(request.getIdAula() !=null){
                Aula aulaObj = aulaRepo.getById(request.getIdAula());
                if(aulaObj == null) {
                    return new BaseObjectResponse<>(400, "Aula no encontrada", null);
                } else {
                    horario.setAula(aulaObj);
                }
            }

            // Guardar cambios
            cursoHorarioRepo.save(horario);

            HorarioDetalleResponse dto = modelMapper.map(horario, HorarioDetalleResponse.class);
            return new BaseObjectResponse<>(200, "Horario actualizado exitosamente", dto);

        } catch (Exception e) {
            return new BaseObjectResponse<>(400, "Error al procesar la actualización del horario", null);
        }
    }

    private HorarioDetalleResponse convertToHorario(CursoHorario obj) {
        return modelMapper.map(obj, HorarioDetalleResponse.class);
    }



}
