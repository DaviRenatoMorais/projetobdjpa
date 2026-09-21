package com.teatro.service;

import com.teatro.dao.AdministradorDao;
import com.teatro.model.Administrador;

import java.util.List;

public class AdministradorService {

    private final AdministradorDao administradorDao;

    public AdministradorService() {
        this.administradorDao = new AdministradorDao();
    }

    public void salvar(Administrador administrador) {

        validarAdministrador(administrador);

        if (!administradorDao.listarTodos().isEmpty()) {
            throw new IllegalArgumentException(
                    "O sistema permite apenas um administrador."
            );
        }

        administradorDao.salvar(administrador);
    }

    public void atualizar(Administrador administrador) {

        validarAdministrador(administrador);

        if (administrador.getId() == null) {
            throw new IllegalArgumentException(
                    "Administrador inválido."
            );
        }

        administradorDao.atualizar(administrador);
    }

    public void remover(Administrador administrador) {
        administradorDao.remover(administrador);
    }

    public Administrador buscarPorId(Long id) {
        return administradorDao.buscarPorId(id);
    }

    public List<Administrador> listarTodos() {
        return administradorDao.listarTodos();
    }

    public Administrador buscarAdministrador() {

        List<Administrador> administradores =
                administradorDao.listarTodos();

        if (administradores.isEmpty()) {
            return null;
        }

        return administradores.get(0);
    }

    public Administrador autenticar(
            String cpf,
            String senha) {

        Administrador administrador =
                administradorDao.buscarPorCpf(cpf);

        if (administrador == null) {
            return null;
        }

        if (administrador.getSenha() == null) {
            return null;
        }

        if (!administrador.getSenha().equals(senha)) {
            return null;
        }

        return administrador;
    }

    public void alterarSenha(
            Long administradorId,
            String senhaAtual,
            String novaSenha) {

        Administrador administrador =
                administradorDao.buscarPorId(administradorId);

        if (administrador == null) {
            throw new IllegalArgumentException(
                    "Administrador não encontrado."
            );
        }

        if (senhaAtual == null || senhaAtual.isBlank()) {
            throw new IllegalArgumentException(
                    "Informe a senha atual."
            );
        }

        if (!senhaAtual.equals(administrador.getSenha())) {
            throw new IllegalArgumentException(
                    "A senha atual está incorreta."
            );
        }

        validarNovaSenha(novaSenha);

        administrador.setSenha(novaSenha);

        administradorDao.atualizar(administrador);
    }

    public void recuperarSenha(
            String cpf,
            String email,
            String dataNascimento,
            String novaSenha) {

        Administrador administrador =
                administradorDao.buscarPorCpf(cpf);

        if (administrador == null) {
            throw new IllegalArgumentException(
                    "Administrador não encontrado."
            );
        }

        if (email == null
                || !email.trim().equalsIgnoreCase(
                administrador.getEmail())) {

            throw new IllegalArgumentException(
                    "CPF e e-mail não conferem."
            );
        }

        if (dataNascimento == null
                || !dataNascimento.equals(
                administrador.getDataNascimento().toString())) {

            throw new IllegalArgumentException(
                    "CPF e data de nascimento não conferem."
            );
        }

        validarNovaSenha(novaSenha);

        administrador.setSenha(novaSenha);

        administradorDao.atualizar(administrador);
    }

    private void validarAdministrador(
            Administrador administrador) {

        if (administrador == null) {
            throw new IllegalArgumentException(
                    "Administrador obrigatório."
            );
        }

        if (administrador.getCpf() == null
                || administrador.getCpf().isBlank()) {

            throw new IllegalArgumentException(
                    "CPF é obrigatório."
            );
        }

        if (administrador.getNomeCompleto() == null
                || administrador.getNomeCompleto().isBlank()) {

            throw new IllegalArgumentException(
                    "Nome é obrigatório."
            );
        }

        if (administrador.getEmail() == null
                || administrador.getEmail().isBlank()) {

            throw new IllegalArgumentException(
                    "E-mail é obrigatório."
            );
        }

        if (administrador.getSenha() == null
                || administrador.getSenha().isBlank()) {

            throw new IllegalArgumentException(
                    "Senha é obrigatória."
            );
        }

        validarNovaSenha(administrador.getSenha());
    }

    private void validarNovaSenha(String senha) {

        if (senha == null || senha.isBlank()) {
            throw new IllegalArgumentException(
                    "A senha é obrigatória."
            );
        }

        if (senha.length() < 4) {
            throw new IllegalArgumentException(
                    "A senha deve possuir pelo menos 4 caracteres."
            );
        }

        if (senha.length() > 20) {
            throw new IllegalArgumentException(
                    "A senha deve possuir no máximo 20 caracteres."
            );
        }
    }
}