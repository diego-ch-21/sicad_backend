package com.sicad.sicad_backend.controller;

import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.dto.carga.CargaDetalleResponse;
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
    public ResponseEntity<BaseListReponse<CargaDetalleResponse>> findAll() throws Exception {
        List<CargaDetalleResponse> lista = service.findByEnabledTrue()
                .stream()
                .map(this::convertToDetalle)
                .toList();
        return ResponseEntity.ok(
                new BaseListReponse<>(200, "Lista de cargas", lista)
        );
    }
    @GetMapping("/listar/{idCicloAcademico}")
    public ResponseEntity<BaseListReponse<CargaDetalleResponse>>
        findAllCicloAcademico(@PathVariable("idCicloAcademico") Integer idCicloAcademico) throws Exception {
        List<CargaDetalleResponse> lista = service.findByEnabledTrueAndCicloAcademico_Id(idCicloAcademico)
                .stream()
                .map(this::convertToDetalle)
                .toList();
        return ResponseEntity.ok(
                new BaseListReponse<>(200, "Lista de cargas", lista)
        );
    }
    @GetMapping("buscar/{idCarga}")
    public ResponseEntity<BaseObjectResponse<CargaDetalleResponse>>  findByIdCarga(@PathVariable("idCarga") Integer id) throws Exception {
        Carga obj  = service.findById(id);
        return ResponseEntity.ok(
                new BaseObjectResponse<>(200, "Docente encontrada", convertToDetalle(obj))
        );
    }
    @GetMapping("principal/buscar/{idCicloAcademico}")
    public ResponseEntity<BaseObjectResponse<CargaDetalleResponse>>  obtenerCargaPrincipal(@PathVariable("idCicloAcademico") Integer id) throws Exception {
        BaseObjectResponse<CargaDetalleResponse> response = serviceImpl.obtenerCargaDefecto(id);
        return ResponseEntity.status(response.status()).body(response);
    }

    @PostMapping("principal/insertar/{idCicloAcademico}/{idCarga}")
    public ResponseEntity<BaseObjectResponse<CargaDetalleResponse>>
            insertarCargaPrincipal(@PathVariable("idCicloAcademico") Integer idCarga,@PathVariable("idCicloAcademico") Integer idCicloAcademico) throws Exception {
        BaseObjectResponse<CargaDetalleResponse> response = serviceImpl.insertarCargaDefecto(idCicloAcademico,idCarga);
        return ResponseEntity.status(response.status()).body(response);
    }

    @DeleteMapping("/eliminar/{idCarga}")
    public ResponseEntity<BaseObjectResponse<String>>
            delete(@PathVariable("idCarga") Integer id) {
        BaseObjectResponse<String> response = serviceImpl.eliminarCarga(id);
        return ResponseEntity.status(response.status()).body(response);
    }

    private CargaDetalleResponse convertToDetalle(Carga obj) {
        return modelMapper.map(obj, CargaDetalleResponse.class);
    }

}
