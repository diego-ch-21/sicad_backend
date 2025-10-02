package com.sicad.sicad_backend.controller;

import com.sicad.sicad_backend.dto.base.GenericObjectResponse;
import com.sicad.sicad_backend.dto.base.GenericReponse;
import com.sicad.sicad_backend.dto.cicloAcademico.CicloAcademicoCreateRequest;
import com.sicad.sicad_backend.dto.cicloAcademico.CicloAcademicoDetalleResponse;
import com.sicad.sicad_backend.dto.cicloAcademico.CicloAcademicoUpdateRequest;
import com.sicad.sicad_backend.model.CicloAcademico;
import com.sicad.sicad_backend.service.impl.CicloAcademicoServiceImpl;
import com.sicad.sicad_backend.service.interfaces.IAsignacionService;
import com.sicad.sicad_backend.service.interfaces.ICicloAcademicoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ciclo-academico")
@RequiredArgsConstructor
public class CicloAcademicoController {

    private final ICicloAcademicoService service;
    private final CicloAcademicoServiceImpl serviceImpl;
    private final ModelMapper modelMapper;

    @GetMapping("/listar")
    public ResponseEntity<GenericReponse<CicloAcademicoDetalleResponse>> findAll() throws Exception {
        List<CicloAcademicoDetalleResponse> lista = service.findByEnabledTrue()
                .stream()
                .map(this::convertToResponseDTO)
                .toList();

        return ResponseEntity.ok(
                new GenericReponse<>(200, "Lista de ciclos académicos", lista)
        );
    }

    @GetMapping("/buscar/{idCicloAcademico}")
    public ResponseEntity<GenericObjectResponse<CicloAcademicoDetalleResponse>>
        findById(@PathVariable("idCicloAcademico") Integer id) throws Exception {
        CicloAcademico ciclo = service.findById(id);
        return ResponseEntity.ok(
                new GenericObjectResponse<>(200, "Ciclo académico encontrado", convertToResponseDTO(ciclo))
        );
    }

    @PostMapping("/insertar")
    public ResponseEntity<GenericObjectResponse<CicloAcademicoDetalleResponse>> registrar(
            @Valid @RequestBody CicloAcademicoCreateRequest request) {
        GenericObjectResponse<CicloAcademicoDetalleResponse> response = serviceImpl.registrarCiclo(request);
        return ResponseEntity.status(response.status()).body(response);
    }

    @PutMapping("/actualizar/{idCicloAcademico}")
    public ResponseEntity<GenericObjectResponse<CicloAcademicoDetalleResponse>> actualizar(
            @PathVariable("idCicloAcademico") Integer id,
            @Valid @RequestBody CicloAcademicoUpdateRequest request) {
        GenericObjectResponse<CicloAcademicoDetalleResponse> response = serviceImpl.actualizarCiclo(id, request);
        return ResponseEntity.status(response.status()).body(response);
    }
    @DeleteMapping("/eliminar/{idCicloAcademico}")
    public ResponseEntity<GenericObjectResponse<String>> delete(@PathVariable("idCicloAcademico") Integer id) {
        GenericObjectResponse<String> response = serviceImpl.eliminarCicloAcademico(id);
        return ResponseEntity.status(response.status()).body(response);
    }

    // Utilidades
    private CicloAcademicoDetalleResponse convertToResponseDTO(CicloAcademico obj) {
        return modelMapper.map(obj, CicloAcademicoDetalleResponse.class);
    }

    private CicloAcademico convertToEntity(CicloAcademicoCreateRequest dto) {
        return modelMapper.map(dto, CicloAcademico.class);
    }
}