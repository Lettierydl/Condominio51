/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cd.sis.bean;

import com.cd.util.OperacaoStringUtil;
import java.io.Serializable;
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
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Lettiery
 */
@Entity
@Table(name = "morador")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Morador.findAll", query = "SELECT m FROM Morador m"),
    @NamedQuery(name = "Morador.findById", query = "SELECT m FROM Morador m WHERE m.id = :id"),
    @NamedQuery(name = "Morador.findByUnidade", query = "SELECT m FROM Morador m WHERE m.unidade = :unidade"),
    @NamedQuery(name = "Morador.findByNome", query = "SELECT m FROM Morador m WHERE m.nome = :nome"),
    @NamedQuery(name = "Morador.findByCpf", query = "SELECT m FROM Morador m WHERE m.cpf = :cpf"),
    @NamedQuery(name = "Morador.findByTelefone", query = "SELECT m FROM Morador m WHERE m.telefone = :telefone"),
    @NamedQuery(name = "Morador.findByEmail", query = "SELECT m FROM Morador m WHERE m.email = :email")})
public class Morador implements Serializable, Comparable<Morador> {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    
    @Column(name = "unidade")
    private String unidade;
    
    @Column(name = "nome")
    private String nome;
    
    @Column(name = "cpf")
    private String cpf;
    
    @Column(name = "telefone")
    private String telefone;
    
    @Column(name = "email")
    private String email;
    
    
    @JoinColumn(name = "id_condominio", referencedColumnName = "id")
    @ManyToOne
    private Condominio idCondominio;
    
    

    public Morador() {
    }

    public Morador(Integer id) {
        this.id = id;
    }
    
    public Morador(String nome) {
        this.nome = nome;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Condominio getCondominio() {
        return idCondominio;
    }

    public void setCondominio(Condominio idCondominio) {
        this.idCondominio = idCondominio;
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
        if (!(object instanceof Morador)) {
            return false;
        }
        Morador other = (Morador) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        if(unidade != null && !unidade.isEmpty()){
            return this.nome+" - "+unidade ;
        }else{
            return this.nome;
        }
    }

    @Override
    public int compareTo(Morador o) {
        try{
            int und = OperacaoStringUtil.converterStringValorInt(o.unidade);
            int und2 = OperacaoStringUtil.converterStringValorInt(unidade);
            return Integer.compare(und,und2);
        }catch(Exception e){}
        
        return unidade.compareToIgnoreCase(o.unidade);
    }

    
}
