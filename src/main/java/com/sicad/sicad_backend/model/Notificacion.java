package com.sicad.sicad_backend.model;

import com.sicad.sicad_backend.Enum.TipoNotificacion;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name="notificacion")
public class Notificacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_notificacion")
    @EqualsAndHashCode.Include
    private Integer idNotificacion;

    @Column(nullable = false, length = 500)
    private String mensaje;

    @CreationTimestamp
    @Column(name = "created_At", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private Boolean leida = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoNotificacion tipo;

    @ManyToOne(fetch =  FetchType.LAZY)
    @JoinColumn(name = "id_usuario_destino", nullable = false,
        foreignKey = @ForeignKey(name = "FK_NOTIFICACION_USUARIO_DESTINO"))
    private Usuario usuarioDestino;

    @ManyToOne(fetch =  FetchType.LAZY)
    @JoinColumn(name = "id_usuario_origen", nullable = true,
            foreignKey = @ForeignKey(name = "FK_NOTIFICACION_USUARIO_ORIGEN"))
    private Usuario usuarioOrigen;


    @Column(nullable = false, name = "enabled")
    private boolean enabled=true;
}
