package com.sicad.sicad_backend.service.impl;

import com.sicad.sicad_backend.Enum.Modulo;
import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.dto.escuela.EscuelaCreateRequest;
import com.sicad.sicad_backend.dto.escuela.EscuelaDetalleResponse;
import com.sicad.sicad_backend.dto.planDeEstudio.PlanDeEstudioCreateRequest;
import com.sicad.sicad_backend.dto.planDeEstudio.PlanDeEstudioDetalleResponse;
import com.sicad.sicad_backend.dto.planDeEstudio.PlanDeEstudioUpdateRequest;
import com.sicad.sicad_backend.model.Escuela;
import com.sicad.sicad_backend.model.PlanDeEstudio;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import com.sicad.sicad_backend.repository.interfaces.IPlanDeEstudioRepo;
import com.sicad.sicad_backend.service.base.CRUDImpl;
import com.sicad.sicad_backend.service.interfaces.IPlanDeEstudioService;
import com.sicad.sicad_backend.utils.CodigoGeneratorUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlanDeEstudioServiceImpl
        extends CRUDImpl<PlanDeEstudio, Integer>
        implements IPlanDeEstudioService {

    private final IPlanDeEstudioRepo planRepo;
    private final ModelMapper modelMapper;

    @Override
    protected IGenericRepo<PlanDeEstudio, Integer> getRepo() {
        return planRepo;
    }


    @Override
    public BaseListReponse<PlanDeEstudioDetalleResponse> listar() {
        List<PlanDeEstudioDetalleResponse> lista = planRepo.findByEnabledTrue()
                .stream()
                .map(this::convPlanDeEstudioDetalle)
                .toList();

        return new BaseListReponse<>(200, Modulo.PLAN_DE_ESTUDIO.listado(), lista);
    }

    @Override
    public BaseObjectResponse<PlanDeEstudioDetalleResponse> buscar(Integer idPlanDeEstudio) {
        Optional<PlanDeEstudio> planOpt = planRepo.findByIdAndEnabledTrue(idPlanDeEstudio);

        if (planOpt.isEmpty()) {
            return new BaseObjectResponse<>(404, Modulo.PLAN_DE_ESTUDIO.noEncontrado(), null);
        }

        return new BaseObjectResponse<>(200, Modulo.PLAN_DE_ESTUDIO.encontrado(), convPlanDeEstudioDetalle(planOpt.get()));    }

    @Override
    public BaseObjectResponse<PlanDeEstudioDetalleResponse> registrar(PlanDeEstudioCreateRequest request) {

        PlanDeEstudio plan = PlanDeEstudio.builder()
                .nombre(request.getNombre())
                .enabled(true)
                .build();

        planRepo.save(plan);
        return new BaseObjectResponse<>(201, Modulo.PLAN_DE_ESTUDIO.registrado(), convPlanDeEstudioDetalle(plan));

    }

    @Override
    public BaseListReponse<PlanDeEstudioDetalleResponse> registrarAll(List<PlanDeEstudioCreateRequest> requests) {
        List<PlanDeEstudioDetalleResponse> registrados = new ArrayList<>();
        int errorCount = 0;

        for (PlanDeEstudioCreateRequest request : requests) {
            try {
                BaseObjectResponse<PlanDeEstudioDetalleResponse> response = registrar(request);
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
        return new BaseListReponse<>(201,Modulo.PLAN_DE_ESTUDIO.resumenAllRegistro(registrados.size(), errorCount),registrados);
    }

    @Override
    public BaseObjectResponse<PlanDeEstudioDetalleResponse> actualizar(Integer idPlanDeEstudio, PlanDeEstudioUpdateRequest request) {
        Optional<PlanDeEstudio> planOpt = planRepo.findByIdAndEnabledTrue(idPlanDeEstudio);

        if (planOpt.isEmpty()) {
            return new BaseObjectResponse<>(404, Modulo.PLAN_DE_ESTUDIO.noEncontrado(), null);
        }
        PlanDeEstudio plan = planOpt.get();

        if (request.getNombre() != null && !request.getNombre().isBlank()) {
            plan.setNombre(request.getNombre());
        }

        planRepo.save(plan);
        return new BaseObjectResponse<>(200, Modulo.PLAN_DE_ESTUDIO.actualizado(), convPlanDeEstudioDetalle(plan));
    }

    @Override
    public BaseObjectResponse<String> eliminar(Integer idPlanDeEstudio) {
        Optional<PlanDeEstudio> planOpt = planRepo.findByIdAndEnabledTrue(idPlanDeEstudio);

        if (planOpt.isEmpty()) {
            return new BaseObjectResponse<>(404, Modulo.PLAN_DE_ESTUDIO.noEncontrado(), null);
        }
        PlanDeEstudio plan = planOpt.get();
        plan.setEnabled(false);
        planRepo.save(plan);
        return new BaseObjectResponse<>(200, Modulo.PLAN_DE_ESTUDIO.eliminado(), null);
    }

    private PlanDeEstudioDetalleResponse convPlanDeEstudioDetalle(PlanDeEstudio obj) {
        return modelMapper.map(obj, PlanDeEstudioDetalleResponse.class);
    }

}