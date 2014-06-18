/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.umm.radonc.ca_dash.model;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author mmcgrath
 */
@Entity
@Table(name = "machine")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Machine.findAll", query = "SELECT m FROM Machine m"),
    @NamedQuery(name = "Machine.findByResourceser", query = "SELECT m FROM Machine m WHERE m.resourceser = :resourceser"),
    @NamedQuery(name = "Machine.findByMachineid", query = "SELECT m FROM Machine m WHERE m.machineid = :machineid"),
    @NamedQuery(name = "Machine.findByMachinename", query = "SELECT m FROM Machine m WHERE m.machinename = :machinename"),
    @NamedQuery(name = "Machine.findByMachinetype", query = "SELECT m FROM Machine m WHERE m.machinetype = :machinetype"),
    @NamedQuery(name = "Machine.findByMachinemodel", query = "SELECT m FROM Machine m WHERE m.machinemodel = :machinemodel"),
    @NamedQuery(name = "Machine.findByOperationstatus", query = "SELECT m FROM Machine m WHERE m.operationstatus = :operationstatus")})
public class Machine implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "resourceser")
    private Integer resourceser;
    @Size(max = 16)
    @Column(name = "machineid")
    private String machineid;
    @Size(max = 64)
    @Column(name = "machinename")
    private String machinename;
    @Size(max = 30)
    @Column(name = "machinetype")
    private String machinetype;
    @Size(max = 64)
    @Column(name = "machinemodel")
    private String machinemodel;
    @Size(max = 32)
    @Column(name = "operationstatus")
    private String operationstatus;

    public Machine() {
    }

    public Machine(Integer resourceser) {
        this.resourceser = resourceser;
    }

    public Integer getResourceser() {
        return resourceser;
    }

    public void setResourceser(Integer resourceser) {
        this.resourceser = resourceser;
    }

    public String getMachineid() {
        return machineid;
    }

    public void setMachineid(String machineid) {
        this.machineid = machineid;
    }

    public String getMachinename() {
        return machinename;
    }

    public void setMachinename(String machinename) {
        this.machinename = machinename;
    }

    public String getMachinetype() {
        return machinetype;
    }

    public void setMachinetype(String machinetype) {
        this.machinetype = machinetype;
    }

    public String getMachinemodel() {
        return machinemodel;
    }

    public void setMachinemodel(String machinemodel) {
        this.machinemodel = machinemodel;
    }

    public String getOperationstatus() {
        return operationstatus;
    }

    public void setOperationstatus(String operationstatus) {
        this.operationstatus = operationstatus;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (resourceser != null ? resourceser.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Machine)) {
            return false;
        }
        Machine other = (Machine) object;
        if ((this.resourceser == null && other.resourceser != null) || (this.resourceser != null && !this.resourceser.equals(other.resourceser))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "edu.umm.radonc.ca_dash.model.Machine[ resourceser=" + resourceser + " ]";
    }
    
}
