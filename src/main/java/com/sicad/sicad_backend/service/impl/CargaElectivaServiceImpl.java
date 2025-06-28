package com.sicad.sicad_backend.service.impl;

import com.sicad.sicad_backend.model.CargaElectiva;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import com.sicad.sicad_backend.repository.interfaces.ICargaElectivaRepo;
import com.sicad.sicad_backend.service.base.CRUDImpl;
import com.sicad.sicad_backend.service.interfaces.ICargaElectivaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CargaElectivaServiceImpl
    extends CRUDImpl<CargaElectiva, Integer>
    implements ICargaElectivaService {
    private final ICargaElectivaRepo repo;
    @Override
    protected IGenericRepo<CargaElectiva, Integer> getRepo() {
        return repo;
    }
}
