package com.sicad.sicad_backend.controller;

import com.sicad.sicad_backend.dto.asignatura.AsignaturaCreateRequest;
import com.sicad.sicad_backend.dto.asignatura.AsignaturaDetalleResponse;
import com.sicad.sicad_backend.dto.asignatura.AsignaturaUpdateRequest;
import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.model.Asignatura;
import com.sicad.sicad_backend.service.impl.AsignaturaServiceImpl;
import com.sicad.sicad_backend.service.interfaces.IAsignaturaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
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
    public ResponseEntity<BaseListReponse<AsignaturaDetalleResponse>> findAll() throws Exception {
        List<AsignaturaDetalleResponse> lista = service.findByEnabledTrue()
                .stream()
                .map(this::convertToDetalle)
                .toList();
        return ResponseEntity.ok(
                new BaseListReponse<>(200, "Lista de asignaturas", lista)
        );
    }

    @GetMapping("/buscar/{idAsignatura}")
    public ResponseEntity<BaseObjectResponse<AsignaturaDetalleResponse>> findById(@PathVariable("idAsignatura") Integer id) throws Exception {
        Asignatura obj = service.findById(id);
        return ResponseEntity.ok(
                new BaseObjectResponse<>(200, "Asignatura encontrada", convertToDetalle(obj))
        );
    }

    @PostMapping("/insertar")
    public ResponseEntity<BaseObjectResponse<AsignaturaDetalleResponse>> registrar(@Valid @RequestBody AsignaturaCreateRequest dto) {
        BaseObjectResponse<AsignaturaDetalleResponse> response = serviceImpl.registrarAsignatura(dto);
        return ResponseEntity.status(response.status()).body(response);
    }
    @PostMapping("/insertar-all")
    public ResponseEntity<BaseListReponse<AsignaturaDetalleResponse>> registrarAll(@Valid @RequestBody List<AsignaturaCreateRequest> dto) {
        BaseListReponse<AsignaturaDetalleResponse> response = serviceImpl.registrarAsignaturasMultiples(dto);
        return ResponseEntity.status(response.status()).body(response);
    }

    @PutMapping("/actualizar/{idAsignatura}")
    public ResponseEntity<BaseObjectResponse<AsignaturaDetalleResponse>> actualizar(@PathVariable("idAsignatura") Integer id, @Valid @RequestBody AsignaturaUpdateRequest dto) {
        BaseObjectResponse<AsignaturaDetalleResponse> response = serviceImpl.actualizarAsignatura(id, dto);
        return ResponseEntity.status(response.status()).body(response);
    }

    @DeleteMapping("/eliminar/{idAsignatura}")
    public ResponseEntity<BaseObjectResponse<String>> delete(@PathVariable("idAsignatura") Integer id) {
        BaseObjectResponse<String> response = serviceImpl.eliminarAsignatura(id);
        return ResponseEntity.status(response.status()).body(response);
    }

    private AsignaturaDetalleResponse convertToDetalle(Asignatura obj) {
        return modelMapper.map(obj, AsignaturaDetalleResponse.class);
    }
}