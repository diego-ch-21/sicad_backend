package com.sicad.sicad_backend.utils;

public class MensajesValidacion {
    public static String campoRequerido(String campo) {
        return campo + " no puede estar vacío";
    }

    // Puedes extender con otros tipos:
    public static String campoInvalido(String campo) {
        return campo + " no es válido";
    }

    public static String campoFueraDeRango(String campo) {
        return campo + " está fuera del rango permitido";
    }
}
