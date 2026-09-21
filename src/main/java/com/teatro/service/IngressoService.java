package com.teatro.service;

import com.teatro.dao.ContratoDao;
import com.teatro.dao.IngressoDao;
import com.teatro.model.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class IngressoService {

    private final IngressoDao ingressoDao;
    private final ContratoDao contratoDao;

    public IngressoService() {
        this.ingressoDao = new IngressoDao();
        this.contratoDao = new ContratoDao();
    }

    public void vender(
            Cliente cliente,
            Sessao sessao,
            Integer quantidade,
            BigDecimal valorIngresso) {

        validarVenda(cliente, sessao, quantidade, valorIngresso);

        Contrato contrato =
                contratoDao.buscarContratoDaSessao(sessao);

        if (contrato == null) {
            throw new IllegalArgumentException(
                    "Não existe contrato ativo para esta sessão."
            );
        }

        if (contrato.getStatus() == StatusContrato.ENCERRADO) {
            throw new IllegalArgumentException(
                    "Não é possível vender ingressos para um contrato encerrado."
            );
        }

        BigDecimal valorTotal =
                valorIngresso.multiply(
                        BigDecimal.valueOf(quantidade)
                );

        Ingresso ingresso = new Ingresso();

        ingresso.setCliente(cliente);
        ingresso.setSessao(sessao);
        ingresso.setQuantidade(quantidade);
        ingresso.setValorTotal(valorTotal);
        ingresso.setDataVenda(LocalDateTime.now());

        ingressoDao.salvar(ingresso);
    }

    private void validarVenda(
            Cliente cliente,
            Sessao sessao,
            Integer quantidade,
            BigDecimal valorIngresso) {

        if (cliente == null) {
            throw new IllegalArgumentException(
                    "O cliente é obrigatório."
            );
        }

        if (sessao == null) {
            throw new IllegalArgumentException(
                    "A sessão é obrigatória."
            );
        }

        if (quantidade == null || quantidade <= 0) {
            throw new IllegalArgumentException(
                    "A quantidade deve ser maior que zero."
            );
        }

        if (valorIngresso == null
                || valorIngresso.signum() <= 0) {
            throw new IllegalArgumentException(
                    "O valor do ingresso deve ser maior que zero."
            );
        }
    }

    public Ingresso buscarPorId(Long id) {
        return ingressoDao.buscarPorId(id);
    }

    public List<Ingresso> listarTodos() {
        return ingressoDao.listarTodos();
    }

    public void remover(Ingresso ingresso) {
        ingressoDao.remover(ingresso);
    }

    public List<Ingresso> listarPresenca(Peca peca, LocalDate data) {
        if (peca == null) {
            throw new IllegalArgumentException("A peça é obrigatória.");
        }

        if (data == null) {
            throw new IllegalArgumentException("A data é obrigatória.");
        }

        return ingressoDao.buscarPorPecaEData(peca, data);
    }
}