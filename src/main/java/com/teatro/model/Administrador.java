package com.teatro.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Administrador extends Pessoa {

    @Column(length = 20)
    private String senha;

    public String toString() {
        return getNomeCompleto();
    }
}