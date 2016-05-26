/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cd.sis.controller;

import com.cd.exception.EntidadeNaoExistenteException;
import com.cd.exception.LoginIncorretoException;
import com.cd.sis.bean.Morador;
import com.cd.sis.bean.Usuario;
import static com.cd.sis.controller.ControllerEntity.em;
import java.util.List;
import javax.persistence.EntityNotFoundException;
import javax.persistence.TypedQuery;

/**
 *
 * @author Lettiery
 */
public class UsuarioController extends ControllerEntity<Usuario>{

    @Override
    public void create(Usuario entity) throws Exception {
        try {
            beginTransaction();
            em.persist(entity);
            commitTransaction();
        } finally {
            closeEntityManager();
        }
    }

    @Override
    public void edit(Usuario entity) throws EntidadeNaoExistenteException {
        try {
            beginTransaction();
            em.merge(entity);
            commitTransaction();
        } finally {
            closeEntityManager();
        }
    }

    @Override
    public void destroy(Usuario entity) throws EntidadeNaoExistenteException {
        try {
            beginTransaction();
            try {
                getReference(entity);
            } catch (EntityNotFoundException enfe) {
                throw new EntidadeNaoExistenteException(
                        "O Usuário com código " + entity.getId()
                        + " não existe.");
            }
            em.remove(getReference(entity));
            commitTransaction();
        } finally {
            closeEntityManager();
        }
    }

    @Override
    public Usuario getReference(Usuario entity) {
        em = getEntityManager();
        return em.getReference(Usuario.class, entity.getId());
    }

    @Override
    public void removeAll() {
    try {
            beginTransaction();
            List <Usuario>resultList = em.createNativeQuery("SELECT * FROM usuario;", Morador.class).getResultList();
            for(Usuario u : resultList){
                em.remove(u);
            }
            commitTransaction();
        } finally {
            closeEntityManager();
        }
    }
    
    public Usuario login(String login, String senha) throws LoginIncorretoException{
        em = getEntityManager();
        String senha_cript = senha;
        TypedQuery<Usuario> q = em.createQuery("select u from Usuario as u where u.login = :login and u.senha = :senha", Usuario.class);
        q.setParameter("login", login);
        q.setParameter("senha", senha_cript);
        
        try{
            return q.getSingleResult();
        }catch(Exception e){
            throw new LoginIncorretoException();
        }
    }
    
    
}
