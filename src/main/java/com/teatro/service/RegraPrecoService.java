package com.teatro.service;

import com.teatro.dao.RegraPrecoDao;
import com.teatro.model.RegraPreco;
import com.teatro.model.Sessao;
import com.teatro.model.Turno;

import java.time.LocalTime;
import java.util.List;

public class RegraPrecoService {

    private final RegraPrecoDao regraPrecoDao;

    public RegraPrecoService() {
        this.regraPrecoDao = new RegraPrecoDao();
    }

    public void salvar(RegraPreco regraPreco) {
        validarRegra(regraPreco);
        regraPrecoDao.salvar(regraPreco);
    }

    public void atualizar(RegraPreco regraPreco) {
        validarRegra(regraPreco);
        regraPrecoDao.atualizar(regraPreco);
    }

    public void remover(RegraPreco regraPreco) {
        regraPrecoDao.remover(regraPreco);
    }

    public RegraPreco buscarPorId(Long id) {
        return regraPrecoDao.buscarPorId(id);
    }

    public List<RegraPreco> listarTodos() {
        return regraPrecoDao.listarTodos();
    }

    private void validarRegra(RegraPreco regraPreco) {

        if (regraPreco.getValorPorHora() == null
                || regraPreco.getValorPorHora().signum() <= 0) {
            throw new IllegalArgumentException(
                    "O valor por hora deve ser maior que zero."
            );
        }

        if (regraPreco.getHorarioInicio() != null
                && regraPreco.getHorarioFim() != null
                && !regraPreco.getHorarioFim().isAfter(regraPreco.getHorarioInicio())) {

            throw new IllegalArgumentException(
                    "O horário final deve ser posterior ao horário inicial."
            );
        }

        if (regraPreco.getMes() != null
                && (regraPreco.getMes() < 1 || regraPreco.getMes() > 12)) {

            throw new IllegalArgumentException(
                    "O mês deve estar entre 1 e 12."
            );
        }
    }

    // Referencia os horários da sessão com os horários do turno
    private boolean sessaoPertenceAoTurno(Sessao sessao, Turno turno) {

        LocalTime inicio = sessao.getHorarioInicio();
        LocalTime fim = sessao.getHorarioFim();

        return switch (turno) {

            case MANHA ->
                    !inicio.isBefore(LocalTime.of(8, 0))
                            && !fim.isAfter(LocalTime.of(12, 0));

            case TARDE ->
                    !inicio.isBefore(LocalTime.of(13, 0))
                            && !fim.isAfter(LocalTime.of(18, 0));

            case NOITE ->
                    !inicio.isBefore(LocalTime.of(19, 0))
                            && !fim.isAfter(LocalTime.of(23, 0));
        };
    }

    // verifica SE uma regra serve para a sessão
    private boolean regraSeAplica(RegraPreco regra, Sessao sessao) {

        if (regra.getDiaSemana() != null
                && regra.getDiaSemana() != sessao.getData().getDayOfWeek()) {
            return false;
        }

        if (regra.getMes() != null
                && !regra.getMes().equals(sessao.getData().getMonthValue())) {
            return false;
        }

        if (regra.getAno() != null
                && !regra.getAno().equals(sessao.getData().getYear())) {
            return false;
        }

        if (regra.getTurno() != null
                && !sessaoPertenceAoTurno(sessao, regra.getTurno())) {
            return false;
        }

        if (regra.getHorarioInicio() != null
                && sessao.getHorarioInicio().isBefore(regra.getHorarioInicio())) {
            return false;
        }

        if (regra.getHorarioFim() != null
                && sessao.getHorarioFim().isAfter(regra.getHorarioFim())) {
            return false;
        }

        return true;
    }

    // entre as regras que servem, escolhe a de MAIOR VALOR
    public RegraPreco encontrarRegraAplicavel(Sessao sessao) {

        List<RegraPreco> regras = regraPrecoDao.listarTodos();

        RegraPreco melhorRegra = null;

        for (RegraPreco regra : regras) {

            if (regraSeAplica(regra, sessao)) {

                if (melhorRegra == null
                        || regra.getValorPorHora()
                        .compareTo(melhorRegra.getValorPorHora()) > 0) {

                    melhorRegra = regra;
                }
            }
        }
        return melhorRegra;
    }
}