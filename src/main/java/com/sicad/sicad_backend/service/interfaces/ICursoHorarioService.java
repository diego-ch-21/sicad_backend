package com.sicad.sicad_backend.service.interfaces;

import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.dto.cursoHorario.HorarioCreateRequest;
import com.sicad.sicad_backend.dto.cursoHorario.HorarioDetalleResponse;
import com.sicad.sicad_backend.dto.cursoHorario.HorarioUpdateRequest;
import com.sicad.sicad_backend.model.CursoHorario;
import com.sicad.sicad_backend.service.base.ICRUD;

import java.util.List;

public interface ICursoHorarioService extends ICRUD<CursoHorario, Integer> {
    BaseListReponse<HorarioDetalleResponse> listarPorCursoHabilitado(Integer idCurso);
    BaseObjectResponse<HorarioDetalleResponse> registrarHorario(Integer idCurso, HorarioCreateRequest request);
    BaseListReponse<HorarioDetalleResponse> registrarVariosHorario(Integer idCurso, List<HorarioCreateRequest> requests);
    BaseObjectResponse<String> eliminarHorario(Integer idCursoHorario);
    BaseObjectResponse<HorarioDetalleResponse> actualizarHorario(Integer idCursoHorario, HorarioUpdateRequest request);
}
