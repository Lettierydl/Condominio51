/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cd.sis.bean;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Lettiery
 */
@Entity
@Table(name = "condominio")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Condominio.findAll", query = "SELECT c FROM Condominio c"),
    @NamedQuery(name = "Condominio.findById", query = "SELECT c FROM Condominio c WHERE c.id = :id"),
    @NamedQuery(name = "Condominio.findByCnpj", query = "SELECT c FROM Condominio c WHERE c.cnpj = :cnpj"),
    @NamedQuery(name = "Condominio.findByNome", query = "SELECT c FROM Condominio c WHERE c.nome = :nome"),
    @NamedQuery(name = "Condominio.findByEndereco", query = "SELECT c FROM Condominio c WHERE c.endereco = :endereco"),
    @NamedQuery(name = "Condominio.findByAgencia", query = "SELECT c FROM Condominio c WHERE c.agencia = :agencia"),
    @NamedQuery(name = "Condominio.findByConta", query = "SELECT c FROM Condominio c WHERE c.conta = :conta"),
    @NamedQuery(name = "Condominio.findByBanco", query = "SELECT c FROM Condominio c WHERE c.banco = :banco"),
    @NamedQuery(name = "Condominio.findByTaxaCondominial", query = "SELECT c FROM Condominio c WHERE c.taxaCondominial = :taxaCondominial"),
    @NamedQuery(name = "Condominio.findByUnidades", query = "SELECT c FROM Condominio c WHERE c.unidades = :unidades")})
public class Condominio implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    
    @Column(name = "cnpj")
    private String cnpj;
    
    @Basic(optional = false)
    @Column(name = "nome")
    private String nome;
    
    @Column(name = "endereco")
    private String endereco;
    
    @Column(name = "agencia")
    private String agencia;
    
    @Column(name = "conta")
    private String conta;
    
    @Column(name = "banco")
    private String banco;
    
    @Column(name = "dia_vencimento")
    private int diaDoVencimento;
    
// @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "taxa_condominial")
    private double taxaCondominial;

    @Column(name = "unidades")
    private int unidades;
    
    @OneToMany(mappedBy = "idCondominio")
    private List<Morador> moradorList;
    
    @JoinColumn(name = "id_sindico", referencedColumnName = "id")
    @ManyToOne
    private Morador idSindico;
    

    public Condominio() {
    }

    public Condominio(Integer id) {
        this.id = id;
    }

    public Condominio(Integer id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getAgencia() {
        return agencia;
    }

    public void setAgencia(String agencia) {
        this.agencia = agencia;
    }

    public String getConta() {
        return conta;
    }

    public void setConta(String conta) {
        this.conta = conta;
    }

    public String getBanco() {
        return banco;
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }

    public double getTaxaCondominial() {
        return taxaCondominial;
    }

    public void setTaxaCondominial(double taxaCondominial) {
        this.taxaCondominial = taxaCondominial;
    }

    public int getDiaDoVencimento() {
        return diaDoVencimento;
    }

    public void setDiaDoVencimento(int diaDoVencimento) {
        this.diaDoVencimento = diaDoVencimento;
    }
    
    public int getUnidades() {
        return unidades;
    }

    public void setUnidades(int unidades) {
        this.unidades = unidades;
    }

    @XmlTransient
    public List<Morador> getMoradorList() {
        return moradorList;
    }

    public void setMoradorList(List<Morador> moradorList) {
        this.moradorList = moradorList;
    }

    public Morador getSindico() {
        return idSindico;
    }

    public void setSindico(Morador idSindico) {
        this.idSindico = idSindico;
    }


    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Condominio)) {
            return false;
        }
        Condominio other = (Condominio) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.cd.sis.bean.Condominio[ id=" + id + " ]";
    }
    
}
