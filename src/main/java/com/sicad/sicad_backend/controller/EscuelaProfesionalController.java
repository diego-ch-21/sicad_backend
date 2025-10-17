package com.sicad.sicad_backend.controller;

import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.dto.base.BaseMessageResponse; // Importación necesaria
import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.escuelaProfesional.EscuelaProfesionalCreateRequest;
import com.sicad.sicad_backend.dto.escuelaProfesional.EscuelaProfesionalDetalleResponse;
import com.sicad.sicad_backend.dto.escuelaProfesional.EscuelaProfesionalUpdateRequest;
import com.sicad.sicad_backend.service.interfaces.IEscuelaProfesionalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/escuela-profesional")
@RequiredArgsConstructor
@Tag(name = "Escuela Profesional", description = "Endpoints para la gestión de Escuelas Profesionales")
public class EscuelaProfesionalController {

    private final IEscuelaProfesionalService service;

    // --- LISTAR ---
    @Operation(
            summary = "Listar Escuelas Profesionales",
            description = "Obtiene una lista de todas las Escuelas Profesionales registradas en el sistema."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de escuelas profesionales obtenida exitosamente")
    })
    @GetMapping("/listar")
    public ResponseEntity<BaseListReponse<EscuelaProfesionalDetalleResponse>>
    listar() throws Exception {
        BaseListReponse<EscuelaProfesionalDetalleResponse> response = service.listar();
        // Se usa response.status() para consistencia
        return ResponseEntity.status(response.status()).body(response);
    }

    // --- BUSCAR POR ID ---
    @Operation(
            summary = "Buscar Escuela Profesional por ID",
            description = "Busca y obtiene los detalles de una Escuela Profesional específica usando su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Escuela Profesional encontrada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Escuela Profesional no encontrada",
                    content = @Content(schema = @Schema(implementation = BaseMessageResponse.class)))
    })
    @GetMapping("/buscar/{idEscuelaProfesional}")
    public ResponseEntity<BaseObjectResponse<EscuelaProfesionalDetalleResponse>>
    buscar(@PathVariable("idEscuelaProfesional") Integer id) {
        BaseObjectResponse<EscuelaProfesionalDetalleResponse> response = service.buscar(id);
        // Se usa response.status() para consistencia
        return ResponseEntity.status(response.status()).body(response);
    }

    // --- INSERTAR ---
    @Operation(
            summary = "Registrar Escuela Profesional",
            description = "Crea y registra una nueva Escuela Profesional en el sistema."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Escuela Profesional registrada exitosamente")
    })
    @PostMapping("/insertar")
    public ResponseEntity<BaseObjectResponse<EscuelaProfesionalDetalleResponse>>
    registrar(@Valid @RequestBody EscuelaProfesionalCreateRequest request) {
        BaseObjectResponse<EscuelaProfesionalDetalleResponse> response = service.registrar(request);
        return ResponseEntity.status(response.status()).body(response);
    }

    // --- ACTUALIZAR ---
    @Operation(
            summary = "Actualizar Escuela Profesional",
            description = "Actualiza los datos de una Escuela Profesional existente usando su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Escuela Profesional actualizada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Escuela Profesional no encontrada para actualizar",
                    content = @Content(schema = @Schema(implementation = BaseMessageResponse.class)))
    })
    @PutMapping("/actualizar/{idEscuelaProfesional}")
    public ResponseEntity<BaseObjectResponse<EscuelaProfesionalDetalleResponse>>
    actualizar(@PathVariable("idEscuelaProfesional") Integer id, @Valid @RequestBody EscuelaProfesionalUpdateRequest dto) {
        BaseObjectResponse<EscuelaProfesionalDetalleResponse> response = service.actualizar(id, dto);
        return ResponseEntity.status(response.status()).body(response);
    }

    // --- ELIMINAR ---
    @Operation(
            summary = "Eliminar Escuela Profesional",
            description = "Elimina (lógicamente) una Escuela Profesional del sistema usando su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Escuela Profesional eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Escuela Profesional no encontrada para eliminar",
                    content = @Content(schema = @Schema(implementation = BaseMessageResponse.class)))
    })
    @DeleteMapping("/eliminar/{idEscuelaProfesional}")
    public ResponseEntity<BaseObjectResponse<String>>
    eliminar(@PathVariable("idEscuelaProfesional") Integer id) {
        BaseObjectResponse<String> response = service.eliminar(id);
        return ResponseEntity.status(response.status()).body(response);
    }
}