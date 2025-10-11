package com.sicad.sicad_backend.service.impl;

import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.dto.dedicacion.DedicacionCreateRequest;
import com.sicad.sicad_backend.dto.dedicacion.DedicacionDetalleResponse;
import com.sicad.sicad_backend.model.Dedicacion;
import com.sicad.sicad_backend.model.Docente;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import com.sicad.sicad_backend.repository.interfaces.IDedicacionRepo;
import com.sicad.sicad_backend.repository.interfaces.IDocenteRepo;
import com.sicad.sicad_backend.service.base.CRUDImpl;
import com.sicad.sicad_backend.service.interfaces.IDedicacionService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DedicacionServiceImpl
        extends CRUDImpl<Dedicacion, Integer>
        implements IDedicacionService {

    private final IDedicacionRepo dedicacionRepo;
    private final IDocenteRepo docenteRepo;
    private final ModelMapper modelMapper;

    @Override
    protected IGenericRepo<Dedicacion, Integer> getRepo() {
        return dedicacionRepo;
    }

    public BaseListReponse<DedicacionDetalleResponse> saveAll(List<DedicacionCreateRequest> requestList) {
        List<Dedicacion> entities = requestList.stream()
                .map(dto -> {
                    Dedicacion dedicacion = modelMapper.map(dto, Dedicacion.class);
                    dedicacion.setEnabled(true);
                    return dedicacion;
                })
                .toList();

        List<Dedicacion> saved = dedicacionRepo.saveAll(entities);

        List<DedicacionDetalleResponse> response = saved.stream()
                .map(ded -> modelMapper.map(ded, DedicacionDetalleResponse.class))
                .toList();

        return new BaseListReponse<>(201, "Dedicaciones creadas", response);
    }
    public BaseObjectResponse<DedicacionDetalleResponse> obtenerDedicacion(Integer idDocente) {
        Optional<Docente>  docente = docenteRepo.findById(idDocente);
        Dedicacion dedicacion = docente.get().getDedicacion();
        if(dedicacion ==null) {
            return new BaseObjectResponse<>(404,"Dedicacion no encontrada",null);
        }
        return new BaseObjectResponse<>(201,"dedicacion encontrada exitosamente",convertToDetalle(dedicacion));

    }
    public BaseObjectResponse<String> eliminarDedicacion(Integer idDedicacion) {
        // Validación de parámetro
        if (idDedicacion == null) {
            return new BaseObjectResponse<>(400, "idDedicacion no proporcionado", null);
        }

        // Validar existencia
        Dedicacion dedicacion = dedicacionRepo.findById(idDedicacion).orElse(null);
        if (dedicacion == null) {
            return new BaseObjectResponse<>(404, "Dedicación  no encontrado", null);
        }

        // desabilitar
        dedicacion.setEnabled(false);
        dedicacionRepo.save(dedicacion);
        return new BaseObjectResponse<>(200, "se elimino la Dedicación exitosamente", null);
    }

    private DedicacionDetalleResponse convertToDetalle(Dedicacion obj) {
        return modelMapper.map(obj, DedicacionDetalleResponse.class);
    }
    @Override
    public List<Dedicacion> findByEnabledTrue() {
        return dedicacionRepo.findByEnabledTrue();
    }
}
