package com.sicad.sicad_backend.Enum;

public enum Modulo {
    DIRECTOR("Director", "Directores"),
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
    CURSO_HORARIO("Horario", "Horarios"),
    DEDICACION("Dedicación", "Dedicaciones"),
    DISPONIBILIDAD("Disponibilidad", "Disponibilidades"),
    DOCENTE("Docente", "Docentes"),
    ESPECIALIZACION("Especialización", "Especializaciones"),

    PLAN_DE_ESTUDIO("Plan de estudio", "Planes de estudio"),
    PREFERENCIA("Preferencia", "Preferencias"),
    PREMATRICULA("Prematrícula", "Prematrículas"),
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

}
