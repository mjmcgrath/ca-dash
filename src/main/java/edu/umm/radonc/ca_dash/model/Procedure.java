/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.umm.radonc.ca_dash.model;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author mmcgrath
 */
@Entity
@Table(name = "procedurecode")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Procedure.findAll", query = "SELECT p FROM Procedure p"),
    @NamedQuery(name = "Procedure.findByProcedurecodeser", query = "SELECT p FROM Procedure p WHERE p.procedurecodeser = :procedurecodeser"),
    @NamedQuery(name = "Procedure.findByCodetype", query = "SELECT p FROM Procedure p WHERE p.codetype = :codetype"),
    @NamedQuery(name = "Procedure.findByProcedurecode", query = "SELECT p FROM Procedure p WHERE p.procedurecode = :procedurecode"),
    @NamedQuery(name = "Procedure.findByShortcomment", query = "SELECT p FROM Procedure p WHERE p.shortcomment = :shortcomment")})
public class Procedure implements Serializable {
    @Size(max = 255)
    @Column(name = "comment")
    private String comment;
    @Column(name = "departmentser")
    private Integer departmentser;
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "procedurecodeser")
    private Integer procedurecodeser;
    @Size(max = 255)
    @Column(name = "codetype")
    private String codetype;
    @Size(max = 255)
    @Column(name = "procedurecode")
    private String procedurecode;
    @Size(max = 255)
    @Column(name = "shortcomment")
    private String shortcomment;
    @OneToMany(mappedBy = "procedurecodeser")
    private Collection<ActivityAIPC> activityCollection;

    public Procedure() {
    }

    public Procedure(Integer procedurecodeser) {
        this.procedurecodeser = procedurecodeser;
    }

    public Integer getProcedurecodeser() {
        return procedurecodeser;
    }

    public void setProcedurecodeser(Integer procedurecodeser) {
        this.procedurecodeser = procedurecodeser;
    }

    public String getCodetype() {
        return codetype;
    }

    public void setCodetype(String codetype) {
        this.codetype = codetype;
    }

    public String getProcedurecode() {
        return procedurecode;
    }

    public void setProcedurecode(String procedurecode) {
        this.procedurecode = procedurecode;
    }

    public String getShortcomment() {
        return shortcomment;
    }

    public void setShortcomment(String shortcomment) {
        this.shortcomment = shortcomment;
    }

    /*@XmlTransient
    public Collection<ActivityAIPC> getActivityCollection() {
        return activityCollection;
    }

    public void setActivityCollection(Collection<ActivityAIPC> activityCollection) {
        this.activityCollection = activityCollection;
    }*/

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (procedurecodeser != null ? procedurecodeser.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Procedure)) {
            return false;
        }
        Procedure other = (Procedure) object;
        if ((this.procedurecodeser == null && other.procedurecodeser != null) || (this.procedurecodeser != null && !this.procedurecodeser.equals(other.procedurecodeser))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "edu.umm.radonc.ca_dash.model.Procedure[ procedurecodeser=" + procedurecodeser + " ]";
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Integer getDepartmentser() {
        return departmentser;
    }

    public void setDepartmentser(Integer departmentser) {
        this.departmentser = departmentser;
    }
    
}
