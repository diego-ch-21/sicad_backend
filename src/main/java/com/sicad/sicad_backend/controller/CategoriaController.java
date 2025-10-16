package com.sicad.sicad_backend.controller;

import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.categoria.CategoriaCreateRequest;
import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.dto.categoria.CategoriaDetalleResponse;
import com.sicad.sicad_backend.dto.categoria.CategoriaUpdateRequest;
import com.sicad.sicad_backend.service.interfaces.ICategoriaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categoria")
@RequiredArgsConstructor
public class CategoriaController {

    private final ICategoriaService service;

    @GetMapping("/listar")
    public ResponseEntity<BaseListReponse<CategoriaDetalleResponse>>
            listar() throws Exception {
        BaseListReponse<CategoriaDetalleResponse> response = service.listar();
        return ResponseEntity.status(response.status()).body(response);
    }

    @GetMapping("/buscar/{idCategoria}")
    public ResponseEntity<BaseObjectResponse<CategoriaDetalleResponse>>
            buscar(@PathVariable("idCategoria") Integer id) {
        BaseObjectResponse<CategoriaDetalleResponse> response = service.buscar(id);
        return ResponseEntity.status(response.status()).body(response);
    }

    @GetMapping("/buscar/docente/{idDocente}")
    public ResponseEntity<BaseObjectResponse<CategoriaDetalleResponse>>
            buscarPorDocente(@PathVariable("idDocente") Integer id) {
        BaseObjectResponse<CategoriaDetalleResponse> response = service.buscarPorDocente(id);
        return ResponseEntity.status(response.status()).body(response);
    }

    @PostMapping("/insertar")
    public ResponseEntity<BaseObjectResponse<CategoriaDetalleResponse>>
            registrar(@Valid @RequestBody CategoriaCreateRequest request) {
        BaseObjectResponse<CategoriaDetalleResponse> response = service.registrar(request);
        return ResponseEntity.status(response.status()).body(response);
    }

    @PostMapping("/insertar-all")
    public ResponseEntity<BaseListReponse<CategoriaDetalleResponse>>
            registrarAll(@Valid @RequestBody List<CategoriaCreateRequest> request) {
        BaseListReponse<CategoriaDetalleResponse> response = service.registrarAll(request);
        return ResponseEntity.status(response.status()).body(response);
    }
    @PutMapping("/actualizar/{idCategoria}")
    public ResponseEntity<BaseObjectResponse<CategoriaDetalleResponse>>
            actualizar(@PathVariable("idCategoria") Integer id, @Valid @RequestBody CategoriaUpdateRequest request) {
        BaseObjectResponse<CategoriaDetalleResponse> response = service.actualizar(id, request);
        return ResponseEntity.status(response.status()).body(response);
    }
    @DeleteMapping("/eliminar/{idCategoria}")
    public ResponseEntity<BaseObjectResponse<String>>
            eliminar(@PathVariable("idCategoria") Integer id) {
        BaseObjectResponse<String> response = service.eliminar(id);
        return ResponseEntity.status(response.status()).body(response);
    }

}