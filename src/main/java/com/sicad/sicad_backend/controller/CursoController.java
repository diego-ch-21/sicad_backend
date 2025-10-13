package com.sicad.sicad_backend.controller;


import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.curso.CursoCreateRequest;
import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.dto.curso.CursoDetalleResponse;
import com.sicad.sicad_backend.dto.curso.CursoUpdateRequest;
import com.sicad.sicad_backend.service.interfaces.ICursoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/curso")
@RequiredArgsConstructor
public class CursoController {

    private final ICursoService service;

    @GetMapping("/listar/{idCicloAcademico}")
    public ResponseEntity<BaseListReponse<CursoDetalleResponse>>
            listar(@PathVariable("idCicloAcademico") Integer id) {
        BaseListReponse<CursoDetalleResponse> response = service.listarPorCicloAcademico(id);
        return ResponseEntity.status(response.status()).body(response);
    }
    @GetMapping("/buscar/{idCurso}")
    public ResponseEntity<BaseObjectResponse<CursoDetalleResponse>>
            buscar(@PathVariable("idCurso") Integer id) {
        BaseObjectResponse<CursoDetalleResponse> response = service.buscar(id);
        return ResponseEntity.status(response.status()).body(response);
    }

    @PostMapping("/insertar")
    public ResponseEntity<BaseObjectResponse<CursoDetalleResponse>>
            registrar(@Valid @RequestBody CursoCreateRequest request) {
        BaseObjectResponse<CursoDetalleResponse> response = service.registrar(request);
        return ResponseEntity.status(response.status()).body(response);
    }

    @PostMapping("/insertar-all")
    public ResponseEntity<BaseListReponse<CursoDetalleResponse>>
            registrarAll(@Valid @RequestBody List<CursoCreateRequest> request) {
        BaseListReponse<CursoDetalleResponse> response = service.registrarAll(request);
        return ResponseEntity.status(response.status()).body(response);
    }
    @PutMapping("/actualizar/{idCurso}")
    public ResponseEntity<BaseObjectResponse<CursoDetalleResponse>>
            actualizar(@PathVariable("idCurso") Integer id, @Valid @RequestBody CursoUpdateRequest dto) {
        BaseObjectResponse<CursoDetalleResponse> response = service.actualizar(id, dto);
        return ResponseEntity.status(response.status()).body(response);
    }
    @DeleteMapping("/eliminar/{idCurso}")
    public ResponseEntity<BaseObjectResponse<String>>
            eliminar(@PathVariable("idCurso") Integer id) {
        BaseObjectResponse<String> response = service.eliminar(id);
        return ResponseEntity.status(response.status()).body(response);
    }
}