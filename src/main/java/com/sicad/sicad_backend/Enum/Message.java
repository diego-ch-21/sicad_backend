package com.sicad.sicad_backend.Enum;

public enum Message {
    CORREO_EN_USO("El correo ya está en uso");


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
