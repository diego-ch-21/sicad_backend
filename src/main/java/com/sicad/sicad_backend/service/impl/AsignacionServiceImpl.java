package com.sicad.sicad_backend.service.impl;

import com.sicad.sicad_backend.dto.asignacion.AsignacionCreateRequest;
import com.sicad.sicad_backend.dto.asignacion.AsignacionDetalleResponse;
import com.sicad.sicad_backend.dto.asignacion.AsignacionUpdateRequest;
import com.sicad.sicad_backend.dto.base.GenericObjectResponse;
import com.sicad.sicad_backend.model.*;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import com.sicad.sicad_backend.repository.interfaces.*;
import com.sicad.sicad_backend.service.base.CRUDImpl;
import com.sicad.sicad_backend.service.interfaces.IAsignacionService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AsignacionServiceImpl
        extends CRUDImpl<Asignacion, Integer>
        implements IAsignacionService {

    private final IAsignacionRepo asignacionRepo;
    private final IDocenteRepo docenteRepo;
    private final ICursoRepo cursoRepo;
    private final ICargaElectivaRepo cargaElectivaRepo;
    private final ModelMapper modelMapper;

    @Override
    protected IGenericRepo<Asignacion, Integer> getRepo() {
        return asignacionRepo;
    }

    public GenericObjectResponse<AsignacionDetalleResponse> registrarAsignacion(AsignacionCreateRequest request) {
        Docente docente = docenteRepo.findById(request.getIdDocente()).orElse(null);
        if (docente == null) {
            return new GenericObjectResponse<>(404, "Docente no encontrado", null);
        }

        Curso curso = cursoRepo.findById(request.getIdCurso()).orElse(null);
        if (curso == null) {
            return new GenericObjectResponse<>(404, "Curso no encontrado", null);
        }

        CargaElectiva carga = cargaElectivaRepo.findById(request.getIdCargaElectiva()).orElse(null);
        if (carga == null) {
            return new GenericObjectResponse<>(404, "Carga Electiva no encontrada", null);
        }

        Asignacion asignacion = new Asignacion();
        asignacion.setDocente(docente);
        asignacion.setCurso(curso);
        asignacion.setCargaElectiva(carga);
        asignacion.setTipoAsignacion(request.getTipoAsignacion());
        asignacion.setEnabled(true);
        asignacion.setCreatedAt(LocalDate.now());
        asignacionRepo.save(asignacion);
        AsignacionDetalleResponse dto = modelMapper.map(asignacion, AsignacionDetalleResponse.class);
        return new GenericObjectResponse<>(201, "Asignación registrada exitosamente", dto);
    }

    public GenericObjectResponse<AsignacionDetalleResponse> actualizarAsignacion(Integer id, AsignacionUpdateRequest request) {
        Asignacion asignacion = asignacionRepo.findById(id).orElse(null);
        if (asignacion == null) {
            return new GenericObjectResponse<>(404, "Asignación no encontrada", null);
        }

        if (request.getIdDocente() != null) {
            docenteRepo.findById(request.getIdDocente()).ifPresent(asignacion::setDocente);
        }

        if (request.getIdCurso() != null) {
            cursoRepo.findById(request.getIdCurso()).ifPresent(asignacion::setCurso);
        }

        if (request.getIdCargaElectiva() != null) {
            cargaElectivaRepo.findById(request.getIdCargaElectiva()).ifPresent(asignacion::setCargaElectiva);
        }

        if (request.getTipoAsignacion() != null) {
            asignacion.setTipoAsignacion(request.getTipoAsignacion());
        }

        try {
            asignacionRepo.save(asignacion);
            AsignacionDetalleResponse dto = modelMapper.map(asignacion, AsignacionDetalleResponse.class);
            return new GenericObjectResponse<>(200, "Asignación actualizada exitosamente", dto);
        } catch (DataIntegrityViolationException e) {
            return new GenericObjectResponse<>(400, "Conflicto de unicidad: ya existe una asignación para este docente y horario", null);
        }
    }
}