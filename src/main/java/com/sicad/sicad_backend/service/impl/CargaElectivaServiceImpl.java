package com.sicad.sicad_backend.service.impl;

import com.sicad.sicad_backend.repository.base.IGenericRepo;
import com.sicad.sicad_backend.service.base.CRUDImpl;
import com.sicad.sicad_backend.service.base.ICRUD;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CargaElectivaServiceImpl
    extends CRUDImpl<CargaElectiva, Integer>
    implements ICRUD<CargaElectiva, Integer> {
    private final ICargaElectivaRepo cargaElectivaRepo;
    @Override
    protected IGenericRepo<CargaElectiva, Integer> getRepo() {
        return cargaElectivaRepo;
    }
}
