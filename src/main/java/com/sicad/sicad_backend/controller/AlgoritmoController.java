package com.sicad.sicad_backend.controller;

import com.sicad.sicad_backend.dto.algoritmo.AlgoritmoCreateRequest;
import com.sicad.sicad_backend.dto.algoritmo.AlgoritmoDetalleResponse;
import com.sicad.sicad_backend.dto.algoritmo.AlgoritmoUpdateRequest;
import com.sicad.sicad_backend.dto.base.GenericObjectResponse;
import com.sicad.sicad_backend.dto.base.GenericReponse;
import com.sicad.sicad_backend.model.Algoritmo;
import com.sicad.sicad_backend.service.impl.AlgoritmoServiceImpl;
import com.sicad.sicad_backend.service.interfaces.IAlgoritmoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/algoritmo")
@RequiredArgsConstructor
public class AlgoritmoController{
    private final  IAlgoritmoService service;
    private final AlgoritmoServiceImpl serviceImpl;
    private final ModelMapper modelMapper;

    @GetMapping("/listar")
    public ResponseEntity<GenericReponse<AlgoritmoDetalleResponse>> listar(){
        List<AlgoritmoDetalleResponse> lista = service.findByEnabledTrue()
                .stream()
                .map(this::convertToDetalle)
                .toList();
        return ResponseEntity.ok(
                new GenericReponse<>(200, "Lista de algoritmos", lista)
        );
    }
    @GetMapping("/buscar/{idAlgoritmo}")
    public ResponseEntity<GenericObjectResponse<AlgoritmoDetalleResponse>> findById(@PathVariable("idAlgoritmo") Integer id) throws Exception {
        Algoritmo obj = service.findById(id);
        return ResponseEntity.ok(
                new GenericObjectResponse<>(200, "Algoritmo encontrada", convertToDetalle(obj))
        );
    }

    @PostMapping("/insertar")
    public ResponseEntity<GenericObjectResponse<AlgoritmoDetalleResponse>> registrar(@Valid @RequestBody AlgoritmoCreateRequest dto) {
        GenericObjectResponse<AlgoritmoDetalleResponse> response = serviceImpl.registrarAlgoritmo(dto);
        return ResponseEntity.status(response.status()).body(response);
    }
    @PostMapping("/insertar-all")
    public ResponseEntity<GenericReponse<AlgoritmoDetalleResponse>> registrarAll(@Valid @RequestBody List<AlgoritmoCreateRequest> dto) {
        GenericReponse<AlgoritmoDetalleResponse> response = serviceImpl.registrarAlgoritmosMultiples(dto);
        return ResponseEntity.status(response.status()).body(response);
    }

    @PutMapping("/actualizar/{idAlgoritmo}")
    public ResponseEntity<GenericObjectResponse<AlgoritmoDetalleResponse>> actualizar(@PathVariable("idAlgoritmo") Integer id, @Valid @RequestBody AlgoritmoUpdateRequest dto) {
        GenericObjectResponse<AlgoritmoDetalleResponse> response = serviceImpl.actualizarAlgoritmo(id, dto);
        return ResponseEntity.status(response.status()).body(response);
    }

    @PutMapping("/principal/{idAlgoritmo}")
    public ResponseEntity<GenericObjectResponse<AlgoritmoDetalleResponse>> principal(@PathVariable("idAlgoritmo") Integer id) {
        GenericObjectResponse<AlgoritmoDetalleResponse> response = serviceImpl.asignarPrincipal(id);
        return ResponseEntity.status(response.status()).body(response);
    }

    @DeleteMapping("/eliminar/{idAlgoritmo}")
    public ResponseEntity<GenericObjectResponse<String>> delete(@Valid @PathVariable("idAlgoritmo") Integer id) {
        GenericObjectResponse<String> response = serviceImpl.eliminarAlgoritmo(id);
        return ResponseEntity.status(response.status()).body(response);
    }

    private AlgoritmoDetalleResponse convertToDetalle(Algoritmo obj) {
        return modelMapper.map(obj, AlgoritmoDetalleResponse.class);

    }
}
