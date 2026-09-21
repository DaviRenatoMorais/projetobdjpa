package com.teatro.dao;

import com.teatro.model.Cliente;
import com.teatro.util.JpaUtil;
import jakarta.persistence.EntityManager;

import java.util.List;

public class ClienteDao implements Dao<Cliente, Long> {
    @Override
    public void salvar(Cliente cliente) {
        EntityManager em = JpaUtil.getEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(cliente);
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
    public void atualizar(Cliente cliente) {
        EntityManager em = JpaUtil.getEntityManager();

        try {
            em.getTransaction().begin();
            em.merge(cliente);
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
    public void remover(Cliente cliente) {
        EntityManager em = JpaUtil.getEntityManager();

        try {
            em.getTransaction().begin();

            Cliente encontrada = em.find(Cliente.class, cliente.getId());

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
    public Cliente buscarPorId(Long id) {
        try (EntityManager em = JpaUtil.getEntityManager()) {
            return em.find(Cliente.class, id);
        }
    }

    @Override
    public List<Cliente> listarTodos() {
        try (EntityManager em = JpaUtil.getEntityManager()) {
            return em.createQuery("SELECT c FROM Cliente c", Cliente.class).getResultList();
        }
    }

    public Cliente buscarPorCpf(String cpf) {
        try (EntityManager em = JpaUtil.getEntityManager()) {
            List<Cliente> resultados = em.createQuery(
                            "SELECT c FROM Cliente c WHERE c.cpf = :cpf",
                            Cliente.class)
                    .setParameter("cpf", cpf)
                    .getResultList();

            return resultados.isEmpty() ? null : resultados.get(0);
        }
    }
}
