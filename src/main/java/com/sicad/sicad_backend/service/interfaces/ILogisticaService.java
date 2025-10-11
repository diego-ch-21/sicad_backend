package com.sicad.sicad_backend.service.interfaces;

import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.logistica.LogisticaCreateRequest;
import com.sicad.sicad_backend.dto.logistica.LogisticaDetalleResponse;
import com.sicad.sicad_backend.dto.logistica.LogisticaUpdateRequest;
import com.sicad.sicad_backend.model.Logistica;
import com.sicad.sicad_backend.service.base.ICRUD;

import java.util.List;

public interface ILogisticaService extends ICRUD<Logistica, Integer> {
    BaseListReponse<LogisticaDetalleResponse> listar();

    BaseObjectResponse<LogisticaDetalleResponse> buscar(Integer idLogistica);

    BaseObjectResponse<LogisticaDetalleResponse> registrar(LogisticaCreateRequest request);

    BaseListReponse<LogisticaDetalleResponse> registrarAll(List<LogisticaCreateRequest> requests);

    BaseObjectResponse<LogisticaDetalleResponse> actualizar(Integer idLogistica, LogisticaUpdateRequest request);

    BaseObjectResponse<String> eliminar(Integer idLogistica);

}
