package com.teatro.service;

import com.teatro.dao.ContratoDao;
import com.teatro.dao.IngressoDao;
import com.teatro.dao.SessaoDao;
import com.teatro.model.Contrato;
import com.teatro.model.RegraPreco;
import com.teatro.model.Sessao;
import com.teatro.model.StatusContrato;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalTime;

import java.time.LocalDate;
import java.util.List;

public class ContratoService {

    private final ContratoDao contratoDao;
    private final SessaoDao sessaoDao;
    private final RegraPrecoService regraPrecoService;
    private final IngressoDao ingressoDao;


    public ContratoService() {
        this.contratoDao = new ContratoDao();
        this.sessaoDao = new SessaoDao();
        this.regraPrecoService = new RegraPrecoService();
        this.ingressoDao = new IngressoDao();
    }

    public void salvar(Contrato contrato) {
        BigDecimal valorAluguel = calcularValorAluguel(contrato);
        contrato.setValorAluguel(valorAluguel);
        contratoDao.salvar(contrato);
    }

    public void atualizar(Contrato contrato) {
        BigDecimal valorAluguel = calcularValorAluguel(contrato);
        contrato.setValorAluguel(valorAluguel);
        contratoDao.atualizar(contrato);
    }

    public void remover(Contrato contrato) {
        contratoDao.remover(contrato);
    }

    public Contrato buscarPorId(Long id) {
        return contratoDao.buscarPorId(id);
    }

    public List<Contrato> listarTodos() {
        return contratoDao.listarTodos();
    }

    public void encerrarContrato(Long contratoId, LocalDate novaDataFim) {
        Contrato contrato = contratoDao.buscarPorId(contratoId);

        if (contrato == null) {
            throw new IllegalArgumentException(
                    "Contrato não encontrado."
            );
        }

        if (contrato.getStatus() == StatusContrato.ENCERRADO) {
            throw new IllegalArgumentException(
                    "O contrato já está encerrado."
            );
        }

        if (novaDataFim == null) {
            throw new IllegalArgumentException(
                    "A data de encerramento é obrigatória.");
        }

        if (novaDataFim.isBefore(contrato.getDataInicio())) {
            throw new IllegalArgumentException(
                    "A data de encerramento não pode ser anterior à data inicial do contrato.");
        }
        if (novaDataFim.isAfter(contrato.getDataFim())) {
            throw new IllegalArgumentException(
                    "A data de encerramento não pode ser posterior à data final do contrato."
            );
        }

        if (ingressoDao.existeIngressoDepoisDaData(
                contrato,
                novaDataFim)) {

            throw new IllegalArgumentException(
                    "Não é possível encerrar o contrato: "
                            + "existem ingressos vendidos para sessões posteriores."
            );
        }

        contrato.setDataFim(novaDataFim);
        contrato.setStatus(StatusContrato.ENCERRADO);
        contratoDao.atualizar(contrato);
    }

    public void prorrogarContrato(
            Long contratoId,
            LocalDate novaDataFim) {

        Contrato contrato = contratoDao.buscarPorId(contratoId);

        if (contrato == null) {
            throw new IllegalArgumentException(
                    "Contrato não encontrado."
            );
        }

        if (contrato.getStatus() == StatusContrato.ENCERRADO) {
            throw new IllegalArgumentException(
                    "Não é possível prorrogar um contrato encerrado."
            );
        }

        if (novaDataFim == null) {
            throw new IllegalArgumentException(
                    "A nova data final é obrigatória."
            );
        }

        if (novaDataFim.isBefore(contrato.getDataFim())) {
            throw new IllegalArgumentException(
                    "A nova data final deve ser posterior à data final atual."
            );
        }

        if (existeConflitoNaProrrogacao(
                contrato,
                novaDataFim)) {

            throw new IllegalArgumentException(
                    "Não é possível prorrogar o contrato: "
                            + "existe outra peça no período solicitado."
            );
        }

        contrato.setDataFim(novaDataFim);
        BigDecimal novoValor =
                calcularValorAluguel(contrato);
        contrato.setValorAluguel(novoValor);
        contratoDao.atualizar(contrato);
    }

    private boolean existeConflitoNaProrrogacao(
            Contrato contrato,
            LocalDate novaDataFim) {

        LocalDate dataInicioProrrogacao =
                contrato.getDataFim().plusDays(1);

        List<Sessao> sessoesExistentes =
                sessaoDao.listarTodos();

        for (Sessao sessao : sessoesExistentes) {
            if (sessao.getData().isBefore(dataInicioProrrogacao)
                    || sessao.getData().isAfter(novaDataFim)) {
                continue;
            }

            if (sessao.getPeca().getId()
                    .equals(contrato.getPeca().getId())) {
                continue;
            }
            return true;
        }
        return false;
    }

    private void validarContrato(Contrato contrato) {

        if (contrato.getDataInicio() == null
                || contrato.getDataFim() == null) {
            throw new IllegalArgumentException(
                    "As datas do contrato são obrigatórias."
            );
        }

        if (contrato.getDataFim().isBefore(contrato.getDataInicio())) {
            throw new IllegalArgumentException(
                    "A data final deve ser posterior ou igual à data inicial."
            );
        }

        if (contrato.getArtista() == null) {
            throw new IllegalArgumentException(
                    "O artista é obrigatório."
            );
        }

        if (contrato.getPeca() == null) {
            throw new IllegalArgumentException(
                    "A peça é obrigatória."
            );
        }

        /*if (contrato.getValorAluguel() == null
                || contrato.getValorAluguel().signum() < 0) {
            throw new IllegalArgumentException(
                    "O valor do aluguel não pode ser negativo."
            );
        }*/

        if (contrato.getStatus() == null) {
            throw new IllegalArgumentException(
                    "O status do contrato é obrigatório."
            );
        }
    }

    public BigDecimal calcularValorSessao(Sessao sessao) {

        RegraPreco regra = regraPrecoService.encontrarRegraAplicavel(sessao);

        if (regra == null) {
            throw new IllegalArgumentException(
                    "Nenhuma regra de preço aplicável foi encontrada."
            );
        }

        // Calcula a duração entre os dois horários
        Duration duracao = Duration.between(
                sessao.getHorarioInicio(),
                sessao.getHorarioFim()
        );

        BigDecimal horas = BigDecimal.valueOf(duracao.toMinutes())
                .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);

        return regra.getValorPorHora()
                .multiply(horas)
                .setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calcularValorAluguel(Contrato contrato) {
        validarContrato(contrato);

        List<Sessao> sessoes = sessaoDao.buscarPorPecaEPeriodo(
                contrato.getPeca(),
                contrato.getDataInicio(),
                contrato.getDataFim()
        );

        if (sessoes.isEmpty()) {
            throw new IllegalArgumentException(
                    "O contrato deve possuir pelo menos uma sessão no período informado."
            );
        }

        BigDecimal valorTotal = BigDecimal.ZERO;

        for (Sessao sessao : sessoes) {
            BigDecimal valorSessao = calcularValorSessao(sessao);
            valorTotal = valorTotal.add(valorSessao);
        }
        return valorTotal.setScale(2, RoundingMode.HALF_UP);
    }
}