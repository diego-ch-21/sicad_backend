package com.sicad.sicad_backend.utils;

public class NumbersUtils {
    public static String convertirARomano(Integer numero) {
        return switch (numero) {
            case 1 -> "I";
            case 2 -> "II";
            case 3 -> "III";
            case 4 -> "IV";
            case 5 -> "V";
            default -> "N/A"; // Valor por defecto si no está mapeado
        };
    }
}
