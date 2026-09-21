package com.teatro;

import com.teatro.model.Administrador;
import com.teatro.service.AdministradorService;

// Classe para deletar um administrador cadastrado, uma vez que o sistema foi planejado para ser monousuário
// Executando esta classe isolada, será permitido inicializar o sistema a partir do (novo) cadastro de um administrador
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
