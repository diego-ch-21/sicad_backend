package com.sicad.sicad_backend.service.interfaces;

import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.preferencia.PreferenciaCreateRequest;
import com.sicad.sicad_backend.dto.preferencia.PreferenciaDetalleResponse;
import com.sicad.sicad_backend.dto.preferencia.PreferenciaResumenResponse;
import com.sicad.sicad_backend.dto.preferencia.PreferenciaUpdateRequest;
import com.sicad.sicad_backend.model.Preferencia;
import com.sicad.sicad_backend.service.base.ICRUD;

import java.util.List;

public interface IPreferenciaService extends ICRUD<Preferencia, Integer> {
    BaseListReponse<PreferenciaResumenResponse> listarPorDocenteCicloAcademico(Integer idCicloAcademico,Integer idDocente);

    BaseObjectResponse<PreferenciaDetalleResponse> buscar(Integer idPreferencia);

    BaseObjectResponse<PreferenciaDetalleResponse> registrar(PreferenciaCreateRequest request);

    BaseListReponse<PreferenciaDetalleResponse> registrarAll(List<PreferenciaCreateRequest> requests);

    BaseObjectResponse<PreferenciaDetalleResponse> actualizar(Integer idPreferencia, PreferenciaUpdateRequest request);

    BaseObjectResponse<String> eliminar(Integer idPreferencia);
}
