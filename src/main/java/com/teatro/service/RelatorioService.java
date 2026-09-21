package com.teatro.service;

import com.teatro.dao.ContratoDao;
import com.teatro.dao.IngressoDao;
import com.teatro.dto.PresencaDto;
import com.teatro.model.Contrato;
import com.teatro.model.Ingresso;
import com.teatro.model.Peca;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RelatorioService {

    private final IngressoDao ingressoDao;
    private final ContratoDao contratoDao;

    public RelatorioService() {
        this.ingressoDao = new IngressoDao();
        this.contratoDao = new ContratoDao();
    }

    // =========================================================
    // LISTA DE PRESENÇA
    // =========================================================

    public List<PresencaDto> listarPresenca(
            Peca peca,
            LocalDate data) {

        if (peca == null) {
            throw new IllegalArgumentException(
                    "A peça é obrigatória."
            );
        }

        if (data == null) {
            throw new IllegalArgumentException(
                    "A data é obrigatória."
            );
        }

        List<Ingresso> ingressos =
                ingressoDao.buscarPorPecaEData(
                        peca,
                        data
                );

        List<PresencaDto> lista =
                new ArrayList<>();

        for (Ingresso ingresso : ingressos) {

            if (ingresso.getCliente() == null
                    || ingresso.getSessao() == null
                    || ingresso.getSessao().getPeca() == null) {
                continue;
            }

            PresencaDto dto =
                    new PresencaDto(
                            ingresso.getCliente()
                                    .getNomeCompleto(),

                            ingresso.getSessao()
                                    .getPeca()
                                    .getTitulo(),

                            ingresso.getSessao()
                                    .getData(),

                            ingresso.getSessao()
                                    .getHorarioInicio(),

                            ingresso.getQuantidade(),

                            ingresso.getValorTotal()
                    );

            lista.add(dto);
        }

        return lista;
    }

    // =========================================================
    // RELATÓRIO FINANCEIRO POR PEÇA
    // =========================================================

    public BigDecimal calcularTotalPorPeca(Peca peca) {

        validarPeca(peca);

        BigDecimal total = BigDecimal.ZERO;

        List<Ingresso> ingressos =
                ingressoDao.listarTodos();

        for (Ingresso ingresso : ingressos) {

            if (ingresso.getSessao() == null
                    || ingresso.getSessao().getPeca() == null
                    || ingresso.getValorTotal() == null) {
                continue;
            }

            if (ingresso.getSessao()
                    .getPeca()
                    .getId()
                    .equals(peca.getId())) {

                total = total.add(
                        ingresso.getValorTotal()
                );
            }
        }

        return total.setScale(2);
    }

    /**
     * Receita obtida com ingressos da peça.
     */
    public BigDecimal calcularReceitaIngressosPorPeca(
            Peca peca) {

        return calcularTotalPorPeca(peca);
    }

    /**
     * Soma dos valores dos contratos da peça.
     */
    public BigDecimal calcularAluguelPorPeca(
            Peca peca) {

        validarPeca(peca);

        BigDecimal total = BigDecimal.ZERO;

        List<Contrato> contratos =
                contratoDao.listarTodos();

        for (Contrato contrato : contratos) {

            if (contrato.getPeca() == null
                    || contrato.getValorAluguel() == null) {
                continue;
            }

            if (contrato.getPeca()
                    .getId()
                    .equals(peca.getId())) {

                total = total.add(
                        contrato.getValorAluguel()
                );
            }
        }

        return total.setScale(2);
    }

    /**
     * Saldo da peça:
     *
     * receita dos ingressos - valor do aluguel.
     */
    public BigDecimal calcularSaldoPorPeca(
            Peca peca) {

        BigDecimal receitaIngressos =
                calcularReceitaIngressosPorPeca(peca);

        BigDecimal valorAluguel =
                calcularAluguelPorPeca(peca);

        return receitaIngressos
                .subtract(valorAluguel)
                .setScale(2);
    }

    // =========================================================
    // RELATÓRIO FINANCEIRO DO TEATRO POR PERÍODO
    // =========================================================

    /**
     * Receita de ingressos das sessões realizadas
     * no período.
     */
    public BigDecimal calcularReceitaIngressosPorPeriodo(
            LocalDate dataInicio,
            LocalDate dataFim) {

        validarPeriodo(dataInicio, dataFim);

        BigDecimal total = BigDecimal.ZERO;

        List<Ingresso> ingressos =
                ingressoDao.listarTodos();

        for (Ingresso ingresso : ingressos) {

            if (ingresso.getSessao() == null
                    || ingresso.getSessao().getData() == null
                    || ingresso.getValorTotal() == null) {
                continue;
            }

            LocalDate dataSessao =
                    ingresso.getSessao().getData();

            if (!dataSessao.isBefore(dataInicio)
                    && !dataSessao.isAfter(dataFim)) {

                total = total.add(
                        ingresso.getValorTotal()
                );
            }
        }

        return total.setScale(2);
    }

    /**
     * Receita dos contratos iniciados no período.
     */
    public BigDecimal calcularReceitaContratosPorPeriodo(
            LocalDate dataInicio,
            LocalDate dataFim) {

        validarPeriodo(dataInicio, dataFim);

        BigDecimal total = BigDecimal.ZERO;

        List<Contrato> contratos =
                contratoDao.listarTodos();

        for (Contrato contrato : contratos) {

            if (contrato.getDataInicio() == null
                    || contrato.getValorAluguel() == null) {
                continue;
            }

            LocalDate inicioContrato =
                    contrato.getDataInicio();

            if (!inicioContrato.isBefore(dataInicio)
                    && !inicioContrato.isAfter(dataFim)) {

                total = total.add(
                        contrato.getValorAluguel()
                );
            }
        }

        return total.setScale(2);
    }

    /**
     * Receita geral do teatro no período:
     *
     * ingressos + contratos.
     */
    public BigDecimal calcularTotalPorPeriodo(
            LocalDate dataInicio,
            LocalDate dataFim) {

        BigDecimal receitaIngressos =
                calcularReceitaIngressosPorPeriodo(
                        dataInicio,
                        dataFim
                );

        BigDecimal receitaContratos =
                calcularReceitaContratosPorPeriodo(
                        dataInicio,
                        dataFim
                );

        return receitaIngressos
                .add(receitaContratos)
                .setScale(2);
    }

    // =========================================================
    // VALIDAÇÕES
    // =========================================================

    private void validarPeca(Peca peca) {

        if (peca == null) {
            throw new IllegalArgumentException(
                    "A peça é obrigatória."
            );
        }
    }

    private void validarPeriodo(
            LocalDate dataInicio,
            LocalDate dataFim) {

        if (dataInicio == null
                || dataFim == null) {

            throw new IllegalArgumentException(
                    "As datas do período são obrigatórias."
            );
        }

        if (dataFim.isBefore(dataInicio)) {

            throw new IllegalArgumentException(
                    "A data final não pode ser anterior à data inicial."
            );
        }
    }
}