package com.sicad.sicad_backend.controller;

import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.dto.cicloAcademico.CicloAcademicoCreateRequest;
import com.sicad.sicad_backend.dto.cicloAcademico.CicloAcademicoDetalleResponse;
import com.sicad.sicad_backend.dto.cicloAcademico.CicloAcademicoUpdateRequest;
import com.sicad.sicad_backend.service.interfaces.ICicloAcademicoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ciclo-academico")
@RequiredArgsConstructor
public class CicloAcademicoController {

    private final ICicloAcademicoService service;
    @GetMapping("/listar")
    public ResponseEntity<BaseListReponse<CicloAcademicoDetalleResponse>>
            listar() throws Exception {
        BaseListReponse<CicloAcademicoDetalleResponse> response = service.listar();
        return ResponseEntity.status(response.status()).body(response);
    }

    @GetMapping("/buscar/{idCicloAcademico}")
    public ResponseEntity<BaseObjectResponse<CicloAcademicoDetalleResponse>>
            buscar(@PathVariable("idCicloAcademico") Integer id) {
        BaseObjectResponse<CicloAcademicoDetalleResponse> response = service.buscar(id);
        return ResponseEntity.status(response.status()).body(response);
    }

    @PostMapping("/insertar")
    public ResponseEntity<BaseObjectResponse<CicloAcademicoDetalleResponse>>
            registrar(@Valid @RequestBody CicloAcademicoCreateRequest request) {
        BaseObjectResponse<CicloAcademicoDetalleResponse> response = service.registrar(request);
        return ResponseEntity.status(response.status()).body(response);
    }

    @PostMapping("/insertar-all")
    public ResponseEntity<BaseListReponse<CicloAcademicoDetalleResponse>>
            registrarAll(@Valid @RequestBody List<CicloAcademicoCreateRequest> request) {
        BaseListReponse<CicloAcademicoDetalleResponse> response = service.registrarAll(request);
        return ResponseEntity.status(response.status()).body(response);
    }
    @PutMapping("/actualizar/{idCicloAcademico}")
    public ResponseEntity<BaseObjectResponse<CicloAcademicoDetalleResponse>>
            actualizar(@PathVariable("idCicloAcademico") Integer id, @Valid @RequestBody CicloAcademicoUpdateRequest dto) {
        BaseObjectResponse<CicloAcademicoDetalleResponse> response = service.actualizar(id, dto);
        return ResponseEntity.status(response.status()).body(response);
    }
    @DeleteMapping("/eliminar/{idCicloAcademico}")
    public ResponseEntity<BaseObjectResponse<String>>
            eliminar(@PathVariable("idCicloAcademico") Integer id) {
        BaseObjectResponse<String> response = service.eliminar(id);
        return ResponseEntity.status(response.status()).body(response);
    }
}