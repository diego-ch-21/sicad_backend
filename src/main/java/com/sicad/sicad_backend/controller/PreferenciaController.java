package com.sicad.sicad_backend.controller;

import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.preferencia.PreferenciaCreateRequest;
import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.dto.base.BaseMessageResponse; // Importación necesaria
import com.sicad.sicad_backend.dto.preferencia.PreferenciaDetalleResponse;
import com.sicad.sicad_backend.dto.preferencia.PreferenciaResumenResponse;
import com.sicad.sicad_backend.dto.preferencia.PreferenciaUpdateRequest;
import com.sicad.sicad_backend.service.interfaces.IPreferenciaService;
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
@RequestMapping("/preferencia")
@RequiredArgsConstructor
@Tag(name = "Preferencia", description = "Endpoints para la gestión de Preferencias Docentes")
public class PreferenciaController {
    private final IPreferenciaService service;

    // --- LISTAR POR CICLO ACADÉMICO Y DOCENTE ---
    @Operation(
            summary = "Listar preferencias por Ciclo Académico y Docente",
            description = "Obtiene una lista de las preferencias registradas para un docente específico en un ciclo académico dado."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de preferencias obtenida exitosamente"),
            @ApiResponse(responseCode = "404", description = "Docente o Ciclo Académico no encontrado",
                    content = @Content(schema = @Schema(implementation = BaseMessageResponse.class)))
    })
    @GetMapping("/listar-por-ciclo-academico-docente/{idCicloAcademico}/{idDocente}")
    public ResponseEntity<BaseListReponse<PreferenciaResumenResponse>>
    listar(@PathVariable("idCicloAcademico") Integer idCicloAcademico,
           @PathVariable("idDocente") Integer idDocente) {
        BaseListReponse<PreferenciaResumenResponse> response = service.listarPorDocenteCicloAcademico(idCicloAcademico,idDocente);
        return ResponseEntity.status(response.status()).body(response);
    }

    // --- BUSCAR POR ID ---
    @Operation(
            summary = "Buscar preferencia por ID",
            description = "Busca y obtiene los detalles de una preferencia específica usando su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Preferencia encontrada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Preferencia no encontrada",
                    content = @Content(schema = @Schema(implementation = BaseMessageResponse.class)))
    })
    @GetMapping("/buscar/{idPreferencia}")
    public ResponseEntity<BaseObjectResponse<PreferenciaDetalleResponse>>
    buscar(@PathVariable("idPreferencia") Integer id) {
        BaseObjectResponse<PreferenciaDetalleResponse> response = service.buscar(id);
        return ResponseEntity.status(response.status()).body(response);
    }

    // --- INSERTAR (Individual) ---
    @Operation(
            summary = "Registrar preferencia",
            description = "Crea y registra una nueva preferencia en el sistema."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Preferencia registrada exitosamente")
    })
    @PostMapping("/insertar")
    public ResponseEntity<BaseObjectResponse<PreferenciaDetalleResponse>>
    registrar(@Valid @RequestBody PreferenciaCreateRequest request) {
        BaseObjectResponse<PreferenciaDetalleResponse> response = service.registrar(request);
        return ResponseEntity.status(response.status()).body(response);
    }

    // --- INSERTAR ALL (Múltiple) ---
    @Operation(
            summary = "Registrar múltiples preferencias",
            description = "Crea y registra múltiples preferencias en el sistema a partir de una lista."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Preferencias registradas exitosamente")
    })
    @PostMapping("/insertar-all")
    public ResponseEntity<BaseListReponse<PreferenciaDetalleResponse>>
    registrarAll(@Valid @RequestBody List<PreferenciaCreateRequest> request) {
        BaseListReponse<PreferenciaDetalleResponse> response = service.registrarAll(request);
        return ResponseEntity.status(response.status()).body(response);
    }

    // --- ACTUALIZAR ---
    @Operation(
            summary = "Actualizar preferencia",
            description = "Actualiza los datos de una preferencia existente usando su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Preferencia actualizada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Preferencia no encontrada para actualizar",
                    content = @Content(schema = @Schema(implementation = BaseMessageResponse.class)))
    })
    @PutMapping("/actualizar/{idPreferencia}")
    public ResponseEntity<BaseObjectResponse<PreferenciaDetalleResponse>>
    actualizar(@PathVariable("idPreferencia") Integer id, @Valid @RequestBody PreferenciaUpdateRequest dto) {
        BaseObjectResponse<PreferenciaDetalleResponse> response = service.actualizar(id, dto);
        return ResponseEntity.status(response.status()).body(response);
    }

    // --- ELIMINAR ---
    @Operation(
            summary = "Eliminar preferencia",
            description = "Elimina (lógicamente) una preferencia del sistema usando su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Preferencia eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Preferencia no encontrada para eliminar",
                    content = @Content(schema = @Schema(implementation = BaseMessageResponse.class)))
    })
    @DeleteMapping("/eliminar/{idPreferencia}")
    public ResponseEntity<BaseObjectResponse<String>>
    eliminar(@PathVariable("idPreferencia") Integer id) {
        BaseObjectResponse<String> response = service.eliminar(id);
        return ResponseEntity.status(response.status()).body(response);
    }
}