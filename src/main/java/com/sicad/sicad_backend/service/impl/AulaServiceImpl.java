package com.sicad.sicad_backend.service.impl;

import com.sicad.sicad_backend.Enum.Modulo;
import com.sicad.sicad_backend.dto.Aula.AulaCreateRequest;
import com.sicad.sicad_backend.dto.Aula.AulaDetalleResponse;
import com.sicad.sicad_backend.dto.Aula.AulaUpdateRequest;
import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.dto.escuela.EscuelaCreateRequest;
import com.sicad.sicad_backend.dto.escuela.EscuelaDetalleResponse;
import com.sicad.sicad_backend.model.Aula;
import com.sicad.sicad_backend.model.Escuela;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import com.sicad.sicad_backend.repository.interfaces.IAulaRepo;
import com.sicad.sicad_backend.service.base.CRUDImpl;
import com.sicad.sicad_backend.service.interfaces.IAulaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
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
    public BaseListReponse<AulaDetalleResponse> listar() {
        List<AulaDetalleResponse> lista = aulaRepo.findByEnabledTrue()
                .stream()
                .map(this::convAulaDetalle)
                .toList();

        return new BaseListReponse<>(200, Modulo.AULA.listado(), lista);
    }

    @Override
    public BaseObjectResponse<AulaDetalleResponse> buscar(Integer idAula) {
        Optional<Aula> aulaOpt = aulaRepo.findByIdAndEnabledTrue(idAula);

        if (aulaOpt.isEmpty()) {
            return new BaseObjectResponse<>(404, Modulo.AULA.noEncontrado(), null);
        }

        return new BaseObjectResponse<>(200, Modulo.AULA.encontrado(), convAulaDetalle(aulaOpt.get()));
    }

    @Override
    public BaseObjectResponse<AulaDetalleResponse> registrar(AulaCreateRequest request) {
        boolean existe = aulaRepo.existsNombre(request.getNombre());
        if(existe){
            return new BaseObjectResponse<>(404, "el nombre "+request.getNombre()+" ya esta en uso", null);
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
        return new BaseObjectResponse<>(201, Modulo.AULA.actualizado(), convAulaDetalle(aula));
    }

    @Override
    public BaseListReponse<AulaDetalleResponse> registrarAll(List<AulaCreateRequest> requests) {
        List<AulaDetalleResponse> registrados = new ArrayList<>();
        int errorCount = 0;

        for (AulaCreateRequest request : requests) {
            try {
                BaseObjectResponse<AulaDetalleResponse> response = registrar(request);
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
        return new BaseListReponse<>(201,Modulo.AULA.resumenAllRegistro(registrados.size(), errorCount),registrados);
    }
    @Override
    public BaseObjectResponse<AulaDetalleResponse> actualizar(Integer idAula, AulaUpdateRequest request) {
        Optional<Aula> aulaOpt = aulaRepo.findByIdAndEnabledTrue(idAula);

        if (aulaOpt.isEmpty()) {
            return new BaseObjectResponse<>(404, Modulo.AULA.noEncontrado(), null);
        }
        Aula aula = aulaOpt.get();
        if(request.getNombre() !=null){
            boolean existe = aulaRepo.existsNombre(request.getNombre());
            if(existe){
                return new BaseObjectResponse<>(404, "el nombre "+request.getNombre()+" ya esta en uso", null);
            } else {
                aula.setNombre(request.getNombre());
            }
        }

        if (request.getTipo() != null) {
            String tipo = request.getTipo().toUpperCase();
            aula.setTipo(tipo);

            // Validación: si es LABORATORIO, numeroEquipos es obligatorio
            if ("LABORATORIO".equals(tipo)) {
                if (request.getNumeroEquipos()== null) {
                    return new BaseObjectResponse<>(400, "Si el tipo es LABORATORIO, debe proporcionar número de equipos", null);
                } else {
                    aula.setNumeroEquipos(request.getNumeroEquipos());
                }

            } else {
                // Si cambia a TEORIA, se elimina el número de equipos
                aula.setNumeroEquipos(null);
            }
        }

        if (request.getPiso() != null) aula.setPiso(request.getPiso());
        if (request.getCapacidad() != null) aula.setCapacidad(request.getCapacidad());
        if (request.getEstado() != null) aula.setEstado(request.getEstado());

        aulaRepo.save(aula);

        return new BaseObjectResponse<>(200, Modulo.AULA.actualizado(), convAulaDetalle(aula));
    }

    @Override
    public BaseObjectResponse<String> eliminar(Integer idAula) {
        Optional<Aula> aulaOpt = aulaRepo.findByIdAndEnabledTrue(idAula);

        if (aulaOpt.isEmpty()) {
            return new BaseObjectResponse<>(404, Modulo.AULA.noEncontrado(), null);
        }
        Aula aula = aulaOpt.get();
        aula.setEnabled(false);
        aulaRepo.save(aula);
        return new BaseObjectResponse<>(200, Modulo.AULA.eliminado(), null);
    }

    private AulaDetalleResponse convAulaDetalle(Aula obj) {
        return modelMapper.map(obj, AulaDetalleResponse.class);
    }
}
