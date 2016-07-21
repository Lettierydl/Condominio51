/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cd.sis.controller;

import com.cd.exception.EntidadeNaoExistenteException;
import com.cd.sis.bean.Condominio;
import java.util.List;
import javax.persistence.EntityNotFoundException;

/**
 *
 * @author Lettiery
 */
public class CondominioController extends ControllerEntity<Condominio>{

    
    
    
    
    @Override
    public void create(Condominio entity) throws Exception {
        try {
            beginTransaction();
            em.persist(entity);
            commitTransaction();
        } finally {
            closeEntityManager();
        }
    }

    @Override
    public void edit(Condominio entity) throws EntidadeNaoExistenteException {
        try {
            beginTransaction();
            em.merge(entity);
            commitTransaction();
        } finally {
            closeEntityManager();
        }
    }

    @Override
    public void destroy(Condominio entity) throws EntidadeNaoExistenteException {
        try {
            beginTransaction();
            try {
                getReference(entity);
            } catch (EntityNotFoundException enfe) {
                throw new EntidadeNaoExistenteException(
                        "O Condominio com código " + entity.getId()
                        + " não existe.");
            }
            em.remove(getReference(entity));
            commitTransaction();
        } finally {
            closeEntityManager();
        }
    }

    
    @Override
    public Condominio getReference(Condominio entity) {
        em = getEntityManager();
        return em.getReference(Condominio.class, entity.getId());
    }

    @Override
    public void removeAll() {
        try {
            beginTransaction();
            List <Condominio>resultList = em.createNativeQuery("SELECT * FROM condominio;", Condominio.class).getResultList();
            for(Condominio m : resultList){
                em.remove(m);
            }
            commitTransaction();
        } finally {
            closeEntityManager();
        }
    }
    
}
