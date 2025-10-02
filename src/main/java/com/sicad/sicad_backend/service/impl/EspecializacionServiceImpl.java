package com.sicad.sicad_backend.service.impl;

import com.sicad.sicad_backend.dto.Especializacion.EspecializacionCreateRequest;
import com.sicad.sicad_backend.dto.Especializacion.EspecializacionDetalleResponse;
import com.sicad.sicad_backend.dto.Especializacion.EspecializacionResumenResponse;
import com.sicad.sicad_backend.dto.Especializacion.EspecializacionUdpdateRequest;
import com.sicad.sicad_backend.dto.base.GenericObjectResponse;
import com.sicad.sicad_backend.dto.base.GenericReponse;
import com.sicad.sicad_backend.model.*;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import com.sicad.sicad_backend.repository.interfaces.IAsignaturaRepo;
import com.sicad.sicad_backend.repository.interfaces.ICicloAcademicoRepo;
import com.sicad.sicad_backend.repository.interfaces.IDocenteRepo;
import com.sicad.sicad_backend.repository.interfaces.IEspecializacionRepo;
import com.sicad.sicad_backend.service.base.CRUDImpl;
import com.sicad.sicad_backend.service.interfaces.IEspecializacionService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EspecializacionServiceImpl
        extends CRUDImpl<Especializacion,Integer>
        implements IEspecializacionService {

    private final IEspecializacionRepo especializacionRepo;
    private final IDocenteRepo docenteRepo;
    private final IAsignaturaRepo asignaturaRepo;
    private final ICicloAcademicoRepo cicloAcademicoRepo;
    private final ModelMapper modelMapper;

    @Override
    protected IGenericRepo<Especializacion, Integer> getRepo() {
        return especializacionRepo;
    }
    public GenericObjectResponse<EspecializacionDetalleResponse> registrarEspecializacion(EspecializacionCreateRequest request){
        Docente docente = docenteRepo.findById(request.getIdDocente()).orElse(null);
        if(docente == null){
            return new GenericObjectResponse<>(404,"docente no encontrado",null);
        }
        Asignatura asignatura =asignaturaRepo.findById(request.getIdAsignatura()).orElse(null);
        if(asignatura == null){
            return new GenericObjectResponse<>(404,"asignatura no encontrada",null);
        }
        Especializacion especializacion = Especializacion.builder()
                .asignatura(asignatura)
                .docente(docente)
                .enabled(true)
                .build();

        especializacionRepo.save(especializacion);


        return new GenericObjectResponse<>(200,"Especializacion creada exitozamente",convertToDetalle(especializacion));
    }
    public GenericReponse<EspecializacionDetalleResponse> registrarAllEspecializacion(List<EspecializacionCreateRequest> request){
        List<EspecializacionDetalleResponse> registrados = new ArrayList<>();
        int errorCount =0;
        for(EspecializacionCreateRequest request1 : request){
            GenericObjectResponse<EspecializacionDetalleResponse> response = registrarEspecializacion(request1);
            if(response.status() ==201 && response.data() !=null){
                registrados.add(response.data());
            } else {
                errorCount++;
            }
        }
        String mensaje = String.format("Error al registrar: %d. fallido: fallido: %d.",registrados.size(),errorCount);
        return new GenericReponse<>(400,mensaje,registrados);
    }
    public GenericObjectResponse<EspecializacionDetalleResponse> actualizarEspecialidad(Integer idEspecializacion, EspecializacionUdpdateRequest request) {
        Especializacion esp= especializacionRepo.findById(idEspecializacion).orElse(null);
        if (esp == null) {
            return new GenericObjectResponse<>(404, "Especialización no encontrado", null);
        }
        if(request.getIdAsignatura()!=null){
            Asignatura asignatura =asignaturaRepo.findById(request.getIdAsignatura()).orElse(null);
            if(asignatura == null){
                return new GenericObjectResponse<>(404,"asignatura no encontrada",null);
            }
            esp.setAsignatura(asignatura);
        }
        if(request.getIdDocente()!=null){
            Docente docente = docenteRepo.findById(request.getIdDocente()).orElse(null);
            if(docente == null){
                return new GenericObjectResponse<>(404,"docente no encontrado",null);
            }
            esp.setDocente(docente);
        }
        especializacionRepo.save(esp);

        EspecializacionDetalleResponse dto = modelMapper.map(esp, EspecializacionDetalleResponse.class);
        return new GenericObjectResponse<>(200, "Especialización actualizado exitosamente", dto);
    }

    public GenericObjectResponse<List<EspecializacionResumenResponse>> listarEspecializacionDocente(Integer idDocente) {
        // Validar existencia de docente
        if (!docenteRepo.existsByIdDocente(idDocente)) {
            return new GenericObjectResponse<>(400, "Docente no encontrado", null);
        }


        // Obtener preferencias filtradas
        List<Especializacion> especializaciones = especializacionRepo.listarEspecializacionesPorDocente(idDocente);
        if(especializaciones.isEmpty()){
            return new GenericObjectResponse<>(400, "No hay especializacion registradas para este docente en este ciclo academico", null);
        }

        // Convertir a DTOs
        List<EspecializacionResumenResponse> listaDTO = especializaciones.stream()
                .map(p -> modelMapper.map(p, EspecializacionResumenResponse.class))
                .collect(Collectors.toList());


        return new GenericObjectResponse<>(200, "Especializaciones de docente obtenida correctamente", listaDTO);
    }
    public GenericObjectResponse<String> eliminarEspecializacion(Integer idEspecializacion) {
        // Validación de parámetro
        if (idEspecializacion == null) {
            return new GenericObjectResponse<>(400, "idEspecializacion no proporcionado", null);
        }

        // Validar existencia del curso
        Especializacion especializacion = especializacionRepo.findById(idEspecializacion).orElse(null);
        if (especializacion == null) {
            return new GenericObjectResponse<>(404, "Especializacion  no encontrado", null);
        }

        // desabilitar
        especializacion.setEnabled(false);
        especializacionRepo.save(especializacion);
        return new GenericObjectResponse<>(200, "se elimino la Especialización exitosamente", null);
    }

    private EspecializacionDetalleResponse convertToDetalle(Especializacion especializacion){
        return modelMapper.map(especializacion, EspecializacionDetalleResponse.class);
    }

    @Override
    public List<Especializacion> findByEnabledTrue() {
        return especializacionRepo.findByEnabledTrue();
    }
}
