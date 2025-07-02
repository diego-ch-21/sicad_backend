package com.sicad.sicad_backend.service.impl;

import com.sicad.sicad_backend.dto.base.GenericObjectResponse;
import com.sicad.sicad_backend.dto.preferencia.PreferenciaCreateRequest;
import com.sicad.sicad_backend.dto.preferencia.PreferenciaDetalleResponse;
import com.sicad.sicad_backend.model.Curso;
import com.sicad.sicad_backend.model.Docente;
import com.sicad.sicad_backend.model.Preferencia;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import com.sicad.sicad_backend.repository.interfaces.ICargaElectivaRepo;
import com.sicad.sicad_backend.repository.interfaces.IDocenteRepo;
import com.sicad.sicad_backend.repository.interfaces.IPreferenciaRepo;
import com.sicad.sicad_backend.service.base.CRUDImpl;
import com.sicad.sicad_backend.service.interfaces.ICursoService;
import com.sicad.sicad_backend.service.interfaces.IPreferenciaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PreferenciaServiceImpl
        extends CRUDImpl<Preferencia, Integer>
        implements IPreferenciaService {

    private final IPreferenciaRepo preferenciaRepo;
    private final IDocenteRepo docenteRepo;
    private final ICursoService cursoRepo;
    private final ICargaElectivaRepo cargaElectivaRepo;

    @Override
    protected IGenericRepo<Preferencia, Integer> getRepo() {
        return preferenciaRepo;
    }


}
