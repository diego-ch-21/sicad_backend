package com.sicad.sicad_backend.controller;

import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.resultado.ResultadoDetalleResponse;
import com.sicad.sicad_backend.service.interfaces.IResultadoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/resultado")
@RequiredArgsConstructor
public class ResultadoController {
    private final IResultadoService service;

    @GetMapping("/buscar/{idResultado}")
    public ResponseEntity<BaseObjectResponse<ResultadoDetalleResponse>>
            buscar(@PathVariable("idResultado") Integer id) {
        BaseObjectResponse<ResultadoDetalleResponse> response = service.buscar(id);
        return ResponseEntity.status(response.status()).body(response);
    }

    @GetMapping("/buscar-por-carga/{idCarga}")
    public ResponseEntity<BaseObjectResponse<ResultadoDetalleResponse>>
            buscarPorDocente(@PathVariable("idCarga") Integer id) {
        BaseObjectResponse<ResultadoDetalleResponse> response = service.buscarPorCarga(id);
        return ResponseEntity.status(response.status()).body(response);
    }
    
}
