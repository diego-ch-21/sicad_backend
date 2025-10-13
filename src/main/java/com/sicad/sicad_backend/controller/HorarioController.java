package com.sicad.sicad_backend.controller;

import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.dto.cursoHorario.HorarioCreateRequest;
import com.sicad.sicad_backend.dto.cursoHorario.HorarioDetalleResponse;
import com.sicad.sicad_backend.dto.cursoHorario.HorarioUpdateRequest;
import com.sicad.sicad_backend.service.interfaces.IHorarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/horario")
@RequiredArgsConstructor
public class HorarioController {
    private final IHorarioService service;

    @GetMapping("/listar/{idCurso}")
    public ResponseEntity<BaseListReponse<HorarioDetalleResponse>>
            listar(@PathVariable("idCurso") Integer idCurso){
        BaseListReponse<HorarioDetalleResponse> response = service.listarPorCurso(idCurso);
        return ResponseEntity.status(response.status()).body(response);
    }

    @GetMapping("/buscar/{idHorario}")
    public ResponseEntity<BaseObjectResponse<HorarioDetalleResponse>>
            buscar(@PathVariable("idHorario") Integer id) {
        BaseObjectResponse<HorarioDetalleResponse> response = service.buscar(id);
        return ResponseEntity.status(response.status()).body(response);
    }

    @PostMapping("/insertar/{idCurso}")
    public ResponseEntity<BaseObjectResponse<HorarioDetalleResponse>>
            registrar(@PathVariable("idCurso") Integer id,@Valid @RequestBody HorarioCreateRequest request) {
        BaseObjectResponse<HorarioDetalleResponse> response = service.registrarPorCurso(id,request);
        return ResponseEntity.status(response.status()).body(response);
    }

    @PostMapping("/insertar-all/{idCurso}")
    public ResponseEntity<BaseListReponse<HorarioDetalleResponse>>
            registrarAll(@PathVariable("idCurso") Integer id,@Valid @RequestBody List<HorarioCreateRequest> request) {
        BaseListReponse<HorarioDetalleResponse> response = service.registrarAllPorCurso(id,request);
        return ResponseEntity.status(response.status()).body(response);
    }
    @PutMapping("/actualizar/{idHorario}")
    public ResponseEntity<BaseObjectResponse<HorarioDetalleResponse>>
            actualizar(@PathVariable("idHorario") Integer id, @Valid @RequestBody HorarioUpdateRequest dto) {
        BaseObjectResponse<HorarioDetalleResponse> response = service.actualizar(id, dto);
        return ResponseEntity.status(response.status()).body(response);
    }
    @DeleteMapping("/eliminar/{idHorario}")
    public ResponseEntity<BaseObjectResponse<String>>
            eliminar(@PathVariable("idHorario") Integer id) {
        BaseObjectResponse<String> response = service.eliminar(id);
        return ResponseEntity.status(response.status()).body(response);
    }

}
