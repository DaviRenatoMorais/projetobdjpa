package com.teatro.service;

import com.teatro.dao.*;
import com.teatro.model.Artista;
import com.teatro.model.Cliente;
import com.teatro.model.Contrato;
import com.teatro.model.Ingresso;
import com.teatro.model.Peca;
import com.teatro.model.RegraPreco;
import com.teatro.model.Sessao;
import com.teatro.model.StatusContrato;
import com.teatro.model.TipoGenero;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class IngressoServiceTest {

    @Test
    void deveVenderIngressoParaContratoAtivo() {

        Artista artista = new Artista();
        artista.setCpf("77788899900");
        artista.setNomeCompleto("Artista Ingresso");
        artista.setTelefone("83999999996");
        artista.setEmail("ingresso@teste.com");
        artista.setGenero(TipoGenero.OUTRO);
        artista.setDataNascimento(LocalDate.of(1990, 1, 1));

        ArtistaDao artistaDao = new ArtistaDao();
        artistaDao.salvar(artista);

        Cliente cliente = new Cliente();
        cliente.setCpf("11122233344");
        cliente.setNomeCompleto("Cliente Teste");
        cliente.setTelefone("83999999995");
        cliente.setEmail("cliente@teste.com");
        cliente.setGenero(TipoGenero.OUTRO);
        cliente.setDataNascimento(LocalDate.of(2000, 1, 1));

        ClienteDao clienteDao = new ClienteDao();
        clienteDao.salvar(cliente);

        Peca peca = new Peca();
        peca.setTitulo("Peça Ingresso");
        peca.setDescricao("Teste de venda");

        PecaDao pecaDao = new PecaDao();
        pecaDao.salvar(peca);

        RegraPreco regra = new RegraPreco();
        regra.setValorPorHora(new BigDecimal("100.00"));

        RegraPrecoDao regraDao = new RegraPrecoDao();
        regraDao.salvar(regra);

        Sessao sessao = new Sessao();
        sessao.setPeca(peca);
        sessao.setData(LocalDate.of(2026, 11, 1));
        sessao.setHorarioInicio(LocalTime.of(19, 0));
        sessao.setHorarioFim(LocalTime.of(21, 0));

        SessaoDao sessaoDao = new SessaoDao();
        sessaoDao.salvar(sessao);

        Contrato contrato = new Contrato();
        contrato.setArtista(artista);
        contrato.setPeca(peca);
        contrato.setDataInicio(LocalDate.of(2026, 11, 1));
        contrato.setDataFim(LocalDate.of(2026, 11, 1));
        contrato.setStatus(StatusContrato.ATIVO);

        ContratoDao contratoDao = new ContratoDao();
        ContratoService contratoService = new ContratoService();

        contratoService.salvar(contrato);

        IngressoService ingressoService =
                new IngressoService();

        ingressoService.vender(
                cliente,
                sessao,
                3,
                new BigDecimal("25.00")
        );

        Ingresso ingresso =
                ingressoService.listarTodos()
                        .stream()
                        .filter(i -> i.getSessao().getId()
                                .equals(sessao.getId()))
                        .findFirst()
                        .orElse(null);

        assertNotNull(ingresso);

        assertEquals(
                cliente.getId(),
                ingresso.getCliente().getId()
        );

        assertEquals(
                3,
                ingresso.getQuantidade()
        );

        assertEquals(
                new BigDecimal("75.00"),
                ingresso.getValorTotal()
        );

        assertNotNull(ingresso.getDataVenda());

        ingressoService.remover(ingresso);
        contratoDao.remover(contrato);
        sessaoDao.remover(sessao);
        regraDao.remover(regra);
        pecaDao.remover(peca);
        clienteDao.remover(cliente);
        artistaDao.remover(artista);
    }

    @Test
    void deveRejeitarVendaParaContratoEncerrado() {

        Artista artista = new Artista();
        artista.setCpf("88899900011");
        artista.setNomeCompleto("Artista Contrato Encerrado");
        artista.setTelefone("83999999994");
        artista.setEmail("encerrado.ingresso@teste.com");
        artista.setGenero(TipoGenero.OUTRO);
        artista.setDataNascimento(LocalDate.of(1990, 1, 1));

        ArtistaDao artistaDao = new ArtistaDao();
        artistaDao.salvar(artista);

        Cliente cliente = new Cliente();
        cliente.setCpf("22233344455");
        cliente.setNomeCompleto("Cliente Contrato Encerrado");
        cliente.setTelefone("83999999993");
        cliente.setEmail("cliente.encerrado@teste.com");
        cliente.setGenero(TipoGenero.OUTRO);
        cliente.setDataNascimento(LocalDate.of(2000, 1, 1));

        ClienteDao clienteDao = new ClienteDao();
        clienteDao.salvar(cliente);

        Peca peca = new Peca();
        peca.setTitulo("Peça Contrato Encerrado");
        peca.setDescricao("Teste");

        PecaDao pecaDao = new PecaDao();
        pecaDao.salvar(peca);

        RegraPreco regra = new RegraPreco();
        regra.setValorPorHora(new BigDecimal("100.00"));

        RegraPrecoDao regraDao = new RegraPrecoDao();
        regraDao.salvar(regra);

        Sessao sessao = new Sessao();
        sessao.setPeca(peca);
        sessao.setData(LocalDate.of(2026, 11, 5));
        sessao.setHorarioInicio(LocalTime.of(19, 0));
        sessao.setHorarioFim(LocalTime.of(21, 0));

        SessaoDao sessaoDao = new SessaoDao();
        sessaoDao.salvar(sessao);

        Contrato contrato = new Contrato();
        contrato.setArtista(artista);
        contrato.setPeca(peca);
        contrato.setDataInicio(LocalDate.of(2026, 11, 5));
        contrato.setDataFim(LocalDate.of(2026, 11, 5));
        contrato.setStatus(StatusContrato.ATIVO);

        ContratoService contratoService = new ContratoService();
        contratoService.salvar(contrato);

        contratoService.encerrarContrato(contrato.getId(), LocalDate.of(2026, 11, 5));

        IngressoService ingressoService =
                new IngressoService();

        assertThrows(
                IllegalArgumentException.class,
                () -> ingressoService.vender(
                        cliente,
                        sessao,
                        2,
                        new BigDecimal("25.00")
                )
        );

        assertEquals(
                0,
                ingressoService.listarTodos()
                        .stream()
                        .filter(i -> i.getSessao().getId()
                                .equals(sessao.getId()))
                        .count()
        );

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
    void deveRejeitarQuantidadeZero() {

        IngressoService ingressoService = new IngressoService();

        assertThrows(
                IllegalArgumentException.class,
                () -> ingressoService.vender(
                        null,
                        null,
                        0,
                        new BigDecimal("25.00")
                )
        );
    }

    @Test
    void deveRejeitarQuantidadeNegativa() {

        IngressoService ingressoService = new IngressoService();

        assertThrows(
                IllegalArgumentException.class,
                () -> ingressoService.vender(
                        null,
                        null,
                        -1,
                        new BigDecimal("25.00")
                )
        );
    }

    @Test
    void deveRejeitarQuantidadeNula() {

        IngressoService ingressoService = new IngressoService();

        assertThrows(
                IllegalArgumentException.class,
                () -> ingressoService.vender(
                        null,
                        null,
                        null,
                        new BigDecimal("25.00")
                )
        );
    }

    @Test
    void deveRejeitarValorIngressoInvalido() {

        IngressoService ingressoService = new IngressoService();

        assertThrows(
                IllegalArgumentException.class,
                () -> ingressoService.vender(
                        null,
                        null,
                        2,
                        null
                )
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> ingressoService.vender(
                        null,
                        null,
                        2,
                        BigDecimal.ZERO
                )
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> ingressoService.vender(
                        null,
                        null,
                        2,
                        new BigDecimal("-10.00")
                )
        );
    }

    @Test
    void deveRejeitarPecaNulaNaListaDePresenca() {
        IngressoService ingressoService = new IngressoService();

        assertThrows(
                IllegalArgumentException.class,
                () -> ingressoService.listarPresenca(null, LocalDate.of(2030, 7, 15))
        );
    }

    @Test
    void deveRejeitarDataNulaNaListaDePresenca() {
        IngressoService ingressoService = new IngressoService();

        Peca peca = new Peca();

        assertThrows(
                IllegalArgumentException.class,
                () -> ingressoService.listarPresenca(peca, null)
        );
    }

    @Test
    void deveListarPresencaPorPecaEData() {

        Artista artista = new Artista();
        artista.setCpf("33333333333");
        artista.setNomeCompleto("Artista Lista Presenca");
        artista.setTelefone("83999999992");
        artista.setEmail("presenca.artista@teste.com");
        artista.setGenero(TipoGenero.OUTRO);
        artista.setDataNascimento(LocalDate.of(1990, 1, 1));

        ArtistaDao artistaDao = new ArtistaDao();
        artistaDao.salvar(artista);

        Cliente cliente = new Cliente();
        cliente.setCpf("44444444444");
        cliente.setNomeCompleto("Cliente Lista Presenca");
        cliente.setTelefone("83999999991");
        cliente.setEmail("presenca.cliente@teste.com");
        cliente.setGenero(TipoGenero.OUTRO);
        cliente.setDataNascimento(LocalDate.of(2000, 1, 1));

        ClienteDao clienteDao = new ClienteDao();
        clienteDao.salvar(cliente);

        Peca peca = new Peca();
        peca.setTitulo("Peça Lista Presenca");
        peca.setDescricao("Teste da lista de presença");

        PecaDao pecaDao = new PecaDao();
        pecaDao.salvar(peca);

        RegraPreco regra = new RegraPreco();
        regra.setValorPorHora(new BigDecimal("100.00"));

        RegraPrecoDao regraDao = new RegraPrecoDao();
        regraDao.salvar(regra);

        LocalDate dataSessao = LocalDate.of(2030, 7, 15);

        Sessao sessao = new Sessao();
        sessao.setPeca(peca);
        sessao.setData(dataSessao);
        sessao.setHorarioInicio(LocalTime.of(19, 0));
        sessao.setHorarioFim(LocalTime.of(21, 0));

        SessaoDao sessaoDao = new SessaoDao();
        sessaoDao.salvar(sessao);

        Contrato contrato = new Contrato();
        contrato.setArtista(artista);
        contrato.setPeca(peca);
        contrato.setDataInicio(dataSessao);
        contrato.setDataFim(dataSessao);
        contrato.setStatus(StatusContrato.ATIVO);

        ContratoService contratoService = new ContratoService();
        contratoService.salvar(contrato);

        IngressoService ingressoService = new IngressoService();

        ingressoService.vender(
                cliente,
                sessao,
                3,
                new BigDecimal("25.00")
        );

        List<Ingresso> resultado =
                ingressoService.listarPresenca(peca, dataSessao);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());

        Ingresso ingresso = resultado.get(0);

        assertEquals(
                cliente.getId(),
                ingresso.getCliente().getId()
        );

        assertEquals(
                peca.getId(),
                ingresso.getSessao().getPeca().getId()
        );

        assertEquals(
                dataSessao,
                ingresso.getSessao().getData()
        );

        assertEquals(
                3,
                ingresso.getQuantidade()
        );

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
}