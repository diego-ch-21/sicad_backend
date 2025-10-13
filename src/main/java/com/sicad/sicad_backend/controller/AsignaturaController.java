package com.sicad.sicad_backend.controller;

import com.sicad.sicad_backend.dto.asignatura.AsignaturaCreateRequest;
import com.sicad.sicad_backend.dto.asignatura.AsignaturaDetalleResponse;
import com.sicad.sicad_backend.dto.asignatura.AsignaturaUpdateRequest;
import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.service.interfaces.IAsignaturaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/asignatura")
@RequiredArgsConstructor
public class AsignaturaController {

    private final IAsignaturaService service;
    @GetMapping("/listar")
    public ResponseEntity<BaseListReponse<AsignaturaDetalleResponse>>
            listar() throws Exception {
        BaseListReponse<AsignaturaDetalleResponse> response = service.listar();
        return ResponseEntity.status(response.status()).body(response);
    }

    @GetMapping("/buscar/{idAsignatura}")
    public ResponseEntity<BaseObjectResponse<AsignaturaDetalleResponse>>
            buscar(@PathVariable("idAsignatura") Integer id) {
        BaseObjectResponse<AsignaturaDetalleResponse> response = service.buscar(id);
        return ResponseEntity.status(response.status()).body(response);
    }

    @PostMapping("/insertar")
    public ResponseEntity<BaseObjectResponse<AsignaturaDetalleResponse>>
            registrar(@Valid @RequestBody AsignaturaCreateRequest request) {
        BaseObjectResponse<AsignaturaDetalleResponse> response = service.registrar(request);
        return ResponseEntity.status(response.status()).body(response);
    }

    @PostMapping("/insertar-all")
    public ResponseEntity<BaseListReponse<AsignaturaDetalleResponse>>
            registrarAll(@Valid @RequestBody List<AsignaturaCreateRequest> request) {
        BaseListReponse<AsignaturaDetalleResponse> response = service.registrarAll(request);
        return ResponseEntity.status(response.status()).body(response);
    }
    @PutMapping("/actualizar/{idAsignatura}")
    public ResponseEntity<BaseObjectResponse<AsignaturaDetalleResponse>>
            actualizar(@PathVariable("idAsignatura") Integer id, @Valid @RequestBody AsignaturaUpdateRequest dto) {
        BaseObjectResponse<AsignaturaDetalleResponse> response = service.actualizar(id, dto);
        return ResponseEntity.status(response.status()).body(response);
    }
    @DeleteMapping("/eliminar/{idAsignatura}")
    public ResponseEntity<BaseObjectResponse<String>>
            eliminar(@PathVariable("idAsignatura") Integer id) {
        BaseObjectResponse<String> response = service.eliminar(id);
        return ResponseEntity.status(response.status()).body(response);
    }


}