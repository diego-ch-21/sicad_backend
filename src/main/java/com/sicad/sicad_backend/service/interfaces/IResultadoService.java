package com.sicad.sicad_backend.service.interfaces;

import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.escuela.EscuelaDetalleResponse;
import com.sicad.sicad_backend.dto.resultado.ResultadoDetalleResponse;

public interface IResultadoService {
    BaseObjectResponse<ResultadoDetalleResponse> buscar(Integer idResultado);
    BaseObjectResponse<ResultadoDetalleResponse> buscarPorCarga(Integer idCarga);

}
