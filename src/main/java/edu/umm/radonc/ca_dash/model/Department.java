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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Table(name = "department")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Department.findAll", query = "SELECT d FROM Department d"),
    @NamedQuery(name = "Department.findByDepartmentser", query = "SELECT d FROM Department d WHERE d.departmentser = :departmentser"),
    @NamedQuery(name = "Department.findByDepartmentname", query = "SELECT d FROM Department d WHERE d.departmentname = :departmentname")})
public class Department implements Serializable {
    @Column(name = "hospitalser")
    private Integer hospitalser;

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "departmentser")
    private Integer departmentser;
    @Size(max = 255)
    @Column(name = "departmentname")
    private String departmentname;
    @OneToMany(mappedBy = "departmentser")
    private Collection<ActivityAIPC> activityCollection;
    @OneToMany(mappedBy = "departmentser")
    private Collection<Activityinstance> activityinstanceCollection;
    @OneToMany(mappedBy = "departmentser")
    private Collection<Activitycapture> activitycaptureCollection;
    
    
    //@JoinColumn(name = "hospitalser", referencedColumnName = "hospitalser")
    //@ManyToOne
    //private Hospital hospitalser;

    public Department() {
    }

    public Department(Integer departmentser) {
        this.departmentser = departmentser;
    }

    public Integer getDepartmentser() {
        return departmentser;
    }

    public void setDepartmentser(Integer departmentser) {
        this.departmentser = departmentser;
    }

    public String getDepartmentname() {
        return departmentname;
    }

    public void setDepartmentname(String departmentname) {
        this.departmentname = departmentname;
    }

    /*@XmlTransient
    public Collection<ActivityAIPC> getActivityCollection() {
        return activityCollection;
    }

    public void setActivityCollection(Collection<ActivityAIPC> activityCollection) {
        this.activityCollection = activityCollection;
    }

    public Hospital getHospital() {
        return Hospital hospitalser;
    }

    public void setHospitalser(Hospital hospitalser) {
        this.hospitalser = hospitalser;
    }*/

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (departmentser != null ? departmentser.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Department)) {
            return false;
        }
        Department other = (Department) object;
        if ((this.departmentser == null && other.departmentser != null) || (this.departmentser != null && !this.departmentser.equals(other.departmentser))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "edu.umm.radonc.ca_dash.model.Department[ departmentser=" + departmentser + " ]";
    }

    /* public Integer getHospitalser() {
        return hospitalser;
    }

    public void setHospitalser(Integer hospitalser) {
        this.hospitalser = hospitalser;
    } */

    @XmlTransient
    public Collection<Activityinstance> getActivityinstanceCollection() {
        return activityinstanceCollection;
    }

    public void setActivityinstanceCollection(Collection<Activityinstance> activityinstanceCollection) {
        this.activityinstanceCollection = activityinstanceCollection;
    }

    @XmlTransient
    public Collection<Activitycapture> getActivitycaptureCollection() {
        return activitycaptureCollection;
    }

    public void setActivitycaptureCollection(Collection<Activitycapture> activitycaptureCollection) {
        this.activitycaptureCollection = activitycaptureCollection;
    }

    public Integer getHospitalser() {
        return hospitalser;
    }

    public void setHospitalser(Integer hospitalser) {
        this.hospitalser = hospitalser;
    }
    
}
