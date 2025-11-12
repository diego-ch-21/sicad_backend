package com.sicad.sicad_backend.controller;

import com.sicad.sicad_backend.dto.aula.AulaCreateRequest;
import com.sicad.sicad_backend.dto.aula.AulaDetalleResponse;
import com.sicad.sicad_backend.dto.aula.AulaUpdateRequest;
import com.sicad.sicad_backend.dto.base.BaseMessageResponse; // Importación necesaria
import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.service.interfaces.IAulaService;
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
@RequestMapping("/aula")
@RequiredArgsConstructor
@Tag(name = "Aula", description = "Endpoints para la gestión de Aulas")
public class AulaController {
    private final IAulaService service;

    // --- LISTAR ---
    @Operation(
            summary = "Listar aulas",
            description = "Obtiene una lista de todas las aulas registradas en el sistema."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de aulas obtenida exitosamente")
    })
    @GetMapping("/listar")
    public ResponseEntity<BaseListReponse<AulaDetalleResponse>>
    listar() throws Exception {
        BaseListReponse<AulaDetalleResponse> response = service.listar();
        return ResponseEntity.status(response.status()).body(response);
    }

    // --- BUSCAR POR ID ---
    @Operation(
            summary = "Buscar aula por ID",
            description = "Busca y obtiene los detalles de un aula específica usando su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Aula encontrada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Aula no encontrada",
                    content = @Content(schema = @Schema(implementation = BaseMessageResponse.class)))
    })
    @GetMapping("/buscar/{idAula}")
    public ResponseEntity<BaseObjectResponse<AulaDetalleResponse>>
    buscar(@PathVariable("idAula") Integer id) {
        BaseObjectResponse<AulaDetalleResponse> response = service.buscar(id);
        return ResponseEntity.status(response.status()).body(response);
    }

    // --- INSERTAR (Individual) ---
    @Operation(
            summary = "Registrar aula",
            description = "Crea y registra una nueva aula en el sistema."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Aula registrada exitosamente")
    })
    @PostMapping("/insertar")
    public ResponseEntity<BaseObjectResponse<AulaDetalleResponse>>
    registrar(@Valid @RequestBody AulaCreateRequest request) {
        BaseObjectResponse<AulaDetalleResponse> response = service.registrar(request);
        return ResponseEntity.status(response.status()).body(response);
    }

    // --- INSERTAR ALL (Múltiple) ---
    @Operation(
            summary = "Registrar múltiples aulas",
            description = "Crea y registra múltiples aulas en el sistema a partir de una lista."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Aulas registradas exitosamente")
    })
    @PostMapping("/insertar-all")
    public ResponseEntity<BaseListReponse<AulaDetalleResponse>>
    registrarAll(@Valid @RequestBody List<AulaCreateRequest> request) {
        BaseListReponse<AulaDetalleResponse> response = service.registrarAll(request);
        return ResponseEntity.status(response.status()).body(response);
    }

    // --- ACTUALIZAR ---
    @Operation(
            summary = "Actualizar aula",
            description = "Actualiza los datos de un aula existente usando su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Aula actualizada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Aula no encontrada para actualizar",
                    content = @Content(schema = @Schema(implementation = BaseMessageResponse.class)))
    })
    @PutMapping("/actualizar/{idAula}")
    public ResponseEntity<BaseObjectResponse<AulaDetalleResponse>>
    actualizar(@PathVariable("idAula") Integer id, @Valid @RequestBody AulaUpdateRequest dto) {
        BaseObjectResponse<AulaDetalleResponse> response = service.actualizar(id, dto);
        return ResponseEntity.status(response.status()).body(response);
    }

    // --- ELIMINAR ---
    @Operation(
            summary = "Eliminar aula",
            description = "Elimina (lógicamente) un aula del sistema usando su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Aula eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Aula no encontrada para eliminar",
                    content = @Content(schema = @Schema(implementation = BaseMessageResponse.class)))
    })
    @DeleteMapping("/eliminar/{idAula}")
    public ResponseEntity<BaseObjectResponse<String>>
    eliminar(@PathVariable("idAula") Integer id) {
        BaseObjectResponse<String> response = service.eliminar(id);
        return ResponseEntity.status(response.status()).body(response);
    }
}