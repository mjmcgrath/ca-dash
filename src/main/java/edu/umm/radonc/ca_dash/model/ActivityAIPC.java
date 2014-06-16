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
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SqlResultSetMapping;
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
@Table(name = "actinstproccode")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ActivityAIPC.findAll", query = "SELECT a FROM Activity a"),
    @NamedQuery(name = "ActivityAIPC.findByActinstproccodeser", query = "SELECT a FROM ActivityAIPC a WHERE a.actinstproccodeser = :actinstproccodeser"),
    @NamedQuery(name = "ActivityAIPC.findByCompleteddatetime", query = "SELECT a FROM ActivityAIPC a WHERE a.completeddatetime = :completeddatetime"),
    @NamedQuery(name = "ActivityAIPC.findByFromdateofservice", query = "SELECT a FROM ActivityAIPC a WHERE a.fromdateofservice = :fromdateofservice"),
    @NamedQuery(name = "ActivityAIPC.findByTodateofservice", query = "SELECT a FROM ActivityAIPC a WHERE a.todateofservice = :todateofservice")})
/*@NamedNativeQuery(name = "Activity.getDailyActivities", query = "SELECT p.shortcomment, p.procedurecode, COUNT (p.procedurecode) AS activityCount" + 
                "FROM actinstproccode a, procedurecode p " + 
                "WHERE p.procedurecodeser = a.procedurecodeser AND a.fromdateofservice = ? " +
                "AND p.procedurecode <> '00000'" +
                "GROUP BY p.procedurecode, p.shortcomment ORDER BY p.procedurecode ASC")*/

@SqlResultSetMapping(name="dailyActivities",
        classes={
            @ConstructorResult(
                    targetClass=edu.umm.radonc.ca_dash.model.ActivityCount.class,
                    columns={
                        @ColumnResult(name="shortcomment", type=String.class),
                        @ColumnResult(name="procedurecode", type=String.class),
                        @ColumnResult(name="activityCount", type=Long.class),
                    }
            )
                
        })

public class ActivityAIPC implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "actinstproccodeser")
    private Integer actinstproccodeser;
    @Column(name = "completeddatetime")
    @Temporal(TemporalType.DATE)
    private Date completeddatetime;
    @Column(name = "fromdateofservice")
    @Temporal(TemporalType.DATE)
    private Date fromdateofservice;
    @Column(name = "todateofservice")
    @Temporal(TemporalType.DATE)
    private Date todateofservice;
    @JoinColumn(name = "departmentser", referencedColumnName = "departmentser")
    @ManyToOne
    private Department departmentser;
    @JoinColumn(name = "procedurecodeser", referencedColumnName = "procedurecodeser")
    @ManyToOne
    private Procedure procedurecodeser;

    public ActivityAIPC() {
    }

    public ActivityAIPC(Integer actinstproccodeser) {
        this.actinstproccodeser = actinstproccodeser;
    }

    public Integer getActinstproccodeser() {
        return actinstproccodeser;
    }

    public void setActinstproccodeser(Integer actinstproccodeser) {
        this.actinstproccodeser = actinstproccodeser;
    }

    public Date getCompleteddatetime() {
        return completeddatetime;
    }

    public void setCompleteddatetime(Date completeddatetime) {
        this.completeddatetime = completeddatetime;
    }

    public Date getFromdateofservice() {
        return fromdateofservice;
    }

    public void setFromdateofservice(Date fromdateofservice) {
        this.fromdateofservice = fromdateofservice;
    }

    public Date getTodateofservice() {
        return todateofservice;
    }

    public void setTodateofservice(Date todateofservice) {
        this.todateofservice = todateofservice;
    }

    public Department getDepartmentser() {
        return departmentser;
    }

    public void setDepartmentser(Department departmentser) {
        this.departmentser = departmentser;
    }

    public Procedure getProcedurecodeser() {
        return procedurecodeser;
    }

    public void setProcedurecodeser(Procedure procedurecodeser) {
        this.procedurecodeser = procedurecodeser;
    }
    
    public void getActivityList() {
        
        
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (actinstproccodeser != null ? actinstproccodeser.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ActivityAIPC)) {
            return false;
        }
        ActivityAIPC other = (ActivityAIPC) object;
        if ((this.actinstproccodeser == null && other.actinstproccodeser != null) || (this.actinstproccodeser != null && !this.actinstproccodeser.equals(other.actinstproccodeser))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "edu.umm.radonc.ca_dash.model.ActivityAIPC[ actinstproccodeser=" + actinstproccodeser + " ]";
    }
    
}
