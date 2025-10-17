package com.sicad.sicad_backend.controller;


import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.curso.CursoCreateRequest;
import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.dto.base.BaseMessageResponse; // Importación necesaria
import com.sicad.sicad_backend.dto.curso.CursoDetalleResponse;
import com.sicad.sicad_backend.dto.curso.CursoUpdateRequest;
import com.sicad.sicad_backend.service.interfaces.ICursoService;
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
@RequestMapping("/curso")
@RequiredArgsConstructor
@Tag(name = "Curso", description = "Endpoints para la gestión de Cursos")
public class CursoController {

    private final ICursoService service;

    // --- LISTAR POR CICLO ACADÉMICO ---
    @Operation(
            summary = "Listar cursos por Ciclo Académico",
            description = "Obtiene una lista de todos los cursos asociados a un ciclo académico específico."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de cursos obtenida exitosamente")
    })
    @GetMapping("/listar-por-ciclo-academico/{idCicloAcademico}")
    public ResponseEntity<BaseListReponse<CursoDetalleResponse>>
    listar(@PathVariable("idCicloAcademico") Integer id) {
        BaseListReponse<CursoDetalleResponse> response = service.listarPorCicloAcademico(id);
        return ResponseEntity.status(response.status()).body(response);
    }

    // --- BUSCAR POR ID ---
    @Operation(
            summary = "Buscar curso por ID",
            description = "Busca y obtiene los detalles de un curso específico usando su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Curso encontrado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Curso no encontrado",
                    content = @Content(schema = @Schema(implementation = BaseMessageResponse.class)))
    })
    @GetMapping("/buscar/{idCurso}")
    public ResponseEntity<BaseObjectResponse<CursoDetalleResponse>>
    buscar(@PathVariable("idCurso") Integer id) {
        BaseObjectResponse<CursoDetalleResponse> response = service.buscar(id);
        return ResponseEntity.status(response.status()).body(response);
    }

    // --- INSERTAR (Individual) ---
    @Operation(
            summary = "Registrar curso",
            description = "Crea y registra un nuevo curso en el sistema. Debe incluir la lista de horarios del curso."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Curso registrado exitosamente")
    })
    @PostMapping("/insertar")
    public ResponseEntity<BaseObjectResponse<CursoDetalleResponse>>
    registrar(@Valid @RequestBody CursoCreateRequest request) {
        BaseObjectResponse<CursoDetalleResponse> response = service.registrar(request);
        return ResponseEntity.status(response.status()).body(response);
    }

    // --- INSERTAR ALL (Múltiple) ---
    @Operation(
            summary = "Registrar múltiples cursos",
            description = "Crea y registra múltiples cursos en el sistema a partir de una lista. Cada curso debe incluir su lista de horarios."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cursos registrados exitosamente")
    })
    @PostMapping("/insertar-all")
    public ResponseEntity<BaseListReponse<CursoDetalleResponse>>
    registrarAll(@Valid @RequestBody List<CursoCreateRequest> request) {
        BaseListReponse<CursoDetalleResponse> response = service.registrarAll(request);
        return ResponseEntity.status(response.status()).body(response);
    }

    // --- ACTUALIZAR ---
    @Operation(
            summary = "Actualizar curso",
            description = "Actualiza los datos de un curso existente usando su ID. **Nota:** Esta operación no actualiza la lista de horarios."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Curso actualizado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Curso no encontrado para actualizar",
                    content = @Content(schema = @Schema(implementation = BaseMessageResponse.class)))
    })
    @PutMapping("/actualizar/{idCurso}")
    public ResponseEntity<BaseObjectResponse<CursoDetalleResponse>>
    actualizar(@PathVariable("idCurso") Integer id, @Valid @RequestBody CursoUpdateRequest dto) {
        BaseObjectResponse<CursoDetalleResponse> response = service.actualizar(id, dto);
        return ResponseEntity.status(response.status()).body(response);
    }

    // --- ELIMINAR ---
    @Operation(
            summary = "Eliminar curso",
            description = "Elimina (lógicamente) un curso del sistema usando su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Curso eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Curso no encontrado para eliminar",
                    content = @Content(schema = @Schema(implementation = BaseMessageResponse.class)))
    })
    @DeleteMapping("/eliminar/{idCurso}")
    public ResponseEntity<BaseObjectResponse<String>>
    eliminar(@PathVariable("idCurso") Integer id) {
        BaseObjectResponse<String> response = service.eliminar(id);
        return ResponseEntity.status(response.status()).body(response);
    }
}