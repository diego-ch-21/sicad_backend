package com.sicad.sicad_backend.controller;

import com.sicad.sicad_backend.dto.Aula.AulaCreateRequest;
import com.sicad.sicad_backend.dto.Aula.AulaDetalleResponse;
import com.sicad.sicad_backend.dto.Aula.AulaUpdateRequest;
import com.sicad.sicad_backend.dto.asignatura.AsignaturaCreateRequest;
import com.sicad.sicad_backend.dto.asignatura.AsignaturaDetalleResponse;
import com.sicad.sicad_backend.dto.base.GenericObjectResponse;
import com.sicad.sicad_backend.dto.base.GenericReponse;
import com.sicad.sicad_backend.dto.cicloAcademico.CicloAcademicoCreateRequest;
import com.sicad.sicad_backend.dto.cicloAcademico.CicloAcademicoDetalleResponse;
import com.sicad.sicad_backend.dto.cicloAcademico.CicloAcademicoUpdateRequest;
import com.sicad.sicad_backend.model.Aula;
import com.sicad.sicad_backend.model.CicloAcademico;
import com.sicad.sicad_backend.service.impl.AulaServiceImpl;
import com.sicad.sicad_backend.service.interfaces.IAulaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/aula")
@RequiredArgsConstructor
public class AulaController {
    private final IAulaService service;
    private final AulaServiceImpl serviceImpl;
    private final ModelMapper modelMapper;

    @GetMapping("/listar")
    public ResponseEntity<GenericReponse<AulaDetalleResponse>> findAll() throws Exception{
        List<AulaDetalleResponse> list = service.findByEnabledTrue()
                .stream()
                .map(this::convertToResponseDTO)
                .toList();
        return ResponseEntity.ok(
                new  GenericReponse<>(200,"Lista de Aulas",list)
        );
    }
    @GetMapping("/buscar/{idAula}")
    public ResponseEntity<GenericObjectResponse<AulaDetalleResponse>>
        findById(@PathVariable("idAula") Integer id) throws Exception {
        Aula aula = service.findById(id);
        return ResponseEntity.ok(
                new GenericObjectResponse<>(200, "Aula encontrado", convertToResponseDTO(aula))
        );
    }

    @PostMapping("/insertar")
    public ResponseEntity<GenericObjectResponse<AulaDetalleResponse>> registrar(
            @Valid @RequestBody AulaCreateRequest request) {
        GenericObjectResponse<AulaDetalleResponse> response = serviceImpl.registrarAula(request);
        return ResponseEntity.status(response.status()).body(response);
    }
    @PostMapping("/insertar-all")
    public ResponseEntity<GenericReponse<AulaDetalleResponse>> registrarAll(@Valid @RequestBody List<AulaCreateRequest> dto) {
        GenericReponse<AulaDetalleResponse> response = serviceImpl.registrarAulasMultiples(dto);
        return ResponseEntity.status(response.status()).body(response);
    }

    @PutMapping("/actualizar/{idAula}")
    public ResponseEntity<GenericObjectResponse<AulaDetalleResponse>> actualizar(
            @PathVariable("idAula") Integer id,
            @Valid @RequestBody AulaUpdateRequest request) {
        GenericObjectResponse<AulaDetalleResponse> response = serviceImpl.actualizarAula(id, request);
        return ResponseEntity.status(response.status()).body(response);
    }
    @DeleteMapping("/eliminar/{idAula}")
    public ResponseEntity<GenericObjectResponse<String>> delete(@PathVariable("idAula") Integer id) {
        GenericObjectResponse<String> response = serviceImpl.eliminarAula(id);
        return ResponseEntity.status(response.status()).body(response);
    }


    private AulaDetalleResponse convertToResponseDTO(Aula obj) {
        return modelMapper.map(obj, AulaDetalleResponse.class);
    }
}
