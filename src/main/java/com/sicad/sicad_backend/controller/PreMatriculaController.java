package com.sicad.sicad_backend.controller;

import com.sicad.sicad_backend.dto.base.GenericObjectResponse;
import com.sicad.sicad_backend.dto.base.GenericReponse;
import com.sicad.sicad_backend.dto.preMatricula.PreMatriculaCreateRequest;
import com.sicad.sicad_backend.dto.preMatricula.PreMatriculaDetalleResponse;
import com.sicad.sicad_backend.dto.preMatricula.PreMatriculaUpdateRequest;
import com.sicad.sicad_backend.model.PreMatricula;
import com.sicad.sicad_backend.service.impl.PreMatriculaServiceImpl;
import com.sicad.sicad_backend.service.interfaces.IPreMatriculaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/pre-matricula")
@RequiredArgsConstructor
public class PreMatriculaController {

    private final IPreMatriculaService service;
    private final ModelMapper modelMapper;
    private final PreMatriculaServiceImpl serviceImpl;

    @GetMapping("/listar")
    public ResponseEntity<GenericReponse<PreMatriculaDetalleResponse>> findAll() throws Exception {
        List<PreMatriculaDetalleResponse> lista = service.findByEnabledTrue()
                .stream()
                .map(this::convertToDTO)
                .toList();
        return ResponseEntity.ok(
                new GenericReponse<>(200, "Lista de Pre-matricula", lista)
        );
    }

    @GetMapping("/buscar/{idPreMatricula}")
    public ResponseEntity<GenericObjectResponse<PreMatriculaDetalleResponse>>
            findById(@PathVariable("idPreMatricula") Integer id) throws Exception {
        PreMatricula obj = service.findById(id);
        return ResponseEntity.ok(
                new GenericObjectResponse<>(200, "idPreMatricula encontrada", convertToDTO(obj))
        );
    }

    @PostMapping("/insertar")
    public ResponseEntity<GenericObjectResponse<PreMatriculaDetalleResponse>> save(@Valid @RequestBody PreMatriculaCreateRequest request){
        GenericObjectResponse<PreMatriculaDetalleResponse> response = serviceImpl.registrarPreMatricula(request);
        return ResponseEntity.status(response.status()).body(response);
    }
    @PostMapping("/insertar-all")
    public ResponseEntity<GenericReponse<PreMatriculaDetalleResponse>> saveAll(@Valid @RequestBody List<PreMatriculaCreateRequest> requests){
        GenericReponse<PreMatriculaDetalleResponse> response = serviceImpl.registrarVariosPreMatricula(requests);
        return ResponseEntity.status(response.status()).body(response);
    }

    @PutMapping("/actualizar/{idPreMatricula}")
    public ResponseEntity<GenericObjectResponse<PreMatriculaDetalleResponse>>
            update(@PathVariable("idPreMatricula") Integer id, @Valid @RequestBody PreMatriculaUpdateRequest request) throws Exception {
        GenericObjectResponse<PreMatriculaDetalleResponse> response = serviceImpl.actualizarPreMatricula(id, request);
        return ResponseEntity.status(response.status()).body(response);
    }
    @DeleteMapping("/eliminar/{idPreMatricula}")
    public ResponseEntity<GenericObjectResponse<String>> delete(@PathVariable("idPreMatricula") Integer id) {
        GenericObjectResponse<String> response = serviceImpl.eliminarPreMatricula(id);
        return ResponseEntity.status(response.status()).body(response);
    }
    @GetMapping("/listar/{idCicloAcademico}")
    public ResponseEntity<GenericReponse<PreMatriculaDetalleResponse>> findAllPorCicloAcademico(
            @PathVariable("idCicloAcademico") Integer id) throws Exception {

        GenericReponse<PreMatriculaDetalleResponse> response = serviceImpl.listarPorCicloAcademico(id);
        return ResponseEntity.ok(response);
    }



    private PreMatriculaDetalleResponse convertToDTO(PreMatricula obj) {
        return modelMapper.map(obj, PreMatriculaDetalleResponse.class);
    }
}
