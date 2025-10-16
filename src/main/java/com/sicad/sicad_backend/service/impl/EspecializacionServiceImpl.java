package com.sicad.sicad_backend.service.impl;

import com.sicad.sicad_backend.Enum.Modulo;
import com.sicad.sicad_backend.dto.Especializacion.EspecializacionCreateRequest;
import com.sicad.sicad_backend.dto.Especializacion.EspecializacionDetalleResponse;
import com.sicad.sicad_backend.dto.Especializacion.EspecializacionResumenResponse;
import com.sicad.sicad_backend.dto.Especializacion.EspecializacionUpdateRequest;
import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.dto.escuela.EscuelaCreateRequest;
import com.sicad.sicad_backend.dto.escuela.EscuelaDetalleResponse;
import com.sicad.sicad_backend.model.*;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import com.sicad.sicad_backend.repository.interfaces.IAsignaturaRepo;
import com.sicad.sicad_backend.repository.interfaces.ICicloAcademicoRepo;
import com.sicad.sicad_backend.repository.interfaces.IDocenteRepo;
import com.sicad.sicad_backend.repository.interfaces.IEspecializacionRepo;
import com.sicad.sicad_backend.service.base.CRUDImpl;
import com.sicad.sicad_backend.service.interfaces.IEspecializacionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EspecializacionServiceImpl
        extends CRUDImpl<Especializacion,Integer>
        implements IEspecializacionService {

    private final IEspecializacionRepo especializacionRepo;
    private final IDocenteRepo docenteRepo;
    private final IAsignaturaRepo asignaturaRepo;
    private final ModelMapper modelMapper;

    @Override
    protected IGenericRepo<Especializacion, Integer> getRepo() {
        return especializacionRepo;
    }

    @Override
    public BaseListReponse<EspecializacionResumenResponse> listarPorDocente(Integer idDocente) {
        List<EspecializacionResumenResponse> response = especializacionRepo.listarEspecializacionesPorDocente(idDocente)
                .stream()
                .map(this::convEspecializacionResumen)
                .toList();

        return new BaseListReponse<>(200, Modulo.ESPECIALIZACION.listado(), response);
    }

    @Override
    public BaseObjectResponse<EspecializacionDetalleResponse> buscar(Integer idEspecializacion) {
        Optional<Especializacion> especializacionOpt = especializacionRepo.findByIdAndEnabledTrue(idEspecializacion);

        if (especializacionOpt.isEmpty()) {
            return new BaseObjectResponse<>(404, Modulo.ESPECIALIZACION.noEncontrado(), null);
        }

        return new BaseObjectResponse<>(200, Modulo.ESPECIALIZACION.encontrado(), convEspecializacionDetalle(especializacionOpt.get()));
    }

    @Override
    public BaseObjectResponse<EspecializacionDetalleResponse> registrar(EspecializacionCreateRequest request) {
        Optional<Docente> docenteOpt = docenteRepo.findByIdAndEnabledTrue(request.getIdDocente());
        if (docenteOpt.isEmpty()) {
            return new BaseObjectResponse<>(404, Modulo.DOCENTE.noEncontrado(), null);
        }

        Optional<Asignatura> asignaturaOpt = asignaturaRepo.findByIdAndEnabledTrue(request.getIdAsignatura());
        if (asignaturaOpt.isEmpty()) {
            return new BaseObjectResponse<>(404, Modulo.ASIGNATURA.noEncontrado(), null);
        }
        Boolean isExiste = especializacionRepo.isReglaAsignacionAsignatura(request.getIdDocente(), request.getIdAsignatura());
        if(isExiste) {
            List<Modulo> modulos = new ArrayList<>();
            modulos.add(Modulo.ASIGNATURA);
            modulos.add(Modulo.DOCENTE);
            return new BaseObjectResponse<>(409, Modulo.ESPECIALIZACION.noCumple(modulos), null);
        }
        Especializacion especializacion = Especializacion.builder()
                .asignatura(asignaturaOpt.get())
                .docente(docenteOpt.get())
                .enabled(true)
                .build();
        especializacionRepo.save(especializacion);
        return new BaseObjectResponse<>(200,Modulo.ESPECIALIZACION.registrado(), convEspecializacionDetalle(especializacion));
    }

    @Override
    public BaseListReponse<EspecializacionDetalleResponse> registrarAll(List<EspecializacionCreateRequest> requests) {
        List<EspecializacionDetalleResponse> registrados = new ArrayList<>();
        int errorCount = 0;

        for (EspecializacionCreateRequest request : requests) {
            try {
                BaseObjectResponse<EspecializacionDetalleResponse> response = registrar(request);
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
        return new BaseListReponse<>(201,Modulo.ESPECIALIZACION.resumenAllRegistro(registrados.size(), errorCount),registrados);
    }

    @Override
    public BaseObjectResponse<EspecializacionDetalleResponse> actualizar(Integer idEspecializacion, EspecializacionUpdateRequest request) {
        Optional<Especializacion> especializacionOpt = especializacionRepo.findByIdAndEnabledTrue(idEspecializacion);
        if (especializacionOpt.isEmpty()) {
            return new BaseObjectResponse<>(404, Modulo.ESPECIALIZACION.noEncontrado(), null);
        }
        Especializacion especializacion = especializacionOpt.get();
        if(request.getIdDocente() !=null){
            Optional<Docente> docenteOpt = docenteRepo.findByIdAndEnabledTrue(request.getIdDocente());
            if (docenteOpt.isEmpty()) {
                return new BaseObjectResponse<>(404, Modulo.DOCENTE.noEncontrado(), null);
            } else {
                especializacion.setDocente(docenteOpt.get());
            }

        }
        if(request.getIdAsignatura() !=null){
            Optional<Asignatura> asignaturaOpt = asignaturaRepo.findByIdAndEnabledTrue(request.getIdAsignatura());
            if (asignaturaOpt.isEmpty()) {
                return new BaseObjectResponse<>(404, Modulo.ASIGNATURA.noEncontrado(), null);
            } else {
                especializacion.setAsignatura(asignaturaOpt.get());
            }
        }
        Boolean isExite = especializacionRepo.isReglaAsignacionAsignatura(
                especializacion.getDocente().getIdDocente(),
                especializacion.getAsignatura().getIdAsignatura());
        if(isExite) {
            List<Modulo> modulos = new ArrayList<>();
            modulos.add(Modulo.ASIGNATURA);
            modulos.add(Modulo.DOCENTE);
            return new BaseObjectResponse<>(409, Modulo.ESPECIALIZACION.noCumple(modulos), null);        }
        especializacionRepo.save(especializacion);

        return new BaseObjectResponse<>(200, Modulo.ESPECIALIZACION.actualizado(), convEspecializacionDetalle(especializacion));
    }

    @Override
    public BaseObjectResponse<String> eliminar(Integer idEspecializacion) {
        Optional<Especializacion> especializacionOpt = especializacionRepo.findByIdAndEnabledTrue(idEspecializacion);

        if (especializacionOpt.isEmpty()) {
            return new BaseObjectResponse<>(404, Modulo.ESPECIALIZACION.noEncontrado(), null);
        }
        Especializacion especializacion = especializacionOpt.get();
        especializacion.setEnabled(false);
        especializacionRepo.save(especializacion);
        return new BaseObjectResponse<>(200, Modulo.ESPECIALIZACION.eliminado(), null);
    }

    private EspecializacionDetalleResponse convEspecializacionDetalle(Especializacion especializacion){
        return modelMapper.map(especializacion, EspecializacionDetalleResponse.class);
    }
    private EspecializacionResumenResponse convEspecializacionResumen(Especializacion especializacion){
        return modelMapper.map(especializacion, EspecializacionResumenResponse.class);
    }
}
