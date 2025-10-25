package com.sicad.sicad_backend.service.interfaces;

import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.notificacion.NotificacionConteoResponse;
import com.sicad.sicad_backend.dto.notificacion.NotificacionDetalleResponse;
import com.sicad.sicad_backend.dto.notificacion.NotificacionResumenResponse;
import com.sicad.sicad_backend.model.CicloAcademico;
import com.sicad.sicad_backend.model.DepartamentoAcademico;
import com.sicad.sicad_backend.model.Docente;
import com.sicad.sicad_backend.model.Notificacion;
import com.sicad.sicad_backend.service.base.ICRUD;

public interface INoficacionService extends ICRUD<Notificacion, Integer> {
    BaseObjectResponse<String> notificarADocentesNuevoCicloAcademico(Integer idCicloAcademico);
    BaseObjectResponse<String> notificarADepartamentoAcademicoSobrePrefDisCompleto(Integer idDocente);
    BaseObjectResponse<String> notificarADocenteSobreAprobacionPrefDis(Integer idDocente);
    BaseListReponse<NotificacionResumenResponse> listarNotificacionesPorUsuario(Integer idUsuario);
    BaseObjectResponse<String> marcarNotificacionComoLeida(Integer idNotificacion);
    BaseObjectResponse<NotificacionConteoResponse> contarNotificacionesPorEstado(Integer idUsuario);
}
