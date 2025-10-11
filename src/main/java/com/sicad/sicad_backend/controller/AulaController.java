package com.sicad.sicad_backend.controller;

import com.sicad.sicad_backend.dto.Aula.AulaCreateRequest;
import com.sicad.sicad_backend.dto.Aula.AulaDetalleResponse;
import com.sicad.sicad_backend.dto.Aula.AulaUpdateRequest;
import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.model.Aula;
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
    public ResponseEntity<BaseListReponse<AulaDetalleResponse>> findAll() throws Exception{
        List<AulaDetalleResponse> list = service.findByEnabledTrue()
                .stream()
                .map(this::convertToResponseDTO)
                .toList();
        return ResponseEntity.ok(
                new BaseListReponse<>(200,"Lista de Aulas",list)
        );
    }
    @GetMapping("/buscar/{idAula}")
    public ResponseEntity<BaseObjectResponse<AulaDetalleResponse>>
        findById(@PathVariable("idAula") Integer id) throws Exception {
        Aula aula = service.findById(id);
        return ResponseEntity.ok(
                new BaseObjectResponse<>(200, "Aula encontrado", convertToResponseDTO(aula))
        );
    }

    @PostMapping("/insertar")
    public ResponseEntity<BaseObjectResponse<AulaDetalleResponse>> registrar(
            @Valid @RequestBody AulaCreateRequest request) {
        BaseObjectResponse<AulaDetalleResponse> response = serviceImpl.registrarAula(request);
        return ResponseEntity.status(response.status()).body(response);
    }
    @PostMapping("/insertar-all")
    public ResponseEntity<BaseListReponse<AulaDetalleResponse>> registrarAll(@Valid @RequestBody List<AulaCreateRequest> dto) {
        BaseListReponse<AulaDetalleResponse> response = serviceImpl.registrarAulasMultiples(dto);
        return ResponseEntity.status(response.status()).body(response);
    }

    @PutMapping("/actualizar/{idAula}")
    public ResponseEntity<BaseObjectResponse<AulaDetalleResponse>> actualizar(
            @PathVariable("idAula") Integer id,
            @Valid @RequestBody AulaUpdateRequest request) {
        BaseObjectResponse<AulaDetalleResponse> response = serviceImpl.actualizarAula(id, request);
        return ResponseEntity.status(response.status()).body(response);
    }
    @DeleteMapping("/eliminar/{idAula}")
    public ResponseEntity<BaseObjectResponse<String>> delete(@PathVariable("idAula") Integer id) {
        BaseObjectResponse<String> response = serviceImpl.eliminarAula(id);
        return ResponseEntity.status(response.status()).body(response);
    }


    private AulaDetalleResponse convertToResponseDTO(Aula obj) {
        return modelMapper.map(obj, AulaDetalleResponse.class);
    }
}
