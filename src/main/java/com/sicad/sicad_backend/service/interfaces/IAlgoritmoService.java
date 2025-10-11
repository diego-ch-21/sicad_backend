package com.sicad.sicad_backend.service.interfaces;

import com.sicad.sicad_backend.dto.algoritmo.AlgoritmoCreateRequest;
import com.sicad.sicad_backend.dto.algoritmo.AlgoritmoDetalleResponse;
import com.sicad.sicad_backend.dto.algoritmo.AlgoritmoUpdateRequest;
import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.model.Algoritmo;
import com.sicad.sicad_backend.service.base.ICRUD;

import java.util.List;

public interface IAlgoritmoService extends ICRUD<Algoritmo, Integer> {
    BaseListReponse<AlgoritmoDetalleResponse> listar();

    BaseObjectResponse<AlgoritmoDetalleResponse> buscar(Integer idAlgoritmo);

    BaseObjectResponse<AlgoritmoDetalleResponse> registrar(AlgoritmoCreateRequest request);

    BaseListReponse<AlgoritmoDetalleResponse> registrarAll(List<AlgoritmoCreateRequest> requests);

    BaseObjectResponse<AlgoritmoDetalleResponse> actualizar(Integer idAlgoritmo, AlgoritmoUpdateRequest request);

    BaseObjectResponse<String> eliminar(Integer idAlgoritmo);

    BaseObjectResponse<AlgoritmoDetalleResponse> asignarPrincipal(Integer idAlgoritmo);

    BaseObjectResponse<AlgoritmoDetalleResponse> buscarPrincipal();
}
