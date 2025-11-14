package com.sicad.sicad_backend.controller;


import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.rol.RolDetalleResponse;
import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.dto.base.BaseMessageResponse; // Importación necesaria
import com.sicad.sicad_backend.service.interfaces.IRolService;
import io.swagger.v3.oas.annotations.Operation; // Importación necesaria
import io.swagger.v3.oas.annotations.media.Content; // Importación necesaria
import io.swagger.v3.oas.annotations.media.Schema; // Importación necesaria
import io.swagger.v3.oas.annotations.responses.ApiResponse; // Importación necesaria
import io.swagger.v3.oas.annotations.responses.ApiResponses; // Importación necesaria
import io.swagger.v3.oas.annotations.tags.Tag; // Importación necesaria
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import static com.sicad.sicad_backend.Enum.RolEnum.*;


import java.util.List;

@RestController
@RequestMapping("/rol")
@RequiredArgsConstructor
@Tag(name = "Rol", description = "Endpoints para la gestión de Roles")
public class RolController {
    private final IRolService service;

    // --- LISTAR ---
    @Operation(
            summary = "Listar roles",
            description = "Obtiene una lista de todos los roles registrados en el sistema."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de roles obtenida exitosamente")
    })
    @PreAuthorize("@access.isAdmin()")
    @GetMapping("/listar")
    public ResponseEntity<BaseListReponse<RolDetalleResponse>>
    listar() throws Exception {
        BaseListReponse<RolDetalleResponse> response = service.listar();
        return ResponseEntity.status(response.status()).body(response);
    }

    // --- BUSCAR POR ID ---
    @Operation(
            summary = "Buscar rol por ID",
            description = "Busca y obtiene los detalles de un rol específico usando su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rol encontrado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Rol no encontrado",
                    content = @Content(schema = @Schema(implementation = BaseMessageResponse.class)))
    })
    @PreAuthorize("@access.isAdmin()")
    @GetMapping("/buscar/{idRol}")
    public ResponseEntity<BaseObjectResponse<RolDetalleResponse>>
    buscar(@PathVariable("idRol") Integer id) {
        BaseObjectResponse<RolDetalleResponse> response = service.buscar(id);
        return ResponseEntity.status(response.status()).body(response);
    }
}