package com.sicad.sicad_backend.Enum;

import java.util.List;
import java.util.stream.Collectors;

public enum Modulo {
    DEPARTAMENTO_ACADEMICO("Departamento academico", "Departamento academicos"),
    ESCUELA_PROFESIONAL("Escuela profesional", "Escuela profesionales"),
    ESCUELA("Escuela", "Escuelas"),
    LOGISTICA("Logística", "Logísticas"),
    ALGORITMO("Algoritmo", "Algoritmos"),
    ASIGNACION("Asignación", "Asignaciones"),
    ASIGNATURA("Asignatura", "Asignaturas"),
    AULA("Aula", "Aulas"),
    CARGA("Carga", "Cargas"),
    CATEGORIA("Categoría", "Categorías"),
    CICLO_ACADEMICO("Ciclo académico", "Ciclos académicos"),
    CURSO("Curso", "Cursos"),
    HORARIO("Horario", "Horarios"),
    DEDICACION("Dedicación", "Dedicaciones"),
    DISPONIBILIDAD("Disponibilidad", "Disponibilidades"),
    DOCENTE("Docente", "Docentes"),
    ESPECIALIZACION("Especialización", "Especializaciones"),

    PLAN_DE_ESTUDIO("Plan de estudio", "Planes de estudio"),
    PREFERENCIA("Preferencia", "Preferencias"),
    RESULTADO("Resultado", "Resultados"),
    ROL("Rol", "Roles"),
    USUARIO("Usuario", "Usuarios");

    private final String item;
    private final String items;

    Modulo(String item, String items) {
        this.item = item;
        this.items = items;
    }

    public String getSingular() {
        return item;
    }

    public String getPlural() {
        return items;
    }
    public String registrado() {
        return item + " registrado exitosamente";
    }

    public String actualizado() {
        return item + " actualizado exitosamente";
    }

    public String eliminado() {
        return item + " eliminado exitosamente";
    }
    public String encontrado() {
        return item + " encontrado";
    }

    public String noEncontrado() {
        return item + " no encontrado";
    }

    public String listado() {
        return "Lista de " + items + " obtenida exitosamente";
    }

    public String resumenAllRegistro(int exitosos, int fallidos) {
        return String.format("%s registrados: %d. Fallidos: %d.", item, exitosos, fallidos);
    }

    public String principalSeleccionado() {
        return item + " asignado como principal exitosamente";
    }
    public String principalYaSeleccionado() {
        return "Este "+ item + " ya está seleccionado como principal";
    }

    public String noPertenece(Modulo mod) {
        return "Esta "+ item + " no pertenece a este " + mod.getSingular();
    }

    public String noCumple(List<Modulo> modulos) {
        String mensaje = "Ya existe una " + item + " con este ";

        // Convertir los 'singular' de cada módulo en un texto tipo: "X, Y y Z"
        String detalles = modulos.stream()
                .map(Modulo::getSingular)
                .collect(Collectors.joining(" y "));

        return mensaje + detalles;
    }
    public String noTiene(Modulo mod) {
        return "Este "+item+" no tiene una "+mod.getSingular();
    }

}
