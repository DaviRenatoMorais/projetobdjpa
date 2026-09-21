package com.teatro.model;

import jakarta.persistence.Entity;

@Entity
public class Artista extends Pessoa {
    @Override
    public String toString() {
        return getNomeCompleto();
    }
}