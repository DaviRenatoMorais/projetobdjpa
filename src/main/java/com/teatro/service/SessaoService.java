package com.teatro.service;

import com.teatro.dao.SessaoDao;
import com.teatro.model.Sessao;

import java.time.LocalTime;
import java.util.List;

public class SessaoService {

    private final SessaoDao sessaoDao;

    public SessaoService() {
        this.sessaoDao = new SessaoDao();
    }

    public void salvar(Sessao sessao) {
        if (!sessao.getHorarioFim().isAfter(sessao.getHorarioInicio())) {
            throw new IllegalArgumentException(
                    "O horário de fim deve ser posterior ao horário de início."
            );
        }

        validarTurno(sessao);

        if (existeConfilto(sessao)) {
            throw new IllegalArgumentException(
                    "Conflito de horário com outra sessão! " +
                            "Já existe uma sessão ocupando o teatro nesse período."
            );
        }
        sessaoDao.salvar(sessao);
    }

    public void atualizar(Sessao sessao) {
        if (!sessao.getHorarioFim().isAfter(sessao.getHorarioInicio())) {
            throw new IllegalArgumentException(
                    "O horário de fim deve ser posterior ao horário de início."
            );
        }

        validarTurno(sessao);

        if (existeConfilto(sessao)) {
            throw new IllegalArgumentException(
                    "Conflito de horário com outra sessão! " +
                            "Já existe uma sessão ocupando o teatro nesse período."
            );
        }
        sessaoDao.atualizar(sessao);
    }

    public void remover(Sessao sessao) {
        sessaoDao.remover(sessao);
    }

    public Sessao buscarPorId(Long id) {
        return sessaoDao.buscarPorId(id);
    }

    public List<Sessao> listarTodos() {
        return sessaoDao.listarTodos();
    }

    public boolean existeConfilto(Sessao novaSessao) {
        List<Sessao> sessoes = sessaoDao.buscarPorData(novaSessao.getData());

        LocalTime novoInicio = novaSessao.getHorarioInicio().minusHours(1);
        LocalTime novoFim = novaSessao.getHorarioFim().plusHours(1);

        for (Sessao existente : sessoes) {

            // Quando estamos atualizando uma sessão,
            // não devemos comparar a sessão com ela mesma.
            if (novaSessao.getId() != null
                    && novaSessao.getId().equals(existente.getId())) {
                continue;
            }

            LocalTime inicioExistente = existente.getHorarioInicio().minusHours(1);
            LocalTime fimExistente = existente.getHorarioFim().plusHours(1);

            boolean conflito = novoInicio.isBefore(fimExistente)
                    && novoFim.isAfter(inicioExistente);

            if (conflito) {
                return true;
            }
        }
        return false;
    }

    private void validarTurno(Sessao sessao) {

        LocalTime inicio = sessao.getHorarioInicio();
        LocalTime fim = sessao.getHorarioFim();

        boolean manha = !inicio.isBefore(LocalTime.of(8, 0))
                && !fim.isAfter(LocalTime.of(12, 0));

        boolean tarde = !inicio.isBefore(LocalTime.of(13, 0))
                && !fim.isAfter(LocalTime.of(18, 0));

        boolean noite = !inicio.isBefore(LocalTime.of(19, 0))
                && !fim.isAfter(LocalTime.of(23, 0));

        if (!manha && !tarde && !noite) {
            throw new IllegalArgumentException(
                    "A sessão deve permanecer dentro de um único turno."
            );
        }
    }
}