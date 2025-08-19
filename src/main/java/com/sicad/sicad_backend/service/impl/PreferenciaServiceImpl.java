package com.sicad.sicad_backend.service.impl;

import com.sicad.sicad_backend.dto.base.GenericObjectResponse;
import com.sicad.sicad_backend.dto.base.GenericReponse;
import com.sicad.sicad_backend.dto.preferencia.PreferenciaCreateRequest;
import com.sicad.sicad_backend.dto.preferencia.PreferenciaDetalleResponse;
import com.sicad.sicad_backend.dto.preferencia.PreferenciaResumenResponse;
import com.sicad.sicad_backend.dto.preferencia.PreferenciaUpdateRequest;
import com.sicad.sicad_backend.model.*;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import com.sicad.sicad_backend.repository.interfaces.IAsignaturaRepo;
import com.sicad.sicad_backend.repository.interfaces.ICicloAcademicoRepo;
import com.sicad.sicad_backend.repository.interfaces.IDocenteRepo;
import com.sicad.sicad_backend.repository.interfaces.IPreferenciaRepo;
import com.sicad.sicad_backend.service.base.CRUDImpl;
import com.sicad.sicad_backend.service.interfaces.IPreferenciaService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PreferenciaServiceImpl
        extends CRUDImpl<Preferencia, Integer>
        implements IPreferenciaService {

    private final IPreferenciaRepo preferenciaRepo;
    private final IDocenteRepo docenteRepo;
    private final IAsignaturaRepo asignaturaRepo;
    private final ICicloAcademicoRepo cicloAcademicoRepo;
    private final ModelMapper modelMapper;

    @Override
    protected IGenericRepo<Preferencia, Integer> getRepo() {
        return preferenciaRepo;
    }

    public GenericObjectResponse<PreferenciaDetalleResponse> registrarPreferencia(PreferenciaCreateRequest request){
        //validar docente
        Docente docente = docenteRepo.findById(request.getIdDocente()).orElse(null);
        if(docente == null){
            System.out.println("Docente no encontrado");
            return new GenericObjectResponse<>(404, "Docente no encontrado", null);
        }
        Asignatura asignatura = asignaturaRepo.findById(request.getIdAsignatura()).orElse(null);
        if(asignatura == null){
            System.out.println("Asignatura no encontrada");
            return new GenericObjectResponse<>(404, "Asignatura no encontrada", null);
        }
        CicloAcademico cicloAcademico = cicloAcademicoRepo.findById(request.getIdCicloAcademico()).orElse(null);
        if (cicloAcademico == null) {
            return new GenericObjectResponse<>(404, "Ciclo academico no encontrada", null);
        }


        Preferencia preferencia = new Preferencia().builder()
                .docente(docente)
                .asignatura(asignatura)
                .cicloAcademico(cicloAcademico)
                .enabled(true)
                .build();
        preferenciaRepo.save(preferencia);

        PreferenciaDetalleResponse dto = modelMapper.map(preferencia, PreferenciaDetalleResponse.class);
        return new GenericObjectResponse<>(201, "Preferencia registrada", dto);
    }

    public GenericReponse<PreferenciaDetalleResponse> registrarVariosPreferencias(List<PreferenciaCreateRequest> requests){
        List<PreferenciaDetalleResponse> registrados = new ArrayList<>();
        int errorCount = 0;

        for(PreferenciaCreateRequest request: requests){
            GenericObjectResponse<PreferenciaDetalleResponse>  response = registrarPreferencia(request);
            System.out.println("status: "+response.status());
            if(response.status() == 201 || response.data() != null){
                registrados.add(response.data());
            } else {
                errorCount++;
            }
        }
        String mensaje = String.format("Preferencia registrados: %d. Fallidos: %d.", registrados.size(), errorCount);
        return new GenericReponse<>(200, mensaje, registrados);
    }



    public GenericObjectResponse<PreferenciaDetalleResponse> actualizarPreferencia(Integer id, PreferenciaUpdateRequest request) {
        Preferencia preferencia = preferenciaRepo.findById(id).orElse(null);
        if (preferencia == null) {
            return new GenericObjectResponse<>(404, "Preferencia no encontrada", null);
        }

        // Validar y actualizar docente si viene en el request
        if (request.getIdDocente() != null) {
            Docente docente = docenteRepo.findById(request.getIdDocente()).orElse(null);
            if (docente == null) {
                return new GenericObjectResponse<>(404, "Docente no encontrado", null);
            }
            preferencia.setDocente(docente);
        }

        // Validar y actualizar asignatura si viene en el request
        if (request.getIdAsignatura() != null) {
            Asignatura asignatura = asignaturaRepo.findById(request.getIdAsignatura()).orElse(null);
            if (asignatura == null) {
                return new GenericObjectResponse<>(404, "Asignatura no encontrada", null);
            }
            preferencia.setAsignatura(asignatura);
        }

        // Validar y actualizar carga electiva si viene en el request
        if (request.getIdCicloAcademico() != null) {
            CicloAcademico cicloAcademico = cicloAcademicoRepo.findById(request.getIdCicloAcademico()).orElse(null);
            if (cicloAcademico == null) {
                return new GenericObjectResponse<>(404, "Ciclo academico no encontrada", null);
            }
            preferencia.setCicloAcademico(cicloAcademico);
        }

        preferenciaRepo.save(preferencia);

        PreferenciaDetalleResponse dto = modelMapper.map(preferencia, PreferenciaDetalleResponse.class);
        return new GenericObjectResponse<>(200, "Preferencia actualizada exitosamente", dto);
    }
    public GenericObjectResponse<List<PreferenciaResumenResponse>> listarPreferenciaDocente(Integer idDocente, Integer idCicloAcademico) {
        System.out.println("idDocente: " + idDocente + " y id ciclo academico: " + idCicloAcademico);
        // Validar existencia de docente
        if (!docenteRepo.existsByIdDocente(idDocente)) {
            return new GenericObjectResponse<>(400, "Docente no encontrado", null);
        }

        // Validar existencia de carga electiva
        if (!cicloAcademicoRepo.existsByIdCicloAcademico(idCicloAcademico)) {
            return new GenericObjectResponse<>(400, "Carga electiva no encontrada", null);
        }

        // Obtener preferencias filtradas
        List<Preferencia> preferencias = preferenciaRepo.buscarPorDocenteYCicloAcademico(idDocente, idCicloAcademico);

        // Convertir a DTOs
        List<PreferenciaResumenResponse> listaDTO = preferencias.stream()
                .map(p -> modelMapper.map(p, PreferenciaResumenResponse.class))
                .collect(Collectors.toList());

        String mensaje = listaDTO.isEmpty()
                ? "No hay preferencias registradas para este docente en este ciclo academico"
                : "Lista obtenida correctamente";

        return new GenericObjectResponse<>(200, mensaje, listaDTO);
    }


    @Override
    public List<Preferencia> findByEnabledTrue() {
        return preferenciaRepo.findByEnabledTrue();
    }
}
