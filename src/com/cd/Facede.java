/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cd;

import com.cd.exception.LoginIncorretoException;
import com.cd.sis.bean.*;
import com.cd.sis.controller.*;
import com.cd.sis.controller.find.FindBoleto;
import java.util.List;

/**
 *
 * @author Lettiery
 */
public class Facede {
    
    private static Facede fac;
    public static String version_system = "1.0";
    
    public static Facede getInstance(){
        if(fac == null){
            fac = new Facede();
        }
        return fac;
    }
    
    private BoletoController bol;
    private CondominioController con;
    private MoradorController mor;
    private UsuarioController use;
    private Usuario logado = null;
    
    
    
    private Facede(){
        bol = new BoletoController();
        mor = new MoradorController();
        con = new CondominioController();
        use = new UsuarioController();
    }
    
    /*Boleto*/
    public void create(Boleto entity) throws Exception{
        bol.create(entity);
    }
    
    public void edit(Boleto entity) throws Exception{
        bol.edit(entity);
    }
    
    public void destroy(Boleto entity) throws Exception{
        bol.destroy(entity);
    }
    
    public List<Boleto> criarBoletosMesAtual(Condominio cond) throws Exception{
        bol.criarBoletosMesAtual(cond);
        return FindBoleto.boletosNaoPagosMesAtualCondominio(cond);
    }
    
    
    
    /*Condominio*/
    public void create(Condominio entity) throws Exception{
        con.create(entity);
    }
    public void edit(Condominio entity) throws Exception{
        con.edit(entity);
    }
    
    public void destroy(Condominio entity) throws Exception{
        con.destroy(entity);
    }
    
    
    /*Usuario*/
    public void create(Usuario entity) throws Exception{
        use.create(entity);
    }
    public void edit(Usuario entity) throws Exception{
        use.edit(entity);
    }
    
    public void destroy(Usuario entity) throws Exception{
        use.destroy(entity);
    }

    public void login(String name, String pass) throws LoginIncorretoException {
        Usuario logado = use.login(name, pass);
        this.logado = logado;
    }
    
    public Usuario getLogado(){
        return this.logado;
    }

    public void logoff() {
        this.logado = null;
    }
    
}
