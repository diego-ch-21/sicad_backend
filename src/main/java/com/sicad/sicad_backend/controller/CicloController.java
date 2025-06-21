package com.sicad.sicad_backend.controller;

import com.sicad.sicad_backend.dto.base.GenericObjectResponse;
import com.sicad.sicad_backend.dto.ciclo.CicloRequestDTO;
import com.sicad.sicad_backend.dto.base.GenericReponse;
import com.sicad.sicad_backend.model.Ciclo;
import com.sicad.sicad_backend.service.interfaces.ICicloService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ciclo")
@RequiredArgsConstructor
public class CicloController {

    private final ICicloService service;
    private final ModelMapper modelMapper;

    @GetMapping("/listar")
    public ResponseEntity<GenericReponse<CicloRequestDTO>> findAll() throws Exception {
        List<CicloRequestDTO> lista = service.findAll()
                .stream()
                .map(this::convertToDTO)
                .toList();
        return ResponseEntity.ok(
                new GenericReponse<>(200, "Lista de Ciclos", lista)
        );
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<GenericObjectResponse<CicloRequestDTO>> findById(@PathVariable("id") Integer id) throws Exception {
        Ciclo obj = service.findById(id);
        return ResponseEntity.ok(
                new GenericObjectResponse<>(200, "Ciclo encontrado", convertToDTO(obj))
        );
    }

    @PostMapping("/guardar")
    public ResponseEntity<GenericReponse<CicloRequestDTO>> save(@Valid @RequestBody CicloRequestDTO dto) throws Exception {
        Ciclo obj = service.save(convertToEntity(dto));
        return new ResponseEntity<>(
                new GenericReponse<>(201, "Ciclo creado", List.of(convertToDTO(obj))),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<GenericReponse<CicloRequestDTO>> update(@PathVariable("id") Integer id, @Valid @RequestBody CicloRequestDTO dto) throws Exception {
        Ciclo obj = service.update(id, convertToEntity(dto));
        return ResponseEntity.ok(
                new GenericReponse<>(200, "Ciclo actualizado", List.of(convertToDTO(obj)))
        );
    }


    private CicloRequestDTO convertToDTO(Ciclo obj) {
        return modelMapper.map(obj, CicloRequestDTO.class);
    }

    private Ciclo convertToEntity(CicloRequestDTO dto) {
        return modelMapper.map(dto, Ciclo.class);
    }
}