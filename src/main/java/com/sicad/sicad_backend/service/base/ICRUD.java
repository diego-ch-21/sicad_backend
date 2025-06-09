package com.sicad.sicad_backend.service.base;

import java.util.List;

public interface ICRUD<T,ID> {
    T save(T T) throws Exception;
    T update(ID id,T T) throws Exception;
    List<T> findAll() throws Exception;
    T findById(ID id) throws Exception;
    void delete(ID id) throws Exception;
}
