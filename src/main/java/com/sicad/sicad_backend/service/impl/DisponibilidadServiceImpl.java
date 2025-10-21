package com.sicad.sicad_backend.service.impl;

import com.sicad.sicad_backend.Enum.Modulo;
import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.dto.disponibilidad.DisponibilidadCreateRequest;
import com.sicad.sicad_backend.dto.disponibilidad.DisponibilidadDetalleResponse;
import com.sicad.sicad_backend.dto.disponibilidad.DisponibilidadResumenResponse;
import com.sicad.sicad_backend.dto.disponibilidad.DisponibilidadUpdateRequest;
// Imports... (manteniendo los otros DTOs)
import com.sicad.sicad_backend.model.CicloAcademico;
import com.sicad.sicad_backend.model.Disponibilidad;
import com.sicad.sicad_backend.model.Docente;
// ... otros models
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import com.sicad.sicad_backend.repository.interfaces.ICicloAcademicoRepo;
import com.sicad.sicad_backend.repository.interfaces.IDisponibilidadRepo;
import com.sicad.sicad_backend.repository.interfaces.IDocenteRepo;
import com.sicad.sicad_backend.service.base.CRUDImpl;
import com.sicad.sicad_backend.service.interfaces.IDisponibilidadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

// import java.sql.Time; // <-- CAMBIO: Eliminado
import java.time.LocalTime; // <-- CAMBIO: Asegurado que esté presente
import java.time.format.DateTimeParseException; // <-- CAMBIO: Para el try-catch
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.sicad.sicad_backend.Enum.Message.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class DisponibilidadServiceImpl
        extends CRUDImpl<Disponibilidad, Integer>
        implements IDisponibilidadService {

    private final IDisponibilidadRepo disponibilidadRepo;
    private final ICicloAcademicoRepo cicloAcademicoRepo;
    private final IDocenteRepo docenteRepo;
    private final ModelMapper modelMapper;

    @Override
    protected IGenericRepo<Disponibilidad, Integer> getRepo() {
        return disponibilidadRepo;
    }

    // --- MÉTODOS ANTIGUOS (Corregidos) ---

    public BaseObjectResponse<DisponibilidadDetalleResponse> registrarDisponibilidad(DisponibilidadCreateRequest request) {
        Docente docente = docenteRepo.findById(request.getIdDocente()).orElse(null);
        if (docente == null) {
            return new BaseObjectResponse<>(404, "Docente no encontrado", null);
        }

        CicloAcademico cicloAcademico = cicloAcademicoRepo.findById(request.getIdCicloAcademico()).orElse(null);
        if (cicloAcademico == null) {
            return new BaseObjectResponse<>(404, "Ciclo academico no encontrada", null);
        }

        try {
            // CAMBIO: Se usa LocalTime.parse() y se quita el "+ :00" (el DTO ya lo tiene)
            LocalTime horaInicio = LocalTime.parse(request.getHoraInicio());
            LocalTime horaFin = LocalTime.parse(request.getHoraFin());

            // CAMBIO: Se usa isAfter() para comparar LocalTime
            if (!horaFin.isAfter(horaInicio)) {
                return new BaseObjectResponse<>(400, "La hora de fin debe ser posterior a la hora de inicio", null);
            }

            Disponibilidad disponibilidad = Disponibilidad.builder()
                    .docente(docente)
                    .cicloAcademico(cicloAcademico)
                    .diaSemana(request.getDiaSemana())
                    .horaInicio(horaInicio) // <-- Ya es LocalTime
                    .horaFin(horaFin)       // <-- Ya es LocalTime
                    .enabled(true)
                    .build();

            disponibilidadRepo.save(disponibilidad);

            DisponibilidadDetalleResponse response = modelMapper.map(disponibilidad, DisponibilidadDetalleResponse.class);
            return new BaseObjectResponse<>(201, "Disponibilidad registrada exitosamente", response);

        } catch (DateTimeParseException e) { // CAMBIO: Captura la excepción correcta
            return new BaseObjectResponse<>(400, "Formato de hora inválido (debe ser HH:00:00)", null);
        }
    }
    public BaseListReponse<DisponibilidadDetalleResponse> registrarVariosDisponiblidadAll(List<DisponibilidadCreateRequest> requests){
        List<DisponibilidadDetalleResponse> registrados = new ArrayList<>();
        int errorCount = 0;

        for(DisponibilidadCreateRequest request: requests){
            BaseObjectResponse<DisponibilidadDetalleResponse> response = registrarDisponibilidad(request); // Llama al método corregido
            if(response.status() == 201 || response.data() != null){
                registrados.add(response.data());
            } else {
                errorCount++;
            }
        }
        String mensaje = String.format("disponibilidad registrados: %d. Fallidos: %d.", registrados.size(), errorCount);
        return new BaseListReponse<>(200, mensaje, registrados);
    }

    public BaseObjectResponse<DisponibilidadDetalleResponse> actualizarDisponibilidad(Integer id, DisponibilidadUpdateRequest request) {
        Disponibilidad disponibilidad = disponibilidadRepo.findById(id).orElse(null);
        if (disponibilidad == null) {
            return new BaseObjectResponse<>(404, "Disponibilidad no encontrada", null);
        }

        // ... (lógica de setDocente y setCicloAcademico sin cambios) ...

        try {
            // CAMBIO: Se usa LocalTime.parse() y se obtienen los valores actuales (que ya son LocalTime)
            LocalTime horaInicio = (request.getHoraInicio() != null)
                    ? LocalTime.parse(request.getHoraInicio())
                    : disponibilidad.getHoraInicio();

            LocalTime horaFin = (request.getHoraFin() != null)
                    ? LocalTime.parse(request.getHoraFin())
                    : disponibilidad.getHoraFin();

            if (request.getHoraInicio() != null || request.getHoraFin() != null) {
                // CAMBIO: Se usa isAfter()
                if (!horaFin.isAfter(horaInicio)) {
                    return new BaseObjectResponse<>(400, "La hora de fin debe ser posterior a la hora de inicio", null);
                }
                disponibilidad.setHoraInicio(horaInicio);
                disponibilidad.setHoraFin(horaFin);
            }

        } catch (DateTimeParseException e) { // CAMBIO: Captura la excepción correcta
            return new BaseObjectResponse<>(400, "Formato de hora inválido (debe ser HH:00:00)", null);
        }

        disponibilidadRepo.save(disponibilidad);

        DisponibilidadDetalleResponse response = modelMapper.map(disponibilidad, DisponibilidadDetalleResponse.class);
        return new BaseObjectResponse<>(200, "Disponibilidad actualizada exitosamente", response);
    }
    public BaseObjectResponse<List<DisponibilidadResumenResponse>> listarDisponibilidadDocente(Integer idDocente, Integer idCicloAcademico) {
        // ... (lógica sin cambios) ...
        if (!docenteRepo.existsByIdDocenteAndEnabledTrue(idDocente)) {
            return new BaseObjectResponse<>(400, "Docente no encontrado", null);
        }
        if (!cicloAcademicoRepo.existsByIdCicloAcademico(idCicloAcademico)) {
            return new BaseObjectResponse<>(400, "ciclo academico no encontrada", null);
        }

        List<Disponibilidad> disponibilidades = disponibilidadRepo.buscarPorDocenteYCicloAcademico(idDocente, idCicloAcademico);

        List<DisponibilidadResumenResponse> listaDTO = disponibilidades.stream()
                .map(disponibilidad -> modelMapper.map(disponibilidad, DisponibilidadResumenResponse.class))
                .collect(Collectors.toList());

        return new BaseObjectResponse<>(200, "Lista obtenida correctamente", listaDTO);
    }
    public BaseObjectResponse<String> eliminarDisponibilidad(Integer idDisponibilidad) {
        // ... (lógica sin cambios) ...
        if (idDisponibilidad == null) {
            return new BaseObjectResponse<>(400, "idDisponibilidad no proporcionado", null);
        }
        Disponibilidad disponibilidad = disponibilidadRepo.findById(idDisponibilidad).orElse(null);
        if (disponibilidad == null) {
            return new BaseObjectResponse<>(404, "Disponibilidad  no encontrado", null);
        }
        disponibilidad.setEnabled(false);
        disponibilidadRepo.save(disponibilidad);
        return new BaseObjectResponse<>(200, "se elimino la disponibilidad exitosamente", null);
    }


    // --- MÉTODOS DE INTERFAZ (Corregidos) ---

    private DisponibilidadDetalleResponse convDisponibilidadDetalle(Disponibilidad disponibilidad) {
        return modelMapper.map(disponibilidad, DisponibilidadDetalleResponse.class);
    }
    private DisponibilidadResumenResponse convDisponibilidadResumen(Disponibilidad disponibilidad) {
        return modelMapper.map(disponibilidad, DisponibilidadResumenResponse.class);
    }

    @Override
    public BaseListReponse<DisponibilidadResumenResponse> listarPorDocenteCicloAcademico(Integer idCicloAcademico, Integer idDocente) {
        List<DisponibilidadResumenResponse> response = disponibilidadRepo.findByEnabledTrueDocenteCicloAcademico(idCicloAcademico,idDocente)
                .stream()
                .map(this::convDisponibilidadResumen)
                .toList();

        return new BaseListReponse<>(200, Modulo.PREFERENCIA.listado(), response);
    }

    @Override
    public BaseObjectResponse<DisponibilidadDetalleResponse> buscar(Integer idDisponibilidad) {
        Optional<Disponibilidad> disponibilidadOpt = disponibilidadRepo.findByIdAndEnabledTrue(idDisponibilidad);
        if (disponibilidadOpt.isEmpty()) {
            return new BaseObjectResponse<>(404, Modulo.DISPONIBILIDAD.noEncontrado(), null);
        }
        return new BaseObjectResponse<>(200, Modulo.DISPONIBILIDAD.encontrado(), convDisponibilidadDetalle(disponibilidadOpt.get()) );
    }

    @Override
    public BaseObjectResponse<DisponibilidadDetalleResponse> registrar(DisponibilidadCreateRequest request) {
        // === 1. Validar existencia de docente y ciclo ===
        Optional<Docente> docenteOpt = docenteRepo.findByIdAndEnabledTrue(request.getIdDocente());
        if (docenteOpt.isEmpty()) {
            return new BaseObjectResponse<>(404, Modulo.DOCENTE.noEncontrado(), null);
        }

        Optional<CicloAcademico> cicloOpt = cicloAcademicoRepo.findByIdAndEnabledTrue(request.getIdCicloAcademico());
        if (cicloOpt.isEmpty()) {
            return new BaseObjectResponse<>(404, Modulo.CICLO_ACADEMICO.noEncontrado(), null);
        }

        // === 2. Convertir y validar horas ===
        // CAMBIO: Se usa LocalTime.parse()
        LocalTime horaInicio = LocalTime.parse(request.getHoraInicio());
        LocalTime horaFin = LocalTime.parse(request.getHoraFin());

        // CAMBIO: Se usa isAfter()
        if (!horaFin.isAfter(horaInicio)) {
            return new BaseObjectResponse<>(400, HORA_FIN_INICIO_ERROR.toString(), null);
        }

        // === 3. Verificar cruce horario ===
        List<Disponibilidad> listaDisponibilidadDelDocente =
                disponibilidadRepo.findByEnabledTrueDocenteCicloAcademico(
                        docenteOpt.get().getIdDocente(),
                        cicloOpt.get().getIdCicloAcademico());

        // CAMBIO: La llamada a hayCruceHorario ahora envía LocalTime
        if (hayCruceHorario(listaDisponibilidadDelDocente, request.getDiaSemana(), horaInicio, horaFin)) {
            return new BaseObjectResponse<>(409, CRUCE_DE_HORARIO_DISPONIBILIDAD.toString(), null);
        }

        // === 4. Crear y guardar nueva disponibilidad ===
        Disponibilidad disponibilidad = Disponibilidad.builder()
                .docente(docenteOpt.get())
                .cicloAcademico(cicloOpt.get())
                .diaSemana(request.getDiaSemana())
                .horaInicio(horaInicio) // <-- Ya es LocalTime
                .horaFin(horaFin)       // <-- Ya es LocalTime
                .enabled(true)
                .build();

        disponibilidadRepo.save(disponibilidad);

        // === 5. Responder ===
        return new BaseObjectResponse<>(201,
                Modulo.DISPONIBILIDAD.registrado(),
                convDisponibilidadDetalle(disponibilidad));
    }

    // CAMBIO: La firma del método ahora acepta LocalTime
    // Verifica si el nuevo horario se cruza o se repite con otro existente
    private boolean hayCruceHorario(List<Disponibilidad> lista, String nuevoDia, LocalTime nuevoInicio, LocalTime nuevoFin) {
        return lista.stream().anyMatch(d ->
                // Mismo día
                d.getDiaSemana().equalsIgnoreCase(nuevoDia) && (

                        // ❌ Caso 1: Repetición exacta
                        (d.getHoraInicio().equals(nuevoInicio) && d.getHoraFin().equals(nuevoFin)) ||

                                // ❌ Caso 2: Cruce de horarios
                                (nuevoInicio.isBefore(d.getHoraFin()) && nuevoFin.isAfter(d.getHoraInicio()))
                )
        );
    }



    @Override
    public BaseListReponse<DisponibilidadDetalleResponse> registrarAll(List<DisponibilidadCreateRequest> requests) {
        List<DisponibilidadDetalleResponse> registrados = new ArrayList<>();
        int errorCount = 0;

        for (DisponibilidadCreateRequest request : requests) {
            try {
                BaseObjectResponse<DisponibilidadDetalleResponse> response = registrar(request); // Llama al método registrar corregido
                if (response.status() == 201 && response.data() != null) {
                    registrados.add(response.data());
                } else {
                    errorCount++;
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                errorCount++;
            }
        }
        return new BaseListReponse<>(201,Modulo.DISPONIBILIDAD.resumenAllRegistro(registrados.size(), errorCount),registrados);
    }


    @Override
    public BaseObjectResponse<DisponibilidadDetalleResponse> actualizar(Integer idDisponibilidad, DisponibilidadUpdateRequest request) {
        // === 1. Validar existencia ===
        Optional<Disponibilidad> disponibilidadOpt = disponibilidadRepo.findByIdAndEnabledTrue(idDisponibilidad);
        if (disponibilidadOpt.isEmpty()) {
            return new BaseObjectResponse<>(404, Modulo.DISPONIBILIDAD.noEncontrado(), null);
        }

        Disponibilidad disponibilidad = disponibilidadOpt.get();

        // ... (Lógica de validación de Docente y Ciclo sin cambios) ...

        // === 3. Validar grupo de horario: los 3 o ninguno ===
        boolean envioDia = request.getDiaSemana() != null;
        boolean envioInicio = request.getHoraInicio() != null;
        boolean envioFin = request.getHoraFin() != null;

        int cantidadCamposHorario = (envioDia ? 1 : 0) + (envioInicio ? 1 : 0) + (envioFin ? 1 : 0);

        if (cantidadCamposHorario > 0 && cantidadCamposHorario < 3) {
            return new BaseObjectResponse<>(400,
                    "Si actualiza el horario, debe enviar diaSemana, horaInicio y horaFin juntos",
                    null);
        }

        // === 4. Validar y aplicar nuevo horario ===
        if (cantidadCamposHorario == 3) {
            // CAMBIO: Se usa LocalTime.parse()
            LocalTime nuevaHoraInicio = LocalTime.parse(request.getHoraInicio());
            LocalTime nuevaHoraFin = LocalTime.parse(request.getHoraFin());

            // CAMBIO: Se usa isAfter()
            if (!nuevaHoraFin.isAfter(nuevaHoraInicio)) {
                return new BaseObjectResponse<>(400, HORA_FIN_INICIO_ERROR.toString(), null);
            }

            List<Disponibilidad> listaDisponibilidadDelDocente =
                    disponibilidadRepo.findByEnabledTrueDocenteCicloAcademico(
                            disponibilidad.getDocente().getIdDocente(),
                            disponibilidad.getCicloAcademico().getIdCicloAcademico());

            // Ignorar la misma disponibilidad
            listaDisponibilidadDelDocente.removeIf(d -> d.getIdDisponibilidad().equals(idDisponibilidad));

            // CAMBIO: La llamada ahora envía LocalTime
            if (hayCruceHorario(listaDisponibilidadDelDocente, request.getDiaSemana(), nuevaHoraInicio, nuevaHoraFin)) {
                return new BaseObjectResponse<>(409, CRUCE_DE_HORARIO_DISPONIBILIDAD.toString(), null);
            }

            disponibilidad.setDiaSemana(request.getDiaSemana());
            disponibilidad.setHoraInicio(nuevaHoraInicio);
            disponibilidad.setHoraFin(nuevaHoraFin);
        }

        // === 5. Guardar cambios ===
        disponibilidadRepo.save(disponibilidad);

        return new BaseObjectResponse<>(200,
                Modulo.DISPONIBILIDAD.actualizado(),
                convDisponibilidadDetalle(disponibilidad));
    }



    @Override
    public BaseObjectResponse<String> eliminar(Integer idDisponibilidad) {
        Optional<Disponibilidad> disponibilidadOpt = disponibilidadRepo.findByIdAndEnabledTrue(idDisponibilidad);
        if (disponibilidadOpt.isEmpty()) {
            return new BaseObjectResponse<>(404, Modulo.DISPONIBILIDAD.noEncontrado(), null);
        }
        Disponibilidad disponibilidad = disponibilidadOpt.get();
        disponibilidad.setEnabled(false);
        disponibilidadRepo.save(disponibilidad);
        // CAMBIO: El mensaje debe ser "eliminado"
        return new BaseObjectResponse<>(200, Modulo.DISPONIBILIDAD.eliminado(), null);
    }
}