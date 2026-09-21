package com.teatro.dao;

import com.teatro.model.RegraPreco;
import com.teatro.util.JpaUtil;
import jakarta.persistence.EntityManager;

import java.util.List;

public class RegraPrecoDao implements Dao<RegraPreco, Long>{
    @Override
    public void salvar(RegraPreco regraPreco) {
        EntityManager em = JpaUtil.getEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(regraPreco);
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
    public void atualizar(RegraPreco regraPreco) {
        EntityManager em = JpaUtil.getEntityManager();

        try {
            em.getTransaction().begin();
            em.merge(regraPreco);
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
    public void remover(RegraPreco regraPreco) {
        EntityManager em = JpaUtil.getEntityManager();

        try {
            em.getTransaction().begin();
            em.remove(em.merge(regraPreco));
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
    public RegraPreco buscarPorId(Long id) {
        try(EntityManager em = JpaUtil.getEntityManager()) {
            return em.find(RegraPreco.class, id);
        }
    }

    @Override
    public List<RegraPreco> listarTodos() {
        try(EntityManager em = JpaUtil.getEntityManager()) {
            return em.createQuery(
                    "SELECT r FROM RegraPreco r",
                    RegraPreco.class)
            .getResultList();
        }
    }
}
