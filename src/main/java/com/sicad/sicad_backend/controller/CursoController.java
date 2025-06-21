package com.sicad.sicad_backend.controller;


import com.sicad.sicad_backend.model.Curso;
import com.sicad.sicad_backend.dto.CursoDTO;
import com.sicad.sicad_backend.dto.base.GenericReponse;
import com.sicad.sicad_backend.service.interfaces.ICursoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/curso")
@RequiredArgsConstructor
public class CursoController {
    private final ICursoService service;

    private final ModelMapper modelMapper;

    @GetMapping("/listar")
    public ResponseEntity<GenericReponse<CursoDTO>> findAll() throws Exception {
        List<CursoDTO> lista = service.findAll()
                .stream()
                .map(this::convertToDTO)
                .toList();

        return ResponseEntity.ok(
                new GenericReponse<>(200, "Lista de Cursos", lista)
        );
    }
    @GetMapping("/buscar/{id}")
    public ResponseEntity<GenericReponse<CursoDTO>>  findById(@PathVariable("id") Integer id) throws Exception {
        Curso obj = service.findById(id);
        return ResponseEntity.ok(
                new GenericReponse<>(200, "Curso encontrada", List.of(convertToDTO(obj)))
        );
    }
    @PostMapping("/guardar")
    public ResponseEntity<GenericReponse<CursoDTO>> save(@Valid @RequestBody CursoDTO dto) throws Exception {
        Curso obj = service.save(convertToEntity(dto));
        return new ResponseEntity<>(new GenericReponse<>(
                201, "Curso creada", List.of(convertToDTO(obj))
        ), HttpStatus.CREATED);
    }
    @PutMapping("/actualizar/{id}")
    public ResponseEntity<GenericReponse<CursoDTO>> update(@Valid @PathVariable("id") Integer id, @RequestBody CursoDTO dto) throws Exception {
        Curso obj = service.update(id,convertToEntity(dto));
        return ResponseEntity.ok(
                new GenericReponse<>(200, "Curso actualizada", List.of(convertToDTO(obj)))
        );
    }

    private CursoDTO convertToDTO(Curso obj) {
        return modelMapper.map(obj, CursoDTO.class);
    }
    private Curso convertToEntity(CursoDTO dto) {
        return modelMapper.map(dto, Curso.class);
    }
}