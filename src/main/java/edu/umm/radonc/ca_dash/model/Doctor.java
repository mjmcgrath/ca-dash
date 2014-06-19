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
@Table(name = "doctor")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Doctor.findAll", query = "SELECT d FROM Doctor d"),
    @NamedQuery(name = "Doctor.findByResourceser", query = "SELECT d FROM Doctor d WHERE d.resourceser = :resourceser"),
    @NamedQuery(name = "Doctor.findByLastname", query = "SELECT d FROM Doctor d WHERE d.lastname = :lastname"),
    @NamedQuery(name = "Doctor.findByFirstname", query = "SELECT d FROM Doctor d WHERE d.firstname = :firstname"),
    @NamedQuery(name = "Doctor.findByMiddlename", query = "SELECT d FROM Doctor d WHERE d.middlename = :middlename"),
    @NamedQuery(name = "Doctor.findByOncologistflag", query = "SELECT d FROM Doctor d WHERE d.oncologistflag = :oncologistflag")})
public class Doctor implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "resourceser")
    private Integer resourceser;
    @Size(max = 50)
    @Column(name = "lastname")
    private String lastname;
    @Size(max = 50)
    @Column(name = "firstname")
    private String firstname;
    @Size(max = 50)
    @Column(name = "middlename")
    private String middlename;
    @Column(name = "oncologistflag")
    private Boolean oncologistflag;
    @OneToMany(mappedBy = "attendingoncologistser")
    private Collection<Activitycapture> activitycaptureCollection;

    public Doctor() {
    }

    public Doctor(Integer resourceser) {
        this.resourceser = resourceser;
    }

    public Integer getResourceser() {
        return resourceser;
    }

    public void setResourceser(Integer resourceser) {
        this.resourceser = resourceser;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getMiddlename() {
        return middlename;
    }

    public void setMiddlename(String middlename) {
        this.middlename = middlename;
    }

    public Boolean getOncologistflag() {
        return oncologistflag;
    }

    public void setOncologistflag(Boolean oncologistflag) {
        this.oncologistflag = oncologistflag;
    }

    /*@XmlTransient
    public Collection<Activitycapture> getActivitycaptureCollection() {
        return activitycaptureCollection;
    }

    public void setActivitycaptureCollection(Collection<Activitycapture> activitycaptureCollection) {
        this.activitycaptureCollection = activitycaptureCollection;
    }*/

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (resourceser != null ? resourceser.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Doctor)) {
            return false;
        }
        Doctor other = (Doctor) object;
        if ((this.resourceser == null && other.resourceser != null) || (this.resourceser != null && !this.resourceser.equals(other.resourceser))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "edu.umm.radonc.ca_dash.model.Doctor[ resourceser=" + resourceser + " ]";
    }
    
}
