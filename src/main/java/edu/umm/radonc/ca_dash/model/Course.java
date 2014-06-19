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
@Table(name = "course")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Course.findAll", query = "SELECT c FROM Course c"),
    @NamedQuery(name = "Course.findByCourseser", query = "SELECT c FROM Course c WHERE c.courseser = :courseser"),
    @NamedQuery(name = "Course.findByPatientser", query = "SELECT c FROM Course c WHERE c.patientser = :patientser"),
    @NamedQuery(name = "Course.findByIntent", query = "SELECT c FROM Course c WHERE c.intent = :intent"),
    @NamedQuery(name = "Course.findByClinicalstatus", query = "SELECT c FROM Course c WHERE c.clinicalstatus = :clinicalstatus"),
    @NamedQuery(name = "Course.findByHstrydatetime", query = "SELECT c FROM Course c WHERE c.hstrydatetime = :hstrydatetime")})
public class Course implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "courseser")
    private Integer courseser;
    @Column(name = "patientser")
    private Integer patientser;
    @Size(max = 255)
    @Column(name = "intent")
    private String intent;
    @Size(max = 255)
    @Column(name = "clinicalstatus")
    private String clinicalstatus;
    @Column(name = "hstrydatetime")
    @Temporal(TemporalType.DATE)
    private Date hstrydatetime;
    @OneToMany(mappedBy = "courseser")
    private Collection<Activitycapture> activitycaptureCollection;

    public Course() {
    }

    public Course(Integer courseser) {
        this.courseser = courseser;
    }

    public Integer getCourseser() {
        return courseser;
    }

    public void setCourseser(Integer courseser) {
        this.courseser = courseser;
    }

    public Integer getPatientser() {
        return patientser;
    }

    public void setPatientser(Integer patientser) {
        this.patientser = patientser;
    }

    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }

    public String getClinicalstatus() {
        return clinicalstatus;
    }

    public void setClinicalstatus(String clinicalstatus) {
        this.clinicalstatus = clinicalstatus;
    }

    public Date getHstrydatetime() {
        return hstrydatetime;
    }

    public void setHstrydatetime(Date hstrydatetime) {
        this.hstrydatetime = hstrydatetime;
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
        hash += (courseser != null ? courseser.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Course)) {
            return false;
        }
        Course other = (Course) object;
        if ((this.courseser == null && other.courseser != null) || (this.courseser != null && !this.courseser.equals(other.courseser))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "edu.umm.radonc.ca_dash.model.Course[ courseser=" + courseser + " ]";
    }
    
}
