package com.sicad.sicad_backend.controller;


import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.base.BaseMessageResponse; // Importación necesaria
import com.sicad.sicad_backend.dto.disponibilidad.DisponibilidadCreateRequest;
import com.sicad.sicad_backend.dto.disponibilidad.DisponibilidadDetalleResponse;
import com.sicad.sicad_backend.dto.disponibilidad.DisponibilidadResumenResponse;
import com.sicad.sicad_backend.dto.disponibilidad.DisponibilidadUpdateRequest;
import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.service.interfaces.IDisponibilidadService;
import io.swagger.v3.oas.annotations.Operation; // Importación necesaria
import io.swagger.v3.oas.annotations.media.Content; // Importación necesaria
import io.swagger.v3.oas.annotations.media.Schema; // Importación necesaria
import io.swagger.v3.oas.annotations.responses.ApiResponse; // Importación necesaria
import io.swagger.v3.oas.annotations.responses.ApiResponses; // Importación necesaria
import io.swagger.v3.oas.annotations.tags.Tag; // Importación necesaria
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/disponibilidad")
@RequiredArgsConstructor
@Tag(name = "Disponibilidad", description = "Endpoints para la gestión de Disponibilidad Horaria de Docentes")
public class DisponibilidadController {
    private final IDisponibilidadService service;

    // --- LISTAR POR CICLO ACADÉMICO Y DOCENTE ---
    @Operation(
            summary = "Listar disponibilidad por Ciclo Académico y Docente",
            description = "Obtiene la lista de disponibilidad horaria registrada para un docente específico en un ciclo académico dado."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de disponibilidad obtenida exitosamente"),
            @ApiResponse(responseCode = "404", description = "Docente o Ciclo Académico no encontrado",
                    content = @Content(schema = @Schema(implementation = BaseMessageResponse.class)))
    })
    @GetMapping("/listar-por-ciclo-academico-docente/{idCicloAcademico}/{idDocente}")
    public ResponseEntity<BaseListReponse<DisponibilidadResumenResponse>>
    listar(@PathVariable("idCicloAcademico") Integer idCicloAcademico,
           @PathVariable("idDocente") Integer idDocente) {
        BaseListReponse<DisponibilidadResumenResponse> response = service.listarPorDocenteCicloAcademico(idCicloAcademico,idDocente);
        return ResponseEntity.status(response.status()).body(response);
    }

    // --- BUSCAR POR ID ---
    @Operation(
            summary = "Buscar disponibilidad por ID",
            description = "Busca y obtiene los detalles de una disponibilidad específica usando su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Disponibilidad encontrada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Disponibilidad no encontrada",
                    content = @Content(schema = @Schema(implementation = BaseMessageResponse.class)))
    })
    @GetMapping("/buscar/{idDisponibilidad}")
    public ResponseEntity<BaseObjectResponse<DisponibilidadDetalleResponse>>
    buscar(@PathVariable("idDisponibilidad") Integer id) {
        BaseObjectResponse<DisponibilidadDetalleResponse> response = service.buscar(id);
        return ResponseEntity.status(response.status()).body(response);
    }

    // --- INSERTAR (Individual) ---
    @Operation(
            summary = "Registrar disponibilidad",
            description = "Crea y registra una nueva disponibilidad horaria en el sistema."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Disponibilidad registrada exitosamente")
    })
    @PostMapping("/insertar")
    public ResponseEntity<BaseObjectResponse<DisponibilidadDetalleResponse>>
    registrar(@Valid @RequestBody DisponibilidadCreateRequest request) {
        BaseObjectResponse<DisponibilidadDetalleResponse> response = service.registrar(request);
        return ResponseEntity.status(response.status()).body(response);
    }

    // --- INSERTAR ALL (Múltiple) ---
    @Operation(
            summary = "Registrar múltiples disponibilidades",
            description = "Crea y registra múltiples disponibilidades horarias en el sistema a partir de una lista."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Disponibilidades registradas exitosamente")
    })
    @PostMapping("/insertar-all")
    public ResponseEntity<BaseListReponse<DisponibilidadDetalleResponse>>
    registrarAll(@Valid @RequestBody List<DisponibilidadCreateRequest> request) {
        BaseListReponse<DisponibilidadDetalleResponse> response = service.registrarAll(request);
        return ResponseEntity.status(response.status()).body(response);
    }

    // --- ACTUALIZAR ---
    @Operation(
            summary = "Actualizar disponibilidad",
            description = "Actualiza los datos de una disponibilidad horaria existente usando su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Disponibilidad actualizada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Disponibilidad no encontrada para actualizar",
                    content = @Content(schema = @Schema(implementation = BaseMessageResponse.class)))
    })
    @PutMapping("/actualizar/{idDisponibilidad}")
    public ResponseEntity<BaseObjectResponse<DisponibilidadDetalleResponse>>
    actualizar(@PathVariable("idDisponibilidad") Integer id, @Valid @RequestBody DisponibilidadUpdateRequest dto) {
        BaseObjectResponse<DisponibilidadDetalleResponse> response = service.actualizar(id, dto);
        return ResponseEntity.status(response.status()).body(response);
    }

    // --- ELIMINAR ---
    @Operation(
            summary = "Eliminar disponibilidad",
            description = "Elimina (lógicamente) una disponibilidad horaria del sistema usando su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Disponibilidad eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Disponibilidad no encontrada para eliminar",
                    content = @Content(schema = @Schema(implementation = BaseMessageResponse.class)))
    })
    @DeleteMapping("/eliminar/{idDisponibilidad}")
    public ResponseEntity<BaseObjectResponse<String>>
    eliminar(@PathVariable("idDisponibilidad") Integer id) {
        BaseObjectResponse<String> response = service.eliminar(id);
        return ResponseEntity.status(response.status()).body(response);
    }
}