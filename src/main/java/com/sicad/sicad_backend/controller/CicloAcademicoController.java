package com.sicad.sicad_backend.controller;

import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.dto.base.BaseMessageResponse; // Importación necesaria
import com.sicad.sicad_backend.dto.cicloAcademico.CicloAcademicoCreateRequest;
import com.sicad.sicad_backend.dto.cicloAcademico.CicloAcademicoDetalleResponse;
import com.sicad.sicad_backend.dto.cicloAcademico.CicloAcademicoFileResponse;
import com.sicad.sicad_backend.dto.cicloAcademico.CicloAcademicoUpdateRequest;
import com.sicad.sicad_backend.service.interfaces.ICicloAcademicoService;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/ciclo-academico")
@RequiredArgsConstructor
@Tag(name = "Ciclo Académico", description = "Endpoints para la gestión de Ciclos Académicos")
public class CicloAcademicoController {

    private final ICicloAcademicoService service;

    // --- LISTAR ---
    @Operation(
            summary = "Listar ciclos académicos",
            description = "Obtiene una lista de todos los ciclos académicos registrados en el sistema."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de ciclos académicos obtenida exitosamente")
    })
    @GetMapping("/listar")
    public ResponseEntity<BaseListReponse<CicloAcademicoDetalleResponse>>
    listar() throws Exception {
        BaseListReponse<CicloAcademicoDetalleResponse> response = service.listar();
        return ResponseEntity.status(response.status()).body(response);
    }

    // --- BUSCAR POR ID ---
    @Operation(
            summary = "Buscar ciclo académico por ID",
            description = "Busca y obtiene los detalles de un ciclo académico específico usando su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ciclo académico encontrado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Ciclo académico no encontrado",
                    content = @Content(schema = @Schema(implementation = BaseMessageResponse.class)))
    })
    @GetMapping("/buscar/{idCicloAcademico}")
    public ResponseEntity<BaseObjectResponse<CicloAcademicoDetalleResponse>>
    buscar(@PathVariable("idCicloAcademico") Integer id) {
        BaseObjectResponse<CicloAcademicoDetalleResponse> response = service.buscar(id);
        return ResponseEntity.status(response.status()).body(response);
    }

    @Operation(
            summary = "Buscar ciclo académico file por ID",
            description = "Busca y obtiene los detalles de un ciclo académico específico usando su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ciclo académico encontrado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Ciclo académico no encontrado",
                    content = @Content(schema = @Schema(implementation = BaseMessageResponse.class)))
    })
    @GetMapping("/buscar-file/{idCicloAcademico}")
    public ResponseEntity<BaseObjectResponse<CicloAcademicoFileResponse>>
    buscarFile(@PathVariable("idCicloAcademico") Integer id) {
        BaseObjectResponse<CicloAcademicoFileResponse> response = service.buscarFile(id);
        return ResponseEntity.status(response.status()).body(response);
    }

    // --- INSERTAR (Individual) ---
    @Operation(
            summary = "Registrar ciclo académico",
            description = "Crea y registra un nuevo ciclo académico en el sistema."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Ciclo académico registrado exitosamente")
    })
    @PostMapping("/insertar")
    public ResponseEntity<BaseObjectResponse<CicloAcademicoDetalleResponse>>
    registrar(@Valid @RequestBody CicloAcademicoCreateRequest request) {
        BaseObjectResponse<CicloAcademicoDetalleResponse> response = service.registrar(request);
        return ResponseEntity.status(response.status()).body(response);
    }

    // --- INSERTAR ALL (Múltiple) ---
    @Operation(
            summary = "Registrar múltiples ciclos académicos",
            description = "Crea y registra múltiples ciclos académicos en el sistema a partir de una lista."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Ciclos académicos registrados exitosamente")
    })
    @PostMapping("/insertar-all")
    public ResponseEntity<BaseListReponse<CicloAcademicoDetalleResponse>>
    registrarAll(@Valid @RequestBody List<CicloAcademicoCreateRequest> request) {
        BaseListReponse<CicloAcademicoDetalleResponse> response = service.registrarAll(request);
        return ResponseEntity.status(response.status()).body(response);
    }

    // --- ACTUALIZAR ---
    @Operation(
            summary = "Actualizar ciclo académico",
            description = "Actualiza los datos de un ciclo académico existente usando su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ciclo académico actualizado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Ciclo académico no encontrado para actualizar",
                    content = @Content(schema = @Schema(implementation = BaseMessageResponse.class)))
    })
    @PutMapping("/actualizar/{idCicloAcademico}")
    public ResponseEntity<BaseObjectResponse<CicloAcademicoDetalleResponse>>
    actualizar(@PathVariable("idCicloAcademico") Integer id, @Valid @RequestBody CicloAcademicoUpdateRequest dto) {
        BaseObjectResponse<CicloAcademicoDetalleResponse> response = service.actualizar(id, dto);
        return ResponseEntity.status(response.status()).body(response);
    }



    @Operation(
            summary = "Actualizar PDF del ciclo académico",
            description = "Sube un PDF para el ciclo académico y reemplaza el existente."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "PDF actualizado correctamente"),
            @ApiResponse(responseCode = "404", description = "Ciclo académico no encontrado",
                    content = @Content(schema = @Schema(implementation = BaseMessageResponse.class)))
    })
    @PutMapping("/actualizar-pdf/{idCicloAcademico}")
    public ResponseEntity<BaseObjectResponse<CicloAcademicoFileResponse>> actualizarFilePdf(
            @PathVariable Integer idCicloAcademico,
            @RequestParam("file") MultipartFile file) {

        BaseObjectResponse<CicloAcademicoFileResponse> response = service.actualizarFilePdf(idCicloAcademico, file);
        return ResponseEntity.status(response.status()).body(response);
    }


    @Operation(
            summary = "Actualizar Excel del ciclo académico",
            description = "Sube un archivo Excel para el ciclo académico y reemplaza el existente."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Excel actualizado correctamente"),
            @ApiResponse(responseCode = "404", description = "Ciclo académico no encontrado",
                    content = @Content(schema = @Schema(implementation = BaseMessageResponse.class)))
    })
    @PutMapping("/actualizar-excel/{idCicloAcademico}")
    public ResponseEntity<BaseObjectResponse<CicloAcademicoFileResponse>> actualizarFileExcel(
            @PathVariable Integer idCicloAcademico,
            @RequestParam("file") MultipartFile file) {

        BaseObjectResponse<CicloAcademicoFileResponse> response = service.actualizarFileExcel(idCicloAcademico, file);
        return ResponseEntity.status(response.status()).body(response);
    }


    // --- ELIMINAR ---
    @Operation(
            summary = "Eliminar ciclo académico",
            description = "Elimina (lógicamente) un ciclo académico del sistema usando su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ciclo académico eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Ciclo académico no encontrado para eliminar",
                    content = @Content(schema = @Schema(implementation = BaseMessageResponse.class)))
    })
    @DeleteMapping("/eliminar/{idCicloAcademico}")
    public ResponseEntity<BaseObjectResponse<String>>
    eliminar(@PathVariable("idCicloAcademico") Integer id) {
        BaseObjectResponse<String> response = service.eliminar(id);
        return ResponseEntity.status(response.status()).body(response);
    }
}