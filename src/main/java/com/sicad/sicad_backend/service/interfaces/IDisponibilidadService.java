package com.sicad.sicad_backend.service.interfaces;


import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.disponibilidad.DisponibilidadCreateRequest;
import com.sicad.sicad_backend.dto.disponibilidad.DisponibilidadDetalleResponse;
import com.sicad.sicad_backend.dto.disponibilidad.DisponibilidadResumenResponse;
import com.sicad.sicad_backend.dto.disponibilidad.DisponibilidadUpdateRequest;
import com.sicad.sicad_backend.model.Disponibilidad;
import com.sicad.sicad_backend.service.base.ICRUD;

import java.util.List;

public interface IDisponibilidadService extends ICRUD<Disponibilidad, Integer> {
    BaseListReponse<DisponibilidadResumenResponse> listarPorDocenteCicloAcademico(Integer idDocente, Integer idCicloAcademico);

    BaseObjectResponse<DisponibilidadDetalleResponse> buscar(Integer idDisponibilidad);

    BaseObjectResponse<DisponibilidadDetalleResponse> registrar(DisponibilidadCreateRequest request);

    BaseListReponse<DisponibilidadDetalleResponse> registrarAll(List<DisponibilidadCreateRequest> requests);

    BaseObjectResponse<DisponibilidadDetalleResponse> actualizar(Integer idDisponibilidad, DisponibilidadUpdateRequest request);

    BaseObjectResponse<String> eliminar(Integer idDisponibilidad);

}
