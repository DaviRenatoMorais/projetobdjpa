package com.teatro.service;

import com.teatro.dao.*;
import com.teatro.model.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class RelatorioServiceTest {

    @Test
    void deveCalcularTotalFinanceiroPorPeca() {

        Artista artista = new Artista();
        artista.setCpf("55566677788");
        artista.setNomeCompleto("Artista Relatorio Peca");
        artista.setTelefone("83999999990");
        artista.setEmail("relatorio.peca@teste.com");
        artista.setGenero(TipoGenero.OUTRO);
        artista.setDataNascimento(LocalDate.of(1990, 1, 1));

        ArtistaDao artistaDao = new ArtistaDao();
        artistaDao.salvar(artista);

        Cliente cliente = new Cliente();
        cliente.setCpf("66677788899");
        cliente.setNomeCompleto("Cliente Relatorio Peca");
        cliente.setTelefone("83999999989");
        cliente.setEmail("relatorio.cliente@teste.com");
        cliente.setGenero(TipoGenero.OUTRO);
        cliente.setDataNascimento(LocalDate.of(2000, 1, 1));

        ClienteDao clienteDao = new ClienteDao();
        clienteDao.salvar(cliente);

        Peca peca = new Peca();
        peca.setTitulo("Peça Relatorio Financeiro");
        peca.setDescricao("Teste de relatório financeiro");

        PecaDao pecaDao = new PecaDao();
        pecaDao.salvar(peca);

        RegraPreco regra = new RegraPreco();
        regra.setValorPorHora(new BigDecimal("100.00"));

        RegraPrecoDao regraDao = new RegraPrecoDao();
        regraDao.salvar(regra);

        LocalDate data = LocalDate.of(2030, 8, 10);

        Sessao sessao = new Sessao();
        sessao.setPeca(peca);
        sessao.setData(data);
        sessao.setHorarioInicio(LocalTime.of(19, 0));
        sessao.setHorarioFim(LocalTime.of(21, 0));

        SessaoDao sessaoDao = new SessaoDao();
        sessaoDao.salvar(sessao);

        Contrato contrato = new Contrato();
        contrato.setArtista(artista);
        contrato.setPeca(peca);
        contrato.setDataInicio(data);
        contrato.setDataFim(data);
        contrato.setStatus(StatusContrato.ATIVO);

        ContratoService contratoService =
                new ContratoService();

        contratoService.salvar(contrato);

        IngressoService ingressoService =
                new IngressoService();

        ingressoService.vender(
                cliente,
                sessao,
                3,
                new BigDecimal("25.00")
        );

        RelatorioService relatorioService =
                new RelatorioService();

        BigDecimal total =
                relatorioService.calcularTotalPorPeca(peca);

        assertEquals(
                new BigDecimal("75.00"),
                total
        );

        Ingresso ingresso =
                ingressoService.listarTodos()
                        .stream()
                        .filter(i -> i.getSessao().getId()
                                .equals(sessao.getId()))
                        .findFirst()
                        .orElse(null);

        assertNotNull(ingresso);

        ingressoService.remover(ingresso);
        contratoService.remover(
                contratoService.buscarPorId(contrato.getId())
        );
        sessaoDao.remover(sessao);
        regraDao.remover(regra);
        pecaDao.remover(peca);
        clienteDao.remover(cliente);
        artistaDao.remover(artista);
    }

    @Test
    void deveCalcularTotalFinanceiroPorPeriodo() {

        Artista artista = new Artista();
        artista.setCpf("77766655544");
        artista.setNomeCompleto("Artista Relatorio Periodo");
        artista.setTelefone("83999999988");
        artista.setEmail("relatorio.periodo@teste.com");
        artista.setGenero(TipoGenero.OUTRO);
        artista.setDataNascimento(LocalDate.of(1990, 1, 1));

        ArtistaDao artistaDao = new ArtistaDao();
        artistaDao.salvar(artista);

        Cliente cliente = new Cliente();
        cliente.setCpf("88877766655");
        cliente.setNomeCompleto("Cliente Relatorio Periodo");
        cliente.setTelefone("83999999987");
        cliente.setEmail("cliente.periodo@teste.com");
        cliente.setGenero(TipoGenero.OUTRO);
        cliente.setDataNascimento(LocalDate.of(2000, 1, 1));

        ClienteDao clienteDao = new ClienteDao();
        clienteDao.salvar(cliente);

        Peca peca = new Peca();
        peca.setTitulo("Peça Relatorio Periodo");
        peca.setDescricao("Teste");

        PecaDao pecaDao = new PecaDao();
        pecaDao.salvar(peca);

        RegraPreco regra = new RegraPreco();
        regra.setValorPorHora(new BigDecimal("100.00"));

        RegraPrecoDao regraDao = new RegraPrecoDao();
        regraDao.salvar(regra);

        LocalDate data = LocalDate.of(2030, 9, 10);

        Sessao sessao = new Sessao();
        sessao.setPeca(peca);
        sessao.setData(data);
        sessao.setHorarioInicio(LocalTime.of(19, 0));
        sessao.setHorarioFim(LocalTime.of(21, 0));

        SessaoDao sessaoDao = new SessaoDao();
        sessaoDao.salvar(sessao);

        Contrato contrato = new Contrato();
        contrato.setArtista(artista);
        contrato.setPeca(peca);
        contrato.setDataInicio(data);
        contrato.setDataFim(data);
        contrato.setStatus(StatusContrato.ATIVO);

        ContratoService contratoService =
                new ContratoService();

        contratoService.salvar(contrato);

        IngressoService ingressoService =
                new IngressoService();

        ingressoService.vender(
                cliente,
                sessao,
                2,
                new BigDecimal("30.00")
        );

        RelatorioService relatorioService =
                new RelatorioService();

        BigDecimal total =
                relatorioService.calcularTotalPorPeriodo(
                        LocalDate.of(2030, 9, 1),
                        LocalDate.of(2030, 9, 30)
                );

        assertEquals(
                new BigDecimal("260.00"),
                total
        );

        Ingresso ingresso =
                ingressoService.listarTodos()
                        .stream()
                        .filter(i -> i.getSessao().getId()
                                .equals(sessao.getId()))
                        .findFirst()
                        .orElse(null);

        assertNotNull(ingresso);

        ingressoService.remover(ingresso);
        contratoService.remover(
                contratoService.buscarPorId(contrato.getId())
        );
        sessaoDao.remover(sessao);
        regraDao.remover(regra);
        pecaDao.remover(peca);
        clienteDao.remover(cliente);
        artistaDao.remover(artista);
    }

    @Test
    void deveRejeitarPecaNulaNoRelatorioFinanceiro() {

        RelatorioService relatorioService =
                new RelatorioService();

        assertThrows(
                IllegalArgumentException.class,
                () -> relatorioService.calcularTotalPorPeca(null)
        );
    }

    @Test
    void deveRejeitarPeriodoInvalido() {

        RelatorioService relatorioService =
                new RelatorioService();

        assertThrows(
                IllegalArgumentException.class,
                () -> relatorioService.calcularTotalPorPeriodo(
                        LocalDate.of(2030, 10, 30),
                        LocalDate.of(2030, 10, 1)
                )
        );
    }
}