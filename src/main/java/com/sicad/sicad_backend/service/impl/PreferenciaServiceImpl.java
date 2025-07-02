package com.sicad.sicad_backend.service.impl;

import com.sicad.sicad_backend.dto.base.GenericObjectResponse;
import com.sicad.sicad_backend.dto.preferencia.PreferenciaCreateRequest;
import com.sicad.sicad_backend.dto.preferencia.PreferenciaDetalleResponse;
import com.sicad.sicad_backend.dto.preferencia.PreferenciaUpdateRequest;
import com.sicad.sicad_backend.model.*;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import com.sicad.sicad_backend.repository.interfaces.IAsignaturaRepo;
import com.sicad.sicad_backend.repository.interfaces.ICargaElectivaRepo;
import com.sicad.sicad_backend.repository.interfaces.IDocenteRepo;
import com.sicad.sicad_backend.repository.interfaces.IPreferenciaRepo;
import com.sicad.sicad_backend.service.base.CRUDImpl;
import com.sicad.sicad_backend.service.interfaces.ICursoService;
import com.sicad.sicad_backend.service.interfaces.IPreferenciaService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PreferenciaServiceImpl
        extends CRUDImpl<Preferencia, Integer>
        implements IPreferenciaService {

    private final IPreferenciaRepo preferenciaRepo;
    private final IDocenteRepo docenteRepo;
    private final IAsignaturaRepo asignaturaRepo;
    private final ICargaElectivaRepo cargaElectivaRepo;
    private final ModelMapper modelMapper;

    @Override
    protected IGenericRepo<Preferencia, Integer> getRepo() {
        return preferenciaRepo;
    }

    public GenericObjectResponse<PreferenciaDetalleResponse> registrarPreferencia(PreferenciaCreateRequest request){
        //validar docente
        Docente docente = docenteRepo.findById(request.getIdDocente()).orElse(null);
        if(docente == null){
            return new GenericObjectResponse<>(404, "Docente no encontrado", null);
        }
        Asignatura asignatura = asignaturaRepo.findById(request.getIdAsignatura()).orElse(null);
        if(asignatura == null){
            return new GenericObjectResponse<>(404, "Asignatura no encontrada", null);
        }
        CargaElectiva cargaElectiva = cargaElectivaRepo.findById(request.getIdCargaElectiva()).orElse(null);
        if(cargaElectiva == null){
            return new GenericObjectResponse<>(404, "Carga Electiva no encontrada", null);
        }

        Preferencia preferencia = new Preferencia().builder()
                .docente(docente)
                .asignatura(asignatura)
                .cargaElectiva(cargaElectiva)
                .enabled(true)
                .build();
        preferenciaRepo.save(preferencia);

        PreferenciaDetalleResponse dto = modelMapper.map(preferencia, PreferenciaDetalleResponse.class);
        return new GenericObjectResponse<>(201, "Preferencia registrada", dto);
    }
    public GenericObjectResponse<PreferenciaDetalleResponse> actualizarPreferencia(Integer id, PreferenciaUpdateRequest request) {
        Preferencia preferencia = preferenciaRepo.findById(id).orElse(null);
        if (preferencia == null) {
            return new GenericObjectResponse<>(404, "Preferencia no encontrada", null);
        }

        // Validar y actualizar docente si viene en el request
        if (request.getIdDocente() != null) {
            Docente docente = docenteRepo.findById(request.getIdDocente()).orElse(null);
            if (docente == null) {
                return new GenericObjectResponse<>(404, "Docente no encontrado", null);
            }
            preferencia.setDocente(docente);
        }

        // Validar y actualizar asignatura si viene en el request
        if (request.getIdAsignatura() != null) {
            Asignatura asignatura = asignaturaRepo.findById(request.getIdAsignatura()).orElse(null);
            if (asignatura == null) {
                return new GenericObjectResponse<>(404, "Asignatura no encontrada", null);
            }
            preferencia.setAsignatura(asignatura);
        }

        // Validar y actualizar carga electiva si viene en el request
        if (request.getIdCargaElectiva() != null) {
            CargaElectiva cargaElectiva = cargaElectivaRepo.findById(request.getIdCargaElectiva()).orElse(null);
            if (cargaElectiva == null) {
                return new GenericObjectResponse<>(404, "Carga Electiva no encontrada", null);
            }
            preferencia.setCargaElectiva(cargaElectiva);
        }

        preferenciaRepo.save(preferencia);

        PreferenciaDetalleResponse dto = modelMapper.map(preferencia, PreferenciaDetalleResponse.class);
        return new GenericObjectResponse<>(200, "Preferencia actualizada exitosamente", dto);
    }



}
