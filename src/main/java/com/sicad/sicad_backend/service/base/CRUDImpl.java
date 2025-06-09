package com.sicad.sicad_backend.service.base;

import com.sicad.sicad_backend.exception.ModelNotFoundException;
import com.sicad.sicad_backend.persistence.repository.base.IGenericRepo;

import java.lang.reflect.Method;
import java.util.List;

public abstract class CRUDImpl<T,ID> implements ICRUD<T, ID> {

    protected abstract IGenericRepo<T, ID> getRepo();

    @Override
    public T save(T t) throws Exception {
        return getRepo().save(t);
    }

    @Override
    public T update(ID id, T t) throws Exception {
        //t.setIdProducto(id); // Assuming T has a method setIdProducto
        //API Reflection
        String className=t.getClass().getSimpleName();
        //setIdXYZ
        String methodName = "setId" + className;
        Method setIdMethod = t.getClass().getMethod(methodName,id.getClass());
        setIdMethod.invoke(t, id);
        getRepo().findById(id).orElseThrow(() -> new ModelNotFoundException("ID "+id+" no encontrado"));
        return getRepo().save(t);
    }

    @Override
    public List<T> findAll() throws Exception {
        return getRepo().findAll();
    }

    @Override
    public T findById(ID id) throws Exception {
        return getRepo().findById(id).orElseThrow(() -> new ModelNotFoundException("ID "+id+" no encontrado"));
    }

    @Override
    public void delete(ID id) throws Exception {
        getRepo().findById(id).orElseThrow(() -> new ModelNotFoundException("ID "+id+" no encontrado"));
        getRepo().deleteById(id);
    }
}
