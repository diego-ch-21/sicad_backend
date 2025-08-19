package com.sicad.sicad_backend.service.impl;

import com.sicad.sicad_backend.dto.base.GenericReponse;
import com.sicad.sicad_backend.dto.dedicacion.DedicacionCreateRequest;
import com.sicad.sicad_backend.dto.dedicacion.DedicacionDetalleResponse;
import com.sicad.sicad_backend.model.Dedicacion;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import com.sicad.sicad_backend.repository.interfaces.IDedicacionRepo;
import com.sicad.sicad_backend.service.base.CRUDImpl;
import com.sicad.sicad_backend.service.interfaces.IDedicacionService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DedicacionServiceImpl
        extends CRUDImpl<Dedicacion, Integer>
        implements IDedicacionService {

    private final IDedicacionRepo dedicacionRepo;
    private final ModelMapper modelMapper;

    @Override
    protected IGenericRepo<Dedicacion, Integer> getRepo() {
        return dedicacionRepo;
    }

    public GenericReponse<DedicacionDetalleResponse> saveAll(List<DedicacionCreateRequest> requestList) {
        List<Dedicacion> entities = requestList.stream()
                .map(dto -> modelMapper.map(dto, Dedicacion.class))
                .toList();
        List<Dedicacion> saved = dedicacionRepo.saveAll(entities);
        List<DedicacionDetalleResponse> response = saved.stream()
                .map(ded -> modelMapper.map(ded, DedicacionDetalleResponse.class))
                .toList();
        return new GenericReponse<>(201, "Dedicaciones creadas", response);
    }

    @Override
    public List<Dedicacion> findByEnabledTrue() {
        return dedicacionRepo.findByEnabledTrue();
    }
}
