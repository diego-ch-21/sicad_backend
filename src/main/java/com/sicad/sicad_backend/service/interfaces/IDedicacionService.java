package com.sicad.sicad_backend.service.interfaces;

import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.dedicacion.DedicacionCreateRequest;
import com.sicad.sicad_backend.dto.dedicacion.DedicacionDetalleResponse;
import com.sicad.sicad_backend.dto.dedicacion.DedicacionUpdateRequest;
import com.sicad.sicad_backend.model.Dedicacion;
import com.sicad.sicad_backend.service.base.ICRUD;

import java.util.List;

public interface IDedicacionService extends ICRUD<Dedicacion, Integer> {
    BaseListReponse<DedicacionDetalleResponse> listar();

    BaseObjectResponse<DedicacionDetalleResponse> buscar(Integer idDedicacion);

    BaseObjectResponse<DedicacionDetalleResponse> buscarPorDocente(Integer idDocente);

    BaseObjectResponse<DedicacionDetalleResponse> registrar(DedicacionCreateRequest request);

    BaseListReponse<DedicacionDetalleResponse> registrarAll(List<DedicacionCreateRequest> requests);

    BaseObjectResponse<DedicacionDetalleResponse> actualizar(Integer idDedicacion, DedicacionUpdateRequest request);

    BaseObjectResponse<String> eliminar(Integer idDedicacion);
}
