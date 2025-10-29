package com.sicad.sicad_backend.service.impl;

import com.sicad.sicad_backend.Enum.Modulo;
import com.sicad.sicad_backend.dto.base.BaseListPageResponse;
import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.dto.categoria.CategoriaCreateRequest;
import com.sicad.sicad_backend.dto.categoria.CategoriaDetalleResponse;
import com.sicad.sicad_backend.dto.categoria.CategoriaUpdateRequest;
import com.sicad.sicad_backend.model.Categoria;
import com.sicad.sicad_backend.model.Docente;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import com.sicad.sicad_backend.repository.interfaces.ICategoriaRepo;
import com.sicad.sicad_backend.repository.interfaces.IDocenteRepo;
import com.sicad.sicad_backend.service.base.CRUDImpl;
import com.sicad.sicad_backend.service.interfaces.ICategoriaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoriaServiceImpl
        extends CRUDImpl<Categoria, Integer>
        implements ICategoriaService {

    private final ICategoriaRepo categoriaRepo;
    private final IDocenteRepo docenteRepo;
    private final ModelMapper modelMapper;

    @Override
    protected IGenericRepo<Categoria, Integer> getRepo() {
        return categoriaRepo;
    }

    @Override
    public BaseListReponse<CategoriaDetalleResponse> listar() {
        List<CategoriaDetalleResponse> lista = categoriaRepo.findByEnabledTrue()
                .stream()
                .map(this::convCategoriaDetalle)
                .toList();

        return new BaseListReponse<>(200, Modulo.CATEGORIA.listado(), lista);
    }

    @Override
    public BaseListPageResponse<CategoriaDetalleResponse> listarPaginado(Pageable pageable) {
        var page = categoriaRepo.findByEnabledTrue(pageable)
                .map(this::convCategoriaDetalle);
        return new BaseListPageResponse<>(
                200,
                Modulo.CATEGORIA.listado(),
                page.getContent(),
                page.getNumber(),
                page.getTotalPages(),
                page.getTotalElements()
        );
    }


    @Override
    public BaseObjectResponse<CategoriaDetalleResponse> buscar(Integer idCategoria) {
        Optional<Categoria> categoriaOpt = categoriaRepo.findByIdAndEnabledTrue(idCategoria);

        if (categoriaOpt.isEmpty()) {
            return new BaseObjectResponse<>(404, Modulo.CATEGORIA.noEncontrado(), null);
        }

        return new BaseObjectResponse<>(200, Modulo.CATEGORIA.encontrado(), convCategoriaDetalle(categoriaOpt.get()));
    }

    @Override
    public BaseObjectResponse<CategoriaDetalleResponse> buscarPorDocente(Integer idDocente) {
        Optional<Docente> docenteOpt = docenteRepo.findById(idDocente);

        if (docenteOpt.isEmpty()) {
            return new BaseObjectResponse<>(404, Modulo.DOCENTE.noEncontrado(), null);
        }
        Categoria categoria = docenteOpt.get().getCategoria();
        if(categoria ==null) {
            return new BaseObjectResponse<>(404, Modulo.DOCENTE.noTiene(Modulo.CATEGORIA), null);
        }

        return new BaseObjectResponse<>(200, Modulo.CATEGORIA.encontrado(), convCategoriaDetalle(docenteOpt.get().getCategoria()));
    }

    @Override
    public BaseObjectResponse<CategoriaDetalleResponse> registrar(CategoriaCreateRequest request) {
        Categoria categoria = Categoria.builder()
                .nombre(request.getNombre())
                .enabled(true)
                .build();

        categoriaRepo.save(categoria);

        return new BaseObjectResponse<>(201, Modulo.CATEGORIA.registrado(), convCategoriaDetalle(categoria));
    }

    @Override
    public BaseListReponse<CategoriaDetalleResponse> registrarAll(List<CategoriaCreateRequest> requests) {
        List<CategoriaDetalleResponse> registrados = new ArrayList<>();
        int errorCount = 0;

        for (CategoriaCreateRequest request : requests) {
            try {
                BaseObjectResponse<CategoriaDetalleResponse> response = registrar(request);
                if (response.status() == 201 && response.data() != null) {
                    registrados.add(response.data());
                } else {
                    errorCount++;
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                errorCount++;
            }
        }

        return new BaseListReponse<>(201, Modulo.CATEGORIA.resumenAllRegistro(registrados.size(), errorCount),registrados);
    }

    @Override
    public BaseObjectResponse<CategoriaDetalleResponse> actualizar(Integer idCategoria, CategoriaUpdateRequest request) {
        Optional<Categoria> categoriaOpt = categoriaRepo.findByIdAndEnabledTrue(idCategoria);

        if (categoriaOpt.isEmpty()) {
            return new BaseObjectResponse<>(404,  Modulo.CATEGORIA.noEncontrado(), null);
        }

        Categoria categoria = categoriaOpt.get();

        if (request.getNombre() != null && !request.getNombre().isBlank()) {
            categoria.setNombre(request.getNombre());
        }

        categoriaRepo.save(categoria);

        return new BaseObjectResponse<>(200,  Modulo.CATEGORIA.actualizado(),convCategoriaDetalle(categoria));
    }

    @Override
    public BaseObjectResponse<String> eliminar(Integer idCategoria) {
        Optional<Categoria> categoriaOpt = categoriaRepo.findByIdAndEnabledTrue(idCategoria);

        if (categoriaOpt.isEmpty()) {
            return new BaseObjectResponse<>(404,  Modulo.CATEGORIA.noEncontrado(), null);
        }

        Categoria categoria = categoriaOpt.get();
        categoria.setEnabled(false);
        categoriaRepo.save(categoria);

        return new BaseObjectResponse<>(200,  Modulo.CATEGORIA.eliminado(), null);
    }

    private CategoriaDetalleResponse convCategoriaDetalle(Categoria obj) {
        return modelMapper.map(obj, CategoriaDetalleResponse.class);
    }

}
