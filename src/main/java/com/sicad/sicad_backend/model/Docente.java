package com.sicad.sicad_backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Date;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name="docente")
public class Docente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @Column(name = "id_docente")
    private Integer idDocente;

    @OneToOne
    @JoinColumn(name = "id_usuario", nullable = false, unique = true)
    private Usuario usuario;

    @Column(nullable = false, unique = true, length = 6, name = "codigo")
    private String codigo;

    @ManyToOne
    @JoinColumn(name = "id_dedicacion", nullable = false,
            foreignKey = @ForeignKey(name = "FK_DOCENTE_DEDICACION"))
    private Dedicacion dedicacion;

    @ManyToOne
    @JoinColumn(name = "id_categoria", nullable = false,
            foreignKey = @ForeignKey(name = "FK_DOCENTE_CATEGORIA"))
    private Categoria categoria;

    @Column(name = "hora_max_lectivas")
    private Integer horasMaxLectivas;

    @Column(name = "tiene_permiso_exceso")
    private Boolean tienePermisoExceso;

    @OneToMany(mappedBy = "docente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Preferencia> preferencias = new ArrayList<>();

    @OneToMany(mappedBy = "docente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Disponibilidad> disponibilidad = new ArrayList<>();


    @Column(nullable = false, name = "enabled")
    private boolean enabled;

}
