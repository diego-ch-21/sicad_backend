package com.sicad.sicad_backend.controller;

import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.departamentoAcademico.DepartamentoAcademicoCreateRequest;
import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.dto.base.BaseMessageResponse; // Importación necesaria
import com.sicad.sicad_backend.dto.departamentoAcademico.DepartamentoAcademicoDetalleResponse;
import com.sicad.sicad_backend.dto.departamentoAcademico.DepartamentoAcademicoUpdateRequest;
import com.sicad.sicad_backend.service.interfaces.IDepartamentoAcademicoService;
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

@RestController
@RequestMapping("/departamento-academico")
@RequiredArgsConstructor
@Tag(name = "Departamento Académico", description = "Endpoints para la gestión de Departamentos Académicos")
public class DepartamentoAcademicoController {

    private final IDepartamentoAcademicoService service;

    // --- LISTAR ---
    @Operation(
            summary = "Listar Departamentos Académicos",
            description = "Obtiene una lista de todos los departamentos académicos registrados."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente")
    })
    @GetMapping("/listar")
    public ResponseEntity<BaseListReponse<DepartamentoAcademicoDetalleResponse>>
    listar() throws Exception {
        BaseListReponse<DepartamentoAcademicoDetalleResponse> response = service.listar();
        return ResponseEntity.status(response.status()).body(response); // Usar status() de la respuesta
    }

    // --- BUSCAR POR ID ---
    @Operation(
            summary = "Buscar Departamento Académico por ID",
            description = "Busca y obtiene los detalles de un departamento específico usando su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Departamento encontrado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Departamento no encontrado",
                    content = @Content(schema = @Schema(implementation = BaseMessageResponse.class)))
    })
    @GetMapping("/buscar/{idDepartamentoAcademico}")
    public ResponseEntity<BaseObjectResponse<DepartamentoAcademicoDetalleResponse>>
    buscar(@PathVariable("idDepartamentoAcademico") Integer id) {
        BaseObjectResponse<DepartamentoAcademicoDetalleResponse> response = service.buscar(id);
        return ResponseEntity.status(response.status()).body(response); // Usar status() de la respuesta
    }

    // --- INSERTAR ---
    @Operation(
            summary = "Registrar Departamento Académico",
            description = "Crea y registra un nuevo departamento académico en el sistema."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Departamento registrado exitosamente")
    })
    @PostMapping("/insertar")
    public ResponseEntity<BaseObjectResponse<DepartamentoAcademicoDetalleResponse>>
    registrar(@Valid @RequestBody DepartamentoAcademicoCreateRequest request) {
        BaseObjectResponse<DepartamentoAcademicoDetalleResponse> response = service.registrar(request);
        return ResponseEntity.status(response.status()).body(response);
    }

    // --- ACTUALIZAR ---
    @Operation(
            summary = "Actualizar Departamento Académico",
            description = "Actualiza los datos de un departamento existente usando su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Departamento actualizado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Departamento no encontrado para actualizar",
                    content = @Content(schema = @Schema(implementation = BaseMessageResponse.class)))
    })
    @PutMapping("/actualizar/{idDepartamentoAcademico}")
    public ResponseEntity<BaseObjectResponse<DepartamentoAcademicoDetalleResponse>>
    actualizar(@PathVariable("idDepartamentoAcademico") Integer id, @Valid @RequestBody DepartamentoAcademicoUpdateRequest dto) {
        BaseObjectResponse<DepartamentoAcademicoDetalleResponse> response = service.actualizar(id, dto);
        return ResponseEntity.status(response.status()).body(response);
    }

    // --- ELIMINAR ---
    @Operation(
            summary = "Eliminar Departamento Académico",
            description = "Marca un departamento académico como inactivo (eliminación lógica) usando su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Departamento eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Departamento no encontrado para eliminar",
                    content = @Content(schema = @Schema(implementation = BaseMessageResponse.class)))
    })
    @DeleteMapping("/eliminar/{idDepartamentoAcademico}")
    public ResponseEntity<BaseObjectResponse<String>>
    eliminar(@PathVariable("idDepartamentoAcademico") Integer id) {
        BaseObjectResponse<String> response = service.eliminar(id);
        return ResponseEntity.status(response.status()).body(response);
    }
}