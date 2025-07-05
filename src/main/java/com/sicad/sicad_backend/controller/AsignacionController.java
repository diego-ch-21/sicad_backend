package com.sicad.sicad_backend.controller;


import com.sicad.sicad_backend.dto.asignacion.AsignacionDetalleResponse;
import com.sicad.sicad_backend.dto.asignacion.AsignacionUpdateRequest;
import com.sicad.sicad_backend.dto.base.GenericObjectResponse;
import com.sicad.sicad_backend.model.Asignacion;
import com.sicad.sicad_backend.dto.asignacion.AsignacionCreateRequest;
import com.sicad.sicad_backend.dto.base.GenericReponse;
import com.sicad.sicad_backend.service.impl.AsignacionServiceImpl;
import com.sicad.sicad_backend.service.interfaces.IAsignacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/asignacion")
@RequiredArgsConstructor
public class AsignacionController {
    private final IAsignacionService service;
    private final AsignacionServiceImpl asignacionServiceImpl;

    private final ModelMapper modelMapper;

    @GetMapping("/listar")
    public ResponseEntity<GenericReponse<AsignacionDetalleResponse>> findAll() throws Exception {
        List<AsignacionDetalleResponse> lista = service.findAll()
                .stream()
                .map(this::convertToDTO)
                .toList();

        return ResponseEntity.ok(
                new GenericReponse<>(200, "Lista de Asignacions", lista)
        );
    }
    @GetMapping("/buscar/{id}")
    public ResponseEntity<GenericObjectResponse<AsignacionDetalleResponse>>  findById(@PathVariable("id") Integer id) throws Exception {
        Asignacion obj = service.findById(id);
        return ResponseEntity.ok(
                new GenericObjectResponse<>(200, "Asignacion encontrada", convertToDTO(obj))
        );
    }
    @PostMapping("/insertar")
    public ResponseEntity<GenericObjectResponse<AsignacionDetalleResponse>> save(@Valid @RequestBody AsignacionCreateRequest request){
        GenericObjectResponse<AsignacionDetalleResponse> response = asignacionServiceImpl.registrarAsignacion(request);
        return ResponseEntity.status(response.status()).body(response);

    }
    @PutMapping("/actualizar/{id}")
    public ResponseEntity<GenericObjectResponse<AsignacionDetalleResponse>> update(@Valid @PathVariable("id") Integer id, @RequestBody AsignacionUpdateRequest request){
        GenericObjectResponse<AsignacionDetalleResponse> response = asignacionServiceImpl.actualizarAsignacion(id, request);
        return ResponseEntity.status(response.status()).body(response);
    }
    private AsignacionDetalleResponse convertToDTO(Asignacion obj) {
        return modelMapper.map(obj, AsignacionDetalleResponse.class);
    }

    @DeleteMapping("/eliminar/{idCargaElectiva}")
    public ResponseEntity<GenericObjectResponse<String>> delete(@PathVariable("idCargaElectiva") Integer id) {
        GenericObjectResponse<String> response = asignacionServiceImpl.eliminarAsignacionesPorCargaElectiva(id);
        return ResponseEntity.status(response.status()).body(response);
    }
}