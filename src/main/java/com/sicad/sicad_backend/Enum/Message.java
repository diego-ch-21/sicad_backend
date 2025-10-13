package com.sicad.sicad_backend.Enum;

public enum Message {
    CORREO_EN_USO("El correo ya está en uso"),
    HORA_FIN_INICIO_ERROR("La hora de fin debe ser posterior a la hora de inicio"),
    DURACION_DIFERENCIA_HORA_ERROR("La duración no coincide con la diferencia entre hora inicio y fin");


    private final String texto;

    Message(String texto) {
        this.texto = texto;
    }

    public String get() {
        return texto;
    }

    @Override
    public String toString() {
        return texto;
    }
}
