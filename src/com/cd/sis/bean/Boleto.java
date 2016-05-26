/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cd.sis.bean;

import java.io.Serializable;
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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Lettiery
 */
@Entity
@Table(name = "boleto")
@XmlRootElement
public class Boleto  implements Serializable {
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
    
    
    
    
    
    
    
    
    
}
