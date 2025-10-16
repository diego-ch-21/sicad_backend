package com.sicad.sicad_backend.controller;


import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.rol.RolDetalleResponse;
import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.service.interfaces.IRolService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rol")
@RequiredArgsConstructor
public class RolController {
    private final IRolService service;


    @GetMapping("/listar")
    public ResponseEntity<BaseListReponse<RolDetalleResponse>>
            listar() throws Exception {
        BaseListReponse<RolDetalleResponse> response = service.listar();
        return ResponseEntity.status(response.status()).body(response);
    }

    @GetMapping("/buscar/{idRol}")
    public ResponseEntity<BaseObjectResponse<RolDetalleResponse>>
            buscar(@PathVariable("idRol") Integer id) {
        BaseObjectResponse<RolDetalleResponse> response = service.buscar(id);
        return ResponseEntity.status(response.status()).body(response);
    }


}