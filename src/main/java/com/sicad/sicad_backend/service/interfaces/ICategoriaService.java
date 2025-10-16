package com.sicad.sicad_backend.service.interfaces;

import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.categoria.CategoriaCreateRequest;
import com.sicad.sicad_backend.dto.categoria.CategoriaDetalleResponse;
import com.sicad.sicad_backend.dto.categoria.CategoriaUpdateRequest;
import com.sicad.sicad_backend.model.Categoria;
import com.sicad.sicad_backend.service.base.ICRUD;

import java.util.List;

public interface ICategoriaService extends ICRUD<Categoria, Integer> {
    BaseListReponse<CategoriaDetalleResponse> listar();

    BaseObjectResponse<CategoriaDetalleResponse> buscar(Integer idCategoria);

    BaseObjectResponse<CategoriaDetalleResponse> buscarPorDocente(Integer idDocente);

    BaseObjectResponse<CategoriaDetalleResponse> registrar(CategoriaCreateRequest request);

    BaseListReponse<CategoriaDetalleResponse> registrarAll(List<CategoriaCreateRequest> requests);

    BaseObjectResponse<CategoriaDetalleResponse> actualizar(Integer idCategoria, CategoriaUpdateRequest request);

    BaseObjectResponse<String> eliminar(Integer idCategoria);
}
