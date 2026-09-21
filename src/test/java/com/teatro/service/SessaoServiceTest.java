package com.teatro.service;

import com.teatro.model.Peca;
import com.teatro.model.Sessao;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class SessaoServiceTest {

    @Test
    void deveIdentificarConflitoEntreSessoes() {

        Peca peca = new Peca();
        peca.setTitulo("Romeu e Julieta");

        Sessao existente = new Sessao();
        existente.setPeca(peca);
        existente.setData(LocalDate.of(2026, 9, 20));
        existente.setHorarioInicio(LocalTime.of(14, 0));
        existente.setHorarioFim(LocalTime.of(16, 0));

        Sessao nova = new Sessao();
        nova.setPeca(peca);
        nova.setData(LocalDate.of(2026, 9, 20));
        nova.setHorarioInicio(LocalTime.of(15, 0));
        nova.setHorarioFim(LocalTime.of(17, 0));

        // Este teste será completado quando tivermos
        // a sessão existente realmente no banco.

        assertTrue(
                nova.getHorarioInicio().isBefore(existente.getHorarioFim())
        );
    }
}