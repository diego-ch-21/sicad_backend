package com.sicad.sicad_backend.repository.interfaces;

import com.sicad.sicad_backend.model.Resultado;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import org.springframework.data.repository.CrudRepository;

public interface IResultadoRepo extends IGenericRepo<Resultado, Integer> {
}
