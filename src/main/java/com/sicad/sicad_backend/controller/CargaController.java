package com.sicad.sicad_backend.controller;

import com.sicad.sicad_backend.dto.asignatura.AsignaturaDetalleResponse;
import com.sicad.sicad_backend.dto.base.GenericObjectResponse;
import com.sicad.sicad_backend.dto.base.GenericReponse;
import com.sicad.sicad_backend.dto.carga.CargaDetalleResponse;
import com.sicad.sicad_backend.dto.docente.DocenteDetalleResponse;
import com.sicad.sicad_backend.model.Asignatura;
import com.sicad.sicad_backend.model.Carga;
import com.sicad.sicad_backend.service.impl.CargaServiceImpl;
import com.sicad.sicad_backend.service.interfaces.ICargaService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/carga")
@RequiredArgsConstructor
public class CargaController {

    private final ICargaService service;
    private final CargaServiceImpl serviceImpl;
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
    @GetMapping("/listar/{idCicloAcademico}")
    public ResponseEntity<GenericReponse<CargaDetalleResponse>>
        findAllCicloAcademico(@PathVariable("idCicloAcademico") Integer idCicloAcademico) throws Exception {
        List<CargaDetalleResponse> lista = service.findByEnabledTrueAndCicloAcademico_Id(idCicloAcademico)
                .stream()
                .map(this::convertToDetalle)
                .toList();
        return ResponseEntity.ok(
                new GenericReponse<>(200, "Lista de cargas", lista)
        );
    }
    @GetMapping("buscar/{idCarga}")
    public ResponseEntity<GenericObjectResponse<CargaDetalleResponse>>  findByIdCarga(@PathVariable("idCarga") Integer id) throws Exception {
        Carga obj  = service.findById(id);
        return ResponseEntity.ok(
                new GenericObjectResponse<>(200, "Docente encontrada", convertToDetalle(obj))
        );
    }
    @GetMapping("principal/{idCicloAcademico}")
    public ResponseEntity<GenericObjectResponse<CargaDetalleResponse>>  obtenerCargaPrincipal(@PathVariable("idCicloAcademico") Integer id) throws Exception {
        GenericObjectResponse<CargaDetalleResponse> response = serviceImpl.obtenerCargaDefecto(id);
        return ResponseEntity.status(response.status()).body(response);
    }

    @DeleteMapping("/eliminar/{idCarga}")
    public ResponseEntity<GenericObjectResponse<String>>
            delete(@PathVariable("idCarga") Integer id) {
        GenericObjectResponse<String> response = serviceImpl.eliminarCarga(id);
        return ResponseEntity.status(response.status()).body(response);
    }

    private CargaDetalleResponse convertToDetalle(Carga obj) {
        return modelMapper.map(obj, CargaDetalleResponse.class);
    }

}
