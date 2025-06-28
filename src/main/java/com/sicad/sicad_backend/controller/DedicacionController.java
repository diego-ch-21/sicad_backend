package com.sicad.sicad_backend.controller;

import com.sicad.sicad_backend.dto.base.GenericObjectResponse;
import com.sicad.sicad_backend.dto.dedicacion.DedicacionCreateRequest;
import com.sicad.sicad_backend.dto.base.GenericReponse;
import com.sicad.sicad_backend.dto.dedicacion.DedicacionDetalleResponse;
import com.sicad.sicad_backend.model.Dedicacion;
import com.sicad.sicad_backend.service.interfaces.IDedicacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dedicacion")
@RequiredArgsConstructor
public class DedicacionController {

    private final IDedicacionService service;
    private final ModelMapper modelMapper;

    @GetMapping("/listar")
    public ResponseEntity<GenericReponse<DedicacionDetalleResponse>> findAll() throws Exception {
        List<DedicacionDetalleResponse> lista = service.findAll()
                .stream()
                .map(this::convertToDetalle)
                .toList();
        return ResponseEntity.ok(
                new GenericReponse<>(200, "Lista de Dedicaciones", lista)
        );
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<GenericObjectResponse<DedicacionDetalleResponse>> findById(@PathVariable("id") Integer id) throws Exception {
        Dedicacion obj = service.findById(id);
        return ResponseEntity.ok(
                new GenericObjectResponse<>(200, "Dedicación encontrada", convertToDetalle(obj))
        );
    }

    @PostMapping("/insertar")
    public ResponseEntity<GenericReponse<DedicacionDetalleResponse>> save(@Valid @RequestBody DedicacionCreateRequest dto) throws Exception {
        Dedicacion obj = service.save(convertToEntity(dto));
        return new ResponseEntity<>(
                new GenericReponse<>(201, "Dedicación creada", List.of(convertToDetalle(obj))),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<GenericReponse<DedicacionDetalleResponse>> update(@PathVariable("id") Integer id, @Valid @RequestBody DedicacionCreateRequest dto) throws Exception {
        Dedicacion obj = service.update(id, convertToEntity(dto));
        return ResponseEntity.ok(
                new GenericReponse<>(200, "Dedicación actualizada", List.of(convertToDetalle(obj)))
        );
    }


    private DedicacionCreateRequest convertToDTO(Dedicacion obj) {
        return modelMapper.map(obj, DedicacionCreateRequest.class);
    }

    private DedicacionDetalleResponse convertToDetalle(Dedicacion obj) {
        return modelMapper.map(obj, DedicacionDetalleResponse.class);
    }

    private Dedicacion convertToEntity(DedicacionCreateRequest dto) {
        return modelMapper.map(dto, Dedicacion.class);
    }
}
