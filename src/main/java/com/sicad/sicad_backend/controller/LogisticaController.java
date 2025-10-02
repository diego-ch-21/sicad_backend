package com.sicad.sicad_backend.controller;


import com.sicad.sicad_backend.dto.base.GenericObjectResponse;
import com.sicad.sicad_backend.dto.base.GenericReponse;

import com.sicad.sicad_backend.dto.logistica.LogisticaCreateRequest;
import com.sicad.sicad_backend.dto.logistica.LogisticaDetalleResponse;
import com.sicad.sicad_backend.dto.logistica.LogisticaUpdateRequest;
import com.sicad.sicad_backend.model.Logistica;
import com.sicad.sicad_backend.service.impl.LogisticaServiceImpl;
import com.sicad.sicad_backend.service.interfaces.ILogisticaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/logistica")
@RequiredArgsConstructor
public class LogisticaController {
    private final ILogisticaService service;
    private final LogisticaServiceImpl serviceImpl;
    private final ModelMapper modelMapper;

    @GetMapping("/listar")
    public ResponseEntity<GenericReponse<LogisticaDetalleResponse>> findAll() throws Exception {
        List<LogisticaDetalleResponse> lista = service.findByEnabledTrue()
                .stream()
                .map(this::convertToResponseDTO)
                .toList();
        return ResponseEntity.ok(
                new GenericReponse<>(200, "Lista de Logisticaes", lista)
        );
    }

    @GetMapping("/buscar/{idLogistica}")
    public ResponseEntity<GenericObjectResponse<LogisticaDetalleResponse>> findById(@PathVariable("idLogistica") Integer id) throws Exception {
        Logistica obj = service.findById(id);
        return ResponseEntity.ok(
                new GenericObjectResponse<>(200, "Logistica encontrado",convertToResponseDTO(obj))
        );
    }


    @PostMapping("/insertar")
    public ResponseEntity<GenericObjectResponse<LogisticaDetalleResponse>> registrarDocente(@Valid @RequestBody LogisticaCreateRequest request) {
        GenericObjectResponse<LogisticaDetalleResponse> response = serviceImpl.registrarLogistica(request);
        return ResponseEntity.status(response.status()).body(response);
    }

    @PutMapping("/actualizar/{idLogistica}")
    public ResponseEntity<GenericObjectResponse<LogisticaDetalleResponse>> update(@Valid @PathVariable("idLogistica") Integer id, @Valid @RequestBody LogisticaUpdateRequest dto) throws Exception {
        GenericObjectResponse<LogisticaDetalleResponse> response = serviceImpl.actualizarLogistica(id, dto);
        return ResponseEntity.status(response.status()).body(response);
    }
    @DeleteMapping("/eliminar/{idLogistica}")
    public ResponseEntity<GenericObjectResponse<String>> delete(@PathVariable("idLogistica") Integer id) {
        GenericObjectResponse<String> response = serviceImpl.eliminarLogistica(id);
        return ResponseEntity.status(response.status()).body(response);
    }

    private LogisticaCreateRequest convertToDTO(Logistica obj) {
        return modelMapper.map(obj, LogisticaCreateRequest.class);
    }
    private LogisticaDetalleResponse convertToResponseDTO(Logistica obj) {
        return modelMapper.map(obj, LogisticaDetalleResponse.class);
    }

    private Logistica convertToEntity(LogisticaCreateRequest dto) {
        return modelMapper.map(dto, Logistica.class);
    }
}
