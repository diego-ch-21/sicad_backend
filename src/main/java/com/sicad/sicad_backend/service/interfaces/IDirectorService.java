package com.sicad.sicad_backend.service.interfaces;

import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.director.DirectorCreateRequest;
import com.sicad.sicad_backend.dto.director.DirectorDetalleResponse;
import com.sicad.sicad_backend.dto.director.DirectorUpdateRequest;
import com.sicad.sicad_backend.model.Director;
import com.sicad.sicad_backend.service.base.ICRUD;

import java.util.List;

public interface IDirectorService extends ICRUD<Director, Integer> {

    BaseListReponse<DirectorDetalleResponse> listar();

    BaseObjectResponse<DirectorDetalleResponse> buscar(Integer idDirector);

    BaseObjectResponse<DirectorDetalleResponse> registrar(DirectorCreateRequest request);

    //BaseListReponse<DirectorDetalleResponse> registrarAll(List<DirectorCreateRequest> requests);

    BaseObjectResponse<DirectorDetalleResponse> actualizar(Integer idDirector, DirectorUpdateRequest request);

    BaseObjectResponse<String> eliminar(Integer idDirector);
}
