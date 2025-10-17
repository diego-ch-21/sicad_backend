package com.sicad.sicad_backend.Enum;

public enum Message {
    CORREO_EN_USO("El correo ya está en uso"),
    CORREO_NO_ENCONTRADO("Correo no encontrado"),
    HORA_FIN_INICIO_ERROR("La hora de fin debe ser posterior a la hora de inicio"),
    DURACION_DIFERENCIA_HORA_ERROR("La duración no coincide con la diferencia entre hora inicio y fin"),
    CRUCE_DE_HORARIO_DISPONIBILIDAD("El horario ingresado se cruza con otra disponibilidad existente en el mismo día"),
    LOGIN_ACCESS("Inicio de sesión exitoso"),
    CREDENCIALES_INCORRECTAS("Credenciales incorrectas"),
    CODIGO_EXISTENTE("Este codigo ya existe");

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

    public String getTexto() {
        return texto;
    }
}
