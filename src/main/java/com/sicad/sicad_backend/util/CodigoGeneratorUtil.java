package com.sicad.sicad_backend.util;

import java.util.Random;

public class CodigoGeneratorUtil {

    private static final String NUMEROS = "0123456789";
    private static final String LETRAS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String ALFANUMERICO = LETRAS + NUMEROS;

    private static final Random random = new Random();

    // ✅ Código solo con números
    public static String generarCodigoNumerico(int longitud) {
        return generarDesdeCaracteres(NUMEROS, longitud);
    }

    // ✅ Código solo con letras
    public static String generarCodigoAlfabetico(int longitud) {
        return generarDesdeCaracteres(LETRAS, longitud);
    }

    // ✅ Código alfanumérico (combinado)
    public static String generarCodigoAlfanumerico(int longitud) {
        return generarDesdeCaracteres(ALFANUMERICO, longitud);
    }

    // 🔧 Método auxiliar genérico
    private static String generarDesdeCaracteres(String caracteres, int longitud) {
        StringBuilder codigo = new StringBuilder();

        for (int i = 0; i < longitud; i++) {
            codigo.append(caracteres.charAt(random.nextInt(caracteres.length())));
        }

        return codigo.toString();
    }
}
