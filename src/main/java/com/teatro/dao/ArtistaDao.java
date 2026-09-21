package com.teatro.dao;

import com.teatro.model.Artista;
import com.teatro.model.Peca;
import com.teatro.util.JpaUtil;
import jakarta.persistence.EntityManager;

import java.util.List;

public class ArtistaDao implements Dao<Artista, Long> {
    @Override
    public void salvar(Artista artista) {
        EntityManager em = JpaUtil.getEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(artista);
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
    public void atualizar(Artista artista) {
        EntityManager em = JpaUtil.getEntityManager();

        try {
            em.getTransaction().begin();
            em.merge(artista);
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
    public void remover(Artista artista) {
        EntityManager em = JpaUtil.getEntityManager();

        try {
            em.getTransaction().begin();

            Artista encontrada = em.find(Artista.class, artista.getId());

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
    public Artista buscarPorId(Long id) {
        try (EntityManager em = JpaUtil.getEntityManager()) {
            return em.find(Artista.class, id);
        }
    }

    @Override
    public List<Artista> listarTodos() {
        try (EntityManager em = JpaUtil.getEntityManager()) {
            return em.createQuery("SELECT a FROM Artista a", Artista.class).getResultList();
        }
    }

    public Artista buscarPorCpf(String cpf) {
        try (EntityManager em = JpaUtil.getEntityManager()) {
            List<Artista> resultados = em.createQuery(
                            "SELECT a FROM Artista a WHERE a.cpf = :cpf",
                            Artista.class)
                    .setParameter("cpf", cpf)
                    .getResultList();

            // Se a lista não estiver vazia, pega o primeiro registro e o retorna
            return resultados.isEmpty() ? null : resultados.get(0);
        }
    }
}
