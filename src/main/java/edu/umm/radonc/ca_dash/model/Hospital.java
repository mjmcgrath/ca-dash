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
@Table(name = "hospital")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Hospital.findAll", query = "SELECT h FROM Hospital h"),
    @NamedQuery(name = "Hospital.findByHospitalser", query = "SELECT h FROM Hospital h WHERE h.hospitalser = :hospitalser"),
    @NamedQuery(name = "Hospital.findByHospitalname", query = "SELECT h FROM Hospital h WHERE h.hospitalname = :hospitalname")})
public class Hospital implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "hospitalser")
    private Integer hospitalser;
    @Size(max = 255)
    @Column(name = "hospitalname")
    private String hospitalname;
    //@OneToMany(mappedBy = "hospitalser")
    //private Collection<Department> departmentCollection;

    public Hospital() {
    }

    public Hospital(Integer hospitalser) {
        this.hospitalser = hospitalser;
    }

    public Integer getHospitalser() {
        return hospitalser;
    }

    public void setHospitalser(Integer hospitalser) {
        this.hospitalser = hospitalser;
    }

    public String getHospitalname() {
        return hospitalname;
    }

    public void setHospitalname(String hospitalname) {
        this.hospitalname = hospitalname;
    }

    /*@XmlTransient
    public Collection<Department> getDepartmentCollection() {
        return departmentCollection;
    }

    public void setDepartmentCollection(Collection<Department> departmentCollection) {
        this.departmentCollection = departmentCollection;
    }*/

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (hospitalser != null ? hospitalser.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Hospital)) {
            return false;
        }
        Hospital other = (Hospital) object;
        if ((this.hospitalser == null && other.hospitalser != null) || (this.hospitalser != null && !this.hospitalser.equals(other.hospitalser))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "edu.umm.radonc.ca_dash.model.Hospital[ hospitalser=" + hospitalser + " ]";
    }
    
}
