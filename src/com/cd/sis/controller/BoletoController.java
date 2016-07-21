/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cd.sis.controller;

import com.cd.exception.BoletoJaSalvo;
import com.cd.exception.EntidadeNaoExistenteException;
import com.cd.sis.bean.*;
import com.cd.sis.controller.find.FindBoleto;
import com.cd.sis.controller.find.FindMorador;
import com.cd.util.Arquivo;
import com.cd.util.OperacaoStringUtil;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;

/**
 *
 * @author Lettiery
 */
public class BoletoController extends ControllerEntity<Boleto> {

    public double debitoMorador(Morador m) {
        double debito = 0;
        for (Boleto b : FindBoleto.boletosNaoPagoDoMorador(m)) {
            debito += b.getTaxa();
        }
        return debito;
    }

    //so cria nao salva no banco de dados
    public List<Boleto> criarBoletosMesAtual(Condominio cond) throws Exception {
        List<Boleto> boletos = new ArrayList<>();
        for (Morador m : FindMorador.moradoresDoCondominio(cond)) {
            if (cond.getSindico().equals(m)) {// sindico isento
                continue;
            }
            Boleto b = new Boleto();
            b.setMesReferencia(new Date());
            b.setCondominio(cond);
            b.setMorador(m);
            b.setTaxa(cond.getTaxaCondominial());
            b.setPago(false);
            boletos.add(b);
        }
        return boletos;
    }

    public double pagarBoletos(List<Boleto> boletos) throws EntidadeNaoExistenteException {
        double val_pago = 0;
        for (Boleto b : boletos) {
            pagarBoleto(b, 0, 0);
            val_pago += b.getValorPago();
        }
        return val_pago;
    }

    public void pagarBoleto(Boleto boleto) throws EntidadeNaoExistenteException {
        pagarBoleto(boleto, 0, 0);
    }

    public void pagarBoleto(Boleto boleto, double juros, double multa) throws EntidadeNaoExistenteException {
        boleto.setPago(true);
        boleto.setDataPago(new Date());
        boleto.setValorPago(boleto.getTaxa() + juros + multa);
        edit(boleto);
    }

    @Override
    public void create(Boleto entity) throws Exception {
        try {
            try {
                Boleto b2 = FindBoleto.getBoleto(entity.getMesReferencia(), entity.getMorador());
                if (b2 != null) {
                    throw new BoletoJaSalvo(b2);
                }
            } catch (NoResultException ne) {
            }
            beginTransaction();
            em.persist(entity);
            commitTransaction();
        } finally {
            closeEntityManager();
        }
    }

    @Override
    public void edit(Boleto entity) throws EntidadeNaoExistenteException {
        try {
            beginTransaction();
            em.merge(entity);
            commitTransaction();
        } finally {
            closeEntityManager();
        }
    }

    @Override
    public void destroy(Boleto entity) throws EntidadeNaoExistenteException {
        try {
            beginTransaction();
            try {
                getReference(entity);
            } catch (EntityNotFoundException enfe) {
                throw new EntidadeNaoExistenteException(
                        "O Boleto com código " + entity.getId()
                        + " não existe.");
            }
            em.remove(getReference(entity));
            commitTransaction();
        } finally {
            closeEntityManager();
        }
    }

    @Override
    public Boleto getReference(Boleto entity) {
        em = getEntityManager();
        return em.getReference(Boleto.class, entity.getId());
    }

    @Override
    public void removeAll() {
        try {
            beginTransaction();
            List<Boleto> resultList = em.createNativeQuery("SELECT * FROM boleto;", Boleto.class).getResultList();
            for (Boleto p : resultList) {
                em.remove(p);
            }
            commitTransaction();
        } finally {
            closeEntityManager();
        }
    }

    public List<Boleto> gerarBoletos(Condominio condominio, List<File> filesBoletos, double taxaPadrao, Date mesReferencia) throws IOException {
        List<File> files = new ArrayList<>(filesBoletos);
        List<Boleto> boletos = new ArrayList<>();
        List<Morador> moradores = FindMorador.moradoresDoCondominio(condominio);

        for (File f : files) {
            String pdfText = PdfTextExtractor.getTextFromPage(new PdfReader(f.getCanonicalPath()), 1);
            String cpf = OperacaoStringUtil.regex(OperacaoStringUtil.REGEX_CPF, pdfText).split("\n")[0];
            Morador eleito = null;
            for (Morador m : moradores) {
                String unidade = m.getUnidade().toUpperCase();
                String Pnome;
                try {
                    Pnome = m.getNome().toUpperCase().split(" ")[0] + " " + m.getNome().toUpperCase().split(" ")[1];
                } catch (ArrayIndexOutOfBoundsException e) {
                    Pnome = m.getNome().toUpperCase().split(" ")[0];
                }
                if (m.getCpf().equals(cpf)) {
                    if (FindMorador.moradoresComCPF(cpf).size() > 1
                            && OperacaoStringUtil.regex("(?i).AP." + unidade, pdfText).isEmpty()) {//dois moradores com mesmo cpf
                        continue;//e outro morador com mesmo cpf
                    }
                    eleito = m;
                    break;
                } else if (!OperacaoStringUtil.regex("(?i)"+Pnome + ".\\w{1,"+Pnome.length()+"}", pdfText).isEmpty()
                        || !OperacaoStringUtil.regex("(?i).AP." + unidade, pdfText).isEmpty()) {
                    eleito = m;
                    break;
                }

            }
            if (eleito == null) {
                // procura pelo nome do arquivo
                String nF = f.getName().toUpperCase();
                for (Morador m : moradores) {
                    String unidade = m.getUnidade().toUpperCase();
                    String Pnome;
                    try {
                        Pnome = m.getNome().toUpperCase().split(" ")[0] + " " + m.getNome().toUpperCase().split(" ")[1];
                    } catch (ArrayIndexOutOfBoundsException e) {
                        Pnome = m.getNome().toUpperCase().split(" ")[0];
                    }
                    if (!OperacaoStringUtil.regex("(?i)"+Pnome + ".\\w{1,"+Pnome.length()+"}", nF).isEmpty()
                            || !OperacaoStringUtil.regex("(?i).AP." + unidade, nF).isEmpty()) {
                        eleito = m;
                        break;
                    }
                }
                if (eleito == null) {
                    continue;
                }
            }
            Boleto b = criarBoleto(condominio, mesReferencia, eleito, taxaPadrao, f);
            boletos.add(b);
        }
        return boletos;
    }

    public Boleto criarBoleto(Condominio condominio, Date mesReferencia, Morador m, double taxaPadrao, File f) throws IOException {
        mesReferencia.setDate(1);
        Boleto b = new Boleto();
        b.setCondominio(condominio);
        b.setMesReferencia(mesReferencia);
        b.setMorador(m);
        b.setTaxa(taxaPadrao);
        b.setArquivo(Arquivo.lerBytesPDF(f));
        b.setNomeAquivo(f.getName());
        return b;
    }

    /*retorna a quantidade de boletos criados*/
    public int salvarBoletos(List<Boleto> boletos) throws Exception {
        List<Boleto> boletosAS = new ArrayList<>();
        for (Boleto b : boletos) {
            try {
                this.create(b);
            } catch (BoletoJaSalvo e) {
                boletosAS.add(b);
            }
        }
        if (!boletosAS.isEmpty()) {
            throw new BoletoJaSalvo(boletosAS);
        }

        return boletos.size();
    }

}
