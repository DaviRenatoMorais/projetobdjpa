package com.teatro.view;

import com.teatro.model.Administrador;
import com.teatro.service.AdministradorService;

import javax.swing.*;
import java.awt.*;

public class LoginView extends JFrame {

    private final JTextField campoCpf;
    private final JPasswordField campoSenha;
    private final JButton botaoEntrar;
    private final JButton botaoRecuperarSenha;

    private final AdministradorService administradorService;

    public LoginView() {

        administradorService =
                new AdministradorService();

        setTitle("Teatro Sapeense - Login");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        campoCpf = new JTextField();
        campoSenha = new JPasswordField();
        botaoEntrar = new JButton("Entrar");
        botaoRecuperarSenha = new JButton("Recuperar senha");

        JPanel painel =
                new JPanel(new GridBagLayout());

        GridBagConstraints gbc =
                new GridBagConstraints();

        gbc.insets =
                new Insets(5, 5, 5, 5);

        gbc.fill =
                GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;

        painel.add(
                new JLabel("CPF:"),
                gbc
        );

        gbc.gridx = 1;

        painel.add(
                campoCpf,
                gbc
        );

        gbc.gridx = 0;
        gbc.gridy = 1;

        painel.add(
                new JLabel("Senha:"),
                gbc
        );

        gbc.gridx = 1;

        painel.add(
                campoSenha,
                gbc
        );

        gbc.gridx = 1;
        gbc.gridy = 2;

        painel.add(
                botaoEntrar,
                gbc
        );

        gbc.gridx = 1;
        gbc.gridy = 3;

        painel.add(
                botaoRecuperarSenha,
                gbc
        );

        add(painel);

        botaoEntrar.addActionListener(
                e -> realizarLogin()
        );

        botaoRecuperarSenha.addActionListener(
                e -> recuperarSenha()
        );
    }

    private void realizarLogin() {

        String cpf =
                campoCpf.getText().trim();

        String senha =
                new String(campoSenha.getPassword());

        if (cpf.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Informe o CPF."
            );

            campoCpf.requestFocus();
            return;
        }

        if (senha.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Informe a senha."
            );

            campoSenha.requestFocus();
            return;
        }

        Administrador administrador =
                administradorService.autenticar(
                        cpf,
                        senha
                );

        if (administrador == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "CPF ou senha inválidos.",
                    "Erro de autenticação",
                    JOptionPane.ERROR_MESSAGE
            );

            campoSenha.setText("");
            campoSenha.requestFocus();

            return;
        }

        JOptionPane.showMessageDialog(
                this,
                "Login realizado com sucesso!"
        );

        abrirMenuPrincipal(administrador);
    }

    private void abrirMenuPrincipal(
            Administrador administrador) {

        MenuPrincipalView menu =
                new MenuPrincipalView(administrador);

        menu.setVisible(true);

        dispose();
    }

    private void recuperarSenha() {

        JTextField campoCpf =
                new JTextField();

        JTextField campoEmail =
                new JTextField();

        JTextField campoNascimento =
                new JTextField();

        JPasswordField campoNovaSenha =
                new JPasswordField();

        JPanel painel =
                new JPanel(
                        new GridLayout(4, 2, 5, 5)
                );

        painel.add(new JLabel("CPF:"));
        painel.add(campoCpf);

        painel.add(new JLabel("E-mail:"));
        painel.add(campoEmail);

        painel.add(new JLabel("Nascimento:"));
        painel.add(campoNascimento);

        painel.add(new JLabel("Nova senha:"));
        painel.add(campoNovaSenha);

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
                    campoCpf.getText().trim(),
                    campoEmail.getText().trim(),
                    campoNascimento.getText().trim(),
                    new String(
                            campoNovaSenha.getPassword()
                    )
            );

            JOptionPane.showMessageDialog(
                    this,
                    "Senha recuperada com sucesso."
            );

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}