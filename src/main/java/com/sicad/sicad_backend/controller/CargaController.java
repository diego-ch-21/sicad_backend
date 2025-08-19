package com.sicad.sicad_backend.controller;

import com.sicad.sicad_backend.dto.asignatura.AsignaturaDetalleResponse;
import com.sicad.sicad_backend.dto.base.GenericReponse;
import com.sicad.sicad_backend.dto.carga.CargaDetalleResponse;
import com.sicad.sicad_backend.model.Asignatura;
import com.sicad.sicad_backend.model.Carga;
import com.sicad.sicad_backend.service.impl.CargaServiceImpl;
import com.sicad.sicad_backend.service.interfaces.ICargaService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/carga")
@RequiredArgsConstructor
public class CargaController {

    private final ICargaService service;
    private final CargaServiceImpl cargaService;
    private final ModelMapper modelMapper;

    @GetMapping("/listar")
    public ResponseEntity<GenericReponse<CargaDetalleResponse>> findAll() throws Exception {
        List<CargaDetalleResponse> lista = service.findByEnabledTrue()
                .stream()
                .map(this::convertToDetalle)
                .toList();
        return ResponseEntity.ok(
                new GenericReponse<>(200, "Lista de cargas", lista)
        );
    }
    @GetMapping("/listar-historial/{id}")
    public ResponseEntity<GenericReponse<CargaDetalleResponse>> findAllCicloAcademico(@PathVariable("id") Integer idCicloAcademico) throws Exception {
        List<CargaDetalleResponse> lista = service.findByEnabledTrueAndCicloAcademico_Id(idCicloAcademico)
                .stream()
                .map(this::convertToDetalle)
                .toList();
        return ResponseEntity.ok(
                new GenericReponse<>(200, "Lista de cargas", lista)
        );
    }

    private CargaDetalleResponse convertToDetalle(Carga obj) {
        return modelMapper.map(obj, CargaDetalleResponse.class);
    }

}
