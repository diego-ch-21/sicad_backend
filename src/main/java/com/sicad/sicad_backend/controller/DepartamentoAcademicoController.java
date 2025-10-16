package com.sicad.sicad_backend.controller;

import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.departamentoAcademico.DepartamentoAcademicoCreateRequest;
import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.dto.departamentoAcademico.DepartamentoAcademicoDetalleResponse;
import com.sicad.sicad_backend.dto.departamentoAcademico.DepartamentoAcademicoUpdateRequest;
import com.sicad.sicad_backend.service.interfaces.IDepartamentoAcademicoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/departamento-academico")
@RequiredArgsConstructor
public class DepartamentoAcademicoController {

    private final IDepartamentoAcademicoService service;

    @GetMapping("/listar")
    public ResponseEntity<BaseListReponse<DepartamentoAcademicoDetalleResponse>>
            listar() throws Exception {
        BaseListReponse<DepartamentoAcademicoDetalleResponse> response = service.listar();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/buscar/{idDepartamentoAcademico}")
    public ResponseEntity<BaseObjectResponse<DepartamentoAcademicoDetalleResponse>>
            buscar(@PathVariable("idDepartamentoAcademico") Integer id) {
        BaseObjectResponse<DepartamentoAcademicoDetalleResponse> response = service.buscar(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/insertar")
    public ResponseEntity<BaseObjectResponse<DepartamentoAcademicoDetalleResponse>>
            registrar(@Valid @RequestBody DepartamentoAcademicoCreateRequest request) {
        BaseObjectResponse<DepartamentoAcademicoDetalleResponse> response = service.registrar(request);
        return ResponseEntity.status(response.status()).body(response);
    }

    @PutMapping("/actualizar/{idDepartamentoAcademico}")
    public ResponseEntity<BaseObjectResponse<DepartamentoAcademicoDetalleResponse>>
            actualizar(@PathVariable("idDepartamentoAcademico") Integer id, @Valid @RequestBody DepartamentoAcademicoUpdateRequest dto) {
        BaseObjectResponse<DepartamentoAcademicoDetalleResponse> response = service.actualizar(id, dto);
        return ResponseEntity.status(response.status()).body(response);
    }

    @DeleteMapping("/eliminar/{idDepartamentoAcademico}")
    public ResponseEntity<BaseObjectResponse<String>>
            eliminar(@PathVariable("idDepartamentoAcademico") Integer id) {
        BaseObjectResponse<String> response = service.eliminar(id);
        return ResponseEntity.status(response.status()).body(response);
    }
}
