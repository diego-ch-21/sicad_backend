package com.sicad.sicad_backend.service.impl;

import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.dto.preMatricula.PreMatriculaCreateRequest;
import com.sicad.sicad_backend.dto.preMatricula.PreMatriculaDetalleResponse;
import com.sicad.sicad_backend.dto.preMatricula.PreMatriculaUpdateRequest;
import com.sicad.sicad_backend.model.Asignatura;
import com.sicad.sicad_backend.model.CicloAcademico;
import com.sicad.sicad_backend.model.PreMatricula;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import com.sicad.sicad_backend.repository.interfaces.IAsignaturaRepo;
import com.sicad.sicad_backend.repository.interfaces.ICicloAcademicoRepo;
import com.sicad.sicad_backend.repository.interfaces.IPreMatriculaRepo;
import com.sicad.sicad_backend.service.base.CRUDImpl;
import com.sicad.sicad_backend.service.interfaces.IPreMatriculaService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PreMatriculaServiceImpl
        extends CRUDImpl<PreMatricula,Integer>
        implements IPreMatriculaService {

    private final IPreMatriculaRepo  preMatriculaRepo;
    private final ICicloAcademicoRepo cicloAcademicoRepo;
    private final IAsignaturaRepo asignaturaRepo;
    private final ModelMapper modelMapper;

    @Override
    protected IGenericRepo<PreMatricula, Integer> getRepo() {
        return preMatriculaRepo;
    }

    public BaseObjectResponse<PreMatriculaDetalleResponse> registrarPreMatricula(PreMatriculaCreateRequest request){
        //validar idCicloAcademico
        CicloAcademico cicloAcademico = cicloAcademicoRepo.findById(request.getIdCicloAcademico()).orElse(null);
        if(cicloAcademico == null){
            return new BaseObjectResponse<>(404,"ciclo academico no encontrado",null);
        }
        //validar idAsignatura
        Asignatura asignatura = asignaturaRepo.findById(request.getIdAsignatura()).orElse(null);
        if(asignatura == null){
            return new BaseObjectResponse<>(404,"asignatura no encontrado",null);
        }
        PreMatricula preMatricula = new PreMatricula().builder()
                .asignatura(asignatura)
                .cicloAcademico(cicloAcademico)
                .cantidad(request.getCantidad())
                .enabled(true)
                .build();
        preMatriculaRepo.save(preMatricula);
        PreMatriculaDetalleResponse response = convertToDTO(preMatricula);
        return new BaseObjectResponse<>(201,"pre-matricula registrado",response);
    }

    public BaseListReponse<PreMatriculaDetalleResponse> registrarVariosPreMatricula(List<PreMatriculaCreateRequest> requests){
        List<PreMatriculaDetalleResponse> registrados = new ArrayList<>();
        int errorCount = 0;

        for(PreMatriculaCreateRequest request: requests){
            BaseObjectResponse<PreMatriculaDetalleResponse> response = registrarPreMatricula(request);
            if(response.status() == 201 || response.data() != null){
                registrados.add(response.data());
            } else {
                errorCount++;
            }
        }
        String mensaje = String.format("PreMatricula registrados: %d. Fallidos: %d.", registrados.size(), errorCount);
        return new BaseListReponse<>(200, mensaje, registrados);
    }
    public BaseObjectResponse<PreMatriculaDetalleResponse> actualizarPreMatricula(Integer id, PreMatriculaUpdateRequest request){
        PreMatricula preMatricula =  preMatriculaRepo.findById(id).orElse(null);
        if(preMatricula == null){
            return new BaseObjectResponse<>(404,"pre-matricula no encontrada",null);
        }

        //validar idCicloAcademico
        CicloAcademico cicloAcademico = cicloAcademicoRepo.findById(request.getIdCicloAcademico()).orElse(null);
        if(cicloAcademico == null){
            return new BaseObjectResponse<>(404,"ciclo academico no encontrado",null);
        }
        //validar idAsignatura
        Asignatura asignatura = asignaturaRepo.findById(request.getIdAsignatura()).orElse(null);
        if(asignatura == null){
            return new BaseObjectResponse<>(404,"asignatura no encontrado",null);
        }
        preMatricula.setCantidad(request.getCantidad());
        preMatricula.setAsignatura(asignatura);
        preMatricula.setCicloAcademico(cicloAcademico);
        preMatriculaRepo.save(preMatricula);
        return new BaseObjectResponse<>(200,"pre-matricula actualizado",null);
    }


    public BaseListReponse<PreMatriculaDetalleResponse> listarPorCicloAcademico(Integer idCicloAcademico){
        CicloAcademico cicloAcademico = cicloAcademicoRepo.findById(idCicloAcademico).orElse(null);
        if(cicloAcademico == null){
            return new BaseListReponse<>(404,"ciclo academico no encontrado",null);
        }
        List<PreMatriculaDetalleResponse> lista = findPreMatriculasActivasPorCiclo(idCicloAcademico)
                .stream()
                .map(this::convertToDTO)
                .toList();

        return new BaseListReponse<>(200, "Lista de Pre-matricula por cicloAcademico", lista);
    }
    public BaseObjectResponse<String> eliminarPreMatricula(Integer idPreMatricula) {
        // Validación de parámetro
        if (idPreMatricula == null) {
            return new BaseObjectResponse<>(400, "idPreMatricula no proporcionado", null);
        }

        // Validar existencia
        PreMatricula preMatricula = preMatriculaRepo.findById(idPreMatricula).orElse(null);
        if (preMatricula == null) {
            return new BaseObjectResponse<>(404, "PreMatricula  no encontrado", null);
        }

        // desabilitar
        preMatricula.setEnabled(false);
        preMatriculaRepo.save(preMatricula);
        return new BaseObjectResponse<>(200, "se elimino la PreMatricula exitosamente", null);
    }


    private PreMatriculaDetalleResponse convertToDTO(PreMatricula obj) {
        return modelMapper.map(obj, PreMatriculaDetalleResponse.class);
    }

    @Override
    public List<PreMatricula> findByEnabledTrue() {
        return preMatriculaRepo.findByEnabledTrue();
    }

    @Override
    public List<PreMatricula> findPreMatriculasActivasPorCiclo(Integer idCicloAcademico) {
        return preMatriculaRepo.findPreMatriculasActivasPorCiclo(idCicloAcademico);
    }


}
