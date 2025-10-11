package com.sicad.sicad_backend.repository.interfaces;

import com.sicad.sicad_backend.model.Algoritmo;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IAlgoritmoRepo extends IGenericRepo<Algoritmo, Integer> {
    @Query("SELECT e FROM Algoritmo e WHERE e.enabled = true AND e.principal = true")
    Optional<Algoritmo> findByPrincipalTrue();

    @Modifying
    @Query("UPDATE Algoritmo e SET e.principal = false")
    void resetPrincipal();

    @Modifying
    @Query("UPDATE Algoritmo e SET e.principal = true WHERE e.id = :id")
    void setPrincipal(@Param("id") Integer id);

    @Query("SELECT e FROM Algoritmo e WHERE e.enabled = true")
    List<Algoritmo> findByEnabledTrue();

    @Query("SELECT e FROM Algoritmo e WHERE e.idAlgoritmo = :id AND e.enabled = true")
    Optional<Algoritmo> findByIdAndEnabledTrue(@Param("id") Integer id);
}
