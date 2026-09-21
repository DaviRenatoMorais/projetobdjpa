package com.teatro.service;

import com.teatro.dao.PecaDao;
import com.teatro.model.Peca;

import java.util.List;

public class PecaService {

    private final PecaDao pecaDao;

    public PecaService() {
        this.pecaDao = new PecaDao();
    }

    public void salvar(Peca peca) {
        pecaDao.salvar(peca);
    }

    public void atualizar(Peca peca) {
        pecaDao.atualizar(peca);
    }

    public void remover(Peca peca) {
        pecaDao.remover(peca);
    }

    public Peca buscarPorId(Long id) {
        return pecaDao.buscarPorId(id);
    }

    public List<Peca> listarTodos() {
        return pecaDao.listarTodos();
    }
}