package com.sicad.sicad_backend.utils;

import java.util.List;
import java.util.stream.Collectors;

public class TextUtils {
    public static String formatearListaComoTexto(List<String> lista) {
        if (lista == null || lista.isEmpty()) {
            return "";
        }
        return lista.stream()
                .map(s -> "\"" + s + "\"") // Agrega comillas
                .collect(Collectors.joining(",")); // Une por comas
    }

}
