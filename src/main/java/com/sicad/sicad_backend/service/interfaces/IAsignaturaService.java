package com.sicad.sicad_backend.service.interfaces;

import com.sicad.sicad_backend.dto.asignatura.AsignaturaCreateRequest;
import com.sicad.sicad_backend.dto.asignatura.AsignaturaDetalleResponse;
import com.sicad.sicad_backend.dto.asignatura.AsignaturaUpdateRequest;
import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.model.Asignatura;
import com.sicad.sicad_backend.service.base.ICRUD;

import java.util.List;

public interface IAsignaturaService extends ICRUD<Asignatura, Integer> {
    BaseListReponse<AsignaturaDetalleResponse> listar();

    BaseObjectResponse<AsignaturaDetalleResponse> buscar(Integer idAsignatura);

    BaseObjectResponse<AsignaturaDetalleResponse> registrar(AsignaturaCreateRequest request);

    BaseListReponse<AsignaturaDetalleResponse> registrarAll(List<AsignaturaCreateRequest> requests);

    BaseObjectResponse<AsignaturaDetalleResponse> actualizar(Integer idAsignatura, AsignaturaUpdateRequest request);

    BaseObjectResponse<String> eliminar(Integer idAsignatura);
}
