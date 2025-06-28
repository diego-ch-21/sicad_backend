package com.sicad.sicad_backend.controller;

import com.sicad.sicad_backend.dto.base.GenericObjectResponse;
import com.sicad.sicad_backend.dto.base.GenericReponse;
import com.sicad.sicad_backend.dto.facultad.FacultadCreateRequest;
import com.sicad.sicad_backend.dto.facultad.FacultadDetalleResponse;
import com.sicad.sicad_backend.model.Facultad;
import com.sicad.sicad_backend.repository.interfaces.IFacultadRepo;
import com.sicad.sicad_backend.service.impl.FacultadServiceImpl;
import com.sicad.sicad_backend.service.interfaces.IFacultadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/facultad")
@RequiredArgsConstructor
public class FacultadController {
    private final IFacultadService service;

    private final ModelMapper modelMapper;

    @GetMapping("/listar")
    public ResponseEntity<GenericReponse<FacultadDetalleResponse>> findAll() throws Exception {
        List<FacultadDetalleResponse> lista = service.findAll()
                .stream()
                .map(this::convertToDetalle)
                .toList();
        return ResponseEntity.ok(
                new GenericReponse<>(200, "Lista de Facultades", lista)
        );
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<GenericObjectResponse<FacultadDetalleResponse>> findById(@PathVariable("id") Integer id) throws Exception {
        Facultad obj = service.findById(id);
        return ResponseEntity.ok(
                new GenericObjectResponse<>(200, "Facultad encontrada", convertToDetalle(obj))
        );
    }

    @PostMapping("/insertar")
    public ResponseEntity<GenericReponse<FacultadDetalleResponse>> save(@Valid @RequestBody FacultadCreateRequest dto) throws Exception {
        Facultad obj = service.save(convertToEntity(dto));
        return new ResponseEntity<>(
                new GenericReponse<>(201, "Facultad creada", List.of(convertToDetalle(obj))),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<GenericReponse<FacultadDetalleResponse>> update(@PathVariable("id") Integer id, @Valid @RequestBody FacultadCreateRequest dto) throws Exception {
        Facultad obj = service.update(id, convertToEntity(dto));
        return ResponseEntity.ok(
                new GenericReponse<>(200, "Facultad actualizada", List.of(convertToDetalle(obj)))
        );
    }

    private FacultadDetalleResponse convertToDetalle(Facultad obj) {
        return modelMapper.map(obj, FacultadDetalleResponse.class);
    }

    private Facultad convertToEntity(FacultadCreateRequest dto) {
        return modelMapper.map(dto, Facultad.class);
    }


}
