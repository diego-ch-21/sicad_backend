package com.sicad.sicad_backend.service.impl;

import com.sicad.sicad_backend.model.Carga;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import com.sicad.sicad_backend.repository.interfaces.ICargaRepo;
import com.sicad.sicad_backend.service.base.CRUDImpl;
import com.sicad.sicad_backend.service.interfaces.ICargaService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CargaServiceImpl
        extends CRUDImpl<Carga, Integer>
        implements ICargaService {

    private final ICargaRepo  cargaRepo;
    private final ModelMapper modelMapper;

    @Override
    protected IGenericRepo<Carga, Integer> getRepo() {
        return null;
    }

    @Override
    public List<Carga> findByEnabledTrue() {
        return cargaRepo.findByEnabledTrue();
    }

    @Override
    public List<Carga> findByEnabledTrueAndCicloAcademico_Id(Integer idCicloAcademico) {
        return cargaRepo.findByEnabledTrueAndCicloAcademico_IdCicloAcademico(idCicloAcademico);
    }
}
