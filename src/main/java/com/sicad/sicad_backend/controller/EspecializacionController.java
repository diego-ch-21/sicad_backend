package com.sicad.sicad_backend.controller;

import com.sicad.sicad_backend.dto.Especializacion.EspecializacionCreateRequest;
import com.sicad.sicad_backend.dto.Especializacion.EspecializacionDetalleResponse;
import com.sicad.sicad_backend.dto.Especializacion.EspecializacionResumenResponse;
import com.sicad.sicad_backend.dto.Especializacion.EspecializacionUpdateRequest;
import com.sicad.sicad_backend.dto.base.BaseMessageResponse; // Importación necesaria
import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.service.interfaces.IEspecializacionService;
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
@RequestMapping("/especializacion")
@RequiredArgsConstructor
@Tag(name = "Especialización", description = "Endpoints para la gestión de Especializaciones de Docentes")
public class EspecializacionController {
    private final IEspecializacionService service;

    // --- LISTAR POR DOCENTE ---
    @Operation(
            summary = "Listar especializaciones por Docente",
            description = "Obtiene una lista de las especializaciones de un docente específico usando su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de especializaciones obtenida exitosamente"),
            @ApiResponse(responseCode = "404", description = "Docente no encontrado",
                    content = @Content(schema = @Schema(implementation = BaseMessageResponse.class)))
    })
    @GetMapping("/listar-por-docente/{idDocente}")
    public ResponseEntity<BaseListReponse<EspecializacionResumenResponse>>
    listar(@PathVariable("idDocente") Integer idDocente) throws Exception {
        BaseListReponse<EspecializacionResumenResponse> response = service.listarPorDocente(idDocente);
        return ResponseEntity.status(response.status()).body(response);
    }

    // --- BUSCAR POR ID ---
    @Operation(
            summary = "Buscar especialización por ID",
            description = "Busca y obtiene los detalles de una especialización específica usando su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Especialización encontrada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Especialización no encontrada",
                    content = @Content(schema = @Schema(implementation = BaseMessageResponse.class)))
    })
    @GetMapping("/buscar/{idEspecializacion}")
    public ResponseEntity<BaseObjectResponse<EspecializacionDetalleResponse>>
    buscar(@PathVariable("idEspecializacion") Integer id) {
        BaseObjectResponse<EspecializacionDetalleResponse> response = service.buscar(id);
        return ResponseEntity.status(response.status()).body(response);
    }

    // --- INSERTAR (Individual) ---
    @Operation(
            summary = "Registrar especialización",
            description = "Crea y registra una nueva especialización en el sistema."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Especialización registrada exitosamente")
    })
    @PostMapping("/insertar")
    public ResponseEntity<BaseObjectResponse<EspecializacionDetalleResponse>>
    registrar(@Valid @RequestBody EspecializacionCreateRequest request) {
        BaseObjectResponse<EspecializacionDetalleResponse> response = service.registrar(request);
        return ResponseEntity.status(response.status()).body(response);
    }

    // --- INSERTAR ALL (Múltiple) ---
    @Operation(
            summary = "Registrar múltiples especializaciones",
            description = "Crea y registra múltiples especializaciones en el sistema a partir de una lista."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Especializaciones registradas exitosamente")
    })
    @PostMapping("/insertar-all")
    public ResponseEntity<BaseListReponse<EspecializacionDetalleResponse>>
    registrarAll(@Valid @RequestBody List<EspecializacionCreateRequest> request) {
        BaseListReponse<EspecializacionDetalleResponse> response = service.registrarAll(request);
        return ResponseEntity.status(response.status()).body(response);
    }

    // --- ACTUALIZAR ---
    @Operation(
            summary = "Actualizar especialización",
            description = "Actualiza los datos de una especialización existente usando su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Especialización actualizada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Especialización no encontrada para actualizar",
                    content = @Content(schema = @Schema(implementation = BaseMessageResponse.class)))
    })
    @PutMapping("/actualizar/{idEspecializacion}")
    public ResponseEntity<BaseObjectResponse<EspecializacionDetalleResponse>>
    actualizar(@PathVariable("idEspecializacion") Integer id, @Valid @RequestBody EspecializacionUpdateRequest dto) {
        BaseObjectResponse<EspecializacionDetalleResponse> response = service.actualizar(id, dto);
        return ResponseEntity.status(response.status()).body(response);
    }

    // --- ELIMINAR ---
    @Operation(
            summary = "Eliminar especialización",
            description = "Elimina (lógicamente) una especialización del sistema usando su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Especialización eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Especialización no encontrada para eliminar",
                    content = @Content(schema = @Schema(implementation = BaseMessageResponse.class)))
    })
    @DeleteMapping("/eliminar/{idEspecializacion}")
    public ResponseEntity<BaseObjectResponse<String>>
    eliminar(@PathVariable("idEspecializacion") Integer id) {
        BaseObjectResponse<String> response = service.eliminar(id);
        return ResponseEntity.status(response.status()).body(response);
    }
}