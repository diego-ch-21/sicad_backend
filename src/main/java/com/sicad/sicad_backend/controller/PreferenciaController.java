package com.sicad.sicad_backend.controller;

import com.sicad.sicad_backend.dto.PreferenciaDTO;
import com.sicad.sicad_backend.dto.base.GenericReponse;
import com.sicad.sicad_backend.model.Preferencia;
import com.sicad.sicad_backend.service.interfaces.IPreferenciaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/preferencia")
@RequiredArgsConstructor
public class PreferenciaController {

    private final IPreferenciaService service;
    private final ModelMapper modelMapper;

    @GetMapping("/listar")
    public ResponseEntity<GenericReponse<PreferenciaDTO>> findAll() throws Exception {
        List<PreferenciaDTO> lista = service.findAll()
                .stream()
                .map(this::convertToDTO)
                .toList();
        return ResponseEntity.ok(
                new GenericReponse<>(200, "Lista de Preferencias", lista)
        );
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<GenericReponse<PreferenciaDTO>> findById(@PathVariable("id") Integer id) throws Exception {
        Preferencia obj = service.findById(id);
        return ResponseEntity.ok(
                new GenericReponse<>(200, "Preferencia encontrada", List.of(convertToDTO(obj)))
        );
    }

    @PostMapping("/guardar")
    public ResponseEntity<GenericReponse<PreferenciaDTO>> save(@Valid @RequestBody PreferenciaDTO dto) throws Exception {
        Preferencia obj = service.save(convertToEntity(dto));
        return new ResponseEntity<>(
                new GenericReponse<>(201, "Preferencia creada", List.of(convertToDTO(obj))),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<GenericReponse<PreferenciaDTO>> update(@PathVariable("id") Integer id, @Valid @RequestBody PreferenciaDTO dto) throws Exception {
        Preferencia obj = service.update(id, convertToEntity(dto));
        return ResponseEntity.ok(
                new GenericReponse<>(200, "Preferencia actualizada", List.of(convertToDTO(obj)))
        );
    }

    private PreferenciaDTO convertToDTO(Preferencia obj) {
        return modelMapper.map(obj, PreferenciaDTO.class);
    }

    private Preferencia convertToEntity(PreferenciaDTO dto) {
        return modelMapper.map(dto, Preferencia.class);
    }
}
