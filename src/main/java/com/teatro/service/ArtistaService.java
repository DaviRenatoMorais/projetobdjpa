package com.teatro.service;

import com.teatro.dao.ArtistaDao;
import com.teatro.model.Artista;

import java.util.List;

public class ArtistaService {

    private final ArtistaDao artistaDao;

    public ArtistaService() {
        this.artistaDao = new ArtistaDao();
    }

    public void salvar(Artista artista) {
        artistaDao.salvar(artista);
    }

    public void atualizar(Artista artista) {
        artistaDao.atualizar(artista);
    }

    public void remover(Artista artista) {
        artistaDao.remover(artista);
    }

    public Artista buscarPorId(Long id) {
        return artistaDao.buscarPorId(id);
    }

    public List<Artista> listarTodos() {
        return artistaDao.listarTodos();
    }

    public Artista buscarPorCpf(String cpf) {
        return artistaDao.buscarPorCpf(cpf);
    }
}