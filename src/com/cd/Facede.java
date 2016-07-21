/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cd;

import com.cd.exception.EntidadeNaoExistenteException;
import com.cd.exception.LoginIncorretoException;
import com.cd.sis.bean.*;
import com.cd.sis.controller.*;
import com.cd.sis.controller.find.FindCondominio;
import com.cd.sis.controller.gerador.GeradorPDF;
import com.cd.util.Arquivo;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import org.apache.commons.mail.EmailException;

/**
 *
 * @author Lettiery
 */
public class Facede {

    private static Facede fac;
    public static String version_system = "1.0";

    public static Facede getInstance() {
        if (fac == null) {
            fac = new Facede();
        }
        return fac;
    }

    private Arquivo a;
    private BoletoController bol;
    private CondominioController con;
    private MoradorController mor;
    private UsuarioController use;
    private EmailController email;
    private Usuario logado = null;

    private GeradorPDF pdf;

    private Facede() {
        bol = new BoletoController();
        mor = new MoradorController();
        con = new CondominioController();
        use = new UsuarioController();
        email = new EmailController();

        pdf = new GeradorPDF();
        a = new Arquivo();
    }

    /*Boleto*/
    public void edit(Boleto entity) throws Exception {
        bol.edit(entity);
    }

    public void destroy(Boleto entity) throws Exception {
        bol.destroy(entity);
    }

    //cria nao salva no banco de dados
    public List<Boleto> criarBoletosMesAtual(Condominio cond) throws Exception {
        return bol.criarBoletosMesAtual(cond);
    }

    //cria nao salva no banco de dados
    public Boleto criarBoleto(Condominio cond, Morador m, double taxa, Date mesReferencia, File f) throws IOException {
        Boleto bo = bol.criarBoleto(cond, mesReferencia, m, taxa, f);
        return bo;
    }

    /*retorna a quantidade de boletos criados*/
    public int salvarBoletos(List<Boleto> boletos) throws Exception {
        return bol.salvarBoletos(boletos);
    }

    // salva o boleto no banco de dados verificando a existencia
    public void salvarBoleto(Boleto boleto) throws Exception {
        bol.create(boleto);
    }

    public double pagarBoletos(List<Boleto> boletos) throws EntidadeNaoExistenteException {
        return bol.pagarBoletos(boletos);
    }

    /*Condominio*/
    public void create(Condominio entity) throws Exception {
        con.create(entity);
    }

    public void edit(Condominio entity) throws Exception {
        con.edit(entity);
    }

    public void destroy(Condominio entity) throws Exception {
        con.destroy(entity);
    }

    public List<Condominio> buscarCondominios() {
        return FindCondominio.buscarCondominios();
    }

    /*Usuario*/
    public void create(Usuario entity) throws Exception {
        use.create(entity);
    }

    public void edit(Usuario entity) throws Exception {
        use.edit(entity);
    }

    public void destroy(Usuario entity) throws Exception {
        use.destroy(entity);
    }

    public void login(String name, String pass) throws LoginIncorretoException {
        Usuario logado = use.login(name, pass);
        this.logado = logado;
    }

    public Usuario getLogado() {
        return this.logado;
    }

    public void logoff() {
        this.logado = null;
    }

    public List<Boleto> gerarBoletos(Condominio condominio, List<File> files, double taxaPadrao, Date mesReferencia) throws IOException {
        return bol.gerarBoletos(condominio, files, taxaPadrao, mesReferencia);
    }

    //Geradores
    public String gerarPdfBoletos(java.util.List<Boleto> boletos, String titulo, String subTitulo, File destino) {
        return pdf.gerarPdfBoletos(boletos, titulo, subTitulo, destino);
    }

    public String gerarPdfCadastro(java.util.List<Morador> moradores, File destino) {
        return pdf.gerarPdfCadastro(moradores, destino);
    }

    public String gerarPdfCadastro(Morador m, File destino) {
        return pdf.gerarPdfCadastro(m, destino);
    }

    public String gerarPdfCadastro(Condominio c, File destino) {
        return pdf.gerarPdfCadastro(c, destino);
    }

    /*Moraodr*/
    public void create(Morador entity) throws Exception {
        mor.create(entity);
    }

    public void edit(Morador entity) throws EntidadeNaoExistenteException {
        mor.edit(entity);
    }

    public void destroy(Morador entity) throws EntidadeNaoExistenteException {
        mor.destroy(entity);
    }

    /*Email*/
    public void enviarEmailCobranca(Boleto boleto, File balancete) throws EmailException {
        email.enviarEmailCobranca(boleto, balancete);
    }

    public void enviarEmailBoletoVencido(List<Boleto> boletos) throws EmailException {
        email.enviarEmailBoletoVencido(boletos);
    }
    
    public List<Morador>  enviarEmails(List<Morador> mor, List<File> anexos, String assunto, String texto) throws EmailException {
        return email.enviarEmails(mor, anexos, assunto, texto);
    }
    
    
    public String getMenssagemEmail(String assunto) {
        String s = a.lerMessange(assunto);
        if (s.isEmpty()) {
            email.salvarMenssagensPadrao();
            s = a.lerMessange(assunto);
        }
        return s;
    }

    public void setMenssagemEmail(String msg, String assunto) {
        a.salvarMessange(msg, assunto);
    }

}
