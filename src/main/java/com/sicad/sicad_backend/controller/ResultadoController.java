package com.sicad.sicad_backend.controller;

import com.sicad.sicad_backend.dto.base.BaseMessageResponse; // Importación necesaria
import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.resultado.ResultadoDetalleResponse;
import com.sicad.sicad_backend.service.interfaces.IResultadoService;
import io.swagger.v3.oas.annotations.Operation; // Importación necesaria
import io.swagger.v3.oas.annotations.media.Content; // Importación necesaria
import io.swagger.v3.oas.annotations.media.Schema; // Importación necesaria
import io.swagger.v3.oas.annotations.responses.ApiResponse; // Importación necesaria
import io.swagger.v3.oas.annotations.responses.ApiResponses; // Importación necesaria
import io.swagger.v3.oas.annotations.tags.Tag; // Importación necesaria
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/resultado")
@RequiredArgsConstructor
@Tag(name = "Resultado", description = "Endpoints para la consulta de Resultados de la ejecución del Algoritmo")
public class ResultadoController {
    private final IResultadoService service;

    // --- BUSCAR POR ID ---
    @Operation(
            summary = "Buscar resultado por ID",
            description = "Busca y obtiene los detalles de un Resultado específico usando su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Resultado encontrado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Resultado no encontrado",
                    content = @Content(schema = @Schema(implementation = BaseMessageResponse.class)))
    })
    @GetMapping("/buscar/{idResultado}")
    public ResponseEntity<BaseObjectResponse<ResultadoDetalleResponse>>
    buscar(@PathVariable("idResultado") Integer id) {
        BaseObjectResponse<ResultadoDetalleResponse> response = service.buscar(id);
        return ResponseEntity.status(response.status()).body(response);
    }

    // --- BUSCAR POR CARGA ---
    @Operation(
            summary = "Buscar resultado por Carga",
            description = "Busca y obtiene el Resultado asociado a una Carga (ejecución del algoritmo) específica usando el ID de la Carga."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Resultado encontrado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Resultado para esta Carga no encontrado",
                    content = @Content(schema = @Schema(implementation = BaseMessageResponse.class)))
    })
    @GetMapping("/buscar-por-carga/{idCarga}")
    public ResponseEntity<BaseObjectResponse<ResultadoDetalleResponse>>
    buscarPorDocente(@PathVariable("idCarga") Integer id) {
        BaseObjectResponse<ResultadoDetalleResponse> response = service.buscarPorCarga(id);
        return ResponseEntity.status(response.status()).body(response);
    }

}