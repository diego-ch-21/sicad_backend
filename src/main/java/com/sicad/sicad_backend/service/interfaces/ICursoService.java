package com.sicad.sicad_backend.service.interfaces;

import com.sicad.sicad_backend.model.Curso;
import com.sicad.sicad_backend.service.base.ICRUD;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ICursoService extends ICRUD<Curso, Integer> {
    List<Curso> findByEnabledTrue();
}
