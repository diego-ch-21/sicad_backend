package com.sicad.sicad_backend.service.impl;

import com.sicad.sicad_backend.Enum.Modulo;
import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.dto.disponibilidad.DisponibilidadCreateRequest;
import com.sicad.sicad_backend.dto.disponibilidad.DisponibilidadDetalleResponse;
import com.sicad.sicad_backend.dto.disponibilidad.DisponibilidadResumenResponse;
import com.sicad.sicad_backend.dto.disponibilidad.DisponibilidadUpdateRequest;
import com.sicad.sicad_backend.dto.preferencia.PreferenciaCreateRequest;
import com.sicad.sicad_backend.dto.preferencia.PreferenciaDetalleResponse;
import com.sicad.sicad_backend.dto.preferencia.PreferenciaResumenResponse;
import com.sicad.sicad_backend.model.CicloAcademico;
import com.sicad.sicad_backend.model.Disponibilidad;
import com.sicad.sicad_backend.model.Docente;
import com.sicad.sicad_backend.model.Preferencia;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import com.sicad.sicad_backend.repository.interfaces.ICicloAcademicoRepo;
import com.sicad.sicad_backend.repository.interfaces.IDisponibilidadRepo;
import com.sicad.sicad_backend.repository.interfaces.IDocenteRepo;
import com.sicad.sicad_backend.service.base.CRUDImpl;
import com.sicad.sicad_backend.service.interfaces.IDisponibilidadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.sicad.sicad_backend.Enum.Message.CRUCE_DE_HORARIO_DISPONIBILIDAD;
import static com.sicad.sicad_backend.Enum.Message.HORA_FIN_INICIO_ERROR;

@Slf4j
@Service
@RequiredArgsConstructor
public class DisponibilidadServiceImpl
        extends CRUDImpl<Disponibilidad, Integer>
        implements IDisponibilidadService {

    private final IDisponibilidadRepo disponibilidadRepo;
    private final ICicloAcademicoRepo cicloAcademicoRepo;
    private final IDocenteRepo docenteRepo;
    private final ModelMapper modelMapper;

    @Override
    protected IGenericRepo<Disponibilidad, Integer> getRepo() {
        return disponibilidadRepo;
    }

    public BaseObjectResponse<DisponibilidadDetalleResponse> registrarDisponibilidad(DisponibilidadCreateRequest request) {
        Docente docente = docenteRepo.findById(request.getIdDocente()).orElse(null);
        if (docente == null) {
            return new BaseObjectResponse<>(404, "Docente no encontrado", null);
        }

        CicloAcademico cicloAcademico = cicloAcademicoRepo.findById(request.getIdCicloAcademico()).orElse(null);
        if (cicloAcademico == null) {
            return new BaseObjectResponse<>(404, "Ciclo academico no encontrada", null);
        }

        try {
            Time horaInicio = Time.valueOf(request.getHoraInicio() + ":00");
            Time horaFin = Time.valueOf(request.getHoraFin() + ":00");

            if (horaFin.getTime() <= horaInicio.getTime()) {
                return new BaseObjectResponse<>(400, "La hora de fin debe ser posterior a la hora de inicio", null);
            }

            Disponibilidad disponibilidad = Disponibilidad.builder()
                    .docente(docente)
                    .cicloAcademico(cicloAcademico)
                    .diaSemana(request.getDiaSemana())
                    .horaInicio(horaInicio)
                    .horaFin(horaFin)
                    .enabled(true)
                    .build();

            disponibilidadRepo.save(disponibilidad);

            DisponibilidadDetalleResponse response = modelMapper.map(disponibilidad, DisponibilidadDetalleResponse.class);
            return new BaseObjectResponse<>(201, "Disponibilidad registrada exitosamente", response);

        } catch (IllegalArgumentException e) {
            return new BaseObjectResponse<>(400, "Formato de hora inválido (debe ser HH:mm)", null);
        }
    }
    public BaseListReponse<DisponibilidadDetalleResponse> registrarVariosDisponiblidadAll(List<DisponibilidadCreateRequest> requests){
        List<DisponibilidadDetalleResponse> registrados = new ArrayList<>();
        int errorCount = 0;

        for(DisponibilidadCreateRequest request: requests){
            BaseObjectResponse<DisponibilidadDetalleResponse> response = registrarDisponibilidad(request);
            if(response.status() == 201 || response.data() != null){
                registrados.add(response.data());
            } else {
                errorCount++;
            }
        }
        String mensaje = String.format("disponibilidad registrados: %d. Fallidos: %d.", registrados.size(), errorCount);
        return new BaseListReponse<>(200, mensaje, registrados);
    }

    public BaseObjectResponse<DisponibilidadDetalleResponse> actualizarDisponibilidad(Integer id, DisponibilidadUpdateRequest request) {
        Disponibilidad disponibilidad = disponibilidadRepo.findById(id).orElse(null);
        if (disponibilidad == null) {
            return new BaseObjectResponse<>(404, "Disponibilidad no encontrada", null);
        }

        if (request.getIdDocente() != null) {
            docenteRepo.findById(request.getIdDocente()).ifPresent(disponibilidad::setDocente);
        }

        if (request.getIdCicloAcademico() != null) {
            cicloAcademicoRepo.findById(request.getIdCicloAcademico()).ifPresent(disponibilidad::setCicloAcademico);
        }

        if (request.getDiaSemana() != null) {
            disponibilidad.setDiaSemana(request.getDiaSemana());
        }

        try {
            Time horaInicio = (request.getHoraInicio() != null)
                    ? Time.valueOf(request.getHoraInicio() + ":00")
                    : disponibilidad.getHoraInicio();

            Time horaFin = (request.getHoraFin() != null)
                    ? Time.valueOf(request.getHoraFin() + ":00")
                    : disponibilidad.getHoraFin();

            if (request.getHoraInicio() != null || request.getHoraFin() != null) {
                if (horaFin.getTime() <= horaInicio.getTime()) {
                    return new BaseObjectResponse<>(400, "La hora de fin debe ser posterior a la hora de inicio", null);
                }
                disponibilidad.setHoraInicio(horaInicio);
                disponibilidad.setHoraFin(horaFin);
            }

        } catch (IllegalArgumentException e) {
            return new BaseObjectResponse<>(400, "Formato de hora inválido (debe ser HH:mm)", null);
        }

        disponibilidadRepo.save(disponibilidad);

        DisponibilidadDetalleResponse response = modelMapper.map(disponibilidad, DisponibilidadDetalleResponse.class);
        return new BaseObjectResponse<>(200, "Disponibilidad actualizada exitosamente", response);
    }
    public BaseObjectResponse<List<DisponibilidadResumenResponse>> listarDisponibilidadDocente(Integer idDocente, Integer idCicloAcademico) {

        if (!docenteRepo.existsByIdDocente(idDocente)) {
            return new BaseObjectResponse<>(400, "Docente no encontrado", null);
        }
        if (!cicloAcademicoRepo.existsByIdCicloAcademico(idCicloAcademico)) {
            return new BaseObjectResponse<>(400, "ciclo academico no encontrada", null);
        }

        List<Disponibilidad> disponibilidades = disponibilidadRepo.buscarPorDocenteYCicloAcademico(idDocente, idCicloAcademico);

        List<DisponibilidadResumenResponse> listaDTO = disponibilidades.stream()
                .map(disponibilidad -> modelMapper.map(disponibilidad, DisponibilidadResumenResponse.class))
                .collect(Collectors.toList());

        return new BaseObjectResponse<>(200, "Lista obtenida correctamente", listaDTO);
    }
    public BaseObjectResponse<String> eliminarDisponibilidad(Integer idDisponibilidad) {
        // Validación de parámetro
        if (idDisponibilidad == null) {
            return new BaseObjectResponse<>(400, "idDisponibilidad no proporcionado", null);
        }

        // Validar existencia del curso
        Disponibilidad disponibilidad = disponibilidadRepo.findById(idDisponibilidad).orElse(null);
        if (disponibilidad == null) {
            return new BaseObjectResponse<>(404, "Disponibilidad  no encontrado", null);
        }

        // desabilitar
        disponibilidad.setEnabled(false);
        disponibilidadRepo.save(disponibilidad);
        return new BaseObjectResponse<>(200, "se elimino la disponibilidad exitosamente", null);
    }




    private DisponibilidadDetalleResponse convDisponibilidadDetalle(Disponibilidad disponibilidad) {
        return modelMapper.map(disponibilidad, DisponibilidadDetalleResponse.class);
    }
    private DisponibilidadResumenResponse convDisponibilidadResumen(Disponibilidad disponibilidad) {
        return modelMapper.map(disponibilidad, DisponibilidadResumenResponse.class);
    }

    @Override
    public BaseListReponse<DisponibilidadResumenResponse> listarPorDocenteCicloAcademico(Integer idDocente, Integer idCicloAcademico) {
        List<DisponibilidadResumenResponse> response = disponibilidadRepo.findByEnabledTrueDocenteCicloAcademico(idDocente,idCicloAcademico)
                .stream()
                .map(this::convDisponibilidadResumen)
                .toList();

        return new BaseListReponse<>(200, Modulo.PREFERENCIA.listado(), response);
    }

    @Override
    public BaseObjectResponse<DisponibilidadDetalleResponse> buscar(Integer idDisponibilidad) {
        Optional<Disponibilidad> disponibilidadOpt = disponibilidadRepo.findByIdAndEnabledTrue(idDisponibilidad);
        if (disponibilidadOpt.isEmpty()) {
            return new BaseObjectResponse<>(404, Modulo.DISPONIBILIDAD.noEncontrado(), null);
        }
        return new BaseObjectResponse<>(200, Modulo.DISPONIBILIDAD.encontrado(), convDisponibilidadDetalle(disponibilidadOpt.get()) );
    }

    @Override
    public BaseObjectResponse<DisponibilidadDetalleResponse> registrar(DisponibilidadCreateRequest request) {
        Optional<Docente> docenteOpt = docenteRepo.findByIdAndEnabledTrue(request.getIdDocente());
        if (docenteOpt.isEmpty()) {
            return new BaseObjectResponse<>(404, Modulo.DOCENTE.noEncontrado(), null);
        }
        Optional<CicloAcademico> cicloOpt = cicloAcademicoRepo.findByIdAndEnabledTrue(request.getIdCicloAcademico());
        if (cicloOpt.isEmpty()) {
            return new BaseObjectResponse<>(404, Modulo.CICLO_ACADEMICO.noEncontrado(), null);
        }

        Time horaInicio = Time.valueOf(request.getHoraInicio() + ":00");
        Time horaFin = Time.valueOf(request.getHoraFin() + ":00");

        if (horaFin.getTime() <= horaInicio.getTime()) {
            return new BaseObjectResponse<>(400, HORA_FIN_INICIO_ERROR.toString(), null);
        }
        List<Disponibilidad> listaDisponibilidadDelDocente =  disponibilidadRepo.findByEnabledTrueDocenteCicloAcademico(
                docenteOpt.get().getIdDocente(),
                cicloOpt.get().getIdCicloAcademico());
        if (hayCruceHorario(listaDisponibilidadDelDocente, request.getDiaSemana(), horaInicio, horaFin)) {
            return new BaseObjectResponse<>(409, CRUCE_DE_HORARIO_DISPONIBILIDAD.toString(), null);
        }

        Disponibilidad disponibilidad = Disponibilidad.builder()
                .docente(docenteOpt.get())
                .cicloAcademico(cicloOpt.get())
                .diaSemana(request.getDiaSemana())
                .horaInicio(horaInicio)
                .horaFin(horaFin)
                .enabled(true)
                .build();

        disponibilidadRepo.save(disponibilidad);

        return new BaseObjectResponse<>(201, Modulo.DISPONIBILIDAD.registrado(), convDisponibilidadDetalle(disponibilidad));

    }
    private boolean hayCruceHorario(List<Disponibilidad> lista, String nuevoDia, Time nuevoInicio, Time nuevoFin) {
        return lista.stream().anyMatch(d ->
                // 👇 Solo valida cruces si es el mismo día
                d.getDiaSemana().equalsIgnoreCase(nuevoDia) && (

                        // 👇 Cruce de horario (nuevoInicio < finExistente && nuevoFin > inicioExistente)
                        nuevoInicio.before(d.getHoraFin()) && nuevoFin.after(d.getHoraInicio())
                )
        );
    }


    @Override
    public BaseListReponse<DisponibilidadDetalleResponse> registrarAll(List<DisponibilidadCreateRequest> requests) {
        List<DisponibilidadDetalleResponse> registrados = new ArrayList<>();
        int errorCount = 0;

        for (DisponibilidadCreateRequest request : requests) {
            try {
                BaseObjectResponse<DisponibilidadDetalleResponse> response = registrar(request);
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
        return new BaseListReponse<>(201,Modulo.DISPONIBILIDAD.resumenAllRegistro(registrados.size(), errorCount),registrados);
    }


    @Override
    public BaseObjectResponse<DisponibilidadDetalleResponse> actualizar(Integer idDisponibilidad, DisponibilidadUpdateRequest request) {
        Optional<Disponibilidad> disponibilidadOpt = disponibilidadRepo.findByIdAndEnabledTrue(idDisponibilidad);
        if (disponibilidadOpt.isEmpty()) {
            return new BaseObjectResponse<>(404, Modulo.DISPONIBILIDAD.noEncontrado(), null);
        }

        Disponibilidad disponibilidad = disponibilidadOpt.get();

        // === Validar cambios de Docente y Ciclo (opcionales) ===
        if (request.getIdDocente() != null) {
            Optional<Docente> docenteOpt = docenteRepo.findByIdAndEnabledTrue(request.getIdDocente());
            if (docenteOpt.isEmpty()) {
                return new BaseObjectResponse<>(404, Modulo.DOCENTE.noEncontrado(), null);
            }
            disponibilidad.setDocente(docenteOpt.get());
        }

        if (request.getIdCicloAcademico() != null) {
            Optional<CicloAcademico> cicloOpt = cicloAcademicoRepo.findByIdAndEnabledTrue(request.getIdCicloAcademico());
            if (cicloOpt.isEmpty()) {
                return new BaseObjectResponse<>(404, Modulo.CICLO_ACADEMICO.noEncontrado(), null);
            }
            disponibilidad.setCicloAcademico(cicloOpt.get());
        }

        // === Validar grupo de horario: o vienen los 3 o ninguno ===
        boolean envioDia = request.getDiaSemana() != null;
        boolean envioInicio = request.getHoraInicio() != null;
        boolean envioFin = request.getHoraFin() != null;

        int cantidadCamposHorario = (envioDia ? 1 : 0) + (envioInicio ? 1 : 0) + (envioFin ? 1 : 0);

        if (cantidadCamposHorario > 0 && cantidadCamposHorario < 3) {
            return new BaseObjectResponse<>(400, "Si actualiza horario, debe enviar diaSemana, horaInicio y horaFin juntos", null);
        }

        if (cantidadCamposHorario == 3) {
            Time nuevaHoraInicio = Time.valueOf(request.getHoraInicio() + ":00");
            Time nuevaHoraFin = Time.valueOf(request.getHoraFin() + ":00");

            if (nuevaHoraFin.getTime() <= nuevaHoraInicio.getTime()) {
                return new BaseObjectResponse<>(400, HORA_FIN_INICIO_ERROR.toString(), null);
            }

            List<Disponibilidad> listaDisponibilidadDelDocente = disponibilidadRepo.findByEnabledTrueDocenteCicloAcademico(
                    disponibilidad.getDocente().getIdDocente(),
                    disponibilidad.getCicloAcademico().getIdCicloAcademico());

            listaDisponibilidadDelDocente.removeIf(d -> d.getIdDisponibilidad().equals(idDisponibilidad)); // Ignorar la misma

            if (hayCruceHorario(listaDisponibilidadDelDocente, request.getDiaSemana(), nuevaHoraInicio, nuevaHoraFin)) {
                return new BaseObjectResponse<>(409, CRUCE_DE_HORARIO_DISPONIBILIDAD.toString(), null);
            }

            disponibilidad.setDiaSemana(request.getDiaSemana());
            disponibilidad.setHoraInicio(nuevaHoraInicio);
            disponibilidad.setHoraFin(nuevaHoraFin);
        }

        // === Guardar ===
        disponibilidadRepo.save(disponibilidad);

        return new BaseObjectResponse<>(200, Modulo.DISPONIBILIDAD.actualizado(), convDisponibilidadDetalle(disponibilidad));
    }


    @Override
    public BaseObjectResponse<String> eliminar(Integer idDisponibilidad) {
        Optional<Disponibilidad> disponibilidadOpt = disponibilidadRepo.findByIdAndEnabledTrue(idDisponibilidad);
        if (disponibilidadOpt.isEmpty()) {
            return new BaseObjectResponse<>(404, Modulo.DISPONIBILIDAD.noEncontrado(), null);
        }
        Disponibilidad disponibilidad = disponibilidadOpt.get();
        disponibilidad.setEnabled(false);
        disponibilidadRepo.save(disponibilidad);
        return new BaseObjectResponse<>(200, Modulo.DISPONIBILIDAD.actualizado(), null);
    }
}
