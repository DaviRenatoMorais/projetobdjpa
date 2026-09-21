package com.teatro.dao;

import com.teatro.model.Contrato;
import com.teatro.model.Sessao;
import com.teatro.model.StatusContrato;
import com.teatro.util.JpaUtil;
import jakarta.persistence.EntityManager;

import java.util.List;

public class ContratoDao implements Dao<Contrato, Long> {
    @Override
    public void salvar(Contrato contrato) {
        EntityManager em = JpaUtil.getEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(contrato);
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
    public void atualizar(Contrato contrato) {
        EntityManager em = JpaUtil.getEntityManager();

        try {
            em.getTransaction().begin();
            em.merge(contrato);
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
    public void remover(Contrato contrato) {
        EntityManager em = JpaUtil.getEntityManager();

        try {
            em.getTransaction().begin();

            Contrato encontrada = em.find(Contrato.class, contrato.getId());

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
    public Contrato buscarPorId(Long id) {
        try(EntityManager em = JpaUtil.getEntityManager()) {
            return em.find(Contrato.class, id);
        }
    }

    @Override
    public List<Contrato> listarTodos() {
        try(EntityManager em = JpaUtil.getEntityManager()) {
            return em.createQuery(
                    "SELECT c FROM Contrato c",
                    Contrato.class)
            .getResultList();
        }
    }

    public Contrato buscarContratoDaSessao(Sessao sessao){
        try(EntityManager em = JpaUtil.getEntityManager()) {
            return em.createQuery(
                    "SELECT c FROM Contrato c " +
                            "WHERE c.peca = :peca " +
                            "AND :dataSessao BETWEEN c.dataInicio AND c.dataFim ",
                    Contrato.class
            )
            .setParameter("peca", sessao.getPeca())
            .setParameter("dataSessao", sessao.getData())
            .getResultStream() // retorna os resultados na forma de uma Stream<Contrato> do Java.
            .findFirst() // Pega apenas o primeiro elemento encontrado na Streame o empacota dentro de um objeto Optional<Contrato>.
            .orElse(null); // Tira o objeto de dentro do Optional.
            // Se a busca encontrou algum contrato, retorna o Contrato
            // se a Stream estiver vazia (nenhum contrato atende aos critérios), retorna null.
        }
    }
}
