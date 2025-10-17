package com.sicad.sicad_backend.controller;

import com.sicad.sicad_backend.dto.algoritmo.AlgoritmoCreateRequest;
import com.sicad.sicad_backend.dto.algoritmo.AlgoritmoDetalleResponse;
import com.sicad.sicad_backend.dto.algoritmo.AlgoritmoUpdateRequest;
import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.dto.base.BaseMessageResponse; // Importación necesaria
import com.sicad.sicad_backend.service.interfaces.IAlgoritmoService;
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
@RequestMapping("/algoritmo")
@RequiredArgsConstructor
@Tag(name = "Algoritmo", description = "Endpoints para la gestión y selección de Algoritmos")
public class AlgoritmoController{
    private final IAlgoritmoService service;

    // --- LISTAR ---
    @Operation(
            summary = "Listar algoritmos",
            description = "Obtiene una lista de todos los algoritmos disponibles en el sistema."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de algoritmos obtenida exitosamente")
    })
    @GetMapping("/listar")
    public ResponseEntity<BaseListReponse<AlgoritmoDetalleResponse>>
    listar() throws Exception {
        BaseListReponse<AlgoritmoDetalleResponse> response = service.listar();
        // Usar response.status() para consistencia
        return ResponseEntity.status(response.status()).body(response);
    }

    // --- BUSCAR POR ID ---
    @Operation(
            summary = "Buscar algoritmo por ID",
            description = "Busca y obtiene los detalles de un algoritmo específico usando su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Algoritmo encontrado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Algoritmo no encontrado",
                    content = @Content(schema = @Schema(implementation = BaseMessageResponse.class)))
    })
    @GetMapping("/buscar/{idAlgoritmo}")
    public ResponseEntity<BaseObjectResponse<AlgoritmoDetalleResponse>>
    buscar(@PathVariable("idAlgoritmo") Integer id) {
        BaseObjectResponse<AlgoritmoDetalleResponse> response = service.buscar(id);
        // Usar response.status() para consistencia
        return ResponseEntity.status(response.status()).body(response);
    }

    // --- INSERTAR (Individual) ---
    @Operation(
            summary = "Registrar algoritmo",
            description = "Crea y registra un nuevo algoritmo en el sistema."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Algoritmo registrado exitosamente")
    })
    @PostMapping("/insertar")
    public ResponseEntity<BaseObjectResponse<AlgoritmoDetalleResponse>>
    registrar(@Valid @RequestBody AlgoritmoCreateRequest request) {
        BaseObjectResponse<AlgoritmoDetalleResponse> response = service.registrar(request);
        return ResponseEntity.status(response.status()).body(response);
    }

    // --- INSERTAR ALL (Múltiple) ---
    @Operation(
            summary = "Registrar múltiples algoritmos",
            description = "Crea y registra múltiples algoritmos en el sistema a partir de una lista."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Algoritmos registrados exitosamente")
    })
    @PostMapping("/insertar-all")
    public ResponseEntity<BaseListReponse<AlgoritmoDetalleResponse>>
    registrarAll(@Valid @RequestBody List<AlgoritmoCreateRequest> request) {
        BaseListReponse<AlgoritmoDetalleResponse> response = service.registrarAll(request);
        return ResponseEntity.status(response.status()).body(response);
    }

    // --- ACTUALIZAR ---
    @Operation(
            summary = "Actualizar algoritmo",
            description = "Actualiza los datos de un algoritmo existente usando su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Algoritmo actualizado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Algoritmo no encontrado para actualizar",
                    content = @Content(schema = @Schema(implementation = BaseMessageResponse.class)))
    })
    @PutMapping("/actualizar/{idAlgoritmo}")
    public ResponseEntity<BaseObjectResponse<AlgoritmoDetalleResponse>>
    actualizar(@PathVariable("idAlgoritmo") Integer id, @Valid @RequestBody AlgoritmoUpdateRequest dto) {
        BaseObjectResponse<AlgoritmoDetalleResponse> response = service.actualizar(id, dto);
        return ResponseEntity.status(response.status()).body(response);
    }

    // --- ELIMINAR ---
    @Operation(
            summary = "Eliminar algoritmo",
            description = "Elimina (lógicamente) un algoritmo del sistema usando su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Algoritmo eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Algoritmo no encontrado para eliminar",
                    content = @Content(schema = @Schema(implementation = BaseMessageResponse.class)))
    })
    @DeleteMapping("/eliminar/{idAlgoritmo}")
    public ResponseEntity<BaseObjectResponse<String>>
    eliminar(@PathVariable("idAlgoritmo") Integer id) {
        BaseObjectResponse<String> response = service.eliminar(id);
        return ResponseEntity.status(response.status()).body(response);
    }

    // --- ASIGNAR PRINCIPAL ---
    @Operation(
            summary = "Asignar algoritmo principal",
            description = "Marca un algoritmo específico como el algoritmo principal del sistema usando su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Algoritmo asignado como principal exitosamente"),
            @ApiResponse(responseCode = "404", description = "Algoritmo no encontrado para asignar como principal",
                    content = @Content(schema = @Schema(implementation = BaseMessageResponse.class)))
    })
    @PutMapping("/principal-asignar/{idAlgoritmo}")
    public ResponseEntity<BaseObjectResponse<AlgoritmoDetalleResponse>>
    asignarPrincipal(@PathVariable("idAlgoritmo") Integer id) {
        BaseObjectResponse<AlgoritmoDetalleResponse> response = service.asignarPrincipal(id);
        // Usar response.status() para consistencia
        return ResponseEntity.status(response.status()).body(response);
    }

    // --- BUSCAR PRINCIPAL ---
    @Operation(
            summary = "Buscar algoritmo principal",
            description = "Obtiene los detalles del algoritmo que está marcado como principal en el sistema."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Algoritmo principal encontrado exitosamente"),
            @ApiResponse(responseCode = "404", description = "No hay un algoritmo principal asignado",
                    content = @Content(schema = @Schema(implementation = BaseMessageResponse.class)))
    })
    @GetMapping("/principal-buscar")
    public ResponseEntity<BaseObjectResponse<AlgoritmoDetalleResponse>>
    buscarPrincipal() {
        BaseObjectResponse<AlgoritmoDetalleResponse> response = service.buscarPrincipal();
        // Usar response.status() para consistencia
        return ResponseEntity.status(response.status()).body(response);
    }
}