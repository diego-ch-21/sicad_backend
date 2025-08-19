package com.sicad.sicad_backend.service.interfaces;

import com.sicad.sicad_backend.model.Preferencia;
import com.sicad.sicad_backend.service.base.ICRUD;

import java.util.List;

public interface IPreferenciaService extends ICRUD<Preferencia, Integer> {

    List<Preferencia> findByEnabledTrue();
}
