/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.umm.radonc.ca_dash.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author mmcgrath
 */
@Entity
@Table(name = "activitycapture")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Activitycapture.findAll", query = "SELECT a FROM Activitycapture a"),
    @NamedQuery(name = "Activitycapture.findByActivitycaptureser", query = "SELECT a FROM Activitycapture a WHERE a.activitycaptureser = :activitycaptureser"),
    @NamedQuery(name = "Activitycapture.findByAttendingoncologistser", query = "SELECT a FROM Activitycapture a WHERE a.attendingoncologistser = :attendingoncologistser"),
    @NamedQuery(name = "Activitycapture.findByHstrydatetime", query = "SELECT a FROM Activitycapture a WHERE a.hstrydatetime = :hstrydatetime")})
public class Activitycapture implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "activitycaptureser")
    private Integer activitycaptureser;
    @JoinColumn(name = "attendingoncologistser", referencedColumnName = "resourceser")
    @ManyToOne
    private Doctor attendingoncologistser;
    @Column(name = "hstrydatetime")
    @Temporal(TemporalType.DATE)
    private Date hstrydatetime;
    @JoinColumn(name = "activityinstanceser", referencedColumnName = "activityinstanceser")
    @ManyToOne
    private Activityinstance activityinstanceser;
    @JoinColumn(name = "courseser", referencedColumnName = "courseser")
    @ManyToOne
    private Course courseser;
    @JoinColumn(name = "departmentser", referencedColumnName = "departmentser")
    @ManyToOne
    private Department departmentser;

    public Activitycapture() {
    }

    public Activitycapture(Integer activitycaptureser) {
        this.activitycaptureser = activitycaptureser;
    }

    public Integer getActivitycaptureser() {
        return activitycaptureser;
    }

    public void setActivitycaptureser(Integer activitycaptureser) {
        this.activitycaptureser = activitycaptureser;
    }

    public Doctor getAttendingoncologistser() {
        return attendingoncologistser;
    }

    public void setAttendingoncologistser(Doctor attendingoncologistser) {
        this.attendingoncologistser = attendingoncologistser;
    }

    public Date getHstrydatetime() {
        return hstrydatetime;
    }

    public void setHstrydatetime(Date hstrydatetime) {
        this.hstrydatetime = hstrydatetime;
    }

    public Activityinstance getActivityinstanceser() {
        return activityinstanceser;
    }

    public void setActivityinstanceser(Activityinstance activityinstanceser) {
        this.activityinstanceser = activityinstanceser;
    }

    public Course getCourseser() {
        return courseser;
    }

    public void setCourseser(Course courseser) {
        this.courseser = courseser;
    }

    public Department getDepartmentser() {
        return departmentser;
    }

    public void setDepartmentser(Department departmentser) {
        this.departmentser = departmentser;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (activitycaptureser != null ? activitycaptureser.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Activitycapture)) {
            return false;
        }
        Activitycapture other = (Activitycapture) object;
        if ((this.activitycaptureser == null && other.activitycaptureser != null) || (this.activitycaptureser != null && !this.activitycaptureser.equals(other.activitycaptureser))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "edu.umm.radonc.ca_dash.model.Activitycapture[ activitycaptureser=" + activitycaptureser + " ]";
    }
    
}
