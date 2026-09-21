package com.teatro;

import com.teatro.service.AdministradorService;
import com.teatro.view.CadastroAdministradorView;
import com.teatro.view.LoginView;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            AdministradorService administradorService =
                    new AdministradorService();

            if (administradorService.buscarAdministrador() == null) {

                CadastroAdministradorView cadastroView =
                        new CadastroAdministradorView();

                cadastroView.setVisible(true);

            } else {

                LoginView loginView =
                        new LoginView();

                loginView.setVisible(true);
            }
        });
    }
}