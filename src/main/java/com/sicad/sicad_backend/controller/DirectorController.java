package com.sicad.sicad_backend.controller;

import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.director.DirectorCreateRequest;
import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.dto.director.DirectorDetalleResponse;
import com.sicad.sicad_backend.dto.director.DirectorUpdateRequest;
import com.sicad.sicad_backend.service.interfaces.IDirectorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/director")
@RequiredArgsConstructor
public class DirectorController {

    private final IDirectorService service;

    @GetMapping("/listar")
    public ResponseEntity<BaseListReponse<DirectorDetalleResponse>>
            listar() throws Exception {
        BaseListReponse<DirectorDetalleResponse> response = service.listar();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/buscar/{idDirector}")
    public ResponseEntity<BaseObjectResponse<DirectorDetalleResponse>>
            buscar(@PathVariable("idDirector") Integer id) {
        BaseObjectResponse<DirectorDetalleResponse> response = service.buscar(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/insertar")
    public ResponseEntity<BaseObjectResponse<DirectorDetalleResponse>>
            registrar(@Valid @RequestBody DirectorCreateRequest request) {
        BaseObjectResponse<DirectorDetalleResponse> response = service.registrar(request);
        return ResponseEntity.status(response.status()).body(response);
    }
    @PostMapping("/insertar-all")
    public ResponseEntity<BaseListReponse<DirectorDetalleResponse>>
    registrarAll(@Valid @RequestBody List<DirectorCreateRequest> request) {
        //BaseListReponse<DirectorDetalleResponse> response = service.registrarAll(request);
        BaseListReponse<DirectorDetalleResponse> response =
                new BaseListReponse<>(200, "funcionalidad no implementada", List.of());
        return ResponseEntity.status(response.status()).body(response);
    }

    @PutMapping("/actualizar/{idDirector}")
    public ResponseEntity<BaseObjectResponse<DirectorDetalleResponse>>
            actualizar(@PathVariable("idDirector") Integer id, @Valid @RequestBody DirectorUpdateRequest dto) {
        BaseObjectResponse<DirectorDetalleResponse> response = service.actualizar(id, dto);
        return ResponseEntity.status(response.status()).body(response);
    }

    @DeleteMapping("/eliminar/{idDirector}")
    public ResponseEntity<BaseObjectResponse<String>>
            eliminar(@PathVariable("idDirector") Integer id) {
        BaseObjectResponse<String> response = service.eliminar(id);
        return ResponseEntity.status(response.status()).body(response);
    }
}
