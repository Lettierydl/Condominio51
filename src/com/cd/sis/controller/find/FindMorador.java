/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cd.sis.controller.find;

import com.cd.sis.controller.FindEntity;
import com.cd.sis.bean.*;
import com.cd.util.OperacaoStringUtil;
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
                        "select o from Morador as o where o.idCondominio = :cond "
                                + "order by o.unidade",
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
    
    public static Morador moradoreComUnidade(String unidade, Condominio c){
        Query q = getEntityManager()
                .createQuery(
                        "select o from Morador as o where o.unidade = :unidade "
                            + "and o.idCondominio = :cond order by o.unidade ",
                        Morador.class);
        q.setParameter("unidade", unidade);
        q.setParameter("cond", c);
        
        Morador m = (Morador) q.getSingleResult();
        return m;
    }
    
    public static List<Morador> moradoreComNomeQueInicia(String nome){
        Query q = getEntityManager()
                .createQuery(
                        "select o from Morador as o where LOWER(o.nome) LIKE LOWER(:nome) "
                        + "order by o.nome",
                        Morador.class);
        nome+= "%";
        q.setParameter("nome", nome);
        List<Morador> m = q.getResultList();
        return m;
    }
    
    public static List<Morador> moradoreComNomeOuUnidadeQueInicia(String nomeOuUnidade, Condominio c){
        Query q = getEntityManager()
                .createQuery(
                        "select o from Morador as o where o.idCondominio = :cond and "
                                + "LOWER(o.nome) LIKE LOWER(:nome) "
                                + "or LOWER(o.unidade) LIKE LOWER(:unidade) "
                                + "and o.idCondominio = :cond "
                        + "order by o.unidade",
                        Morador.class);
        nomeOuUnidade+= "%";
        q.setParameter("nome", nomeOuUnidade);
        q.setParameter("unidade", nomeOuUnidade);
        q.setParameter("cond", c);
        List<Morador> m = q.getResultList();
        return m;
    }

    public static Morador moradorComCPF(String cpf) {
         Query q = getEntityManager()
                .createQuery(
                        "select o from Morador as o where o.cpf = :cpf ",
                        Morador.class);
        q.setParameter("cpf", OperacaoStringUtil.retirarMascaras(cpf));
        Morador m = (Morador) q.getSingleResult();
        return m;
    }
    
    public static List<Morador> moradoresComCPF(String cpf) {
         Query q = getEntityManager()
                .createQuery(
                        "select o from Morador as o where o.cpf = :cpf ",
                        Morador.class);
        q.setParameter("cpf", OperacaoStringUtil.retirarMascaras(cpf));
        List<Morador> m =  q.getResultList();
        return m;
    }
    
    
}
