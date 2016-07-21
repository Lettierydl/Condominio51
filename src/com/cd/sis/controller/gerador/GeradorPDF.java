/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cd.sis.controller.gerador;

import com.cd.sis.bean.Boleto;
import com.cd.sis.bean.Condominio;
import com.cd.sis.bean.Morador;
import com.cd.ui.img.IMG;
import com.cd.util.Arquivo;
import com.cd.util.OperacaoStringUtil;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Lettiery
 */
public class GeradorPDF {
    public static final Font TITULO = new Font(Font.FontFamily.COURIER, 20, Font.BOLD);
    public static final Font SUBTITULO = new Font(Font.FontFamily.COURIER, 16, Font.BOLD);
    public static final Font INFORMACOES = new Font(Font.FontFamily.COURIER, 14, Font.NORMAL);
    
    public static final Font BOLD_UNDERLINED = new Font(FontFamily.TIMES_ROMAN, 12, Font.BOLD | Font.UNDERLINE);
    public static final Font NORMAL = new Font(FontFamily.TIMES_ROMAN, 12);

    public static final BaseColor COLOR_DESTAQUE = new BaseColor(0,255, 239);
    
    private Arquivo a;

    public GeradorPDF() {
        a = new Arquivo();
    }
    
    //Cadastros
    public String gerarPdfCadastro(Condominio c, File destino) {
        Document doc = new Document();
        try {
            String path = a.getRelatorio().getCanonicalPath() + destino.getName();
            PdfWriter.getInstance(doc, new FileOutputStream(path));
            doc.open();
            String info = "\n\nUnides: "+c.getUnidades()
                    +"\nInformações Bancárias"
                    + "\n\t•\tBanco: "+ c.getBanco()+"\n\t•\tAgência: "+c.getAgencia()
                    +"\n\t•\tConta: "+c.getConta();
            String sindico = "\n"+c.getSindico().getNome()
                    +"\nCPF "+ c.getSindico().getCpf()+
                    "\nSíndico";
            inserirHead(doc, "Dados Cadastrais", "");
            Paragraph p = new Paragraph(c.getTituloDocumento(), SUBTITULO);
            p.setAlignment(Element.ALIGN_CENTER);
            
            Paragraph p2 = new Paragraph(sindico, INFORMACOES);
            p2.setAlignment(Element.ALIGN_CENTER);
            
            Paragraph p3 = new Paragraph(info, INFORMACOES);
            p3.setAlignment(Element.ALIGN_JUSTIFIED);
            
            doc.add(p);
            doc.add(p2);
            doc.add(p3);

            doc.close();
            Arquivo.copyFile(new File(path), destino);
            return destino.getAbsolutePath();
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
            return "";
        }
    }
    
    public String gerarPdfCadastro(Morador m, File destino) {
        Document doc = new Document();
        try {
            String path = a.getRelatorio().getCanonicalPath() + destino.getName();
            PdfWriter.getInstance(doc, new FileOutputStream(path));
            doc.open();
            String info = m.getNome()+
                    "\nUnidade "+m.getUnidade()+
                    "\nCPF "+m.getCpf()+
                    "\nEmail "+m.getEmail()+
                    "\nTelefone "+m.getTelefone()+
                    "\nEndereço "+m.getCondominio().getEndereco()
                    + " - APT " +m.getUnidade();
            inserirHead(doc, "Dados Cadastrais", "\n\n");
            Paragraph p = new Paragraph(
                    m.getCondominio().getTituloDocumento()+"\n", SUBTITULO);
            p.setAlignment(Element.ALIGN_CENTER);
            Paragraph p2 = new Paragraph(info, INFORMACOES);
            p2.setAlignment(Element.ALIGN_JUSTIFIED);
            
            doc.add(p);
            doc.add(p2);

            doc.close();
            Arquivo.copyFile(new File(path), destino);
            return destino.getAbsolutePath();
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
            return "";
        }
    }
    
    public String gerarPdfCadastro(List<Morador> moradores, File destino) {
        Document doc = new Document();
        try {
            String path = a.getRelatorio().getCanonicalPath() + destino.getName();
            PdfWriter.getInstance(doc, new FileOutputStream(path));
            doc.open();
            inserirHead(doc, "Dados Cadastrais\n\n"+
                    moradores.get(0).getCondominio().getTituloDocumento()
                    ,moradores.get(0).getCondominio().getUnidades()+ " Condôminos");
            adicionarListMoradores(doc, moradores);

            doc.close();
            Arquivo.copyFile(new File(path), destino);
            return destino.getAbsolutePath();
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
            return "";
        }
    }
    
    //Boletos
    public String gerarPdfBoletos(java.util.List<Boleto> boletos, String titulo, String subTitulo, File destino) {
        Document doc = new Document();
        try {
            String path = a.getRelatorio().getCanonicalPath() + destino.getName();
            PdfWriter.getInstance(doc, new FileOutputStream(path));
            doc.open();
            inserirHead(doc, titulo, subTitulo);

            adicionarListBoletos(doc, boletos);

            doc.close();
            Arquivo.copyFile(new File(path), destino);
            return destino.getAbsolutePath();
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public void inserirHead(Document doc, String titulo, String subTitulo) throws MalformedURLException, DocumentException {
        Image img = null;
        try {
            img = Image.getInstance(IMG.class.getResource("logo_relatorio.png"));

            img.setAlignment(Element.ALIGN_LEFT);

            doc.add(img);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Paragraph p = new Paragraph(titulo, TITULO);
        Paragraph p2 = new Paragraph(subTitulo, SUBTITULO);
        p.setAlignment(Element.ALIGN_CENTER);

        doc.add(p);
        doc.add(p2);
    }

    private void adicionarListBoletos(Document doc, java.util.List<Boleto> boletos) throws DocumentException {
        Map<Morador, java.util.List<Boleto>> bol
                = GeradorRelatorio.separarPorMorador(boletos);
        PdfPTable table = new PdfPTable(6);
        table.setWidths(new int[]{3, 1, 1, 1, 1, 1});
        table.setWidthPercentage(100f);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

        table.getDefaultCell().setBackgroundColor(new BaseColor(60, 171, 198));
        table.addCell("Unidade");table.addCell("Mês de Ref.");
        table.addCell("Valor"); table.addCell("Juros");
        table.addCell("Multa");table.addCell("Total");
        table.getDefaultCell().setBackgroundColor(null);
        double Tjuros = 0, Tmulta = 0, Tvalor = 0, Ttotal = 0;
        for (Morador m : bol.keySet()) {
            double juros = 0, multa = 0, valor = 0, total = 0;
            PdfPCell t = new PdfPCell(new Paragraph(m.toString()));
            t.setColspan(6);t.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(t);
            for (Boleto b : bol.get(m)) {
                table.addCell("Taxa Condominial " + b.getStatus());
                table.addCell(OperacaoStringUtil.formatDataMesValor(b.getMesReferencia()));
                table.addCell(OperacaoStringUtil.formatarStringValorMoeda(b.getTaxa()));
                table.addCell(OperacaoStringUtil.formatarStringValorMoeda(b.getJuros()));
                table.addCell(OperacaoStringUtil.formatarStringValorMoeda(b.getMulta()));
                table.addCell(OperacaoStringUtil.formatarStringValorMoeda(b.getTotalAPagar()));
                valor += b.getTaxa();
                total += b.getTotalAPagar();
                multa += b.getMulta();
                juros += b.getJuros();
            }
            PdfPCell c = new PdfPCell(new Paragraph("Total " + m.getUnidade() + ""));
            c.setColspan(2);c.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(c);
            table.addCell(OperacaoStringUtil.formatarStringValorMoeda(valor));
            table.addCell(OperacaoStringUtil.formatarStringValorMoeda(juros));
            table.addCell(OperacaoStringUtil.formatarStringValorMoeda(multa));
            table.addCell(OperacaoStringUtil.formatarStringValorMoeda(total));
            PdfPCell f = new PdfPCell(new Paragraph(" "));
            f.setColspan(6);
            f.setRowspan(1);
            table.addCell(f);
            Tvalor += valor;
            Ttotal += total;
            Tmulta += multa;
            Tjuros += juros;
        }
        table.getDefaultCell().setBackgroundColor(new BaseColor(60, 171, 198));
        PdfPCell c = new PdfPCell(new Paragraph("Total "));
        c.setColspan(2);c.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(c);
        table.addCell(OperacaoStringUtil.formatarStringValorMoeda(Tvalor));
        table.addCell(OperacaoStringUtil.formatarStringValorMoeda(Tjuros));
        table.addCell(OperacaoStringUtil.formatarStringValorMoeda(Tmulta));
        table.addCell(OperacaoStringUtil.formatarStringValorMoeda(Ttotal));
        doc.add(table);
    }
    
    
    private void adicionarListMoradores(Document doc, java.util.List<Morador> moradores) throws DocumentException {
        Collections.sort(moradores);
        PdfPTable table = new PdfPTable(5);
        table.setWidths(new float[]{1.2f, 4f, 2.2f, 2f, 2f});
        table.setWidthPercentage(100f);
        table.setSpacingBefore(5f);
        table.setSpacingAfter(5f);
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

        table.getDefaultCell().setBackgroundColor(new BaseColor(60, 171, 198));
        table.addCell("Unidade");table.addCell("Nome");
        table.addCell("CPF"); table.addCell("Telefone");
        table.addCell("Email");
        table.getDefaultCell().setBackgroundColor(null);
        for (Morador m : moradores) {
            if(m.getCondominio().getSindico().equals(m)){
                table.getDefaultCell().setBackgroundColor(COLOR_DESTAQUE);
            }else{
                table.getDefaultCell().setBackgroundColor(null);
            }
            
            table.addCell(m.getUnidade());
            table.addCell(m.getNome());
            table.addCell(m.getCpf());
            table.addCell(m.getTelefone());
            table.addCell(m.getEmail().toLowerCase());
            
        }
        doc.add(table);
    }

}
