package com.sicad.sicad_backend.service.impl;

import com.sicad.sicad_backend.dto.Aula.AulaCreateRequest;
import com.sicad.sicad_backend.dto.Aula.AulaDetalleResponse;
import com.sicad.sicad_backend.dto.Aula.AulaUpdateRequest;
import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.model.Aula;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import com.sicad.sicad_backend.repository.interfaces.IAulaRepo;
import com.sicad.sicad_backend.service.base.CRUDImpl;
import com.sicad.sicad_backend.service.interfaces.IAulaService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AulaServiceImpl
        extends CRUDImpl<Aula, Integer>
        implements IAulaService {

    private final IAulaRepo aulaRepo;
    private final ModelMapper modelMapper;

    @Override
    protected IGenericRepo<Aula, Integer> getRepo() {
        return aulaRepo;
    }

    @Override
    public List<Aula> findByEnabledTrue() {
        return aulaRepo.findByEnabledTrue();
    }

    public BaseObjectResponse<AulaDetalleResponse> registrarAula(AulaCreateRequest request) {
        boolean existe = aulaRepo.existsNombre(request.getNombre());
        if(existe){
            return new BaseObjectResponse<>(
                    404,
                    "el nombre "+request.getNombre()+" ya esta en uso",
                    null
            );
        }
        Aula aula = new Aula();
        String tipo = request.getTipo();

        aula.setTipo(tipo);
        switch (tipo.toUpperCase()) {
            case "TEORIA" -> aula.setNumeroEquipos(null);
            case "LABORATORIO" -> {

                if (request.getNumeroEquipos() != null) {
                    aula.setNumeroEquipos(request.getNumeroEquipos());
                } else {
                    return new BaseObjectResponse<>(404,"el campo de numero de equipos es necesaro para los aulas laboratorio",null);
                }

            }
        }

        aula.setNombre(request.getNombre());
        aula.setPiso(request.getPiso());
        aula.setCapacidad(request.getCapacidad());
        aula.setEstado(request.getEstado());
        aula.setEnabled(true);
        aulaRepo.save(aula);
        AulaDetalleResponse response  = convertToResponseDTO(aula);
        return new BaseObjectResponse<>(
                201,
                "Aula registrada correctamente",
                response
        );
    }

    public BaseListReponse<AulaDetalleResponse> registrarAulasMultiples(List<AulaCreateRequest> requests) {
        List<AulaDetalleResponse> registrados = new ArrayList<>();
        int errorCount = 0;

        for (AulaCreateRequest request : requests) {
            BaseObjectResponse<AulaDetalleResponse> response = registrarAula(request);
            if (response.status() == 201 && response.data() != null) {
                registrados.add(response.data());
            } else {
                errorCount++;
            }
        }

        String mensaje = String.format("Aulas registrados: %d. Fallidos: %d.", registrados.size(), errorCount);
        return new BaseListReponse<>(201, mensaje,registrados);
    }

    public BaseObjectResponse<AulaDetalleResponse> actualizarAula(Integer id, AulaUpdateRequest request) {
        Aula aulaExistente = aulaRepo.findById(id).orElse(null);
        if (aulaExistente == null) {
            return new BaseObjectResponse<>(404, "Aula no encontrada", null);
        }
        if(!(aulaExistente.getNombre().equals(request.getNombre()))){
            boolean existe = aulaRepo.existsNombre(request.getNombre());
            if(existe){
                return new BaseObjectResponse<>(
                        404,
                        "el nombre "+request.getNombre()+" ya esta en uso",
                        null
                );
            }
        }


        // Actualizar tipo
        if (request.getTipo() != null) {
            String tipoUpper = request.getTipo().toUpperCase();
            aulaExistente.setTipo(tipoUpper);

            // Validación: si es LABORATORIO, numeroEquipos es obligatorio
            if ("LABORATORIO".equals(tipoUpper)) {
                if (request.getNumeroEquipos()== null) {
                    return new BaseObjectResponse<>(400, "Si el tipo es LABORATORIO, debe proporcionar número de equipos", null);
                } else {
                    aulaExistente.setNumeroEquipos(request.getNumeroEquipos());
                }

            } else {
                // Si cambia a TEORIA, se elimina el número de equipos
                aulaExistente.setNumeroEquipos(null);
            }
        }

        // Actualizar los demás campos solo si vienen en el request
        if (request.getNombre() != null) aulaExistente.setNombre(request.getNombre());
        if (request.getPiso() != null) aulaExistente.setPiso(request.getPiso());
        if (request.getCapacidad() != null) aulaExistente.setCapacidad(request.getCapacidad());
        if (request.getEstado() != null) aulaExistente.setEstado(request.getEstado());

        // Guardar cambios
        Aula aulaActualizada = aulaRepo.save(aulaExistente);
        AulaDetalleResponse response  = convertToResponseDTO(aulaActualizada);

        return new BaseObjectResponse<>(
                200,
                "Aula actualizada correctamente",
                response
        );
    }
    public BaseObjectResponse<String> eliminarAula(Integer idAula) {
        // Validación de parámetro
        if (idAula == null) {
            return new BaseObjectResponse<>(400, "idAula no proporcionado", null);
        }

        // Validar existencia
        Aula aula = aulaRepo.findById(idAula).orElse(null);
        if (aula == null) {
            return new BaseObjectResponse<>(404, "Aula  no encontrado", null);
        }

        // desabilitar
        aula.setEnabled(false);
        aulaRepo.save(aula);
        return new BaseObjectResponse<>(200, "se elimino el aula exitosamente", null);
    }

    private AulaDetalleResponse convertToResponseDTO(Aula obj) {
        return modelMapper.map(obj, AulaDetalleResponse.class);
    }
}
