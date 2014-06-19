/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.umm.radonc.ca_dash.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author mmcgrath
 */
@Entity
@Table(name = "activityinstance")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Activityinstance.findAll", query = "SELECT a FROM Activityinstance a"),
    @NamedQuery(name = "Activityinstance.findByActivityinstanceser", query = "SELECT a FROM Activityinstance a WHERE a.activityinstanceser = :activityinstanceser"),
    @NamedQuery(name = "Activityinstance.findByObjectstatus", query = "SELECT a FROM Activityinstance a WHERE a.objectstatus = :objectstatus"),
    @NamedQuery(name = "Activityinstance.findByDuration", query = "SELECT a FROM Activityinstance a WHERE a.duration = :duration"),
    @NamedQuery(name = "Activityinstance.findByHstrydatetime", query = "SELECT a FROM Activityinstance a WHERE a.hstrydatetime = :hstrydatetime")})
public class Activityinstance implements Serializable {
    @Column(name = "activityser")
    private Integer activityser;
    @Column(name = "departmentser")
    private Integer departmentser;
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "activityinstanceser")
    private Integer activityinstanceser;
    @Size(max = 25)
    @Column(name = "objectstatus")
    private String objectstatus;
    @Column(name = "duration")
    private Integer duration;
    @Column(name = "hstrydatetime")
    @Temporal(TemporalType.DATE)
    private Date hstrydatetime;
    /*@JoinColumn(name = "activityser", referencedColumnName = "activityser")
    @ManyToOne
    private Activity activityser;
    @JoinColumn(name = "departmentser", referencedColumnName = "departmentser")
    @ManyToOne
    private Department departmentser;*/
    @OneToMany(mappedBy = "activityinstanceser")
    private Collection<Activitycapture> activitycaptureCollection;

    public Activityinstance() {
    }

    public Activityinstance(Integer activityinstanceser) {
        this.activityinstanceser = activityinstanceser;
    }

    public Integer getActivityinstanceser() {
        return activityinstanceser;
    }

    public void setActivityinstanceser(Integer activityinstanceser) {
        this.activityinstanceser = activityinstanceser;
    }

    public String getObjectstatus() {
        return objectstatus;
    }

    public void setObjectstatus(String objectstatus) {
        this.objectstatus = objectstatus;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Date getHstrydatetime() {
        return hstrydatetime;
    }

    public void setHstrydatetime(Date hstrydatetime) {
        this.hstrydatetime = hstrydatetime;
    }

    /*public Activity getActivityser() {
        return activityser;
    }

    public void setActivityser(Activity activityser) {
        this.activityser = activityser;
    }

    public Department getDepartmentser() {
        return departmentser;
    }

    public void setDepartmentser(Department departmentser) {
        this.departmentser = departmentser;
    }*/

    @XmlTransient
    public Collection<Activitycapture> getActivitycaptureCollection() {
        return activitycaptureCollection;
    }

    public void setActivitycaptureCollection(Collection<Activitycapture> activitycaptureCollection) {
        this.activitycaptureCollection = activitycaptureCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (activityinstanceser != null ? activityinstanceser.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Activityinstance)) {
            return false;
        }
        Activityinstance other = (Activityinstance) object;
        if ((this.activityinstanceser == null && other.activityinstanceser != null) || (this.activityinstanceser != null && !this.activityinstanceser.equals(other.activityinstanceser))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "edu.umm.radonc.ca_dash.model.Activityinstance[ activityinstanceser=" + activityinstanceser + " ]";
    }

    public Integer getActivityser() {
        return activityser;
    }

    public void setActivityser(Integer activityser) {
        this.activityser = activityser;
    }

    public Integer getDepartmentser() {
        return departmentser;
    }

    public void setDepartmentser(Integer departmentser) {
        this.departmentser = departmentser;
    }
    
}
