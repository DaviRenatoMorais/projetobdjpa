package com.teatro.view;

import com.teatro.model.Administrador;

import javax.swing.*;
import java.awt.*;

public class MenuPrincipalView extends JFrame {

    private final Administrador administrador;

    public MenuPrincipalView(Administrador administrador) {

        this.administrador = administrador;

        setTitle("Teatro Sapeense - Menu Principal");
        setSize(800, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        criarInterface();
    }

    private void criarInterface() {

        JPanel painelPrincipal =
                new JPanel(new BorderLayout(10, 10));

        painelPrincipal.setBorder(
                BorderFactory.createEmptyBorder(
                        20, 20, 20, 20
                )
        );

        JLabel titulo =
                new JLabel(
                        "TEATRO SAPEENSE",
                        SwingConstants.CENTER
                );

        titulo.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        28
                )
        );

        JLabel subtitulo =
                new JLabel(
                        "Sistema de Gerenciamento",
                        SwingConstants.CENTER
                );

        subtitulo.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        16
                )
        );

        JPanel painelCabecalho =
                new JPanel(new GridLayout(2, 1));

        painelCabecalho.add(titulo);
        painelCabecalho.add(subtitulo);

        painelPrincipal.add(
                painelCabecalho,
                BorderLayout.NORTH
        );

        JPanel painelBotoes =
                new JPanel(
                        new GridLayout(
                                3,
                                3,
                                15,
                                15
                        )
                );

        JButton botaoAdministrador =
                criarBotao("Administrador");

        JButton botaoArtistas =
                criarBotao("Artistas");

        JButton botaoClientes =
                criarBotao("Clientes");

        JButton botaoPecas =
                criarBotao("Peças");

        JButton botaoRegras =
                criarBotao("Regras de Preço");

        JButton botaoSessoes =
                criarBotao("Sessões");

        JButton botaoContratos =
                criarBotao("Contratos");

        JButton botaoIngressos =
                criarBotao("Ingressos");

        JButton botaoRelatorios =
                criarBotao("Relatórios");

        painelBotoes.add(botaoAdministrador);
        painelBotoes.add(botaoArtistas);
        painelBotoes.add(botaoClientes);
        painelBotoes.add(botaoPecas);
        painelBotoes.add(botaoRegras);
        painelBotoes.add(botaoSessoes);
        painelBotoes.add(botaoContratos);
        painelBotoes.add(botaoIngressos);
        painelBotoes.add(botaoRelatorios);

        painelPrincipal.add(
                painelBotoes,
                BorderLayout.CENTER
        );

        JPanel painelRodape =
                new JPanel(new BorderLayout());

        JLabel usuario =
                new JLabel(
                        "Administrador: "
                                + administrador.getNomeCompleto()
                );

        JButton botaoSair =
                new JButton("Sair");

        painelRodape.add(
                usuario,
                BorderLayout.WEST
        );

        painelRodape.add(
                botaoSair,
                BorderLayout.EAST
        );

        painelPrincipal.add(
                painelRodape,
                BorderLayout.SOUTH
        );

        add(painelPrincipal);

        botaoSair.addActionListener(e -> sair());

        botaoAdministrador.addActionListener(
                e -> abrirAdministrador()
        );

        botaoArtistas.addActionListener(
                e -> abrirArtistas()
        );

        botaoClientes.addActionListener(
                e -> abrirClientes()
        );

        botaoPecas.addActionListener(
                e -> abrirPecas()
        );

        botaoRegras.addActionListener(
                e -> abrirRegrasPreco()
        );

        botaoSessoes.addActionListener(
                e -> abrirSessoes()
        );

        botaoContratos.addActionListener(
                e -> abrirContratos()
        );

        botaoIngressos.addActionListener(
                e -> abrirIngressos()
        );

        botaoRelatorios.addActionListener(e -> abrirRelatorios());
    }

    private JButton criarBotao(String texto) {

        JButton botao =
                new JButton(texto);

        botao.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        15
                )
        );

        return botao;
    }

    private void abrirArtistas() {
        setVisible(false);
        ArtistaView artistaView =
                new ArtistaView(this);
        artistaView.setVisible(true);
    }

    private void abrirClientes() {
        setVisible(false);
        ClienteView clienteView =
                new ClienteView(this);
        clienteView.setVisible(true);
    }

    private void abrirPecas() {

        setVisible(false);

        PecaView pecaView =
                new PecaView(this);

        pecaView.setVisible(true);
    }

    private void abrirRegrasPreco() {

        setVisible(false);

        RegraPrecoView regraPrecoView =
                new RegraPrecoView(this);

        regraPrecoView.setVisible(true);
    }

    private void abrirSessoes() {

        setVisible(false);

        SessaoView sessaoView =
                new SessaoView(this);

        sessaoView.setVisible(true);
    }

    private void abrirContratos() {

        setVisible(false);

        ContratoView contratoView =
                new ContratoView(this);

        contratoView.setVisible(true);
    }

    private void abrirIngressos() {

        setVisible(false);

        IngressoView ingressoView =
                new IngressoView(this);

        ingressoView.setVisible(true);
    }

    private void abrirRelatorios() {

        setVisible(false);

        RelatorioView relatorioView =
                new RelatorioView(this);

        relatorioView.setVisible(true);
    }

    private void abrirAdministrador() {

        setVisible(false);

        AdministradorView administradorView =
                new AdministradorView(this);

        administradorView.setVisible(true);
    }

    private void mostrarMensagem(String modulo) {

        JOptionPane.showMessageDialog(
                this,
                modulo + " será implementado."
        );
    }

    private void sair() {

        int resposta =
                JOptionPane.showConfirmDialog(
                        this,
                        "Deseja realmente sair?",
                        "Sair",
                        JOptionPane.YES_NO_OPTION
                );

        if (resposta == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }
}