package com.sicad.sicad_backend.controller;

import com.sicad.sicad_backend.dto.DedicacionDTO;
import com.sicad.sicad_backend.dto.base.GenericReponse;
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
    public ResponseEntity<GenericReponse<DedicacionDTO>> findAll() throws Exception {
        List<DedicacionDTO> lista = service.findAll()
                .stream()
                .map(this::convertToDTO)
                .toList();
        return ResponseEntity.ok(
                new GenericReponse<>(200, "Lista de Dedicaciones", lista)
        );
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<GenericReponse<DedicacionDTO>> findById(@PathVariable("id") Integer id) throws Exception {
        Dedicacion obj = service.findById(id);
        return ResponseEntity.ok(
                new GenericReponse<>(200, "Dedicación encontrada", List.of(convertToDTO(obj)))
        );
    }

    @PostMapping("/guardar")
    public ResponseEntity<GenericReponse<DedicacionDTO>> save(@Valid @RequestBody DedicacionDTO dto) throws Exception {
        Dedicacion obj = service.save(convertToEntity(dto));
        return new ResponseEntity<>(
                new GenericReponse<>(201, "Dedicación creada", List.of(convertToDTO(obj))),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<GenericReponse<DedicacionDTO>> update(@PathVariable("id") Integer id, @Valid @RequestBody DedicacionDTO dto) throws Exception {
        Dedicacion obj = service.update(id, convertToEntity(dto));
        return ResponseEntity.ok(
                new GenericReponse<>(200, "Dedicación actualizada", List.of(convertToDTO(obj)))
        );
    }


    private DedicacionDTO convertToDTO(Dedicacion obj) {
        return modelMapper.map(obj, DedicacionDTO.class);
    }

    private Dedicacion convertToEntity(DedicacionDTO dto) {
        return modelMapper.map(dto, Dedicacion.class);
    }
}
