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
public class FindCondominio extends FindEntity{
    
    public static List<Condominio> buscarCondominios(){
        Query q = getEntityManager()
                .createQuery(
                        "select o from Condominio as o order by o.nome ",
                        Condominio.class);
        List<Condominio> c = q.getResultList();
        return c;
    }
    
    public static Condominio condominioComNome(String nome){
        Query q = getEntityManager()
                .createQuery(
                        "select o from Condominio as o where o.nome = :nome ",
                        Condominio.class);
        q.setParameter("nome", nome);
        Condominio m = (Condominio) q.getSingleResult();
        return m;
    }
    
    public static List<Condominio> condominiosComNomeQueInicia(String nome){
        Query q = getEntityManager()
                .createQuery(
                        "select o from Condominio as o where LOWER(o.nome) LIKE LOWER(:nome) "
                                + "order by o.nome",
                        Condominio.class);
        nome += "%";
        q.setParameter("nome", nome);
        List<Condominio> m = q.getResultList();
        return m;
    }

    public static Condominio buscarCondominioPorCNPJ(String cnpj) {
        Query q = getEntityManager()
                .createQuery(
                        "select o from Condominio as o where o.cnpj = :cnpj ",
                        Condominio.class);
        cnpj = OperacaoStringUtil.retirarMascaraDeCNPJ(cnpj);
        q.setParameter("cnpj", cnpj);
        Condominio m = (Condominio) q.getSingleResult();
        return m;
    }
    
    
}
