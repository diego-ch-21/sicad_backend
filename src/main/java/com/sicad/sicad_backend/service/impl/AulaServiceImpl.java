package com.sicad.sicad_backend.service.impl;

import com.sicad.sicad_backend.dto.Aula.AulaCreateRequest;
import com.sicad.sicad_backend.dto.Aula.AulaDetalleResponse;
import com.sicad.sicad_backend.dto.Aula.AulaUpdateRequest;
import com.sicad.sicad_backend.dto.base.GenericObjectResponse;
import com.sicad.sicad_backend.dto.base.GenericReponse;
import com.sicad.sicad_backend.dto.cicloAcademico.CicloAcademicoDetalleResponse;
import com.sicad.sicad_backend.dto.curso.CursoCreateRequest;
import com.sicad.sicad_backend.dto.curso.CursoDetalleResponse;
import com.sicad.sicad_backend.model.Aula;
import com.sicad.sicad_backend.model.CicloAcademico;
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

    public GenericObjectResponse<AulaDetalleResponse> registrarAula(AulaCreateRequest request) {
        Aula aula = construirAulaDesdeRequest(request);
        if (aula == null) {
            return new GenericObjectResponse<>(
                    400,
                    "El laboratorio debe tener una cantidad válida de equipos",
                    null
            );
        }

        aula.setEnabled(true);
        aulaRepo.save(aula);
        AulaDetalleResponse response  = convertToResponseDTO(aula);
        return new GenericObjectResponse<>(
                201,
                "Aula registrada correctamente",
                response
        );
    }

    public GenericReponse<AulaDetalleResponse> registrarAulasMultiples(List<AulaCreateRequest> requests) {
        List<AulaDetalleResponse> registrados = new ArrayList<>();
        int errorCount = 0;

        for (AulaCreateRequest request : requests) {
            GenericObjectResponse<AulaDetalleResponse> response = registrarAula(request);
            if (response.status() == 201 && response.data() != null) {
                registrados.add(response.data());
            } else {
                errorCount++;
            }
        }

        String mensaje = String.format("Aulas registrados: %d. Fallidos: %d.", registrados.size(), errorCount);
        return new GenericReponse<>(201, mensaje,registrados);
    }

    public GenericObjectResponse<AulaDetalleResponse> actualizarAula(Integer id, AulaUpdateRequest request) {
        Aula aulaExistente = aulaRepo.findById(id).orElse(null);
        if (aulaExistente == null) {
            return new GenericObjectResponse<>(404, "Aula no encontrada", null);
        }

        // Actualizar tipo
        if (request.getTipo() != null) {
            String tipoUpper = request.getTipo().toUpperCase();
            aulaExistente.setTipo(tipoUpper);

            // Validación: si es LABORATORIO, numeroEquipos es obligatorio
            if ("LABORATORIO".equals(tipoUpper)) {
                Integer equipos = request.getNumeroEquipos() != null ? request.getNumeroEquipos() : aulaExistente.getNumeroEquipos();
                if (equipos == null) {
                    return new GenericObjectResponse<>(400, "Si el tipo es LABORATORIO, debe proporcionar número de equipos", null);
                }
                aulaExistente.setNumeroEquipos(equipos);
            } else {
                // Si cambia a TEORIA, se elimina el número de equipos
                aulaExistente.setNumeroEquipos(null);
            }
        }

        // Actualizar los demás campos solo si vienen en el request
        if (request.getCodigo() != null) aulaExistente.setCodigo(request.getCodigo());
        if (request.getPiso() != null) aulaExistente.setPiso(request.getPiso());
        if (request.getCapacidad() != null) aulaExistente.setCapacidad(request.getCapacidad());
        if (request.getEstado() != null) aulaExistente.setEstado(request.getEstado());

        // Guardar cambios
        Aula aulaActualizada = aulaRepo.save(aulaExistente);
        AulaDetalleResponse response  = convertToResponseDTO(aulaActualizada);

        return new GenericObjectResponse<>(
                200,
                "Aula actualizada correctamente",
                response
        );
    }



    private Aula construirAulaDesdeRequest(AulaCreateRequest request) {
        Aula aula = new Aula();
        String tipo = request.getTipoAula();

        aula.setTipo(tipo);
        switch (tipo.toUpperCase()) {
            case "TEORIA" -> aula.setNumeroEquipos(null);
            case "LABORATORIO" -> {
                if (request.getNumeroEquipos() == null) {
                    return null; // retorna error
                }
                aula.setNumeroEquipos(request.getNumeroEquipos());
            }
        }

        aula.setCodigo(request.getCodigo());
        aula.setPiso(request.getPiso());
        aula.setCapacidad(request.getCapacidad());
        aula.setEstado(request.getEstado());

        return aula;
    }

    private AulaDetalleResponse convertToResponseDTO(Aula obj) {
        return modelMapper.map(obj, AulaDetalleResponse.class);
    }
}
