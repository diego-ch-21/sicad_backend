package com.sicad.sicad_backend.repository.interfaces;

import com.sicad.sicad_backend.model.CicloAcademico;
import com.sicad.sicad_backend.repository.base.IGenericRepo;

public interface ICicloAcademicoRepo extends IGenericRepo<CicloAcademico, Integer> {


    boolean existsByNombre(String nombre);
}
