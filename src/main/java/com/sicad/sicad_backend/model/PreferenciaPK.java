package com.sicad.sicad_backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PreferenciaPK implements Serializable {
    private Integer docente;
    private Integer curso;
}
