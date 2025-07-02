package com.sicad.sicad_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "preferencia")
public class Preferencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_preferencia")
    private Integer idPreferencia;

    @ManyToOne
    @JoinColumn(nullable = false, name = "id_docente",
            foreignKey = @ForeignKey(name = "FK_PREFERENCIA_DOCENTE"))
    private Docente docente;

    @ManyToOne
    @JoinColumn(nullable = false, name = "id_curso",
            foreignKey = @ForeignKey(name = "FK_PREFERENCIA_ASIGNATURA"))
    private Asignatura asignatura;

    @ManyToOne
    @JoinColumn(name = "id_carga_electiva", nullable = false,
            foreignKey = @ForeignKey(name = "FK_PREFERENCIA_CARGA_ELECTIVA"))
    private CargaElectiva cargaElectiva;

    @Column(nullable = false, name = "enabled")
    private boolean enabled;
}
