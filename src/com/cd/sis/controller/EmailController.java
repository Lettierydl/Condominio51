/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cd.sis.controller;

import com.cd.sis.bean.Boleto;
import com.cd.sis.bean.Morador;
import com.cd.util.Arquivo;
import com.cd.util.OperacaoStringUtil;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;

/**
 *
 * @author Lettiery
 */
public class EmailController {

    public static final String ASSUNTO_RELATORIOS = "Relatórios";
    public static final String ASSUNTO_BOLETO_MES_ATUAL = "Boleto do Mês atual";
    public static final String ASSUNTO_COBRANCA_BOLETO = "Boleto em aberto";

    private static final String HOST_NAME = "smtp.gmail.com";
    private static final String EMAIL_REMETENTE = "severino@contabilizepb.com.br";
    private static final String EMAIL_SENHA = "radum321onireves";

    public List<Morador>  enviarEmails(List<Morador> mor, List<File> anexos, String assunto, String texto) throws EmailException {
        if (mor == null || mor.isEmpty()) {
            return null;
        }
        List<Morador> falha = new ArrayList<>();
        for (Morador m : mor) {
            MultiPartEmail e = criarEmail(m,
                    "[CONDOMÍNIO " + m.getCondominio().getNome() + "] "
                    + assunto,
                    msg(m, assunto));
            for (File f : anexos) {
                e.attach(this.criarAnexo(f, f.getName(), f.getName()));
            }
            try{
                System.out.println(e.send());//e.send();
            }catch(Exception | Error er){
                falha.add(m);
                er.printStackTrace();
            }
        }
        return falha;
    }

    public void enviarEmailBoletoVencido(List<Boleto> bols) throws EmailException {
        if (bols == null || bols.isEmpty()) {
            return;
        }
        double debito = 0;
        for (Boleto b : bols) {
            debito += b.getTotalAPagar();
        }
        MultiPartEmail e = criarEmail(bols.get(0),
                "[" + bols.get(0).getCondominio().getNome() + "] "
                + ASSUNTO_COBRANCA_BOLETO,
                msg(bols.get(0), ASSUNTO_COBRANCA_BOLETO, debito));
        List<File> files = new ArrayList<>();
        for (Boleto b : bols) {
            File f = new File("");
            try {
                f = File.createTempFile("Condominio51_Boleto", ".pdf");
                files.add(f);
                Arquivo.criarPDF(b.getArquivo(), f);
            } catch (IOException ex) {
                Logger.getLogger(EmailController.class.getName()).log(Level.SEVERE, null, ex);
            }
            String nomeBol = "Boleto_" + OperacaoStringUtil.formatDataMesValor(b.getMesReferencia()) + ".pdf";
            e.attach(this.criarAnexo(f, nomeBol, nomeBol));
        }

        System.out.println(e.send());//e.send();
        for (File f : files) {
            f.delete();
        }
    }

    public void enviarEmailCobranca(Boleto b, File balancete) throws EmailException {
        MultiPartEmail e = criarEmail(b, "[CONDOMÍNIO " + b.getCondominio().getNome() + "] "
                + ASSUNTO_BOLETO_MES_ATUAL, msg(b, ASSUNTO_BOLETO_MES_ATUAL));
        File f = new File("");
        try {
            f = File.createTempFile("Condominio51_Boleto", ".pdf");
            Arquivo.criarPDF(b.getArquivo(), f);
        } catch (IOException ex) {
            Logger.getLogger(EmailController.class.getName()).log(Level.SEVERE, null, ex);
        }
        String nomeBol = "Boleto_" + OperacaoStringUtil.formatDataMesValor(b.getMesReferencia()) + ".pdf";
        e.attach(this.criarAnexo(f, nomeBol, nomeBol));
        try {
            e.attach(this.criarAnexo(balancete, "Balancete.pdf", "Balancete.pdf"));
        } catch (NullPointerException ne) {
        }//sem balancete
        //deletar arquivos criados

        System.out.println(e.send());//e.send();
        f.delete();
    }

    private MultiPartEmail criarEmail(Boleto boleto, String assunto, String msg) throws EmailException {
        MultiPartEmail email = new MultiPartEmail();
        email.setHostName(HOST_NAME); // o servidor SMTP para envio do e-mail
        email.addTo(boleto.getMorador().getEmail()); //destinatário
        email.setFrom(EMAIL_REMETENTE); // remetente
        email.setSubject(assunto); // assunto do e-mail
        email.setMsg(msg); //conteudo do e-mail
        email.setAuthentication(EMAIL_REMETENTE, new StringBuffer(EMAIL_SENHA).reverse().toString());
        email.setSmtpPort(465);
        email.setSSL(true);
        email.setTLS(true);
        return email;
    }

    private MultiPartEmail criarEmail(Morador morador, String assunto, String msg) throws EmailException {
        MultiPartEmail email = new MultiPartEmail();
        email.setHostName(HOST_NAME); // o servidor SMTP para envio do e-mail
        email.addTo(morador.getEmail()); //destinatário
        email.setFrom(EMAIL_REMETENTE); // remetente
        email.setSubject(assunto); // assunto do e-mail
        email.setMsg(msg); //conteudo do e-mail
        email.setAuthentication(EMAIL_REMETENTE, new StringBuffer(EMAIL_SENHA).reverse().toString());
        email.setSmtpPort(465);
        email.setSSL(true);
        email.setTLS(true);
        return email;
    }

    private String msg(Boleto boleto, String assunto) {
        return msg(boleto, assunto, 0);
    }

    
    private String msg(Boleto boleto, String assunto, double debito) {
        Arquivo a = new Arquivo();
        String s = a.lerMessange(assunto);
        if (s.isEmpty()) {
            salvarMenssagensPadrao();
            s = a.lerMessange(assunto);
        }
        Calendar balancete = Calendar.getInstance();
        balancete.set(Calendar.MONTH, balancete.get(Calendar.MONTH) - 1);
        s = s.replace("<NOME_MORADOR>", boleto.getMorador().getNome())
                .replace("<TITULO_CONDOMINIO>", boleto.getCondominio().getTituloDocumento())
                .replace("<MES_REFERENCIA>", OperacaoStringUtil.formatDataMesValor(boleto.getMesReferencia()))
                .replace("<MES_BALANCETE>", OperacaoStringUtil.formatDataMesValor(balancete))
                .replace("<TAXA>", OperacaoStringUtil.formatarStringValorMoeda(boleto.getTaxa()))
                .replace("<SALDACAO>", saldacao())
                .replace("<DEBITO>", OperacaoStringUtil.formatarStringValorMoeda(debito)
                );
        return s;
    }
    
    private String msg(Morador m, String assunto) {
        Arquivo a = new Arquivo();
        String s = a.lerMessange(assunto);
        if (s.isEmpty()) {
            salvarMenssagensPadrao();
            s = a.lerMessange(assunto);
        }
        Calendar balancete = Calendar.getInstance();
        balancete.set(Calendar.MONTH, balancete.get(Calendar.MONTH) - 1);
        s = s.replace("<NOME_MORADOR>", m.getNome())
                .replace("<TITULO_CONDOMINIO>", m.getCondominio().getTituloDocumento())
                .replace("<NOME_CONDOMINIO>", m.getCondominio().getNome())
                .replace("<TAXA>", OperacaoStringUtil.formatarStringValorMoeda(m.getCondominio().getTaxaCondominial()))
                .replace("<SALDACAO>", saldacao())
                .replace("<SINDICO>", m.getCondominio().getSindico().getNome());
        return s;
    }
    
    public void salvarMenssagensPadrao() {
        Arquivo a = new Arquivo();
        a.salvarMessange("<SALDACAO>, Sr(a) <NOME_MORADOR>\n\n"
                + "O Sr(a) <SINDICO> - Síndico do Condomínio <NOME_CONDOMINIO>, nas suas atribuições legais,\n"
                + "vem por meio deste reportar a informação contida no documento em anexo.\n"
                + "\nNo caso de dúvidas, contate-nos.\n\n"
                + "Desde já agradeçemos a sua colaboração.\n\n"
                + "<TITULO_CONDOMINIO>\n\n", ASSUNTO_RELATORIOS);
        
        a.salvarMessange("<SALDACAO>, Sr(a) <NOME_MORADOR>\n\n"
                + "Segue em anexo o boleto referente ao mês <MES_REFERENCIA>, e balancete do mês <MES_BALANCETE>.\n\n"
                + "Desde já agradeçemos o pagamento\n\n"
                + "<TITULO_CONDOMINIO>\n\n", ASSUNTO_BOLETO_MES_ATUAL);

        a.salvarMessange("<SALDACAO>, Sr(a) <NOME_MORADOR>\n\n"
                + "Consta no nosso sistema um débito no valor <DEBITO>,"
                + "segue em anexo o(s) boleto(s) não pago(s).\n"
                + "Gostaria de confirmar a data de pagamento desse(s) boleto(s).\n\n"
                + "Desde já agradeçemos o pagamento\n\n"
                + "<TITULO_CONDOMINIO>\n\n", ASSUNTO_COBRANCA_BOLETO);
        
        
    }
    
    private String saldacao() {
        int hora = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (hora < 13) {
            return "Bom Dia";
        } else if (hora < 18) {
            return "Boa Tade";
        } else {
            return "Boa Noite";
        }
    }


    private EmailAttachment criarAnexo(File anexo, String descricao, String nome) {
        EmailAttachment attachment = new EmailAttachment();
        attachment.setPath(anexo.getAbsolutePath()); //caminho da imagem
        attachment.setDisposition(EmailAttachment.ATTACHMENT);
        attachment.setDescription(descricao);
        attachment.setName(nome);
        return attachment;
    }
}
