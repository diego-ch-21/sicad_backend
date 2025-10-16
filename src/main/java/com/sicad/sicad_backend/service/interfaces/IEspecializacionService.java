package com.sicad.sicad_backend.service.interfaces;

import com.sicad.sicad_backend.dto.Especializacion.EspecializacionResumenResponse;
import com.sicad.sicad_backend.dto.Especializacion.EspecializacionUpdateRequest;
import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.Especializacion.EspecializacionCreateRequest;
import com.sicad.sicad_backend.dto.Especializacion.EspecializacionDetalleResponse;
import com.sicad.sicad_backend.model.Especializacion;
import com.sicad.sicad_backend.service.base.ICRUD;

import java.util.List;

public interface IEspecializacionService extends ICRUD<Especializacion,Integer> {
    BaseListReponse<EspecializacionResumenResponse> listarPorDocente(Integer idDocente);

    BaseObjectResponse<EspecializacionDetalleResponse> buscar(Integer idEspecializacion);

    BaseObjectResponse<EspecializacionDetalleResponse> registrar(EspecializacionCreateRequest request);

    BaseListReponse<EspecializacionDetalleResponse> registrarAll(List<EspecializacionCreateRequest> requests);

    BaseObjectResponse<EspecializacionDetalleResponse> actualizar(Integer idEspecializacion, EspecializacionUpdateRequest request);

    BaseObjectResponse<String> eliminar(Integer idEspecializacion);}
