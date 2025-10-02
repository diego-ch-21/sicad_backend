package com.sicad.sicad_backend.service.impl;

import com.sicad.sicad_backend.dto.base.GenericObjectResponse;
import com.sicad.sicad_backend.dto.carga.CargaDetalleResponse;
import com.sicad.sicad_backend.model.Carga;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import com.sicad.sicad_backend.repository.interfaces.ICargaRepo;
import com.sicad.sicad_backend.repository.interfaces.ICicloAcademicoRepo;
import com.sicad.sicad_backend.service.base.CRUDImpl;
import com.sicad.sicad_backend.service.interfaces.ICargaService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CargaServiceImpl
        extends CRUDImpl<Carga, Integer>
        implements ICargaService {

    private final ICargaRepo  cargaRepo;
    private final ICicloAcademicoRepo cicloAcademicoRepo;
    private final ModelMapper modelMapper;

    @Override
    protected IGenericRepo<Carga, Integer> getRepo() {
        return cargaRepo;
    }

    @Override
    public List<Carga> findByEnabledTrue() {
        return cargaRepo.findByEnabledTrue();
    }

    @Override
    public List<Carga> findByEnabledTrueAndCicloAcademico_Id(Integer idCicloAcademico) {
        return cargaRepo.findByEnabledTrueAndCicloAcademico_IdCicloAcademico(idCicloAcademico);
    }

    public GenericObjectResponse<CargaDetalleResponse> obtenerCargaDefecto(Integer idCicloAcademico){
        Boolean isCicloAcademico = cicloAcademicoRepo.existsById(idCicloAcademico);
        if(isCicloAcademico){
            return new GenericObjectResponse<>(404, "Docente no encontrado", null);
        }
        Optional<Carga> cargaObejct = cargaRepo.findByCicloAcademicoAndEnabledTrue(idCicloAcademico);
        if(!cargaObejct.isPresent()){
            return new GenericObjectResponse<>(200, "carga no encontrada", null);
        }
        return new GenericObjectResponse<>(200, "Carga encontrada correctamente", convertToDetalle(cargaObejct.get()));
    }
    public GenericObjectResponse<String> eliminarCarga(Integer idCarga) {
        // Validación de parámetro
        if (idCarga == null) {
            return new GenericObjectResponse<>(400, "idCarga no proporcionado", null);
        }

        // Validar existencia del curso
        Carga carga = cargaRepo.findById(idCarga).orElse(null);
        if (carga == null) {
            return new GenericObjectResponse<>(404, "Carga  no encontrado", null);
        }

        // desabilitar
        carga.setEnabled(false);
        carga.setPrincipal(false);
        cargaRepo.save(carga);
        return new GenericObjectResponse<>(200, "se elimino la carga exitosamente", null);
    }

    private CargaDetalleResponse convertToDetalle(Carga obj) {
        return modelMapper.map(obj, CargaDetalleResponse.class);
    }

}
