package com.sicad.sicad_backend.service.impl;

import com.sicad.sicad_backend.dto.base.GenericObjectResponse;
import com.sicad.sicad_backend.dto.base.GenericReponse;
import com.sicad.sicad_backend.dto.disponibilidad.DisponibilidadCreateRequest;
import com.sicad.sicad_backend.dto.disponibilidad.DisponibilidadDetalleResponse;
import com.sicad.sicad_backend.dto.disponibilidad.DisponibilidadResumenResponse;
import com.sicad.sicad_backend.dto.disponibilidad.DisponibilidadUpdateRequest;
import com.sicad.sicad_backend.dto.preferencia.PreferenciaCreateRequest;
import com.sicad.sicad_backend.dto.preferencia.PreferenciaDetalleResponse;
import com.sicad.sicad_backend.model.CargaElectiva;
import com.sicad.sicad_backend.model.Disponibilidad;
import com.sicad.sicad_backend.model.Docente;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import com.sicad.sicad_backend.repository.interfaces.ICargaElectivaRepo;
import com.sicad.sicad_backend.repository.interfaces.IDisponibilidadRepo;
import com.sicad.sicad_backend.repository.interfaces.IDocenteRepo;
import com.sicad.sicad_backend.service.base.CRUDImpl;
import com.sicad.sicad_backend.service.interfaces.IDisponibilidadService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DisponibilidadServiceImpl extends CRUDImpl<Disponibilidad, Integer> implements IDisponibilidadService {

    private final IDisponibilidadRepo disponibilidadRepo;
    private final IDocenteRepo docenteRepo;
    private final ICargaElectivaRepo cargaElectivaRepo;
    private final ModelMapper modelMapper;

    @Override
    protected IGenericRepo<Disponibilidad, Integer> getRepo() {
        return disponibilidadRepo;
    }

    public GenericObjectResponse<DisponibilidadDetalleResponse> registrarDisponibilidad(DisponibilidadCreateRequest request) {
        Docente docente = docenteRepo.findById(request.getIdDocente()).orElse(null);
        if (docente == null) {
            return new GenericObjectResponse<>(404, "Docente no encontrado", null);
        }

        CargaElectiva carga = cargaElectivaRepo.findById(request.getIdCargaElectiva()).orElse(null);
        if (carga == null) {
            return new GenericObjectResponse<>(404, "Carga electiva no encontrada", null);
        }

        try {
            Time horaInicio = Time.valueOf(request.getHoraInicio() + ":00");
            Time horaFin = Time.valueOf(request.getHoraFin() + ":00");

            if (horaFin.getTime() <= horaInicio.getTime()) {
                return new GenericObjectResponse<>(400, "La hora de fin debe ser posterior a la hora de inicio", null);
            }

            Disponibilidad disponibilidad = Disponibilidad.builder()
                    .docente(docente)
                    .cargaElectiva(carga)
                    .diaSemana(request.getDiaSemana())
                    .horaInicio(horaInicio)
                    .horaFin(horaFin)
                    .enabled(true)
                    .build();

            disponibilidadRepo.save(disponibilidad);

            DisponibilidadDetalleResponse response = modelMapper.map(disponibilidad, DisponibilidadDetalleResponse.class);
            return new GenericObjectResponse<>(201, "Disponibilidad registrada exitosamente", response);

        } catch (IllegalArgumentException e) {
            return new GenericObjectResponse<>(400, "Formato de hora inválido (debe ser HH:mm)", null);
        }
    }
    public GenericReponse<DisponibilidadDetalleResponse> registrarVariosDisponiblidadAll(List<DisponibilidadCreateRequest> requests){
        List<DisponibilidadDetalleResponse> registrados = new ArrayList<>();
        int errorCount = 0;

        for(DisponibilidadCreateRequest request: requests){
            GenericObjectResponse<DisponibilidadDetalleResponse>  response = registrarDisponibilidad(request);
            if(response.status() == 201 || response.data() != null){
                registrados.add(response.data());
            } else {
                errorCount++;
            }
        }
        String mensaje = String.format("disponibilidad registrados: %d. Fallidos: %d.", registrados.size(), errorCount);
        return new GenericReponse<>(200, mensaje, registrados);
    }

    public GenericObjectResponse<DisponibilidadDetalleResponse> actualizarDisponibilidad(Integer id, DisponibilidadUpdateRequest request) {
        Disponibilidad disponibilidad = disponibilidadRepo.findById(id).orElse(null);
        if (disponibilidad == null) {
            return new GenericObjectResponse<>(404, "Disponibilidad no encontrada", null);
        }

        if (request.getIdDocente() != null) {
            docenteRepo.findById(request.getIdDocente()).ifPresent(disponibilidad::setDocente);
        }

        if (request.getIdCargaElectiva() != null) {
            cargaElectivaRepo.findById(request.getIdCargaElectiva()).ifPresent(disponibilidad::setCargaElectiva);
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
                    return new GenericObjectResponse<>(400, "La hora de fin debe ser posterior a la hora de inicio", null);
                }
                disponibilidad.setHoraInicio(horaInicio);
                disponibilidad.setHoraFin(horaFin);
            }

        } catch (IllegalArgumentException e) {
            return new GenericObjectResponse<>(400, "Formato de hora inválido (debe ser HH:mm)", null);
        }

        disponibilidadRepo.save(disponibilidad);

        DisponibilidadDetalleResponse response = modelMapper.map(disponibilidad, DisponibilidadDetalleResponse.class);
        return new GenericObjectResponse<>(200, "Disponibilidad actualizada exitosamente", response);
    }
    public GenericObjectResponse<List<DisponibilidadResumenResponse>> listarDisponibilidadDocente(Integer idDocente, Integer idCargaElectiva) {

        if (!docenteRepo.existsByIdDocente(idDocente)) {
            return new GenericObjectResponse<>(400, "Docente no encontrado", null);
        }
        if (!cargaElectivaRepo.existsByIdCargaElectiva(idCargaElectiva)) {
            return new GenericObjectResponse<>(400, "Carga electiva no encontrada", null);
        }

        List<Disponibilidad> disponibilidades = disponibilidadRepo.buscarPorDocenteYCargaElectiva(idDocente, idCargaElectiva);

        List<DisponibilidadResumenResponse> listaDTO = disponibilidades.stream()
                .map(disponibilidad -> modelMapper.map(disponibilidad, DisponibilidadResumenResponse.class))
                .collect(Collectors.toList());

        return new GenericObjectResponse<>(200, "Lista obtenida correctamente", listaDTO);
    }


}
