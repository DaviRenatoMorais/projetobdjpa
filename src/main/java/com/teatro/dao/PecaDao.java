package com.teatro.dao;

import com.teatro.model.Peca;
import com.teatro.util.JpaUtil;
import jakarta.persistence.EntityManager;

import java.util.List;

public class PecaDao implements Dao<Peca, Long>{
    @Override
    public void salvar(Peca peca) {
        EntityManager em = JpaUtil.getEntityManager();

        // Inicia o bloco try para tentar executar a operação de banco com segurança
        try {
            em.getTransaction().begin();
            em.persist(peca);
            em.getTransaction().commit();
        } catch (Exception e) {
            // Se ocorrer qualquer erro, verifica se a transação chegou a ser aberta e continua ativa
            if (em.getTransaction().isActive()) {
                // Desfaz todas as alterações pendentes para não deixar dados parciais ou corrompidos no banco
                em.getTransaction().rollback();
            }
            // Relança a exceção original para avisar as camadas superiores que a operação falhou
            throw e;
        } finally {
            // Garantia de execução: fecha o EntityManager ocorrendo sucesso ou erro, liberando a conexão
            em.close();
        }
    }

    @Override
    public void atualizar(Peca peca) {
        EntityManager em = JpaUtil.getEntityManager();

        try {
            em.getTransaction().begin();
            em.merge(peca);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public void remover(Peca peca) {
        EntityManager em = JpaUtil.getEntityManager();

        try {
            em.getTransaction().begin();

            Peca encontrada = em.find(Peca.class, peca.getId());

            if (encontrada != null) {
                em.remove(encontrada);
            }

            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public Peca buscarPorId(Long id) {
        // EntityManager implementa AutoCloseable -> try-with-resources
        // O recurso é fechado automaticamente ao sair do bloco try
        try (EntityManager em = JpaUtil.getEntityManager()) {
            return em.find(Peca.class, id);
        }
    }

    @Override
    public List<Peca> listarTodos() {
        // O recurso é fechado automaticamente ao sair do bloco try
        try (EntityManager em = JpaUtil.getEntityManager()) {
            return em.createQuery("SELECT p FROM Peca p", Peca.class).getResultList();
        }
    }
}