package com.sicad.sicad_backend.controller;

import com.sicad.sicad_backend.dto.CicloDTO;
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
    public ResponseEntity<GenericReponse<CicloDTO>> findAll() throws Exception {
        List<CicloDTO> lista = service.findAll()
                .stream()
                .map(this::convertToDTO)
                .toList();
        return ResponseEntity.ok(
                new GenericReponse<>(200, "Lista de Ciclos", lista)
        );
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<GenericReponse<CicloDTO>> findById(@PathVariable("id") Integer id) throws Exception {
        Ciclo obj = service.findById(id);
        return ResponseEntity.ok(
                new GenericReponse<>(200, "Ciclo encontrado", List.of(convertToDTO(obj)))
        );
    }

    @PostMapping("/guardar")
    public ResponseEntity<GenericReponse<CicloDTO>> save(@Valid @RequestBody CicloDTO dto) throws Exception {
        Ciclo obj = service.save(convertToEntity(dto));
        return new ResponseEntity<>(
                new GenericReponse<>(201, "Ciclo creado", List.of(convertToDTO(obj))),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<GenericReponse<CicloDTO>> update(@PathVariable("id") Integer id, @Valid @RequestBody CicloDTO dto) throws Exception {
        Ciclo obj = service.update(id, convertToEntity(dto));
        return ResponseEntity.ok(
                new GenericReponse<>(200, "Ciclo actualizado", List.of(convertToDTO(obj)))
        );
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Integer id) throws Exception {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    private CicloDTO convertToDTO(Ciclo obj) {
        return modelMapper.map(obj, CicloDTO.class);
    }

    private Ciclo convertToEntity(CicloDTO dto) {
        return modelMapper.map(dto, Ciclo.class);
    }
}