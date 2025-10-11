package com.sicad.sicad_backend.service.interfaces;

import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.dto.escuela.EscuelaCreateRequest;
import com.sicad.sicad_backend.dto.escuela.EscuelaDetalleResponse;
import com.sicad.sicad_backend.dto.escuela.EscuelaUpdateRequest;
import com.sicad.sicad_backend.model.Escuela;
import com.sicad.sicad_backend.service.base.ICRUD;

import java.util.List;

public interface IEscuelaService extends ICRUD<Escuela, Integer> {
    BaseListReponse<EscuelaDetalleResponse> listar();

    BaseObjectResponse<EscuelaDetalleResponse> buscar(Integer idEscuela);

    BaseObjectResponse<EscuelaDetalleResponse> registrar(EscuelaCreateRequest request);

    BaseListReponse<EscuelaDetalleResponse> registrarAll(List<EscuelaCreateRequest> requests);

    BaseObjectResponse<EscuelaDetalleResponse> actualizar(Integer idEscuela, EscuelaUpdateRequest request);

    BaseObjectResponse<String> eliminar(Integer idEscuela);
}
