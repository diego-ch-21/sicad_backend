package com.sicad.sicad_backend.service.impl;

import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.dto.categoria.CategoriaCreateRequest;
import com.sicad.sicad_backend.dto.categoria.CategoriaDetalleResponse;
import com.sicad.sicad_backend.model.Categoria;
import com.sicad.sicad_backend.model.Docente;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import com.sicad.sicad_backend.repository.interfaces.ICategoriaRepo;
import com.sicad.sicad_backend.repository.interfaces.IDocenteRepo;
import com.sicad.sicad_backend.service.base.CRUDImpl;
import com.sicad.sicad_backend.service.interfaces.ICategoriaService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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


    public BaseListReponse<CategoriaDetalleResponse> saveAll(List<CategoriaCreateRequest> requestList) {
        List<Categoria> entities = requestList.stream()
                .map(dto -> modelMapper.map(dto, Categoria.class))
                .toList();
        List<Categoria> saved = categoriaRepo.saveAll(entities);
        List<CategoriaDetalleResponse> response = saved.stream()
                .map(cat -> modelMapper.map(cat, CategoriaDetalleResponse.class))
                .toList();
        return new BaseListReponse<>(201, "Categorías creadas", response);
    }
    public BaseObjectResponse<CategoriaDetalleResponse> obtenerCategoria(Integer idDocente) {
        Optional<Docente> docente = docenteRepo.findById(idDocente);
        Categoria categoria = docente.get().getCategoria();
        if(categoria ==null) {
            return new BaseObjectResponse<>(404,"Categoria no encontrada",null);
        }
        return new BaseObjectResponse<>(201,"categoria encontrada exitosamente",convertToDetalle(categoria));

    }
    public BaseObjectResponse<String> eliminarCategoria(Integer idCategoria) {
        // Validación de parámetro
        if (idCategoria == null) {
            return new BaseObjectResponse<>(400, "idCategoria no proporcionado", null);
        }

        // Validar existencia
        Categoria categoria = categoriaRepo.findById(idCategoria).orElse(null);
        if (categoria == null) {
            return new BaseObjectResponse<>(404, "Categoria  no encontrado", null);
        }

        // desabilitar
        categoria.setEnabled(false);
        categoriaRepo.save(categoria);
        return new BaseObjectResponse<>(200, "se elimino la categoria exitosamente", null);
    }

    private CategoriaDetalleResponse convertToDetalle(Categoria obj) {
        return modelMapper.map(obj, CategoriaDetalleResponse.class);
    }

    @Override
    public List<Categoria> findByEnabledTrue() {
        return categoriaRepo.findByEnabledTrue();
    }
}
