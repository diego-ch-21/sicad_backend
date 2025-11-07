package com.sicad.sicad_backend.model;

import lombok.Builder;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "mensaje")
public class Mensaje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_mensaje")
    private Integer idMensaje;

    @ManyToOne
    @JoinColumn(name = "id_remitente", nullable = false,
            foreignKey = @ForeignKey(name = "FK_MENSAJE_REMITENTE"))
    private Usuario remitente;

    @ManyToOne
    @JoinColumn(name = "id_destinatario", nullable = false,
            foreignKey = @ForeignKey(name = "FK_MENSAJE_DESTINATARIO"))
    private Usuario destinatario;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenido;

    @Column(nullable = false, name = "fecha_envio")
    private LocalDateTime fechaEnvio;

    @Column(nullable = false, name = "leido")
    private Boolean leido;

    @Column(nullable = false, name = "enabled")
    private Boolean enabled;

    @PrePersist
    public void prePersist() {
        this.fechaEnvio = LocalDateTime.now();
        this.leido = false;
        this.enabled = true;
    }
}