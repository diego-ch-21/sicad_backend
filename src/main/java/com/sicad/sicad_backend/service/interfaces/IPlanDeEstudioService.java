package com.sicad.sicad_backend.service.interfaces;

import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.planDeEstudio.PlanDeEstudioCreateRequest;
import com.sicad.sicad_backend.dto.planDeEstudio.PlanDeEstudioDetalleResponse;
import com.sicad.sicad_backend.dto.planDeEstudio.PlanDeEstudioUpdateRequest;
import com.sicad.sicad_backend.model.PlanDeEstudio;
import com.sicad.sicad_backend.service.base.ICRUD;

import java.util.List;

public interface IPlanDeEstudioService extends ICRUD<PlanDeEstudio, Integer> {
    BaseListReponse<PlanDeEstudioDetalleResponse> listar();

    BaseObjectResponse<PlanDeEstudioDetalleResponse> buscar(Integer idPlanDeEstudio);

    BaseObjectResponse<PlanDeEstudioDetalleResponse> registrar(PlanDeEstudioCreateRequest request);

    BaseListReponse<PlanDeEstudioDetalleResponse> registrarAll(List<PlanDeEstudioCreateRequest> requests);

    BaseObjectResponse<PlanDeEstudioDetalleResponse> actualizar(Integer idPlanDeEstudio, PlanDeEstudioUpdateRequest request);

    BaseObjectResponse<String> eliminar(Integer idPlanDeEstudio);

}
