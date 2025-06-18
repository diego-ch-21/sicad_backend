package com.sicad.sicad_backend.controller;


import com.sicad.sicad_backend.model.Docente;
import com.sicad.sicad_backend.dto.DocenteDTO;
import com.sicad.sicad_backend.dto.base.GenericReponse;
import com.sicad.sicad_backend.service.interfaces.IDocenteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/docente")
@RequiredArgsConstructor
public class DocenteController {
    private final IDocenteService service;

    private final ModelMapper modelMapper;

    @GetMapping("/listar")
    public ResponseEntity<GenericReponse<DocenteDTO>> findAll() throws Exception {
        List<DocenteDTO> lista = service.findAll()
                .stream()
                .map(this::convertToDTO)
                .toList();

        return ResponseEntity.ok(
                new GenericReponse<>(200, "Lista de Docentes", lista)
        );
    }
    @GetMapping("/buscar/{id}")
    public ResponseEntity<GenericReponse<DocenteDTO>>  findById(@PathVariable("id") Integer id) throws Exception {
        Docente obj = service.findById(id);
        return ResponseEntity.ok(
                new GenericReponse<>(200, "Docente encontrada", List.of(convertToDTO(obj)))
        );
    }
    @PostMapping("/guardar")
    public ResponseEntity<GenericReponse<DocenteDTO>> save(@Valid @RequestBody DocenteDTO dto) throws Exception {
        Docente obj = service.save(convertToEntity(dto));
        return new ResponseEntity<>(new GenericReponse<>(
                201, "Docente creada", List.of(convertToDTO(obj))
        ), HttpStatus.CREATED);
    }
    @PutMapping("/actualizar/{id}")
    public ResponseEntity<GenericReponse<DocenteDTO>> update(@Valid @PathVariable("id") Integer id, @RequestBody DocenteDTO dto) throws Exception {
        Docente obj = service.update(id,convertToEntity(dto));
        return ResponseEntity.ok(
                new GenericReponse<>(200, "Docente actualizada", List.of(convertToDTO(obj)))
        );
    }
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Integer id) throws Exception {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }




    private DocenteDTO convertToDTO(Docente obj) {
        return modelMapper.map(obj, DocenteDTO.class);
    }
    private Docente convertToEntity(DocenteDTO dto) {
        return modelMapper.map(dto, Docente.class);
    }
}