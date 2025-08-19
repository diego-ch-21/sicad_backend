package com.sicad.sicad_backend.controller;


import com.sicad.sicad_backend.dto.asignacion.AsignacionDetalleResponse;
import com.sicad.sicad_backend.dto.asignacion.AsignacionUpdateRequest;
import com.sicad.sicad_backend.dto.base.GenericObjectResponse;
import com.sicad.sicad_backend.model.Asignacion;
import com.sicad.sicad_backend.dto.asignacion.AsignacionCreateRequest;
import com.sicad.sicad_backend.dto.base.GenericReponse;
import com.sicad.sicad_backend.service.impl.AsignacionServiceImpl;
import com.sicad.sicad_backend.service.interfaces.IAsignacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/asignacion")
@RequiredArgsConstructor
public class AsignacionController {
    private final IAsignacionService service;
    private final AsignacionServiceImpl asignacionServiceImpl;

    private final ModelMapper modelMapper;

    @GetMapping("/listar")
    public ResponseEntity<GenericReponse<AsignacionDetalleResponse>> findAll() throws Exception {
        List<AsignacionDetalleResponse> lista = service.findAll()
                .stream()
                .map(this::convertToDTO)
                .toList();

        return ResponseEntity.ok(
                new GenericReponse<>(200, "Lista de Asignacions", lista)
        );
    }
    @GetMapping("/buscar/{id}")
    public ResponseEntity<GenericObjectResponse<AsignacionDetalleResponse>>  findById(@PathVariable("id") Integer id) throws Exception {
        Asignacion obj = service.findById(id);
        return ResponseEntity.ok(
                new GenericObjectResponse<>(200, "Asignacion encontrada", convertToDTO(obj))
        );
    }
    /*
    @PostMapping("/insertar")
    public ResponseEntity<GenericObjectResponse<AsignacionDetalleResponse>> save(@Valid @RequestBody AsignacionCreateRequest request){
        GenericObjectResponse<AsignacionDetalleResponse> response = asignacionServiceImpl.registrarAsignacion(request);
        return ResponseEntity.status(response.status()).body(response);

    }

     */
    @PutMapping("/actualizar/{id}")
    public ResponseEntity<GenericObjectResponse<AsignacionDetalleResponse>> update(@Valid @PathVariable("id") Integer id, @RequestBody AsignacionUpdateRequest request){
        GenericObjectResponse<AsignacionDetalleResponse> response = asignacionServiceImpl.actualizarAsignacion(id, request);
        return ResponseEntity.status(response.status()).body(response);
    }
    private AsignacionDetalleResponse convertToDTO(Asignacion obj) {
        return modelMapper.map(obj, AsignacionDetalleResponse.class);
    }
    /*
    @DeleteMapping("/eliminar/{idCicloAcademico}")
    public ResponseEntity<GenericObjectResponse<String>> delete(@PathVariable("idCicloAcademico") Integer id) {
        GenericObjectResponse<String> response = asignacionServiceImpl.eliminarAsignacionCicloAcademico(id);
        return ResponseEntity.status(response.status()).body(response);
    }

     */
    // NUEVO ENDPOINT PARA EL ALGORITMO HÍBRIDO GA + PSO
    @PostMapping("/algoritmo/{idCicloAcademico}")
    public ResponseEntity<GenericObjectResponse<List<AsignacionDetalleResponse>>> asignarConAlgoritmoHibrido(
            @PathVariable("idCicloAcademico") Integer idCicloAcademico) {

        try {
            System.out.println("Solicitud de asignación con algoritmo híbrido para ciclo academico: " + idCicloAcademico);

            // Llamar al método del service que ejecuta el algoritmo híbrido GA+PSO
            GenericObjectResponse<List<AsignacionDetalleResponse>> response =
                    asignacionServiceImpl.asignarConAlgoritmoGeneticoPSO(idCicloAcademico);

            // Log del resultado
            if (response.status() == 201) {
                System.out.println("Algoritmo híbrido completado exitosamente. Asignaciones generadas: " +
                        (response.data() != null ? response.data().size() : 0));
            } else {
                System.out.println("Algoritmo híbrido falló con status: " + response.status() +
                        " - Mensaje: " + response.message());
            }

            return ResponseEntity.status(response.status()).body(response);

        } catch (IllegalArgumentException e) {
            System.out.println("Error de validación en algoritmo híbrido: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new GenericObjectResponse<>(400,
                            "Error de validación: " + e.getMessage(), null));

        } catch (Exception e) {
            System.out.println("Error crítico en algoritmo híbrido para carga electiva " + idCicloAcademico + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new GenericObjectResponse<>(500,
                            "Error interno del servidor en algoritmo híbrido: " + e.getMessage(), null));
        }
    }
    // ENDPOINT PARA OBTENER ESTADÍSTICAS DETALLADAS DEL ALGORITMO
    @GetMapping("/algoritmo/estadisticas/{idCargaElectiva}")
    public ResponseEntity<GenericObjectResponse<Map<String, Object>>> obtenerEstadisticasAsignacion(
            @PathVariable("idCargaElectiva") Integer idCargaElectiva) {

        try {
            System.out.println("Solicitud de estadísticas de asignación para carga electiva: " + idCargaElectiva);

            // Llamar al service para obtener estadísticas completas
            GenericObjectResponse<Map<String, Object>> response =
                    asignacionServiceImpl.obtenerEstadisticasAsignacion(idCargaElectiva);

            // System.out.println del resultado según el status
            if (response.status() == 200) {
                Map<String, Object> estadisticas = response.data();
                if (estadisticas != null) {
                    System.out.println("Estadísticas generadas exitosamente:");
                    System.out.println("   • Total cursos: " + estadisticas.get("totalCursos"));
                    System.out.println("   • Cursos asignados: " + estadisticas.get("cursosAsignados"));
                    System.out.println("   • Docentes utilizados: " + estadisticas.get("docentesUtilizados"));
                    System.out.println("   • Cobertura: " + String.format("%.1f", (Double) estadisticas.get("porcentajeCobertura")) + "%");
                }
            } else {
                System.out.println("Error al generar estadísticas - Status: " + response.status() + " - " + response.message());
            }

            return ResponseEntity.status(response.status()).body(response);

        } catch (IllegalArgumentException e) {
            System.out.println("Error de validación en estadísticas: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new GenericObjectResponse<>(400,
                            "Error de validación: " + e.getMessage(), null));

        } catch (Exception e) {
            System.out.println("Error crítico al obtener estadísticas para carga electiva " + idCargaElectiva + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new GenericObjectResponse<>(500,
                            "Error interno del servidor al obtener estadísticas: " + e.getMessage(), null));
        }
    }
}