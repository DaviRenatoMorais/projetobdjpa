package com.teatro.service;

import com.teatro.dao.ClienteDao;
import com.teatro.model.Cliente;

import java.util.List;

public class ClienteService {

    private final ClienteDao clienteDao;

    public ClienteService() {
        this.clienteDao = new ClienteDao();
    }

    public void salvar(Cliente cliente) {
        clienteDao.salvar(cliente);
    }

    public void atualizar(Cliente cliente) {
        clienteDao.atualizar(cliente);
    }

    public void remover(Cliente cliente) {
        clienteDao.remover(cliente);
    }

    public Cliente buscarPorId(Long id) {
        return clienteDao.buscarPorId(id);
    }

    public List<Cliente> listarTodos() {
        return clienteDao.listarTodos();
    }

    public Cliente buscarPorCpf(String cpf) {
        return clienteDao.buscarPorCpf(cpf);
    }
}