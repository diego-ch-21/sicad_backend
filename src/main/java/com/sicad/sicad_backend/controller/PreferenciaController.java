package com.sicad.sicad_backend.controller;

import com.sicad.sicad_backend.dto.base.GenericObjectResponse;
import com.sicad.sicad_backend.dto.preferencia.PreferenciaCreateRequest;
import com.sicad.sicad_backend.dto.base.GenericReponse;
import com.sicad.sicad_backend.dto.preferencia.PreferenciaDetalleResponse;
import com.sicad.sicad_backend.dto.preferencia.PreferenciaUpdateRequest;
import com.sicad.sicad_backend.model.Preferencia;
import com.sicad.sicad_backend.service.impl.PreferenciaServiceImpl;
import com.sicad.sicad_backend.service.interfaces.IPreferenciaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/preferencia")
@RequiredArgsConstructor
public class PreferenciaController {
    private final IPreferenciaService service;
    private final ModelMapper modelMapper;
    private final PreferenciaServiceImpl preferenciaServiceImpl;

    @GetMapping("/listar")
    public ResponseEntity<GenericReponse<PreferenciaDetalleResponse>> findAll() throws Exception {
        List<PreferenciaDetalleResponse> lista = service.findAll()
                .stream()
                .map(this::convertToDTO)
                .toList();
        return ResponseEntity.ok(
                new GenericReponse<>(200, "Lista de Preferencias", lista)
        );
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<GenericObjectResponse<PreferenciaDetalleResponse>> findById(@PathVariable("id") Integer id) throws Exception {
        Preferencia obj = service.findById(id);
        return ResponseEntity.ok(
                new GenericObjectResponse<>(200, "Preferencia encontrada", convertToDTO(obj))
        );
    }

    @PostMapping("/insertar")
    public ResponseEntity<GenericObjectResponse<PreferenciaDetalleResponse>> save(@Valid @RequestBody PreferenciaCreateRequest request){
        GenericObjectResponse<PreferenciaDetalleResponse> response = preferenciaServiceImpl.registrarPreferencia(request);
        return ResponseEntity.status(response.status()).body(response);
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<GenericObjectResponse<PreferenciaDetalleResponse>> update(@PathVariable("id") Integer id, @Valid @RequestBody PreferenciaUpdateRequest request) throws Exception {
        GenericObjectResponse<PreferenciaDetalleResponse> response = preferenciaServiceImpl.actualizarPreferencia(id, request);
        return ResponseEntity.status(response.status()).body(response);
    }


    private PreferenciaDetalleResponse convertToDTO(Preferencia obj) {
        return modelMapper.map(obj, PreferenciaDetalleResponse.class);
    }
}
