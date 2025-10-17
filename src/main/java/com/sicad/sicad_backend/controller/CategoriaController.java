package com.sicad.sicad_backend.controller;

import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.base.BaseMessageResponse; // Importación necesaria
import com.sicad.sicad_backend.dto.categoria.CategoriaCreateRequest;
import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.dto.categoria.CategoriaDetalleResponse;
import com.sicad.sicad_backend.dto.categoria.CategoriaUpdateRequest;
import com.sicad.sicad_backend.service.interfaces.ICategoriaService;
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
@RequestMapping("/categoria")
@RequiredArgsConstructor
@Tag(name = "Categoría", description = "Endpoints para la gestión de Categorías Docentes")
public class CategoriaController {

    private final ICategoriaService service;

    // --- LISTAR ---
    @Operation(
            summary = "Listar categorías",
            description = "Obtiene una lista de todas las categorías docentes registradas."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de categorías obtenida exitosamente")
    })
    @GetMapping("/listar")
    public ResponseEntity<BaseListReponse<CategoriaDetalleResponse>>
    listar() throws Exception {
        BaseListReponse<CategoriaDetalleResponse> response = service.listar();
        return ResponseEntity.status(response.status()).body(response);
    }

    // --- BUSCAR POR ID ---
    @Operation(
            summary = "Buscar categoría por ID",
            description = "Busca y obtiene los detalles de una categoría específica usando su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoría encontrada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Categoría no encontrada",
                    content = @Content(schema = @Schema(implementation = BaseMessageResponse.class)))
    })
    @GetMapping("/buscar/{idCategoria}")
    public ResponseEntity<BaseObjectResponse<CategoriaDetalleResponse>>
    buscar(@PathVariable("idCategoria") Integer id) {
        BaseObjectResponse<CategoriaDetalleResponse> response = service.buscar(id);
        return ResponseEntity.status(response.status()).body(response);
    }

    // --- BUSCAR POR DOCENTE ---
    @Operation(
            summary = "Buscar categoría por ID de Docente",
            description = "Busca y obtiene la categoría actual asociada al ID del docente."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoría encontrada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Categoría o Docente no encontrado",
                    content = @Content(schema = @Schema(implementation = BaseMessageResponse.class)))
    })
    @GetMapping("/buscar-por-docente/{idDocente}")
    public ResponseEntity<BaseObjectResponse<CategoriaDetalleResponse>>
    buscarPorDocente(@PathVariable("idDocente") Integer id) {
        BaseObjectResponse<CategoriaDetalleResponse> response = service.buscarPorDocente(id);
        return ResponseEntity.status(response.status()).body(response);
    }

    // --- INSERTAR (Individual) ---
    @Operation(
            summary = "Registrar categoría",
            description = "Crea y registra una nueva categoría en el sistema."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Categoría registrada exitosamente")
    })
    @PostMapping("/insertar")
    public ResponseEntity<BaseObjectResponse<CategoriaDetalleResponse>>
    registrar(@Valid @RequestBody CategoriaCreateRequest request) {
        BaseObjectResponse<CategoriaDetalleResponse> response = service.registrar(request);
        return ResponseEntity.status(response.status()).body(response);
    }

    // --- INSERTAR ALL (Múltiple) ---
    @Operation(
            summary = "Registrar múltiples categorías",
            description = "Crea y registra múltiples categorías en el sistema a partir de una lista."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Categorías registradas exitosamente")
    })
    @PostMapping("/insertar-all")
    public ResponseEntity<BaseListReponse<CategoriaDetalleResponse>>
    registrarAll(@Valid @RequestBody List<CategoriaCreateRequest> request) {
        BaseListReponse<CategoriaDetalleResponse> response = service.registrarAll(request);
        return ResponseEntity.status(response.status()).body(response);
    }

    // --- ACTUALIZAR ---
    @Operation(
            summary = "Actualizar categoría",
            description = "Actualiza los datos de una categoría existente usando su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoría actualizada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Categoría no encontrada para actualizar",
                    content = @Content(schema = @Schema(implementation = BaseMessageResponse.class)))
    })
    @PutMapping("/actualizar/{idCategoria}")
    public ResponseEntity<BaseObjectResponse<CategoriaDetalleResponse>>
    actualizar(@PathVariable("idCategoria") Integer id, @Valid @RequestBody CategoriaUpdateRequest request) {
        BaseObjectResponse<CategoriaDetalleResponse> response = service.actualizar(id, request);
        return ResponseEntity.status(response.status()).body(response);
    }

    // --- ELIMINAR ---
    @Operation(
            summary = "Eliminar categoría",
            description = "Elimina (lógicamente) una categoría del sistema usando su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoría eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Categoría no encontrada para eliminar",
                    content = @Content(schema = @Schema(implementation = BaseMessageResponse.class)))
    })
    @DeleteMapping("/eliminar/{idCategoria}")
    public ResponseEntity<BaseObjectResponse<String>>
    eliminar(@PathVariable("idCategoria") Integer id) {
        BaseObjectResponse<String> response = service.eliminar(id);
        return ResponseEntity.status(response.status()).body(response);
    }
}