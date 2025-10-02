package com.sicad.sicad_backend.controller;

import com.sicad.sicad_backend.dto.base.GenericObjectResponse;
import com.sicad.sicad_backend.dto.base.GenericReponse;
import com.sicad.sicad_backend.dto.jefeDepartamento.JefeDepartamentoCreateRequest;
import com.sicad.sicad_backend.dto.jefeDepartamento.JefeDepartamentoDetalleResponse;
import com.sicad.sicad_backend.dto.jefeDepartamento.JefeDepartamentoUpdateRequest;
import com.sicad.sicad_backend.model.JefeDepartamento;
import com.sicad.sicad_backend.service.impl.JefeDepartamentoServiceImpl;
import com.sicad.sicad_backend.service.interfaces.IJefeDepartamentoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/jefe-departamento")
@RequiredArgsConstructor
public class JefeDepartamentoController {
    private final IJefeDepartamentoService service;
    private final JefeDepartamentoServiceImpl serviceImpl;
    private final ModelMapper modelMapper;

    @GetMapping("/listar")
    public ResponseEntity<GenericReponse<JefeDepartamentoDetalleResponse>> findAll() throws Exception {
        List<JefeDepartamentoDetalleResponse> lista = service.findByEnabledTrue()
                .stream()
                .map(this::convertToResponseDTO)
                .toList();
        return ResponseEntity.ok(
                new GenericReponse<>(200, "Lista de JefeDepartamentos", lista)
        );
    }

    @GetMapping("/buscar/{idJefeDepartamento}")
    public ResponseEntity<GenericObjectResponse<JefeDepartamentoDetalleResponse>> findById(@PathVariable("idJefeDepartamento") Integer id) throws Exception {
        JefeDepartamento obj = service.findById(id);
        return ResponseEntity.ok(
                new GenericObjectResponse<>(200, "JefeDepartamento encontrado",convertToResponseDTO(obj))
        );
    }


    @PostMapping("/insertar")
    public ResponseEntity<GenericObjectResponse<JefeDepartamentoDetalleResponse>> registrarJefeDepartamento(@Valid @RequestBody JefeDepartamentoCreateRequest request) {
        GenericObjectResponse<JefeDepartamentoDetalleResponse> response = serviceImpl.registrarJefeDepartamento(request);
        return ResponseEntity.status(response.status()).body(response);
    }

    @PutMapping("/actualizar/{idJefeDepartamento}")
    public ResponseEntity<GenericObjectResponse<JefeDepartamentoDetalleResponse>> update(@Valid @PathVariable("idJefeDepartamento") Integer id, @Valid @RequestBody JefeDepartamentoUpdateRequest dto) throws Exception {
        GenericObjectResponse<JefeDepartamentoDetalleResponse> response = serviceImpl.actualizarJefeDepartamento(id, dto);
        return ResponseEntity.status(response.status()).body(response);
    }
    @DeleteMapping("/eliminar/{idJefeDepartamento}")
    public ResponseEntity<GenericObjectResponse<String>> delete(@PathVariable("idJefeDepartamento") Integer id) {
        GenericObjectResponse<String> response = serviceImpl.eliminarJefeDepartamento(id);
        return ResponseEntity.status(response.status()).body(response);
    }

    private JefeDepartamentoCreateRequest convertToDTO(JefeDepartamento obj) {
        return modelMapper.map(obj, JefeDepartamentoCreateRequest.class);
    }
    private JefeDepartamentoDetalleResponse convertToResponseDTO(JefeDepartamento obj) {
        return modelMapper.map(obj, JefeDepartamentoDetalleResponse.class);
    }

    private JefeDepartamento convertToEntity(JefeDepartamentoCreateRequest dto) {
        return modelMapper.map(dto, JefeDepartamento.class);
    }
}
