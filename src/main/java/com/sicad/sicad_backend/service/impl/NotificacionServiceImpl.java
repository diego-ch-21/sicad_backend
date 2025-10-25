package com.sicad.sicad_backend.service.impl;

import com.sicad.sicad_backend.Enum.Modulo;
import com.sicad.sicad_backend.Enum.TipoNotificacion;
import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.cicloAcademico.CicloAcademicoDetalleResponse;
import com.sicad.sicad_backend.dto.notificacion.NotificacionConteoResponse;
import com.sicad.sicad_backend.dto.notificacion.NotificacionDetalleResponse;
import com.sicad.sicad_backend.dto.notificacion.NotificacionResumenResponse;
import com.sicad.sicad_backend.model.*;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import com.sicad.sicad_backend.repository.interfaces.*;
import com.sicad.sicad_backend.service.base.CRUDImpl;
import com.sicad.sicad_backend.service.interfaces.INoficacionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificacionServiceImpl
        extends CRUDImpl<Notificacion, Integer>
        implements INoficacionService {

    private final INotificacionRepo notificacionRepo;
    private final IDocenteRepo docenteRepo;
    private final IDepartamentoAcademicoRepo depaAcaRepo;
    private final ModelMapper modelMapper;
    private final ICicloAcademicoRepo cicloRepo;
    private final IDepartamentoAcademicoRepo departamentoAcademicoRepo;


    @Override
    protected IGenericRepo<Notificacion, Integer> getRepo() {
        return notificacionRepo;
    }

    @Override
    public BaseObjectResponse<String> notificarADocentesNuevoCicloAcademico(Integer idCicloAcademico) {
        Optional<CicloAcademico> cicloOpt = cicloRepo.findByIdAndEnabledTrue(idCicloAcademico);

        if (cicloOpt.isEmpty()) {
            return new BaseObjectResponse<>(404, Modulo.CICLO_ACADEMICO.noEncontrado(), null);
        }
        CicloAcademico ciclo = cicloOpt.get();

        try {



            List<Docente> docentes = docenteRepo.findByEnabledTrue();

            if (docentes.isEmpty()) {
                return new BaseObjectResponse<>(404, "No hay docentes activos para notificar.", null);
            }

            String mensaje = "Se ha creado un nuevo ciclo académico: " + ciclo.getNombre();

            List<Notificacion> notificaciones = docentes.stream()
                    .map(docente -> Notificacion.builder()
                            .mensaje(mensaje)
                            .tipo(TipoNotificacion.INFO)
                            .usuarioDestino(docente.getUsuario())
                            .usuarioOrigen(null)
                            .leida(false)
                            .enabled(true)
                            .build())
                    .toList();

            notificacionRepo.saveAll(notificaciones);

            String successMsg = String.format("Se crearon %d notificaciones para informar a los docente del nuevo ciclo academico", notificaciones.size());
            return new BaseObjectResponse<>(200, successMsg, null);

        } catch (Exception e) {
            String errorMsg = String.format(
                    "Error al crear notificaciones para el nuevo ciclo académico: %s. Detalle: %s",
                    (ciclo != null ? ciclo.getNombre() : "desconocido"),
                    e.getMessage()
            );
            return new BaseObjectResponse<>(500, errorMsg, null);
        }
    }

    /**
     * Crea una notificación para el Director informando que un docente ha completado
     * su registro de preferencias y disponibilidad.
     */
    @Override
    public BaseObjectResponse<String> notificarADepartamentoAcademicoSobrePrefDisCompleto(Integer idDocente) {
        Optional<Docente> docenteOpt = docenteRepo.findByIdAndEnabledTrue(idDocente);

        if (docenteOpt.isEmpty()) {
            return new BaseObjectResponse<>(404, Modulo.DOCENTE.noEncontrado(), null);
        }
        Docente docente = docenteOpt.get();
        try {


            List<DepartamentoAcademico> departamentosAcademicos = depaAcaRepo.findByEnabledTrue();

            if (departamentosAcademicos.isEmpty()) {
                return new BaseObjectResponse<>(404, "No hay departamentos académicos activos para notificar.", null);
            }

            String nombreCompletoDocente = docente.getUsuario().getNombre() + " " + docente.getUsuario().getApellido();
            String mensaje = String.format(
                    "El docente %s ha completado su registro de preferencias y disponibilidad.",
                    nombreCompletoDocente
            );

            List<Notificacion> notificaciones = departamentosAcademicos.stream()
                    .map(departamento -> Notificacion.builder()
                            .mensaje(mensaje)
                            .tipo(TipoNotificacion.INFO)
                            .usuarioDestino(departamento.getUsuario())
                            .leida(false)
                            .enabled(true)
                            .build())
                    .toList();

            notificacionRepo.saveAll(notificaciones);

            log.info("Notificación creada para los departamentos académicos sobre el docente: {}", nombreCompletoDocente);

            return new BaseObjectResponse<>(200, "Notificación enviada correctamente a los departamentos académicos.", mensaje);

        } catch (Exception e) {
            log.error("Error al intentar notificar a los departamentos académicos sobre el docente ID {}: {}", docente.getIdDocente(), e.getMessage(), e);
            return new BaseObjectResponse<>(500, "Error al enviar la notificación: " + e.getMessage(), null);
        }
    }

    @Override
    public BaseObjectResponse<String> notificarADocenteSobreAprobacionPrefDis(Integer idDocente) {
        Optional<Docente> docenteOpt = docenteRepo.findByIdAndEnabledTrue(idDocente);

        if (docenteOpt.isEmpty()) {
            return new BaseObjectResponse<>(404, Modulo.DOCENTE.noEncontrado(), null);
        }
        Docente docente = docenteOpt.get();

        try {
            if (docente.getUsuario() == null) {
                return new BaseObjectResponse<>(400, "El docente o su usuario asociado no existen.", null);
            }

            String nombreCompletoDocente = docente.getUsuario().getNombre() + " " + docente.getUsuario().getApellido();
            String nombreDepartamento = "el departamento académico"; // Texto genérico

            String mensaje = String.format(
                    "El %s ha aprobado su registro de preferencias y disponibilidad. ¡Felicidades, %s!",
                    nombreDepartamento,
                    nombreCompletoDocente
            );

            Notificacion notificacion = Notificacion.builder()
                    .mensaje(mensaje)
                    .tipo(TipoNotificacion.INFO)
                    .usuarioDestino(docente.getUsuario())
                    .leida(false)
                    .enabled(true)
                    .build();

            notificacionRepo.save(notificacion);

            log.info("Notificación enviada al docente {} sobre la aprobación de su preferencia y disponibilidad.", nombreCompletoDocente);

            return new BaseObjectResponse<>(200, "Notificación enviada correctamente al docente.", mensaje);

        } catch (Exception e) {
            log.error("Error al intentar notificar al docente ID {}: {}", docente.getIdDocente(), e.getMessage(), e);
            return new BaseObjectResponse<>(500, "Error al enviar la notificación: " + e.getMessage(), null);
        }
    }


    @Override
    public BaseListReponse<NotificacionResumenResponse> listarNotificacionesPorUsuario(Integer idUsuario) {
        try {
            // Buscar todas las notificaciones asociadas al usuario
            List<Notificacion> notificaciones = notificacionRepo
                    .findByUsuarioDestinoIdAndEnabledTrueOrderByCreatedAtDesc(idUsuario);

            if (notificaciones.isEmpty()) {
                return new BaseListReponse<>(404, "No se encontraron notificaciones para este usuario.", List.of());
            }

            // Usar el método de conversión con ModelMapper
            List<NotificacionResumenResponse> notificacionesResponse = notificaciones.stream()
                    .map(this::convNotifiacionResumen)
                    .toList();

            log.info("Listadas {} notificaciones para el usuario ID {}", notificaciones.size(), idUsuario);

            return new BaseListReponse<>(200, "Notificaciones obtenidas correctamente.", notificacionesResponse);

        } catch (Exception e) {
            log.error("Error al listar notificaciones del usuario ID {}: {}", idUsuario, e.getMessage(), e);
            return new BaseListReponse<>(500, "Error al obtener las notificaciones: " + e.getMessage(), List.of());
        }
    }

    @Override
    public BaseObjectResponse<String> marcarNotificacionComoLeida(Integer idNotificacion) {
        try {
            // Buscar la notificación activa
            Optional<Notificacion> notificacionOpt = notificacionRepo.findByIdAndEnabledTrue(idNotificacion);

            if (notificacionOpt.isEmpty()) {
                return new BaseObjectResponse<>(404, "No se encontró la notificación.", null);
            }

            Notificacion notificacion = notificacionOpt.get();

            // Si ya está leída, no es necesario actualizar
            if (Boolean.TRUE.equals(notificacion.getLeida())) {
                return new BaseObjectResponse<>(200, "La notificación ya estaba marcada como leída.", null);
            }

            // Cambiar el estado a leída
            notificacion.setLeida(true);
            notificacionRepo.save(notificacion);

            log.info("Notificación ID {} marcada como leída correctamente.", idNotificacion);
            return new BaseObjectResponse<>(200, "Notificación marcada como leída correctamente.", null);

        } catch (Exception e) {
            log.error("Error al marcar notificación ID {} como leída: {}", idNotificacion, e.getMessage(), e);
            return new BaseObjectResponse<>(500, "Error al marcar la notificación como leída: " + e.getMessage(), null);
        }
    }
    @Override
    public BaseObjectResponse<NotificacionConteoResponse> contarNotificacionesPorEstado(Integer idUsuario) {
        try {
            // Buscar todas las notificaciones activas del usuario
            List<Notificacion> notificaciones = notificacionRepo.findByUsuarioDestinoIdAndEnabledTrueOrderByCreatedAtDesc(idUsuario);

            if (notificaciones.isEmpty()) {
                return new BaseObjectResponse<>(404, "No se encontraron notificaciones para este usuario.", null);
            }

            // Contar las notificaciones leídas y no leídas
            int leidas = (int) notificaciones.stream().filter(Notificacion::getLeida).count();
            int noLeidas = (int) notificaciones.stream().filter(n -> !n.getLeida()).count();
            int total = notificaciones.size();

            // Crear objeto de respuesta
            NotificacionConteoResponse conteoResponse = new NotificacionConteoResponse(leidas, noLeidas, total);

            log.info("Conteo de notificaciones para usuario ID {} -> Leídas: {}, No leídas: {}, Total: {}",
                    idUsuario, leidas, noLeidas, total);

            return new BaseObjectResponse<>(200, "Conteo de notificaciones obtenido correctamente.", conteoResponse);

        } catch (Exception e) {
            log.error("Error al contabilizar notificaciones del usuario ID {}: {}", idUsuario, e.getMessage(), e);
            return new BaseObjectResponse<>(500, "Error al contar las notificaciones: " + e.getMessage(), null);
        }
    }



    private NotificacionResumenResponse convNotifiacionResumen(Notificacion obj) {
        return modelMapper.map(obj, NotificacionResumenResponse.class);
    }

}
