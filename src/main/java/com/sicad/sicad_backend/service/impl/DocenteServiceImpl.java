package com.sicad.sicad_backend.service.impl;

import com.sicad.sicad_backend.dto.DocenteDTO;
import com.sicad.sicad_backend.model.Docente;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import com.sicad.sicad_backend.repository.interfaces.IDocenteRepo;
import com.sicad.sicad_backend.service.base.CRUDImpl;
import com.sicad.sicad_backend.service.interfaces.IDocenteService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DocenteServiceImpl
        extends CRUDImpl<Docente, Integer>
        implements IDocenteService {

    private final IDocenteRepo repo;
    private final ModelMapper modelMapper;

    @Override
    protected IGenericRepo<Docente, Integer> getRepo() {
        return repo;
    }


    private DocenteDTO convertToDTO(Docente obj) {
        return modelMapper.map(obj, DocenteDTO.class);
    }
    private Docente convertToEntity(DocenteDTO dto) {
        return modelMapper.map(dto, Docente.class);
    }
}
