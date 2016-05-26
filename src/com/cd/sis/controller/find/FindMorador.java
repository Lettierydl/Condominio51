/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cd.sis.controller.find;

import com.cd.sis.controller.FindEntity;
import com.cd.sis.bean.*;
import java.util.List;
import javax.persistence.Query;

/**
 *
 * @author Lettiery
 */
public class FindMorador extends FindEntity{
    
    public static List<Morador> moradoresDoCondominio(Condominio c){
        Query q = getEntityManager()
                .createQuery(
                        "select o from Morador as o where o.idCondominio = :cond ",
                        Morador.class);
        q.setParameter("cond", c);
        List<Morador> moradores = q.getResultList();
        return moradores;
    }
    
    public static Morador moradoreComNome(String nome){
        Query q = getEntityManager()
                .createQuery(
                        "select o from Morador as o where o.nome = :nome ",
                        Morador.class);
        q.setParameter("nome", nome);
        Morador m = (Morador) q.getSingleResult();
        return m;
    }
    
    
}
