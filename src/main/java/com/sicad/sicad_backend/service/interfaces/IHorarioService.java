package com.sicad.sicad_backend.service.interfaces;

import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.dto.Horario.HorarioCreateRequest;
import com.sicad.sicad_backend.dto.Horario.HorarioDetalleResponse;
import com.sicad.sicad_backend.dto.Horario.HorarioUpdateRequest;
import com.sicad.sicad_backend.model.Horario;
import com.sicad.sicad_backend.service.base.ICRUD;

import java.util.List;

public interface IHorarioService extends ICRUD<Horario, Integer> {
    BaseListReponse<HorarioDetalleResponse> listarPorCurso(Integer idCurso);

    BaseObjectResponse<HorarioDetalleResponse> buscar(Integer idHorario);

    BaseObjectResponse<HorarioDetalleResponse> registrarPorCurso(Integer idCurso, HorarioCreateRequest request);

    BaseListReponse<HorarioDetalleResponse> registrarAllPorCurso(Integer idCurso, List<HorarioCreateRequest> requests);

    BaseObjectResponse<HorarioDetalleResponse> actualizar(Integer idHorario, HorarioUpdateRequest request);

    BaseObjectResponse<String> eliminar(Integer idHorario);
}
