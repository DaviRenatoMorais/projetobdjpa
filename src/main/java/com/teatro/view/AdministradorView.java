package com.teatro.view;

import com.teatro.model.Administrador;
import com.teatro.model.TipoGenero;
import com.teatro.service.AdministradorService;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class AdministradorView extends JFrame {

    private final MenuPrincipalView menuPrincipalView;
    private final AdministradorService administradorService;

    private Administrador administrador;

    private JTextField campoNome;
    private JTextField campoCpf;
    private JTextField campoTelefone;
    private JTextField campoEmail;
    private JComboBox<TipoGenero> comboGenero;
    private JTextField campoDataNascimento;

    private JButton botaoSalvar;
    private JButton botaoAlterarSenha;
    private JButton botaoRecuperarSenha;

    public AdministradorView(
            MenuPrincipalView menuPrincipalView) {

        this.menuPrincipalView = menuPrincipalView;
        this.administradorService =
                new AdministradorService();

        setTitle("Teatro Sapeense - Administrador");
        setSize(600, 500);
        setLocationRelativeTo(null);
        setResizable(false);

        setDefaultCloseOperation(
                JFrame.DO_NOTHING_ON_CLOSE
        );

        addWindowListener(
                new java.awt.event.WindowAdapter() {

                    @Override
                    public void windowClosing(
                            java.awt.event.WindowEvent e) {

                        voltarAoMenu();
                    }
                }
        );

        criarInterface();
        carregarAdministrador();
    }

    private void criarInterface() {

        JPanel painelPrincipal =
                new JPanel(new BorderLayout(10, 10));

        painelPrincipal.setBorder(
                BorderFactory.createEmptyBorder(
                        15, 15, 15, 15
                )
        );

        JLabel titulo =
                new JLabel(
                        "ADMINISTRADOR",
                        SwingConstants.CENTER
                );

        titulo.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        24
                )
        );

        painelPrincipal.add(
                titulo,
                BorderLayout.NORTH
        );

        JPanel formulario =
                new JPanel(
                        new GridBagLayout()
                );

        GridBagConstraints gbc =
                new GridBagConstraints();

        gbc.insets =
                new Insets(5, 5, 5, 5);

        gbc.fill =
                GridBagConstraints.HORIZONTAL;

        campoNome = new JTextField();
        campoCpf = new JTextField();
        campoTelefone = new JTextField();
        campoEmail = new JTextField();

        comboGenero =
                new JComboBox<>(
                        TipoGenero.values()
                );

        campoDataNascimento =
                new JTextField();

        adicionarCampo(
                formulario,
                gbc,
                0,
                "Nome completo:",
                campoNome
        );

        adicionarCampo(
                formulario,
                gbc,
                1,
                "CPF:",
                campoCpf
        );

        adicionarCampo(
                formulario,
                gbc,
                2,
                "Telefone:",
                campoTelefone
        );

        adicionarCampo(
                formulario,
                gbc,
                3,
                "E-mail:",
                campoEmail
        );

        adicionarCampo(
                formulario,
                gbc,
                4,
                "Gênero:",
                comboGenero
        );

        adicionarCampo(
                formulario,
                gbc,
                5,
                "Nascimento (AAAA-MM-DD):",
                campoDataNascimento
        );

        painelPrincipal.add(
                formulario,
                BorderLayout.CENTER
        );

        JPanel painelBotoes =
                new JPanel(
                        new GridLayout(
                                2,
                                2,
                                8,
                                8
                        )
                );

        botaoSalvar =
                new JButton("Salvar alterações");

        botaoAlterarSenha =
                new JButton("Alterar senha");

        botaoRecuperarSenha =
                new JButton("Recuperar senha");

        JButton botaoVoltar =
                new JButton("Voltar ao Menu");

        painelBotoes.add(botaoSalvar);
        painelBotoes.add(botaoAlterarSenha);
        painelBotoes.add(botaoRecuperarSenha);
        painelBotoes.add(botaoVoltar);

        painelPrincipal.add(
                painelBotoes,
                BorderLayout.SOUTH
        );

        add(painelPrincipal);

        botaoSalvar.addActionListener(
                e -> salvarAlteracoes()
        );

        botaoAlterarSenha.addActionListener(
                e -> alterarSenha()
        );

        botaoRecuperarSenha.addActionListener(
                e -> recuperarSenha()
        );

        botaoVoltar.addActionListener(
                e -> voltarAoMenu()
        );
    }

    private void adicionarCampo(
            JPanel painel,
            GridBagConstraints gbc,
            int linha,
            String texto,
            JComponent componente) {

        gbc.gridx = 0;
        gbc.gridy = linha;
        gbc.weightx = 0;

        painel.add(
                new JLabel(texto),
                gbc
        );

        gbc.gridx = 1;
        gbc.weightx = 1;

        painel.add(
                componente,
                gbc
        );
    }

    private void carregarAdministrador() {

        administrador =
                administradorService.buscarAdministrador();

        if (administrador == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Nenhum administrador cadastrado."
            );

            botaoAlterarSenha.setEnabled(false);
            botaoRecuperarSenha.setEnabled(false);

            return;
        }

        campoNome.setText(
                administrador.getNomeCompleto()
        );

        campoCpf.setText(
                administrador.getCpf()
        );

        campoTelefone.setText(
                administrador.getTelefone()
        );

        campoEmail.setText(
                administrador.getEmail()
        );

        comboGenero.setSelectedItem(
                administrador.getGenero()
        );

        campoDataNascimento.setText(
                administrador.getDataNascimento()
                        .toString()
        );
    }

    private void salvarAlteracoes() {

        try {

            if (administrador == null) {

                JOptionPane.showMessageDialog(
                        this,
                        "Nenhum administrador cadastrado."
                );

                return;
            }

            administrador.setNomeCompleto(
                    campoNome.getText().trim()
            );

            administrador.setCpf(
                    campoCpf.getText().trim()
            );

            administrador.setTelefone(
                    campoTelefone.getText().trim()
            );

            administrador.setEmail(
                    campoEmail.getText().trim()
            );

            administrador.setGenero(
                    (TipoGenero)
                            comboGenero.getSelectedItem()
            );

            administrador.setDataNascimento(
                    LocalDate.parse(
                            campoDataNascimento
                                    .getText()
                                    .trim()
                    )
            );

            administradorService.atualizar(
                    administrador
            );

            JOptionPane.showMessageDialog(
                    this,
                    "Dados do administrador atualizados."
            );

        } catch (Exception ex) {

            mostrarErro(ex);
        }
    }

    private void alterarSenha() {

        if (administrador == null) {
            return;
        }

        JPasswordField campoSenhaAtual =
                new JPasswordField();

        JPasswordField campoNovaSenha =
                new JPasswordField();

        JPanel painel =
                new JPanel(
                        new GridLayout(2, 2, 5, 5)
                );

        painel.add(
                new JLabel("Senha atual:")
        );

        painel.add(
                campoSenhaAtual
        );

        painel.add(
                new JLabel("Nova senha:")
        );

        painel.add(
                campoNovaSenha
        );

        int opcao =
                JOptionPane.showConfirmDialog(
                        this,
                        painel,
                        "Alterar senha",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE
                );

        if (opcao != JOptionPane.OK_OPTION) {
            return;
        }

        try {

            administradorService.alterarSenha(
                    administrador.getId(),
                    new String(
                            campoSenhaAtual.getPassword()
                    ),
                    new String(
                            campoNovaSenha.getPassword()
                    )
            );

            JOptionPane.showMessageDialog(
                    this,
                    "Senha alterada com sucesso."
            );

        } catch (Exception ex) {

            mostrarErro(ex);
        }
    }

    private void recuperarSenha() {

        JTextField campoCpfRecuperacao =
                new JTextField();

        JTextField campoEmailRecuperacao =
                new JTextField();

        JTextField campoNascimentoRecuperacao =
                new JTextField();

        JPasswordField campoNovaSenha =
                new JPasswordField();

        JPanel painel =
                new JPanel(
                        new GridLayout(4, 2, 5, 5)
                );

        painel.add(
                new JLabel("CPF:")
        );

        painel.add(
                campoCpfRecuperacao
        );

        painel.add(
                new JLabel("E-mail:")
        );

        painel.add(
                campoEmailRecuperacao
        );

        painel.add(
                new JLabel("Nascimento:")
        );

        painel.add(
                campoNascimentoRecuperacao
        );

        painel.add(
                new JLabel("Nova senha:")
        );

        painel.add(
                campoNovaSenha
        );

        int opcao =
                JOptionPane.showConfirmDialog(
                        this,
                        painel,
                        "Recuperação de senha",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE
                );

        if (opcao != JOptionPane.OK_OPTION) {
            return;
        }

        try {

            administradorService.recuperarSenha(
                    campoCpfRecuperacao
                            .getText()
                            .trim(),

                    campoEmailRecuperacao
                            .getText()
                            .trim(),

                    campoNascimentoRecuperacao
                            .getText()
                            .trim(),

                    new String(
                            campoNovaSenha.getPassword()
                    )
            );

            JOptionPane.showMessageDialog(
                    this,
                    "Senha recuperada com sucesso."
            );

        } catch (Exception ex) {

            mostrarErro(ex);
        }
    }

    private void mostrarErro(Exception ex) {

        JOptionPane.showMessageDialog(
                this,
                ex.getMessage(),
                "Erro",
                JOptionPane.ERROR_MESSAGE
        );
    }

    private void voltarAoMenu() {

        dispose();

        menuPrincipalView.setVisible(true);
    }
}