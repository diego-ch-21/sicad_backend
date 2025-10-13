package com.sicad.sicad_backend.controller;

import com.sicad.sicad_backend.dto.Aula.AulaCreateRequest;
import com.sicad.sicad_backend.dto.Aula.AulaDetalleResponse;
import com.sicad.sicad_backend.dto.Aula.AulaUpdateRequest;
import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.service.interfaces.IAulaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/aula")
@RequiredArgsConstructor
public class AulaController {
    private final IAulaService service;
    @GetMapping("/listar")
    public ResponseEntity<BaseListReponse<AulaDetalleResponse>>
    listar() throws Exception {
        BaseListReponse<AulaDetalleResponse> response = service.listar();
        return ResponseEntity.status(response.status()).body(response);
    }

    @GetMapping("/buscar/{idAula}")
    public ResponseEntity<BaseObjectResponse<AulaDetalleResponse>>
    buscar(@PathVariable("idAula") Integer id) {
        BaseObjectResponse<AulaDetalleResponse> response = service.buscar(id);
        return ResponseEntity.status(response.status()).body(response);
    }

    @PostMapping("/insertar")
    public ResponseEntity<BaseObjectResponse<AulaDetalleResponse>>
    registrar(@Valid @RequestBody AulaCreateRequest request) {
        BaseObjectResponse<AulaDetalleResponse> response = service.registrar(request);
        return ResponseEntity.status(response.status()).body(response);
    }

    @PostMapping("/insertar-all")
    public ResponseEntity<BaseListReponse<AulaDetalleResponse>>
    registrarAll(@Valid @RequestBody List<AulaCreateRequest> request) {
        BaseListReponse<AulaDetalleResponse> response = service.registrarAll(request);
        return ResponseEntity.status(response.status()).body(response);
    }
    @PutMapping("/actualizar/{idAula}")
    public ResponseEntity<BaseObjectResponse<AulaDetalleResponse>>
    actualizar(@PathVariable("idAula") Integer id, @Valid @RequestBody AulaUpdateRequest dto) {
        BaseObjectResponse<AulaDetalleResponse> response = service.actualizar(id, dto);
        return ResponseEntity.status(response.status()).body(response);
    }
    @DeleteMapping("/eliminar/{idAula}")
    public ResponseEntity<BaseObjectResponse<String>>
    eliminar(@PathVariable("idAula") Integer id) {
        BaseObjectResponse<String> response = service.eliminar(id);
        return ResponseEntity.status(response.status()).body(response);
    }

}
