package com.sicad.sicad_backend.controller;

import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.base.BaseListReponse;
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
    public ResponseEntity<BaseListReponse<PreMatriculaDetalleResponse>> findAll() throws Exception {
        List<PreMatriculaDetalleResponse> lista = service.findByEnabledTrue()
                .stream()
                .map(this::convertToDTO)
                .toList();
        return ResponseEntity.ok(
                new BaseListReponse<>(200, "Lista de Pre-matricula", lista)
        );
    }

    @GetMapping("/buscar/{idPreMatricula}")
    public ResponseEntity<BaseObjectResponse<PreMatriculaDetalleResponse>>
            findById(@PathVariable("idPreMatricula") Integer id) throws Exception {
        PreMatricula obj = service.findById(id);
        return ResponseEntity.ok(
                new BaseObjectResponse<>(200, "idPreMatricula encontrada", convertToDTO(obj))
        );
    }

    @PostMapping("/insertar")
    public ResponseEntity<BaseObjectResponse<PreMatriculaDetalleResponse>> save(@Valid @RequestBody PreMatriculaCreateRequest request){
        BaseObjectResponse<PreMatriculaDetalleResponse> response = serviceImpl.registrarPreMatricula(request);
        return ResponseEntity.status(response.status()).body(response);
    }
    @PostMapping("/insertar-all")
    public ResponseEntity<BaseListReponse<PreMatriculaDetalleResponse>> saveAll(@Valid @RequestBody List<PreMatriculaCreateRequest> requests){
        BaseListReponse<PreMatriculaDetalleResponse> response = serviceImpl.registrarVariosPreMatricula(requests);
        return ResponseEntity.status(response.status()).body(response);
    }

    @PutMapping("/actualizar/{idPreMatricula}")
    public ResponseEntity<BaseObjectResponse<PreMatriculaDetalleResponse>>
            update(@PathVariable("idPreMatricula") Integer id, @Valid @RequestBody PreMatriculaUpdateRequest request) throws Exception {
        BaseObjectResponse<PreMatriculaDetalleResponse> response = serviceImpl.actualizarPreMatricula(id, request);
        return ResponseEntity.status(response.status()).body(response);
    }
    @DeleteMapping("/eliminar/{idPreMatricula}")
    public ResponseEntity<BaseObjectResponse<String>> delete(@PathVariable("idPreMatricula") Integer id) {
        BaseObjectResponse<String> response = serviceImpl.eliminarPreMatricula(id);
        return ResponseEntity.status(response.status()).body(response);
    }
    @GetMapping("/listar/{idCicloAcademico}")
    public ResponseEntity<BaseListReponse<PreMatriculaDetalleResponse>> findAllPorCicloAcademico(
            @PathVariable("idCicloAcademico") Integer id) throws Exception {

        BaseListReponse<PreMatriculaDetalleResponse> response = serviceImpl.listarPorCicloAcademico(id);
        return ResponseEntity.ok(response);
    }



    private PreMatriculaDetalleResponse convertToDTO(PreMatricula obj) {
        return modelMapper.map(obj, PreMatriculaDetalleResponse.class);
    }
}
