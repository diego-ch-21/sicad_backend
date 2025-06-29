package com.sicad.sicad_backend.service.base;

import com.sicad.sicad_backend.exception.ModelNotFoundException;
import com.sicad.sicad_backend.repository.base.IGenericRepo;

import java.lang.reflect.Method;
import java.util.List;

public abstract class CRUDImpl<T,ID> implements ICRUD<T, ID> {

    protected abstract IGenericRepo<T, ID> getRepo();

    @Override
    public T save(T t) throws Exception {
        return getRepo().save(t);
    }
    /*
    @Override
    public T update(ID id, T t) throws Exception {

        String className=t.getClass().getSimpleName();
        String methodName = "setId" + className;
        Method setIdMethod = t.getClass().getMethod(methodName,id.getClass());
        setIdMethod.invoke(t, id);
        getRepo().findById(id).orElseThrow(() -> new ModelNotFoundException("ID "+id+" no encontrado"));
        return getRepo().save(t);
    }


     */
    @Override
    public T update(ID id, T t) throws Exception {
        T original = getRepo().findById(id)
                .orElseThrow(() -> new ModelNotFoundException("ID " + id + " no encontrado"));

        // Copiar solo los campos no nulos de t a original usando reflexión
        for (Method method : t.getClass().getMethods()) {
            if (method.getName().startsWith("get") && !method.getName().equals("getClass")) {
                Object value = method.invoke(t);
                if (value != null) {
                    // Encontrar el setter correspondiente
                    String setterName = method.getName().replace("get", "set");
                    try {
                        Method setter = t.getClass().getMethod(setterName, method.getReturnType());
                        setter.invoke(original, value);
                    } catch (NoSuchMethodException ignored) {
                        // No setter encontrado, ignorar
                    }
                }
            }
        }

        return getRepo().save(original);
    }
    @Override
    public List<T> findAll() throws Exception {
        return getRepo().findAll();
    }


    @Override
    public T findById(ID id) throws Exception {
        return getRepo().findById(id).orElseThrow(() -> new ModelNotFoundException("ID "+id+" no encontrado"));
    }

}
