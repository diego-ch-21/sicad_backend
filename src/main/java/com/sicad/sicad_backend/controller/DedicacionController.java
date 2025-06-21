package com.sicad.sicad_backend.controller;

import com.sicad.sicad_backend.dto.dedicacion.DedicacionRequestDTO;
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
    public ResponseEntity<GenericReponse<DedicacionRequestDTO>> findAll() throws Exception {
        List<DedicacionRequestDTO> lista = service.findAll()
                .stream()
                .map(this::convertToDTO)
                .toList();
        return ResponseEntity.ok(
                new GenericReponse<>(200, "Lista de Dedicaciones", lista)
        );
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<GenericReponse<DedicacionRequestDTO>> findById(@PathVariable("id") Integer id) throws Exception {
        Dedicacion obj = service.findById(id);
        return ResponseEntity.ok(
                new GenericReponse<>(200, "Dedicación encontrada", List.of(convertToDTO(obj)))
        );
    }

    @PostMapping("/guardar")
    public ResponseEntity<GenericReponse<DedicacionRequestDTO>> save(@Valid @RequestBody DedicacionRequestDTO dto) throws Exception {
        Dedicacion obj = service.save(convertToEntity(dto));
        return new ResponseEntity<>(
                new GenericReponse<>(201, "Dedicación creada", List.of(convertToDTO(obj))),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<GenericReponse<DedicacionRequestDTO>> update(@PathVariable("id") Integer id, @Valid @RequestBody DedicacionRequestDTO dto) throws Exception {
        Dedicacion obj = service.update(id, convertToEntity(dto));
        return ResponseEntity.ok(
                new GenericReponse<>(200, "Dedicación actualizada", List.of(convertToDTO(obj)))
        );
    }


    private DedicacionRequestDTO convertToDTO(Dedicacion obj) {
        return modelMapper.map(obj, DedicacionRequestDTO.class);
    }

    private Dedicacion convertToEntity(DedicacionRequestDTO dto) {
        return modelMapper.map(dto, Dedicacion.class);
    }
}
