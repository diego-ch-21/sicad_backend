package com.sicad.sicad_backend.Enum;

public enum RolEnum {
    ADMIN(1),
    DEPARTAMENTO_ACADEMICO(2),
    DOCENTE(3),
    ESCUELA_PROFESIONAL(4),
    LOGISTICA(5);

    private final Integer numero;

    RolEnum(Integer numero) {
        this.numero = numero;
    }

    public Integer getNumero() {
        return numero;
    }
    public static RolEnum obtener(int numero) {
        for (RolEnum rolEnum : RolEnum.values()) {
            if (rolEnum.getNumero() == numero) {
                return rolEnum;
            }
        }
        return null;
    }

}
