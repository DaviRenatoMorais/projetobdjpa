package com.teatro.dto;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
public class PresencaDto {

    private String cliente;
    private String peca;
    private LocalDate data;
    private LocalTime horario;
    private Integer quantidade;
    private BigDecimal valorTotal;

    public PresencaDto(
            String cliente,
            String peca,
            LocalDate data,
            LocalTime horario,
            Integer quantidade,
            BigDecimal valorTotal) {

        this.cliente = cliente;
        this.peca = peca;
        this.data = data;
        this.horario = horario;
        this.quantidade = quantidade;
        this.valorTotal = valorTotal;
    }

    public String getCliente() {
        return cliente;
    }

    public String getPeca() {
        return peca;
    }

    public LocalDate getData() {
        return data;
    }

    public LocalTime getHorario() {
        return horario;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }
}