package com.teatro.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity
@Table(name = "tb_regras_preco")
@NoArgsConstructor
@Getter
@Setter
public class RegraPreco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valorPorHora;

    @Column
    @Enumerated(EnumType.STRING)
    private DayOfWeek diaSemana;

    @Column
    @Enumerated(EnumType.STRING)
    private Turno turno;

    @Column
    private LocalTime horarioInicio;

    @Column
    private LocalTime horarioFim;

    @Column
    private Integer mes;

    @Column
    private Integer ano;
}