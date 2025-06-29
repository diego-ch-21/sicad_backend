package com.sicad.sicad_backend.service.impl;

import com.sicad.sicad_backend.dto.base.GenericObjectResponse;
import com.sicad.sicad_backend.dto.planDeEstudio.PlanDeEstudioCreateRequest;
import com.sicad.sicad_backend.dto.planDeEstudio.PlanDeEstudioDetalleResponse;
import com.sicad.sicad_backend.dto.planDeEstudio.PlanDeEstudioUpdateRequest;
import com.sicad.sicad_backend.model.Facultad;
import com.sicad.sicad_backend.model.PlanDeEstudio;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import com.sicad.sicad_backend.repository.interfaces.IFacultadRepo;
import com.sicad.sicad_backend.repository.interfaces.IPlanDeEstudioRepo;
import com.sicad.sicad_backend.service.base.CRUDImpl;
import com.sicad.sicad_backend.service.interfaces.IPlanDeEstudioService;
import com.sicad.sicad_backend.utils.CodigoGeneratorUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlanDeEstudioServiceImpl extends CRUDImpl<PlanDeEstudio, Integer> implements IPlanDeEstudioService {

    private final IPlanDeEstudioRepo planRepo;
    private final IFacultadRepo facultadRepo;
    private final ModelMapper modelMapper;

    @Override
    protected IGenericRepo<PlanDeEstudio, Integer> getRepo() {
        return planRepo;
    }

    public GenericObjectResponse<PlanDeEstudioDetalleResponse> registrarPlan(PlanDeEstudioCreateRequest request) {
        // 1. Verificar facultad
        Facultad facultad = facultadRepo.findById(request.getIdFacultad()).orElse(null);
        if (facultad == null) {
            return new GenericObjectResponse<>(404, "Facultad no encontrada", null);
        }

        // 2. Generar código único (suponiendo código numérico de 6 dígitos)
        Integer codigo;
        do {
            codigo = Integer.parseInt(CodigoGeneratorUtil.generarCodigoNumerico(6));
        } while (planRepo.existsByCodigo(codigo));

        // 3. Crear PlanDeEstudio
        PlanDeEstudio plan = PlanDeEstudio.builder()
                .facultad(facultad)
                .codigo(codigo)
                .nombre(request.getNombre())
                .enabled(true)
                .build();

        planRepo.save(plan);

        PlanDeEstudioDetalleResponse dto = modelMapper.map(plan, PlanDeEstudioDetalleResponse.class);
        return new GenericObjectResponse<>(201, "Plan de Estudio registrado exitosamente", dto);
    }

    public GenericObjectResponse<PlanDeEstudioDetalleResponse> actualizarPlan(Integer idPlan, PlanDeEstudioUpdateRequest request) {
        PlanDeEstudio plan = planRepo.findById(idPlan).orElse(null);
        if (plan == null) {
            return new GenericObjectResponse<>(404, "Plan de Estudio no encontrado", null);
        }

        // Actualizar facultad si viene
        if (request.getIdFacultad() != null) {
            Facultad facultad = facultadRepo.findById(request.getIdFacultad()).orElse(null);
            if (facultad == null) {
                return new GenericObjectResponse<>(404, "Facultad no encontrada", null);
            }
            plan.setFacultad(facultad);
        }

        // Actualizar nombre si viene
        if (request.getNombre() != null && !request.getNombre().isBlank()) {
            plan.setNombre(request.getNombre());
        }

        planRepo.save(plan);

        PlanDeEstudioDetalleResponse dto = modelMapper.map(plan, PlanDeEstudioDetalleResponse.class);
        return new GenericObjectResponse<>(200, "Plan de Estudio actualizado exitosamente", dto);
    }
}