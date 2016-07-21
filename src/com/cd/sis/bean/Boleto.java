/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cd.sis.bean;

import com.cd.util.OperacaoStringUtil;
import com.cd.util.VariaveisDeConfiguracaoUtil;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;
import org.joda.time.Days;

/**
 *
 * @author Lettiery
 */
@Entity
@Table(name = "boleto")
@XmlRootElement
public class Boleto implements Serializable, Comparable<Boleto> {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;

    @Column(name = "mes_referencia")
    @Temporal(TemporalType.DATE)
    private Date mesReferencia;

    @Column(name = "pago")
    private boolean pago;

    @Column(name = "data_pago")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataPago;

    @Column(name = "valor_pago")
    private double valorPago;

    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(precision = 22)
    private double taxa;

    @Lob
    private byte[] arquivo;

    @Column(name = "nomeAquivo")
    private String nomeAquivo;

    @JoinColumn(name = "id_condominio", referencedColumnName = "id")
    @ManyToOne
    private Condominio condominio;

    @JoinColumn(name = "id_morador", referencedColumnName = "id")
    @ManyToOne
    private Morador idMorador;

    public Boleto() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getMesReferencia() {
        return mesReferencia;
    }

    public void setMesReferencia(Date mesReferencia) {
        this.mesReferencia = mesReferencia;
    }

    public boolean isPago() {
        return pago;
    }

    public void setPago(boolean pago) {
        this.pago = pago;
    }

    public double getTaxa() {
        return taxa;
    }

    public void setTaxa(double taxa) {
        this.taxa = taxa;
    }

    public byte[] getArquivo() {
        return arquivo;
    }

    public void setArquivo(byte[] arquivo) {
        this.arquivo = arquivo;
    }

    public String getNomeAquivo() {
        return nomeAquivo;
    }

    public void setNomeAquivo(String nomeAquivo) {
        this.nomeAquivo = nomeAquivo;
    }

    public Condominio getCondominio() {
        return condominio;
    }

    public void setCondominio(Condominio condominio) {
        this.condominio = condominio;
    }

    public Morador getMorador() {
        return idMorador;
    }

    public void setMorador(Morador morador) {
        this.idMorador = morador;
    }

    public Date getDataPago() {
        return dataPago;
    }

    public void setDataPago(Date dataPago) {
        this.dataPago = dataPago;
    }

    public double getValorPago() {
        return valorPago;
    }

    public void setValorPago(double valorPago) {
        this.valorPago = valorPago;
    }

    public double getJuros() {
        int diaDoVencimento = condominio.getDiaDoVencimento();
        Calendar hoje = Calendar.getInstance();
        Calendar vencimento = Calendar.getInstance();
        vencimento.setTime(mesReferencia);
        vencimento.set(Calendar.DAY_OF_MONTH, diaDoVencimento);
       
        if (isPago() || vencimento.after(hoje) ) {
            return 0;
        }
        vencimento.setTime(mesReferencia);
        vencimento.set(Calendar.DAY_OF_MONTH, diaDoVencimento);
        long DAY = 24L * 60L * 60L * 1000L;
        int diferenca = (int) ((hoje.getTime().getTime() - vencimento.getTime().getTime() ) / DAY);
        diferenca = (diferenca < 0 ? 0 : diferenca);
        return (taxa
                * VariaveisDeConfiguracaoUtil.PORCENTAGEM_JUROS_AO_DIA)
                * diferenca;
    }

    public double getMulta() {
        int diaDoVencimento = condominio.getDiaDoVencimento();
        Calendar hoje = Calendar.getInstance();
        Calendar vencimento = Calendar.getInstance();
        vencimento.setTime(mesReferencia);
        vencimento.set(Calendar.DAY_OF_MONTH, diaDoVencimento);
        
        if (isPago() || vencimento.after(hoje)) {
            return 0;
        }
        return condominio.getTaxaCondominial()
                * VariaveisDeConfiguracaoUtil.PORCENTAGEM_MULTA;

    }

    public double getTotalAPagar() {
        if (isPago()) {
            return 0;
        }
        return taxa + getJuros() + getMulta();
    }

    @Override
    public String toString() {
        return "Boleto{" + "id=" + id + ", mesReferencia=" + mesReferencia + ", pago=" + pago + ", taxa=" + taxa + ", condominio=" + condominio + ", morador=" + idMorador.getNome() + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Boleto other = (Boleto) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(Boleto o) {
        return o.getMesReferencia().compareTo(mesReferencia);
    }

    public String getStatus() {
        if (isPago()) {
            return "Pago";
        } else {
            return "Em Aberto";
        }
    }
}
