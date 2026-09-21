package com.teatro.dao;

import com.teatro.model.Administrador;
import com.teatro.util.JpaUtil;
import jakarta.persistence.EntityManager;

import java.util.List;

public class AdministradorDao implements Dao<Administrador, Long> {
    @Override
    public void salvar(Administrador administrador) {
        EntityManager em = JpaUtil.getEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(administrador);
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
    public void atualizar(Administrador administrador) {
        EntityManager em = JpaUtil.getEntityManager();

        try {
            em.getTransaction().begin();
            em.merge(administrador);
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
    public void remover(Administrador administrador) {
        EntityManager em = JpaUtil.getEntityManager();

        try {
            em.getTransaction().begin();

            Administrador encontrada = em.find(Administrador.class, administrador.getId());

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
    public Administrador buscarPorId(Long id) {
        try (EntityManager em = JpaUtil.getEntityManager()) {
            return em.find(Administrador.class, id);
        }
    }

    @Override
    public List<Administrador> listarTodos() {
        try (EntityManager em = JpaUtil.getEntityManager()) {
            return em.createQuery("SELECT a FROM Administrador a", Administrador.class).getResultList();
        }
    }

    public Administrador buscarPorCpf(String cpf) {
        try (EntityManager em = JpaUtil.getEntityManager()) {
            List<Administrador> resultados = em.createQuery(
                            "SELECT a FROM Administrador a WHERE a.cpf = :cpf",
                            Administrador.class)
                    .setParameter("cpf", cpf)
                    .getResultList();

            return resultados.isEmpty() ? null : resultados.get(0);
        }
    }
}