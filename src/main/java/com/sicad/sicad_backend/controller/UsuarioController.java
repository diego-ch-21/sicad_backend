package com.sicad.sicad_backend.controller;


import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.rol.RolDetalleResponse;
import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.dto.base.BaseMessageResponse; // Importación necesaria
import com.sicad.sicad_backend.dto.usuario.UsuarioDetalleResponse;
import com.sicad.sicad_backend.service.interfaces.IUsuarioService;
import io.swagger.v3.oas.annotations.Operation; // Importación necesaria
import io.swagger.v3.oas.annotations.media.Content; // Importación necesaria
import io.swagger.v3.oas.annotations.media.Schema; // Importación necesaria
import io.swagger.v3.oas.annotations.responses.ApiResponse; // Importación necesaria
import io.swagger.v3.oas.annotations.responses.ApiResponses; // Importación necesaria
import io.swagger.v3.oas.annotations.tags.Tag; // Importación necesaria
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/usuario")
@RequiredArgsConstructor
@Tag(name = "Usuario", description = "Endpoints para la gestión de Usuarios")
public class UsuarioController {
    private final IUsuarioService service;

    // --- LISTAR ---
    @Operation(
            summary = "Listar usuarios",
            description = "Obtiene una lista de todos los usuarios registrados en el sistema."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida exitosamente")
    })
    @GetMapping("/listar")
    public ResponseEntity<BaseListReponse<UsuarioDetalleResponse>>
    listar() {
        BaseListReponse<UsuarioDetalleResponse> response = service.listar();
        return ResponseEntity.status(response.status()).body(response);
    }

    // --- BUSCAR POR ID ---
    @Operation(
            summary = "Buscar usuario por ID",
            description = "Busca y obtiene los detalles de un usuario específico usando su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
                    content = @Content(schema = @Schema(implementation = BaseMessageResponse.class)))
    })
    @GetMapping("/buscar/{idUsuario}")
    public ResponseEntity<BaseObjectResponse<UsuarioDetalleResponse>>
    buscar(@PathVariable("idUsuario") Integer id) {
        BaseObjectResponse<UsuarioDetalleResponse> response = service.buscar(id);
        return ResponseEntity.status(response.status()).body(response);
    }

    @Operation(
            summary = "Actualizar URL de perfil",
            description = "Actualiza la foto de perfil de un usuario subiendo una imagen a Supabase Storage."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "URL de perfil actualizada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
                    content = @Content(schema = @Schema(implementation = BaseMessageResponse.class))),
            @ApiResponse(responseCode = "400", description = "Archivo inválido o error en la subida",
                    content = @Content(schema = @Schema(implementation = BaseMessageResponse.class)))
    })
    @PutMapping("/actualizar-url-perfil/{idUsuario}")
    public ResponseEntity<BaseObjectResponse<UsuarioDetalleResponse>> actualizarUrlPerfil(
            @PathVariable("idUsuario") Integer idUsuario,
            @RequestParam("file") MultipartFile file) {

        BaseObjectResponse<UsuarioDetalleResponse> response = service.actualizarUrlPerfil(idUsuario, file);
        return ResponseEntity.status(response.status()).body(response);
    }

}