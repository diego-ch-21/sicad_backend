package com.sicad.sicad_backend.service.impl;

import com.sicad.sicad_backend.Enum.Modulo;
import com.sicad.sicad_backend.dto.Especializacion.EspecializacionCreateRequest;
import com.sicad.sicad_backend.dto.Especializacion.EspecializacionDetalleResponse;
import com.sicad.sicad_backend.dto.Especializacion.EspecializacionResumenResponse;
import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.dto.preferencia.PreferenciaCreateRequest;
import com.sicad.sicad_backend.dto.preferencia.PreferenciaDetalleResponse;
import com.sicad.sicad_backend.dto.preferencia.PreferenciaResumenResponse;
import com.sicad.sicad_backend.dto.preferencia.PreferenciaUpdateRequest;
import com.sicad.sicad_backend.model.*;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import com.sicad.sicad_backend.repository.interfaces.IAsignaturaRepo;
import com.sicad.sicad_backend.repository.interfaces.ICicloAcademicoRepo;
import com.sicad.sicad_backend.repository.interfaces.IDocenteRepo;
import com.sicad.sicad_backend.repository.interfaces.IPreferenciaRepo;
import com.sicad.sicad_backend.service.base.CRUDImpl;
import com.sicad.sicad_backend.service.interfaces.IPreferenciaService;
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
public class PreferenciaServiceImpl
        extends CRUDImpl<Preferencia, Integer>
        implements IPreferenciaService {

    private final IPreferenciaRepo preferenciaRepo;
    private final IDocenteRepo docenteRepo;
    private final IAsignaturaRepo asignaturaRepo;
    private final ICicloAcademicoRepo cicloAcademicoRepo;
    private final ModelMapper modelMapper;

    @Override
    protected IGenericRepo<Preferencia, Integer> getRepo() {
        return preferenciaRepo;
    }

    @Override
    public BaseListReponse<PreferenciaResumenResponse> listarPorDocenteCicloAcademico(Integer idDocente, Integer idCicloAcademico) {
        List<PreferenciaResumenResponse> response = preferenciaRepo.findByEnabledTrueDocenteCicloAcademico(idDocente,idCicloAcademico)
                .stream()
                .map(this::convPreferenciaResumen)
                .toList();

        return new BaseListReponse<>(200, Modulo.PREFERENCIA.listado(), response);
    }

    @Override
    public BaseObjectResponse<PreferenciaDetalleResponse> buscar(Integer idPreferencia) {
        Optional<Preferencia> preferencianOpt = preferenciaRepo.findByIdAndEnabledTrue(idPreferencia);
        if (preferencianOpt.isEmpty()) {
            return new BaseObjectResponse<>(404, Modulo.PREFERENCIA.noEncontrado(), null);
        }
        return new BaseObjectResponse<>(200, Modulo.PREFERENCIA.encontrado(), convPreferenciaDetalle(preferencianOpt.get()));
    }

    @Override
    public BaseObjectResponse<PreferenciaDetalleResponse> registrar(PreferenciaCreateRequest request) {
        Optional<Docente> docentenOpt = docenteRepo.findByIdAndEnabledTrue(request.getIdDocente());
        if (docentenOpt.isEmpty()) {
            return new BaseObjectResponse<>(404, Modulo.DOCENTE.noEncontrado(), null);
        }
        Optional<Asignatura> asignaturaOpt = asignaturaRepo.findByIdAndEnabledTrue(request.getIdAsignatura());
        if (asignaturaOpt.isEmpty()) {
            return new BaseObjectResponse<>(404, Modulo.ASIGNATURA.noEncontrado(), null);
        }
        Optional<CicloAcademico> cicloAcademicoOpt = cicloAcademicoRepo.findByIdAndEnabledTrue(request.getIdCicloAcademico());
        if (cicloAcademicoOpt.isEmpty()) {
            return new BaseObjectResponse<>(404, Modulo.CICLO_ACADEMICO.noEncontrado(), null);
        }
        Boolean isExiste = preferenciaRepo.isRestriccionPreferencia(request.getIdDocente(), request.getIdAsignatura(), request.getIdCicloAcademico());
        if(isExiste) {
            List<Modulo> modulos = new ArrayList<>();
            modulos.add(Modulo.DOCENTE);
            modulos.add(Modulo.ASIGNATURA);
            modulos.add(Modulo.CICLO_ACADEMICO);
            return new BaseObjectResponse<>(409, Modulo.PREFERENCIA.noCumple(modulos), null);
        }
        Preferencia preferencia = new Preferencia().builder()
                .docente(docentenOpt.get())
                .asignatura(asignaturaOpt.get())
                .cicloAcademico(cicloAcademicoOpt.get())
                .enabled(true)
                .build();
        preferenciaRepo.save(preferencia);
        return new BaseObjectResponse<>(200,Modulo.PREFERENCIA.registrado(), convPreferenciaDetalle(preferencia));
    }

    @Override
    public BaseListReponse<PreferenciaDetalleResponse> registrarAll(List<PreferenciaCreateRequest> requests) {
        List<PreferenciaDetalleResponse> registrados = new ArrayList<>();
        int errorCount = 0;

        for (PreferenciaCreateRequest request : requests) {
            try {
                BaseObjectResponse<PreferenciaDetalleResponse> response = registrar(request);
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
        return new BaseListReponse<>(201,Modulo.PREFERENCIA.resumenAllRegistro(registrados.size(), errorCount),registrados);
    }

    @Override
    public BaseObjectResponse<PreferenciaDetalleResponse> actualizar(Integer idPreferencia, PreferenciaUpdateRequest request) {
        Optional<Preferencia> preferencianOpt = preferenciaRepo.findByIdAndEnabledTrue(idPreferencia);
        if (preferencianOpt.isEmpty()) {
            return new BaseObjectResponse<>(404, Modulo.PREFERENCIA.noEncontrado(), null);
        }
        Preferencia preferencia = preferencianOpt.get();
        if(request.getIdDocente() != null) {
            Optional<Docente> docentenOpt = docenteRepo.findByIdAndEnabledTrue(request.getIdDocente());
            if (docentenOpt.isEmpty()) {
                return new BaseObjectResponse<>(404, Modulo.DOCENTE.noEncontrado(), null);
            } else {
                preferencia.setDocente(docentenOpt.get());
            }
        }
        if(request.getIdAsignatura() != null) {
            Optional<Asignatura> asignaturaOpt = asignaturaRepo.findByIdAndEnabledTrue(request.getIdAsignatura());
            if (asignaturaOpt.isEmpty()) {
                return new BaseObjectResponse<>(404, Modulo.ASIGNATURA.noEncontrado(), null);
            } else {
                preferencia.setAsignatura(asignaturaOpt.get());
            }
        }
        if(request.getIdCicloAcademico() != null) {
            Optional<CicloAcademico> cicloAcademicoOpt = cicloAcademicoRepo.findByIdAndEnabledTrue(request.getIdCicloAcademico());
            if (cicloAcademicoOpt.isEmpty()) {
                return new BaseObjectResponse<>(404, Modulo.CICLO_ACADEMICO.noEncontrado(), null);
            } else {
                preferencia.setCicloAcademico(cicloAcademicoOpt.get());
            }
        }
        Boolean isExiste = preferenciaRepo.isRestriccionPreferencia(
                preferencia.getDocente().getIdDocente(),
                preferencia.getAsignatura().getIdAsignatura(),
                preferencia.getCicloAcademico().getIdCicloAcademico());
        if(isExiste) {
            List<Modulo> modulos = new ArrayList<>();
            modulos.add(Modulo.DOCENTE);
            modulos.add(Modulo.ASIGNATURA);
            modulos.add(Modulo.CICLO_ACADEMICO);
            return new BaseObjectResponse<>(409, Modulo.PREFERENCIA.noCumple(modulos), null);
        }
        preferenciaRepo.save(preferencia);
        return new BaseObjectResponse<>(200, Modulo.PREFERENCIA.actualizado(), convPreferenciaDetalle(preferencia));

    }

    @Override
    public BaseObjectResponse<String> eliminar(Integer idPreferencia) {
        Optional<Preferencia> preferencianOpt = preferenciaRepo.findByIdAndEnabledTrue(idPreferencia);
        if (preferencianOpt.isEmpty()) {
            return new BaseObjectResponse<>(404, Modulo.PREFERENCIA.noEncontrado(), null);
        }
        Preferencia preferencia = preferencianOpt.get();
        preferencia.setEnabled(false);
        preferenciaRepo.save(preferencia);
        return new BaseObjectResponse<>(200, Modulo.PREFERENCIA.eliminado(), null);
    }

    private PreferenciaDetalleResponse convPreferenciaDetalle(Preferencia preferencia){
        return modelMapper.map(preferencia, PreferenciaDetalleResponse.class);
    }
    private PreferenciaResumenResponse convPreferenciaResumen(Preferencia preferencia){
        return modelMapper.map(preferencia, PreferenciaResumenResponse.class);
    }
}
