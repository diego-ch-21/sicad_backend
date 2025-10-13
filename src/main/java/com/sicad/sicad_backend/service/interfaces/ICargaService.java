package com.sicad.sicad_backend.service.interfaces;

import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.carga.CargaDetalleResponse;
import com.sicad.sicad_backend.model.Carga;
import com.sicad.sicad_backend.service.base.ICRUD;
import java.util.List;

public interface ICargaService extends ICRUD<Carga, Integer> {

    BaseListReponse<CargaDetalleResponse> listarPorCicloAcademico(Integer idCicloAcademico);

    BaseObjectResponse<CargaDetalleResponse> buscar(Integer idCarga);

    BaseObjectResponse<String> eliminar(Integer idCarga);

    BaseObjectResponse<CargaDetalleResponse> asignarPrincipal(Integer idCicloAcademico,Integer idCarga);

    BaseObjectResponse<CargaDetalleResponse> buscarPrincipal(Integer idCicloAcademico);
}
