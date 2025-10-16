package com.sicad.sicad_backend.controller;


import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.rol.RolDetalleResponse;

import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.dto.usuario.UsuarioDetalleResponse;
import com.sicad.sicad_backend.service.interfaces.IUsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuario")
@RequiredArgsConstructor
public class UsuarioController {
    private final IUsuarioService service;


    @GetMapping("/listar")
    public ResponseEntity<BaseListReponse<UsuarioDetalleResponse>>
            listar() {
        BaseListReponse<UsuarioDetalleResponse> response = service.listar();
        return ResponseEntity.status(response.status()).body(response);
    }

    @GetMapping("/buscar/{idUsuario}")
    public ResponseEntity<BaseObjectResponse<UsuarioDetalleResponse>>
            buscar(@PathVariable("idUsuario") Integer id) {
        BaseObjectResponse<UsuarioDetalleResponse> response = service.buscar(id);
        return ResponseEntity.status(response.status()).body(response);
    }

}