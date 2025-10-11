package com.sicad.sicad_backend.controller;

import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.dto.cursoHorario.HorarioCreateRequest;
import com.sicad.sicad_backend.dto.cursoHorario.HorarioDetalleResponse;
import com.sicad.sicad_backend.dto.cursoHorario.HorarioUpdateRequest;
import com.sicad.sicad_backend.service.interfaces.ICursoHorarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/horario")
@RequiredArgsConstructor
public class CursoHorarioController {
    private final ICursoHorarioService service;

    @GetMapping("/listar/{idCurso}")
    public ResponseEntity<BaseListReponse<HorarioDetalleResponse>>
        listarHorarioCurso(@PathVariable("idCurso") Integer id){
        BaseListReponse<HorarioDetalleResponse> response = service.listarPorCursoHabilitado(id);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/{idCurso}/insertar")
    public ResponseEntity<BaseObjectResponse<HorarioDetalleResponse>>
            saveHorario(@PathVariable("idCurso") Integer id,@Valid @RequestBody HorarioCreateRequest request) {
        BaseObjectResponse<HorarioDetalleResponse> response = service.registrarHorario(id,request);
        return ResponseEntity.status(response.status()).body(response);
    }

    @PostMapping("/{idCurso}/insertar-all")
    public ResponseEntity<BaseListReponse<HorarioDetalleResponse>>
            saveHorarioAll(@PathVariable("idCurso") Integer id, @Valid @RequestBody List<HorarioCreateRequest> request) {
        BaseListReponse<HorarioDetalleResponse> response = service.registrarVariosHorario(id,request);
        return ResponseEntity.status(response.status()).body(response);
    }
    @PutMapping("/actualizar/{idCursoHorario}")
    public ResponseEntity<BaseObjectResponse<HorarioDetalleResponse>>
    actualizarHorario(@PathVariable("idCursoHorario") Integer id,@Valid @RequestBody HorarioUpdateRequest request) {
        BaseObjectResponse<HorarioDetalleResponse> response = service.actualizarHorario(id,request);
        return ResponseEntity.status(response.status()).body(response);
    }
    @DeleteMapping("/eliminar/{idCursoHorario}")
    public ResponseEntity<BaseObjectResponse<String>> deleteHorario(@PathVariable("idCursoHorario") Integer id) {
        BaseObjectResponse<String> response = service.eliminarHorario(id);
        return ResponseEntity.status(response.status()).body(response);
    }


}
