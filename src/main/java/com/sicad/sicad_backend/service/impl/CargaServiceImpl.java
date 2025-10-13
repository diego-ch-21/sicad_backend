package com.sicad.sicad_backend.service.impl;

import com.sicad.sicad_backend.Enum.Modulo;
import com.sicad.sicad_backend.dto.algoritmo.AlgoritmoDetalleResponse;
import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.carga.CargaDetalleResponse;
import com.sicad.sicad_backend.dto.docente.DocenteAsignacionResponse;
import com.sicad.sicad_backend.model.Algoritmo;
import com.sicad.sicad_backend.model.Carga;
import com.sicad.sicad_backend.model.CicloAcademico;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import com.sicad.sicad_backend.repository.interfaces.ICargaRepo;
import com.sicad.sicad_backend.repository.interfaces.ICicloAcademicoRepo;
import com.sicad.sicad_backend.service.base.CRUDImpl;
import com.sicad.sicad_backend.service.interfaces.ICargaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
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
    public BaseListReponse<CargaDetalleResponse> listarPorCicloAcademico(Integer idCicloAcademico) {

        Optional<CicloAcademico> cicloOpt = cicloAcademicoRepo.findByIdAndEnabledTrue(idCicloAcademico);
        if (cicloOpt.isEmpty()) {
            return new BaseListReponse<>(404, Modulo.CICLO_ACADEMICO.noEncontrado(), null);
        }
        List<CargaDetalleResponse> lista = cargaRepo.findByIdCicloAcademicoAndEnabledTrue(idCicloAcademico)
                .stream()
                .map(this::convCargaDetalle)
                .toList();

        return new BaseListReponse<>(200, Modulo.CARGA.listado(), lista);
    }

    @Override
    public BaseObjectResponse<CargaDetalleResponse> buscar(Integer idCarga) {
        Optional<Carga> cargaOpt = cargaRepo.findByIdAndEnabledTrue(idCarga);
        if (cargaOpt.isEmpty()) {
            return new BaseObjectResponse<>(404, Modulo.CARGA.noEncontrado(), null);
        }
        Carga carga = cargaOpt.get();

        return new BaseObjectResponse<>(200, Modulo.CARGA.encontrado(), convCargaDetalle(carga));
    }

    @Override
    public BaseObjectResponse<String> eliminar(Integer idCarga) {
        Optional<Carga> cargaOpt = cargaRepo.findByIdAndEnabledTrue(idCarga);
        if (cargaOpt.isEmpty()) {
            return new BaseObjectResponse<>(404, Modulo.CARGA.noEncontrado(), null);
        }
        Carga carga = cargaOpt.get();
        carga.setEnabled(false);
        carga.setPrincipal(false);
        cargaRepo.save(carga);

        return new BaseObjectResponse<>(200, Modulo.CARGA.eliminado(), null);
    }

    @Override
    public BaseObjectResponse<CargaDetalleResponse> asignarPrincipal(Integer idCicloAcademico,Integer idCarga) {
        //verificar carga
        Optional<Carga> cargaOpt = cargaRepo.findByIdAndEnabledTrue(idCarga);
        if (cargaOpt.isEmpty()) {
            return new BaseObjectResponse<>(404, Modulo.CARGA.noEncontrado(), null);
        }
        Carga cargaNew = cargaOpt.get();
        //verificar ciclo academico
        Optional<CicloAcademico> cicloAcademicoOpt = cicloAcademicoRepo.findByIdAndEnabledTrue(idCicloAcademico);
        if (cicloAcademicoOpt.isEmpty()) {
            return new BaseObjectResponse<>(404, Modulo.CICLO_ACADEMICO.noEncontrado(), null);
        }

        if(!idCicloAcademico.equals(cargaNew.getCicloAcademico().getIdCicloAcademico())){
            return new BaseObjectResponse<>(404, Modulo.CARGA.noPertenece(Modulo.CICLO_ACADEMICO), null);
        }

        if(cargaNew.getPrincipal()){
            return new BaseObjectResponse<>(200, Modulo.CARGA.principalYaSeleccionado(), convCargaDetalle(cargaNew));
        }

        Optional<Carga> cargaPrincipalAnterior = cargaRepo.findPrincipalByCicloAcademicoAndEnabledTrue(idCicloAcademico);
        if(!cargaPrincipalAnterior.isEmpty()){
            Carga cargaPrincipal = cargaPrincipalAnterior.get();
            cargaPrincipal.setPrincipal(false);
            cargaRepo.save(cargaPrincipal);
        }

        cargaNew.setPrincipal(true);
        cargaRepo.save(cargaNew);

        return new BaseObjectResponse<>(201, Modulo.CARGA.principalSeleccionado(), convCargaDetalle(cargaNew));
    }

    @Override
    public BaseObjectResponse<CargaDetalleResponse> buscarPrincipal(Integer idCicloAcademico) {
        Optional<Carga> optCarga = cargaRepo.findPrincipalByCicloAcademicoAndEnabledTrue(idCicloAcademico);
        if (optCarga.isEmpty()) {
            return new BaseObjectResponse<>(404, Modulo.CARGA.noEncontrado(), null);
        }
        Carga carga = optCarga.get();
        return new BaseObjectResponse<>(201, Modulo.CARGA.encontrado(),convCargaDetalle(carga));
    }

    public void exportarCargaElectiva(Integer idCarga) {


    }
    public void exportarCargaElectivaDocente(Integer docentem,Integer idCarga){

    }


    private CargaDetalleResponse convCargaDetalle(Carga obj) {
        return modelMapper.map(obj, CargaDetalleResponse.class);
    }
}
