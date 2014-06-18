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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author mmcgrath
 */
@Entity
@Table(name = "attendee")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Attendee.findAll", query = "SELECT a FROM Attendee a"),
    @NamedQuery(name = "Attendee.findByAttendeeser", query = "SELECT a FROM Attendee a WHERE a.attendeeser = :attendeeser"),
    @NamedQuery(name = "Attendee.findByActivityinstanceser", query = "SELECT a FROM Attendee a WHERE a.activityinstanceser = :activityinstanceser"),
    @NamedQuery(name = "Attendee.findByHstrydatetime", query = "SELECT a FROM Attendee a WHERE a.hstrydatetime = :hstrydatetime"),
    @NamedQuery(name = "Attendee.findByObjectstatus", query = "SELECT a FROM Attendee a WHERE a.objectstatus = :objectstatus"),
    @NamedQuery(name = "Attendee.findByPrimaryflag", query = "SELECT a FROM Attendee a WHERE a.primaryflag = :primaryflag"),
    @NamedQuery(name = "Attendee.findByResourceser", query = "SELECT a FROM Attendee a WHERE a.resourceser = :resourceser")})
public class Attendee implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "attendeeser")
    private Integer attendeeser;
    @Column(name = "activityinstanceser")
    private Integer activityinstanceser;
    @Column(name = "hstrydatetime")
    @Temporal(TemporalType.DATE)
    private Date hstrydatetime;
    @Size(max = 16)
    @Column(name = "objectstatus")
    private String objectstatus;
    @Column(name = "primaryflag")
    private Boolean primaryflag;
    @Column(name = "resourceser")
    private Integer resourceser;

    public Attendee() {
    }

    public Attendee(Integer attendeeser) {
        this.attendeeser = attendeeser;
    }

    public Integer getAttendeeser() {
        return attendeeser;
    }

    public void setAttendeeser(Integer attendeeser) {
        this.attendeeser = attendeeser;
    }

    public Integer getActivityinstanceser() {
        return activityinstanceser;
    }

    public void setActivityinstanceser(Integer activityinstanceser) {
        this.activityinstanceser = activityinstanceser;
    }

    public Date getHstrydatetime() {
        return hstrydatetime;
    }

    public void setHstrydatetime(Date hstrydatetime) {
        this.hstrydatetime = hstrydatetime;
    }

    public String getObjectstatus() {
        return objectstatus;
    }

    public void setObjectstatus(String objectstatus) {
        this.objectstatus = objectstatus;
    }

    public Boolean getPrimaryflag() {
        return primaryflag;
    }

    public void setPrimaryflag(Boolean primaryflag) {
        this.primaryflag = primaryflag;
    }

    public Integer getResourceser() {
        return resourceser;
    }

    public void setResourceser(Integer resourceser) {
        this.resourceser = resourceser;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (attendeeser != null ? attendeeser.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Attendee)) {
            return false;
        }
        Attendee other = (Attendee) object;
        if ((this.attendeeser == null && other.attendeeser != null) || (this.attendeeser != null && !this.attendeeser.equals(other.attendeeser))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "edu.umm.radonc.ca_dash.model.Attendee[ attendeeser=" + attendeeser + " ]";
    }
    
}
