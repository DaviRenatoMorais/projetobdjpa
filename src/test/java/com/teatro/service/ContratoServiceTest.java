package com.teatro.service;

import com.teatro.dao.ArtistaDao;
import com.teatro.dao.ClienteDao;
import com.teatro.dao.ContratoDao;
import com.teatro.dao.IngressoDao;
import com.teatro.dao.PecaDao;
import com.teatro.dao.RegraPrecoDao;
import com.teatro.dao.SessaoDao;
import com.teatro.model.Artista;
import com.teatro.model.Cliente;
import com.teatro.model.Contrato;
import com.teatro.model.Ingresso;
import com.teatro.model.Peca;
import com.teatro.model.RegraPreco;
import com.teatro.model.Sessao;
import com.teatro.model.StatusContrato;
import com.teatro.model.TipoGenero;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ContratoServiceTest {
    private final ArtistaDao artistaDao = new ArtistaDao();
    private final ClienteDao clienteDao = new ClienteDao();
    private final PecaDao pecaDao = new PecaDao();
    private final RegraPrecoDao regraPrecoDao = new RegraPrecoDao();
    private final SessaoDao sessaoDao = new SessaoDao();
    private final ContratoDao contratoDao = new ContratoDao();
    private final IngressoDao ingressoDao = new IngressoDao();

    private final List<Artista> artistasCriados = new ArrayList<>();
    private final List<Cliente> clientesCriados = new ArrayList<>();
    private final List<Peca> pecasCriadas = new ArrayList<>();
    private final List<RegraPreco> regrasCriadas = new ArrayList<>();
    private final List<Sessao> sessoesCriadas = new ArrayList<>();
    private final List<Contrato> contratosCriados = new ArrayList<>();
    private final List<Ingresso> ingressosCriados = new ArrayList<>();

    @AfterEach
    void limparDadosDoTeste() {

        for (Ingresso ingresso : ingressosCriados) {
            try {
                ingressoDao.remover(ingresso);
            } catch (Exception ignored) {
            }
        }

        for (Contrato contrato : contratosCriados) {
            try {
                contratoDao.remover(contrato);
            } catch (Exception ignored) {
            }
        }

        for (Sessao sessao : sessoesCriadas) {
            try {
                sessaoDao.remover(sessao);
            } catch (Exception ignored) {
            }
        }

        for (RegraPreco regra : regrasCriadas) {
            try {
                regraPrecoDao.remover(regra);
            } catch (Exception ignored) {
            }
        }

        for (Peca peca : pecasCriadas) {
            try {
                pecaDao.remover(peca);
            } catch (Exception ignored) {
            }
        }

        for (Cliente cliente : clientesCriados) {
            try {
                clienteDao.remover(cliente);
            } catch (Exception ignored) {
            }
        }

        for (Artista artista : artistasCriados) {
            try {
                artistaDao.remover(artista);
            } catch (Exception ignored) {
            }
        }

        ingressosCriados.clear();
        contratosCriados.clear();
        sessoesCriadas.clear();
        regrasCriadas.clear();
        pecasCriadas.clear();
        clientesCriados.clear();
        artistasCriados.clear();
    }

    @Test
    void deveSalvarEBuscarContrato() {

        Artista artista = criarArtista(
                "12345600001",
                "Artista Teste"
        );

        Peca peca = criarPeca(
                "Peça Teste",
                "Peça utilizada no teste."
        );

        RegraPreco regra = criarRegra(
                new BigDecimal("100.00")
        );

        Sessao sessao = criarSessao(
                peca,
                LocalDate.of(2026, 9, 15),
                LocalTime.of(19, 0),
                LocalTime.of(21, 0)
        );

        Contrato contrato = criarContrato(
                artista,
                peca,
                LocalDate.of(2026, 9, 15),
                LocalDate.of(2026, 9, 20),
                StatusContrato.ATIVO
        );

        ContratoService service = new ContratoService();

        service.salvar(contrato);

        assertNotNull(contrato.getId());

        Contrato encontrado =
                service.buscarPorId(contrato.getId());

        assertNotNull(encontrado);
        assertEquals(
                "Peça Teste",
                encontrado.getPeca().getTitulo()
        );
        assertEquals(
                "Artista Teste",
                encontrado.getArtista().getNomeCompleto()
        );
        assertEquals(
                new BigDecimal("200.00"),
                encontrado.getValorAluguel()
        );
        assertEquals(
                StatusContrato.ATIVO,
                encontrado.getStatus()
        );

        contratosCriados.add(contrato);
    }

    @Test
    void deveRejeitarContratoComDataFinalAnterior() {

        ContratoService service = new ContratoService();

        Contrato contrato = new Contrato();

        contrato.setDataInicio(
                LocalDate.of(2026, 9, 20)
        );

        contrato.setDataFim(
                LocalDate.of(2026, 9, 15)
        );

        IllegalArgumentException excecao =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> service.salvar(contrato)
                );

        assertEquals(
                "A data final deve ser posterior ou igual à data inicial.",
                excecao.getMessage()
        );
    }

    @Test
    void deveCalcularValorDaSessao() {

        RegraPreco regra = criarRegra(
                new BigDecimal("80.00")
        );

        Sessao sessao = new Sessao();

        sessao.setData(
                LocalDate.of(2030, 9, 14)
        );

        sessao.setHorarioInicio(
                LocalTime.of(19, 0)
        );

        sessao.setHorarioFim(
                LocalTime.of(21, 0)
        );

        ContratoService contratoService =
                new ContratoService();

        BigDecimal valor =
                contratoService.calcularValorSessao(sessao);

        assertEquals(
                new BigDecimal("160.00"),
                valor
        );
    }

    @Test
    void deveCalcularValorTotalDoAluguel() {

        Artista artista = criarArtista(
                "98765432000",
                "Artista Aluguel"
        );

        Peca peca = criarPeca(
                "Peça Aluguel",
                "Teste de cálculo."
        );

        RegraPreco regra = criarRegra(
                new BigDecimal("80.00")
        );

        Sessao sessao1 = criarSessao(
                peca,
                LocalDate.of(2030, 9, 15),
                LocalTime.of(19, 0),
                LocalTime.of(21, 0)
        );

        Sessao sessao2 = criarSessao(
                peca,
                LocalDate.of(2030, 9, 16),
                LocalTime.of(19, 0),
                LocalTime.of(20, 0)
        );

        Contrato contrato = criarContrato(
                artista,
                peca,
                LocalDate.of(2030, 9, 15),
                LocalDate.of(2030, 9, 16),
                StatusContrato.ATIVO
        );

        ContratoService service =
                new ContratoService();

        BigDecimal valor =
                service.calcularValorAluguel(contrato);

        /*
         * Sessão 1:
         * 2 horas × R$ 80 = R$ 160
         *
         * Sessão 2:
         * 1 hora × R$ 80 = R$ 80
         *
         * Total:
         * R$ 240
         */

        assertEquals(
                new BigDecimal("240.00"),
                valor
        );
    }

    @Test
    void deveCalcularValorAluguelAutomaticamenteAoSalvar() {

        Artista artista = criarArtista(
                "19122233344",
                "Artista Automatico"
        );

        Peca peca = criarPeca(
                "Peça Automática",
                "Teste do cálculo automático."
        );

        RegraPreco regra = criarRegra(
                new BigDecimal("100.00")
        );

        Sessao sessao = criarSessao(
                peca,
                LocalDate.of(2030, 9, 20),
                LocalTime.of(19, 0),
                LocalTime.of(21, 0)
        );

        Contrato contrato = criarContrato(
                artista,
                peca,
                LocalDate.of(2030, 9, 20),
                LocalDate.of(2030, 9, 20),
                StatusContrato.ATIVO
        );

        ContratoService service =
                new ContratoService();

        service.salvar(contrato);

        assertEquals(
                new BigDecimal("200.00"),
                contrato.getValorAluguel()
        );

        Contrato encontrado =
                service.buscarPorId(contrato.getId());

        assertNotNull(encontrado);

        assertEquals(
                new BigDecimal("200.00"),
                encontrado.getValorAluguel()
        );

        contratosCriados.add(contrato);
    }

    @Test
    void deveEncerrarContrato() {

        Artista artista = criarArtista(
                "22233344455",
                "Artista Encerramento"
        );

        Peca peca = criarPeca(
                "Peça Encerramento",
                "Teste de encerramento."
        );

        RegraPreco regra = criarRegra(
                new BigDecimal("100.00")
        );

        Sessao sessao = criarSessao(
                peca,
                LocalDate.of(2030, 9, 20),
                LocalTime.of(19, 0),
                LocalTime.of(21, 0)
        );

        Contrato contrato = criarContrato(
                artista,
                peca,
                LocalDate.of(2030, 9, 15),
                LocalDate.of(2030, 9, 25),
                StatusContrato.ATIVO
        );

        ContratoService service =
                new ContratoService();

        service.salvar(contrato);

        service.encerrarContrato(
                contrato.getId(),
                LocalDate.of(2030, 9, 20)
        );

        Contrato encontrado =
                service.buscarPorId(contrato.getId());

        assertNotNull(encontrado);

        assertEquals(
                StatusContrato.ENCERRADO,
                encontrado.getStatus()
        );

        assertEquals(
                LocalDate.of(2030, 9, 20),
                encontrado.getDataFim()
        );

        contratosCriados.add(contrato);
    }

    @Test
    void deveRejeitarEncerramentoDeContratoJaEncerrado() {

        Artista artista = criarArtista(
                "33344455566",
                "Artista Encerrado"
        );

        Peca peca = criarPeca(
                "Peça Encerrada",
                "Teste de contrato já encerrado."
        );

        RegraPreco regra = criarRegra(
                new BigDecimal("100.00")
        );

        Sessao sessao = criarSessao(
                peca,
                LocalDate.of(2030, 9, 26),
                LocalTime.of(19, 0),
                LocalTime.of(21, 0)
        );

        Contrato contrato = criarContrato(
                artista,
                peca,
                LocalDate.of(2030, 9, 26),
                LocalDate.of(2030, 9, 26),
                StatusContrato.ATIVO
        );

        ContratoService service =
                new ContratoService();

        service.salvar(contrato);

        /*
         * Primeiro encerramos corretamente o contrato
         * na própria data final.
         */
        service.encerrarContrato(
                contrato.getId(),
                LocalDate.of(2030, 9, 26)
        );

        /*
         * Agora o contrato já está ENCERRADO.
         * Portanto, a segunda tentativa deve falhar
         * pela regra de contrato já encerrado.
         */
        IllegalArgumentException excecao =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> service.encerrarContrato(
                                contrato.getId(),
                                LocalDate.of(2030, 9, 26)
                        )
                );

        assertEquals(
                "O contrato já está encerrado.",
                excecao.getMessage()
        );

        contratosCriados.add(contrato);
    }

    @Test
    void deveRejeitarEncerramentoQuandoExistirIngressoPosterior() {

        Artista artista = criarArtista(
                "11111111111",
                "Artista Ingresso Posterior"
        );

        Cliente cliente = criarCliente(
                "22222222222",
                "Cliente Posterior"
        );

        Peca peca = criarPeca(
                "Peça Ingresso Posterior",
                "Teste"
        );

        RegraPreco regra = criarRegra(
                new BigDecimal("100.00")
        );

        Sessao sessaoContrato = criarSessao(
                peca,
                LocalDate.of(2030, 12, 5),
                LocalTime.of(19, 0),
                LocalTime.of(21, 0)
        );

        Sessao sessaoPosterior = criarSessao(
                peca,
                LocalDate.of(2030, 12, 15),
                LocalTime.of(19, 0),
                LocalTime.of(21, 0)
        );

        Contrato contrato = criarContrato(
                artista,
                peca,
                LocalDate.of(2030, 12, 1),
                LocalDate.of(2030, 12, 20),
                StatusContrato.ATIVO
        );

        ContratoService contratoService =
                new ContratoService();

        contratoService.salvar(contrato);

        IngressoService ingressoService =
                new IngressoService();

        ingressoService.vender(
                cliente,
                sessaoPosterior,
                2,
                new BigDecimal("25.00")
        );

        /*
         * O ingresso foi vendido para 15/12.
         *
         * O encerramento está sendo solicitado para 10/12.
         *
         * Portanto existe ingresso para uma sessão
         * posterior à nova data de encerramento.
         */
        IllegalArgumentException excecao =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> contratoService.encerrarContrato(
                                contrato.getId(),
                                LocalDate.of(2030, 12, 10)
                        )
                );

        assertEquals(
                "Não é possível encerrar o contrato: " +
                        "existem ingressos vendidos para sessões posteriores.",
                excecao.getMessage()
        );

        Contrato contratoAtualizado =
                contratoService.buscarPorId(
                        contrato.getId()
                );

        assertNotNull(contratoAtualizado);

        assertEquals(
                StatusContrato.ATIVO,
                contratoAtualizado.getStatus()
        );

        assertEquals(
                LocalDate.of(2030, 12, 20),
                contratoAtualizado.getDataFim()
        );

        Ingresso ingresso =
                ingressoService.listarTodos()
                        .stream()
                        .filter(i ->
                                i.getSessao()
                                        .getId()
                                        .equals(
                                                sessaoPosterior.getId()
                                        ))
                        .findFirst()
                        .orElse(null);

        assertNotNull(ingresso);

        ingressosCriados.add(ingresso);
        contratosCriados.add(contrato);
    }

    @Test
    void deveProrrogarContrato() {

        Artista artista = criarArtista(
                "44455566677",
                "Artista Prorrogação"
        );

        Peca peca = criarPeca(
                "Peça Prorrogação",
                "Teste de prorrogação"
        );

        RegraPreco regra = criarRegra(
                new BigDecimal("100.00")
        );

        Sessao sessao1 = criarSessao(
                peca,
                LocalDate.of(2030, 10, 1),
                LocalTime.of(19, 0),
                LocalTime.of(21, 0)
        );

        Sessao sessao2 = criarSessao(
                peca,
                LocalDate.of(2030, 10, 2),
                LocalTime.of(19, 0),
                LocalTime.of(21, 0)
        );

        Contrato contrato = criarContrato(
                artista,
                peca,
                LocalDate.of(2030, 10, 1),
                LocalDate.of(2030, 10, 1),
                StatusContrato.ATIVO
        );

        ContratoService service =
                new ContratoService();

        service.salvar(contrato);

        service.prorrogarContrato(
                contrato.getId(),
                LocalDate.of(2030, 10, 2)
        );

        Contrato contratoAtualizado =
                service.buscarPorId(
                        contrato.getId()
                );

        assertNotNull(contratoAtualizado);

        assertEquals(
                LocalDate.of(2030, 10, 2),
                contratoAtualizado.getDataFim()
        );

        assertEquals(
                new BigDecimal("400.00"),
                contratoAtualizado.getValorAluguel()
        );

        contratosCriados.add(contrato);
    }

    @Test
    void deveRejeitarProrrogacaoDeContratoEncerrado() {

        Artista artista = criarArtista(
                "55566677788",
                "Artista Encerrado"
        );

        Peca peca = criarPeca(
                "Peça Encerrada",
                "Teste"
        );

        RegraPreco regra = criarRegra(
                new BigDecimal("100.00")
        );

        Sessao sessao = criarSessao(
                peca,
                LocalDate.of(2030, 10, 5),
                LocalTime.of(19, 0),
                LocalTime.of(21, 0)
        );

        Contrato contrato = criarContrato(
                artista,
                peca,
                LocalDate.of(2030, 10, 5),
                LocalDate.of(2030, 10, 5),
                StatusContrato.ATIVO
        );

        ContratoService service =
                new ContratoService();

        service.salvar(contrato);

        /*
         * Primeiro encerramos o contrato
         * na própria data final.
         */
        service.encerrarContrato(
                contrato.getId(),
                LocalDate.of(2030, 10, 5)
        );

        /*
         * Agora tentamos prorrogá-lo.
         * Como o status é ENCERRADO, a operação
         * deve ser rejeitada.
         */
        IllegalArgumentException excecao =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> service.prorrogarContrato(
                                contrato.getId(),
                                LocalDate.of(2030, 10, 6)
                        )
                );

        assertEquals(
                "Não é possível prorrogar um contrato encerrado.",
                excecao.getMessage()
        );

        contratosCriados.add(contrato);
    }

    @Test
    void deveRejeitarProrrogacaoQuandoHouverConflito() {

        Artista artista = criarArtista(
                "66677788899",
                "Artista Conflito"
        );

        Peca pecaContrato = criarPeca(
                "Peça do Contrato",
                "Peça principal"
        );

        Peca outraPeca = criarPeca(
                "Outra Peça",
                "Peça que causa conflito"
        );

        RegraPreco regra = criarRegra(
                new BigDecimal("100.00")
        );

        Sessao sessaoContrato = criarSessao(
                pecaContrato,
                LocalDate.of(2030, 10, 1),
                LocalTime.of(19, 0),
                LocalTime.of(21, 0)
        );

        Sessao sessaoConflito = criarSessao(
                outraPeca,
                LocalDate.of(2030, 10, 2),
                LocalTime.of(19, 0),
                LocalTime.of(21, 0)
        );

        Contrato contrato = criarContrato(
                artista,
                pecaContrato,
                LocalDate.of(2030, 10, 1),
                LocalDate.of(2030, 10, 1),
                StatusContrato.ATIVO
        );

        ContratoService service =
                new ContratoService();

        service.salvar(contrato);

        IllegalArgumentException excecao =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> service.prorrogarContrato(
                                contrato.getId(),
                                LocalDate.of(2030, 10, 2)
                        )
                );

        assertEquals(
                "Não é possível prorrogar o contrato: " +
                        "existe outra peça no período solicitado.",
                excecao.getMessage()
        );

        Contrato contratoAtualizado =
                service.buscarPorId(
                        contrato.getId()
                );

        assertNotNull(contratoAtualizado);

        assertEquals(
                LocalDate.of(2030, 10, 1),
                contratoAtualizado.getDataFim()
        );

        contratosCriados.add(contrato);
    }

    private Artista criarArtista(
            String cpf,
            String nome) {

        Artista artista = new Artista();

        artista.setCpf(cpf);
        artista.setNomeCompleto(nome);
        artista.setTelefone("83999999999");
        artista.setEmail(
                nome.toLowerCase().replace(" ", "")
                        + "@teste.com"
        );
        artista.setGenero(TipoGenero.OUTRO);
        artista.setDataNascimento(
                LocalDate.of(1990, 1, 1)
        );

        artistaDao.salvar(artista);

        artistasCriados.add(artista);

        return artista;
    }

    private Cliente criarCliente(
            String cpf,
            String nome) {

        Cliente cliente = new Cliente();

        cliente.setCpf(cpf);
        cliente.setNomeCompleto(nome);
        cliente.setTelefone("83988888888");
        cliente.setEmail(
                nome.toLowerCase().replace(" ", "")
                        + "@teste.com"
        );
        cliente.setGenero(TipoGenero.OUTRO);
        cliente.setDataNascimento(
                LocalDate.of(2000, 1, 1)
        );

        clienteDao.salvar(cliente);

        clientesCriados.add(cliente);

        return cliente;
    }

    private Peca criarPeca(
            String titulo,
            String descricao) {

        Peca peca = new Peca();

        peca.setTitulo(titulo);
        peca.setDescricao(descricao);

        pecaDao.salvar(peca);

        pecasCriadas.add(peca);

        return peca;
    }

    private RegraPreco criarRegra(
            BigDecimal valorPorHora) {

        RegraPreco regra = new RegraPreco();

        regra.setValorPorHora(valorPorHora);

        regraPrecoDao.salvar(regra);

        regrasCriadas.add(regra);

        return regra;
    }

    private Sessao criarSessao(
            Peca peca,
            LocalDate data,
            LocalTime horarioInicio,
            LocalTime horarioFim) {

        Sessao sessao = new Sessao();

        sessao.setPeca(peca);
        sessao.setData(data);
        sessao.setHorarioInicio(horarioInicio);
        sessao.setHorarioFim(horarioFim);

        sessaoDao.salvar(sessao);

        sessoesCriadas.add(sessao);

        return sessao;
    }

    private Contrato criarContrato(
            Artista artista,
            Peca peca,
            LocalDate dataInicio,
            LocalDate dataFim,
            StatusContrato status) {

        Contrato contrato = new Contrato();

        contrato.setArtista(artista);
        contrato.setPeca(peca);
        contrato.setDataInicio(dataInicio);
        contrato.setDataFim(dataFim);
        contrato.setStatus(status);

        return contrato;
    }
}
