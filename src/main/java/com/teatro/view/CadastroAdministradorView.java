package com.teatro.view;

import com.teatro.model.Administrador;
import com.teatro.model.TipoGenero;
import com.teatro.service.AdministradorService;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class CadastroAdministradorView extends JFrame {

    private final AdministradorService administradorService;

    private JTextField campoNome;
    private JTextField campoCpf;
    private JTextField campoTelefone;
    private JTextField campoEmail;
    private JComboBox<TipoGenero> comboGenero;
    private JTextField campoDataNascimento;
    private JPasswordField campoSenha;

    public CadastroAdministradorView() {

        administradorService =
                new AdministradorService();

        setTitle("Teatro Sapeense - Cadastro do Administrador");
        setSize(600, 500);
        setLocationRelativeTo(null);
        setResizable(false);

        setDefaultCloseOperation(
                JFrame.EXIT_ON_CLOSE
        );

        criarInterface();
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
                        "CADASTRO DO ADMINISTRADOR",
                        SwingConstants.CENTER
                );

        titulo.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        22
                )
        );

        painelPrincipal.add(
                titulo,
                BorderLayout.NORTH
        );

        JPanel formulario =
                new JPanel(new GridBagLayout());

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

        campoSenha =
                new JPasswordField();

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

        adicionarCampo(
                formulario,
                gbc,
                6,
                "Senha:",
                campoSenha
        );

        painelPrincipal.add(
                formulario,
                BorderLayout.CENTER
        );

        JButton botaoCadastrar =
                new JButton("Cadastrar");

        JPanel painelBotao =
                new JPanel();

        painelBotao.add(botaoCadastrar);

        painelPrincipal.add(
                painelBotao,
                BorderLayout.SOUTH
        );

        add(painelPrincipal);

        botaoCadastrar.addActionListener(
                e -> cadastrar()
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

    private void cadastrar() {

        try {

            Administrador administrador =
                    new Administrador();

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

            administrador.setSenha(
                    new String(
                            campoSenha.getPassword()
                    )
            );

            administradorService.salvar(
                    administrador
            );

            JOptionPane.showMessageDialog(
                    this,
                    "Administrador cadastrado com sucesso."
            );

            abrirLogin();

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void abrirLogin() {

        dispose();

        LoginView loginView =
                new LoginView();

        loginView.setVisible(true);
    }
}
