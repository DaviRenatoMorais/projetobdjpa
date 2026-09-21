package com.teatro;

import com.teatro.model.Administrador;
import com.teatro.service.AdministradorService;

public class ResetAdministrador {

    public static void main(String[] args) {

        AdministradorService service =
                new AdministradorService();

        Administrador administrador =
                service.buscarAdministrador();

        if (administrador == null) {
            System.out.println(
                    "Nenhum administrador cadastrado."
            );
            return;
        }

        service.remover(administrador);

        System.out.println(
                "Administrador removido com sucesso."
        );
    }
}