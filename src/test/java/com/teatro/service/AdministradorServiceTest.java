package com.teatro.service;

import com.teatro.dao.AdministradorDao;
import com.teatro.model.Administrador;
import com.teatro.model.TipoGenero;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class AdministradorServiceTest {

    @Test
    void deveAutenticarAdministradorComCredenciaisCorretas() {

        Administrador administrador = new Administrador();

        administrador.setCpf("55555555555");
        administrador.setNomeCompleto("Administrador Teste");
        administrador.setTelefone("83999999999");
        administrador.setEmail("admin@teste.com");
        administrador.setGenero(TipoGenero.OUTRO);
        administrador.setDataNascimento(
                LocalDate.of(1990, 1, 1)
        );
        administrador.setSenha("123456");

        AdministradorDao administradorDao =
                new AdministradorDao();

        administradorDao.salvar(administrador);

        AdministradorService service =
                new AdministradorService();

        Administrador resultado =
                service.autenticar(
                        "55555555555",
                        "123456"
                );

        assertNotNull(resultado);

        assertEquals(
                administrador.getId(),
                resultado.getId()
        );

        administradorDao.remover(administrador);
    }

    @Test
    void deveRejeitarSenhaIncorreta() {

        Administrador administrador = new Administrador();

        administrador.setCpf("23456789012");
        administrador.setNomeCompleto("Administrador Senha");
        administrador.setTelefone("83999999998");
        administrador.setEmail("admin.senha@teste.com");
        administrador.setGenero(TipoGenero.OUTRO);
        administrador.setDataNascimento(
                LocalDate.of(1990, 1, 1)
        );
        administrador.setSenha("123456");

        AdministradorDao administradorDao =
                new AdministradorDao();

        administradorDao.salvar(administrador);

        AdministradorService service =
                new AdministradorService();

        Administrador resultado =
                service.autenticar(
                        "23456789012",
                        "senhaErrada"
                );

        assertNull(resultado);

        administradorDao.remover(administrador);
    }

    @Test
    void deveRejeitarCpfDeAdministradorInexistente() {

        AdministradorService service =
                new AdministradorService();

        Administrador resultado =
                service.autenticar(
                        "66666666666",
                        "123456"
                );

        assertNull(resultado);
    }
}