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
@Table(name = "activity")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Activity.findAll", query = "SELECT a FROM Activity a"),
    @NamedQuery(name = "Activity.findByActivityser", query = "SELECT a FROM Activity a WHERE a.activityser = :activityser"),
    @NamedQuery(name = "Activity.findByActivitycode", query = "SELECT a FROM Activity a WHERE a.activitycode = :activitycode"),
    @NamedQuery(name = "Activity.findByObjectstatus", query = "SELECT a FROM Activity a WHERE a.objectstatus = :objectstatus")})
public class Activity implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "activityser")
    private Integer activityser;
    @Size(max = 255)
    @Column(name = "activitycode")
    private String activitycode;
    @Size(max = 255)
    @Column(name = "objectstatus")
    private String objectstatus;
   // @OneToMany(mappedBy = "activityser")
    //private Collection<Activityinstance> activityinstanceCollection;

    public Activity() {
    }

    public Activity(Integer activityser) {
        this.activityser = activityser;
    }

    public Integer getActivityser() {
        return activityser;
    }

    public void setActivityser(Integer activityser) {
        this.activityser = activityser;
    }

    public String getActivitycode() {
        return activitycode;
    }

    public void setActivitycode(String activitycode) {
        this.activitycode = activitycode;
    }

    public String getObjectstatus() {
        return objectstatus;
    }

    public void setObjectstatus(String objectstatus) {
        this.objectstatus = objectstatus;
    }

    /*@XmlTransient
    public Collection<Activityinstance> getActivityinstanceCollection() {
        return activityinstanceCollection;
    }

    public void setActivityinstanceCollection(Collection<Activityinstance> activityinstanceCollection) {
        this.activityinstanceCollection = activityinstanceCollection;
    }*/

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (activityser != null ? activityser.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Activity)) {
            return false;
        }
        Activity other = (Activity) object;
        if ((this.activityser == null && other.activityser != null) || (this.activityser != null && !this.activityser.equals(other.activityser))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "edu.umm.radonc.ca_dash.model.Activity[ activityser=" + activityser + " ]";
    }
    
}
