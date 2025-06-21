package com.sicad.sicad_backend.controller;


import com.sicad.sicad_backend.dto.base.GenericObjectResponse;
import com.sicad.sicad_backend.model.HorarioCurso;
import com.sicad.sicad_backend.dto.horarioCurso.HorarioCursoRequestDTO;
import com.sicad.sicad_backend.dto.base.GenericReponse;
import com.sicad.sicad_backend.service.interfaces.IHorarioCursoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/horario_curso")
@RequiredArgsConstructor
public class HorarioCursoController {
    private final IHorarioCursoService service;

    private final ModelMapper modelMapper;

    @GetMapping("/listar")
    public ResponseEntity<GenericReponse<HorarioCursoRequestDTO>> findAll() throws Exception {
        List<HorarioCursoRequestDTO> lista = service.findAll()
                .stream()
                .map(this::convertToDTO)
                .toList();

        return ResponseEntity.ok(
                new GenericReponse<>(200, "Lista de HorarioCursos", lista)
        );
    }
    @GetMapping("/buscar/{id}")
    public ResponseEntity<GenericObjectResponse<HorarioCursoRequestDTO>>  findById(@PathVariable("id") Integer id) throws Exception {
        HorarioCurso obj = service.findById(id);
        return ResponseEntity.ok(
                new GenericObjectResponse<>(200, "HorarioCurso encontrada", convertToDTO(obj))
        );
    }
    @PostMapping("/guardar")
    public ResponseEntity<GenericReponse<HorarioCursoRequestDTO>> save(@Valid @RequestBody HorarioCursoRequestDTO dto) throws Exception {
        HorarioCurso obj = service.save(convertToEntity(dto));
        return new ResponseEntity<>(new GenericReponse<>(
                201, "HorarioCurso creada", List.of(convertToDTO(obj))
        ), HttpStatus.CREATED);
    }
    @PutMapping("/actualizar/{id}")
    public ResponseEntity<GenericReponse<HorarioCursoRequestDTO>> update(@Valid @PathVariable("id") Integer id, @RequestBody HorarioCursoRequestDTO dto) throws Exception {
        HorarioCurso obj = service.update(id,convertToEntity(dto));
        return ResponseEntity.ok(
                new GenericReponse<>(200, "HorarioCurso actualizada", List.of(convertToDTO(obj)))
        );
    }


    private HorarioCursoRequestDTO convertToDTO(HorarioCurso obj) {
        return modelMapper.map(obj, HorarioCursoRequestDTO.class);
    }
    private HorarioCurso convertToEntity(HorarioCursoRequestDTO dto) {
        return modelMapper.map(dto, HorarioCurso.class);
    }
}