package com.sicad.sicad_backend.service.impl;


import com.sicad.sicad_backend.Enum.Modulo;
import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.escuela.EscuelaDetalleResponse;
import com.sicad.sicad_backend.dto.resultado.ResultadoDetalleResponse;
import com.sicad.sicad_backend.model.Carga;
import com.sicad.sicad_backend.model.Escuela;
import com.sicad.sicad_backend.model.Resultado;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import com.sicad.sicad_backend.repository.interfaces.ICargaRepo;
import com.sicad.sicad_backend.repository.interfaces.IDocenteRepo;
import com.sicad.sicad_backend.repository.interfaces.IResultadoRepo;
import com.sicad.sicad_backend.service.base.CRUDImpl;
import com.sicad.sicad_backend.service.interfaces.IDocenteService;
import com.sicad.sicad_backend.service.interfaces.IResultadoService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ResultadoServiceImpl
    extends CRUDImpl<Resultado, Integer>
    implements IResultadoService {

    private final IResultadoRepo resultadoRepo;
    private final ICargaRepo cargaRepo;
    private final ModelMapper modelMapper;
    @Override
    protected IGenericRepo<Resultado, Integer> getRepo() {
        return resultadoRepo;
    }

    @Override
    public BaseObjectResponse<ResultadoDetalleResponse> buscar(Integer idResultado) {
        Optional<Resultado> resultadoOpt = resultadoRepo.findByIdAndEnabledTrue(idResultado);
        if (resultadoOpt.isEmpty()) {
            return new BaseObjectResponse<>(404, Modulo.RESULTADO.noEncontrado(), null);
        }
        return new BaseObjectResponse<>(200, Modulo.ESCUELA.encontrado(), convResultadoDetalle(resultadoOpt.get()));
    }

    @Override
    public BaseObjectResponse<ResultadoDetalleResponse> buscarPorCarga(Integer idCarga) {
        Optional<Carga> cargaOpt = cargaRepo.findByIdAndEnabledTrue(idCarga);
        if (cargaOpt.isEmpty()) {
            return new BaseObjectResponse<>(404, Modulo.CARGA.noEncontrado(), null);
        }
        Resultado resultado = cargaOpt.get().getResultado();
        if(resultado ==null){
            return new BaseObjectResponse<>(404, Modulo.CARGA.noTiene(Modulo.RESULTADO), null);
        }
        return new BaseObjectResponse<>(200, Modulo.CARGA.encontrado(), null);
    }

    private ResultadoDetalleResponse convResultadoDetalle(Resultado obj) {
        return modelMapper.map(obj, ResultadoDetalleResponse.class);
    }
}
