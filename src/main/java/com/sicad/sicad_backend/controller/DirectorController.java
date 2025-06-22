package com.sicad.sicad_backend.controller;

import com.sicad.sicad_backend.dto.base.GenericObjectResponse;
import com.sicad.sicad_backend.dto.director.DirectorRequestDTO;
import com.sicad.sicad_backend.dto.base.GenericReponse;
import com.sicad.sicad_backend.dto.director.DirectorResponseDTO;
import com.sicad.sicad_backend.dto.director.DirectorUpdateRequestDTO;
import com.sicad.sicad_backend.dto.docente.DocenteRequestDTO;
import com.sicad.sicad_backend.dto.docente.DocenteResponseDTO;
import com.sicad.sicad_backend.model.Director;
import com.sicad.sicad_backend.service.impl.DirectorServiceImpl;
import com.sicad.sicad_backend.service.impl.DocenteServiceImpl;
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
    private final DirectorServiceImpl serviceImpl;
    private final ModelMapper modelMapper;

    @GetMapping("/listar")
    public ResponseEntity<GenericReponse<DirectorResponseDTO>> findAll() throws Exception {
        List<DirectorResponseDTO> lista = service.findAll()
                .stream()
                .map(this::convertToResponseDTO)
                .toList();
        return ResponseEntity.ok(
                new GenericReponse<>(200, "Lista de Directores", lista)
        );
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<GenericObjectResponse<DirectorResponseDTO>> findById(@PathVariable("id") Integer id) throws Exception {
        Director obj = service.findById(id);
        return ResponseEntity.ok(
                new GenericObjectResponse<>(200, "Director encontrado",convertToResponseDTO(obj))
        );
    }


    @PostMapping("/registrar")
    public ResponseEntity<GenericObjectResponse<DirectorResponseDTO>> registrarDocente(@Valid @RequestBody DirectorRequestDTO request) {
        GenericObjectResponse<DirectorResponseDTO> response = serviceImpl.registrarDirector(request);
        return ResponseEntity.status(response.status()).body(response);
    }



    @PutMapping("/actualizar/{id}")
    public ResponseEntity<GenericObjectResponse<DirectorResponseDTO>> update(@Valid @PathVariable("id") Integer id,@Valid @RequestBody DirectorUpdateRequestDTO dto) throws Exception {
        GenericObjectResponse<DirectorResponseDTO> response = serviceImpl.actualizarDirector(id, dto);
        return ResponseEntity.status(response.status()).body(response);
    }

    private DirectorRequestDTO convertToDTO(Director obj) {
        return modelMapper.map(obj, DirectorRequestDTO.class);
    }
    private DirectorResponseDTO convertToResponseDTO(Director obj) {
        return modelMapper.map(obj, DirectorResponseDTO.class);
    }

    private Director convertToEntity(DirectorRequestDTO dto) {
        return modelMapper.map(dto, Director.class);
    }
}
