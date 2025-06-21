package com.sicad.sicad_backend.controller;

import com.sicad.sicad_backend.dto.director.DirectorRequestDTO;
import com.sicad.sicad_backend.dto.base.GenericReponse;
import com.sicad.sicad_backend.model.Director;
import com.sicad.sicad_backend.service.interfaces.IDirectorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/director")
@RequiredArgsConstructor
public class DirectorController {

    private final IDirectorService service;
    private final ModelMapper modelMapper;

    @GetMapping("/listar")
    public ResponseEntity<GenericReponse<DirectorRequestDTO>> findAll() throws Exception {
        List<DirectorRequestDTO> lista = service.findAll()
                .stream()
                .map(this::convertToDTO)
                .toList();
        return ResponseEntity.ok(
                new GenericReponse<>(200, "Lista de Directores", lista)
        );
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<GenericReponse<DirectorRequestDTO>> findById(@PathVariable("id") Integer id) throws Exception {
        Director obj = service.findById(id);
        return ResponseEntity.ok(
                new GenericReponse<>(200, "Director encontrado", List.of(convertToDTO(obj)))
        );
    }

    @PostMapping("/guardar")
    public ResponseEntity<GenericReponse<DirectorRequestDTO>> save(@Valid @RequestBody DirectorRequestDTO dto) throws Exception {
        Director obj = service.save(convertToEntity(dto));
        return new ResponseEntity<>(
                new GenericReponse<>(201, "Director creado", List.of(convertToDTO(obj))),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<GenericReponse<DirectorRequestDTO>> update(@PathVariable("id") Integer id, @Valid @RequestBody DirectorRequestDTO dto) throws Exception {
        Director obj = service.update(id, convertToEntity(dto));
        return ResponseEntity.ok(
                new GenericReponse<>(200, "Director actualizado", List.of(convertToDTO(obj)))
        );
    }

    private DirectorRequestDTO convertToDTO(Director obj) {
        return modelMapper.map(obj, DirectorRequestDTO.class);
    }

    private Director convertToEntity(DirectorRequestDTO dto) {
        return modelMapper.map(dto, Director.class);
    }
}
