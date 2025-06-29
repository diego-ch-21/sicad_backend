package com.sicad.sicad_backend.controller;

import com.sicad.sicad_backend.dto.asignatura.AsignaturaCreateRequest;
import com.sicad.sicad_backend.dto.asignatura.AsignaturaDetalleResponse;
import com.sicad.sicad_backend.dto.asignatura.AsignaturaUpdateRequest;
import com.sicad.sicad_backend.dto.base.GenericObjectResponse;
import com.sicad.sicad_backend.dto.base.GenericReponse;
import com.sicad.sicad_backend.model.Asignatura;
import com.sicad.sicad_backend.service.impl.AsignaturaServiceImpl;
import com.sicad.sicad_backend.service.interfaces.IAsignaturaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/asignatura")
@RequiredArgsConstructor
public class AsignaturaController {

    private final IAsignaturaService service;
    private final AsignaturaServiceImpl serviceImpl;
    private final ModelMapper modelMapper;

    @GetMapping("/listar")
    public ResponseEntity<GenericReponse<AsignaturaDetalleResponse>> findAll() throws Exception {
        List<AsignaturaDetalleResponse> lista = service.findAll()
                .stream()
                .map(this::convertToDetalle)
                .toList();
        return ResponseEntity.ok(
                new GenericReponse<>(200, "Lista de asignaturas", lista)
        );
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<GenericObjectResponse<AsignaturaDetalleResponse>> findById(@PathVariable("id") Integer id) throws Exception {
        Asignatura obj = service.findById(id);
        return ResponseEntity.ok(
                new GenericObjectResponse<>(200, "Asignatura encontrada", convertToDetalle(obj))
        );
    }

    @PostMapping("/insertar")
    public ResponseEntity<GenericObjectResponse<AsignaturaDetalleResponse>> registrar(@Valid @RequestBody AsignaturaCreateRequest dto) {
        GenericObjectResponse<AsignaturaDetalleResponse> response = serviceImpl.registrarAsignatura(dto);
        return ResponseEntity.status(response.status()).body(response);
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<GenericObjectResponse<AsignaturaDetalleResponse>> actualizar(@PathVariable("id") Integer id, @Valid @RequestBody AsignaturaUpdateRequest dto) {
        GenericObjectResponse<AsignaturaDetalleResponse> response = serviceImpl.actualizarAsignatura(id, dto);
        return ResponseEntity.status(response.status()).body(response);
    }

    private AsignaturaDetalleResponse convertToDetalle(Asignatura obj) {
        return modelMapper.map(obj, AsignaturaDetalleResponse.class);
    }
}