package com.teatro.service;

import com.teatro.model.RegraPreco;
import com.teatro.model.Sessao;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class RegraPrecoServiceTest {

    @Test
    void deveEscolherRegraComMaiorValor() {

        RegraPrecoService service = new RegraPrecoService();

        RegraPreco regra1 = new RegraPreco();
        regra1.setValorPorHora(new BigDecimal("50.00"));

        RegraPreco regra2 = new RegraPreco();
        regra2.setValorPorHora(new BigDecimal("80.00"));

        service.salvar(regra1);
        service.salvar(regra2);

        Sessao sessao = new Sessao();
        sessao.setData(LocalDate.of(2026, 9, 14));
        sessao.setHorarioInicio(LocalTime.of(19, 0));
        sessao.setHorarioFim(LocalTime.of(21, 0));

        RegraPreco resultado = service.encontrarRegraAplicavel(sessao);

        assertNotNull(resultado);
        assertEquals(
                new BigDecimal("80.00"),
                resultado.getValorPorHora()
        );

        service.remover(regra1);
        service.remover(regra2);
    }
}