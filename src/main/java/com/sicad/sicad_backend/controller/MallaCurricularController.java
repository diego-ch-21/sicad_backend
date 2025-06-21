package com.sicad.sicad_backend.controller;

import com.sicad.sicad_backend.dto.base.GenericObjectResponse;
import com.sicad.sicad_backend.dto.mallaCurriculara.MallaCurricularRequestDTO;
import com.sicad.sicad_backend.dto.base.GenericReponse;
import com.sicad.sicad_backend.model.MallaCurricular;
import com.sicad.sicad_backend.service.interfaces.IMallaCurricularService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/malla-curricular")
@RequiredArgsConstructor
public class MallaCurricularController {

    private final IMallaCurricularService service;
    private final ModelMapper modelMapper;

    @GetMapping("/listar")
    public ResponseEntity<GenericReponse<MallaCurricularRequestDTO>> findAll() throws Exception {
        List<MallaCurricularRequestDTO> lista = service.findAll()
                .stream()
                .map(this::convertToDTO)
                .toList();
        return ResponseEntity.ok(
                new GenericReponse<>(200, "Lista de Mallas Curriculares", lista)
        );
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<GenericObjectResponse<MallaCurricularRequestDTO>> findById(@PathVariable("id") Integer id) throws Exception {
        MallaCurricular obj = service.findById(id);
        return ResponseEntity.ok(
                new GenericObjectResponse<>(200, "Malla Curricular encontrada", convertToDTO(obj))
        );
    }

    @PostMapping("/guardar")
    public ResponseEntity<GenericReponse<MallaCurricularRequestDTO>> save(@Valid @RequestBody MallaCurricularRequestDTO dto) throws Exception {
        MallaCurricular obj = service.save(convertToEntity(dto));
        return new ResponseEntity<>(
                new GenericReponse<>(201, "Malla Curricular creada", List.of(convertToDTO(obj))),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<GenericReponse<MallaCurricularRequestDTO>> update(@PathVariable("id") Integer id, @Valid @RequestBody MallaCurricularRequestDTO dto) throws Exception {
        MallaCurricular obj = service.update(id, convertToEntity(dto));
        return ResponseEntity.ok(
                new GenericReponse<>(200, "Malla Curricular actualizada", List.of(convertToDTO(obj)))
        );
    }

    private MallaCurricularRequestDTO convertToDTO(MallaCurricular obj) {
        return modelMapper.map(obj, MallaCurricularRequestDTO.class);
    }

    private MallaCurricular convertToEntity(MallaCurricularRequestDTO dto) {
        return modelMapper.map(dto, MallaCurricular.class);
    }
}
