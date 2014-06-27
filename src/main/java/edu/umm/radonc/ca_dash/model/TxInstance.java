/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.umm.radonc.ca_dash.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author mmcgrath
 */
@Entity
@Table(name = "tx_flat_q")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TxInstance.findAll", query = "SELECT t FROM TxInstance t"),
    @NamedQuery(name = "TxInstance.findByActivityinstanceser", query = "SELECT t FROM TxInstance t WHERE t.activityinstanceser = :activityinstanceser"),
    @NamedQuery(name = "TxInstance.findByObjectstatus", query = "SELECT t FROM TxInstance t WHERE t.objectstatus = :objectstatus"),
    @NamedQuery(name = "TxInstance.findByActivitycaptureser", query = "SELECT t FROM TxInstance t WHERE t.activitycaptureser = :activitycaptureser"),
    @NamedQuery(name = "TxInstance.findByPatientser", query = "SELECT t FROM TxInstance t WHERE t.patientser = :patientser"),
    @NamedQuery(name = "TxInstance.findByActivitycode", query = "SELECT t FROM TxInstance t WHERE t.activitycode = :activitycode"),
    @NamedQuery(name = "TxInstance.findByProcedurecode", query = "SELECT t FROM TxInstance t WHERE t.procedurecode = :procedurecode"),
    @NamedQuery(name = "TxInstance.findByShortcomment", query = "SELECT t FROM TxInstance t WHERE t.shortcomment = :shortcomment"),
    @NamedQuery(name = "TxInstance.findByHospitalname", query = "SELECT t FROM TxInstance t WHERE t.hospitalname = :hospitalname"),
    @NamedQuery(name = "TxInstance.findByHospitalser", query = "SELECT t FROM TxInstance t WHERE t.hospitalser = :hospitalser"),
    @NamedQuery(name = "TxInstance.findByPhysId", query = "SELECT t FROM TxInstance t WHERE t.physId = :physId"),
    @NamedQuery(name = "TxInstance.findByPhys", query = "SELECT t FROM TxInstance t WHERE t.phys = :phys"),
    @NamedQuery(name = "TxInstance.findByDt", query = "SELECT t FROM TxInstance t WHERE t.dt = :dt")})
public class TxInstance implements Serializable {
    private static final long serialVersionUID = 1L;
     @Id
    @Column(name = "activityinstanceser")
    private Integer activityinstanceser;
    @Size(max = 25)
    @Column(name = "objectstatus")
    private String objectstatus;
    @Column(name = "activitycaptureser")
    private Integer activitycaptureser;
    @Column(name = "patientser")
    private Integer patientser;
    @Size(max = 255)
    @Column(name = "activitycode")
    private String activitycode;
    @Size(max = 255)
    @Column(name = "procedurecode")
    private String procedurecode;
    @Size(max = 255)
    @Column(name = "shortcomment")
    private String shortcomment;
    @Size(max = 255)
    @Column(name = "hospitalname")
    private String hospitalname;
    @Column(name = "hospitalser")
    private Integer hospitalser;
    @Column(name = "phys_id")
    private Integer physId;
    @Size(max = 2147483647)
    @Column(name = "phys")
    private String phys;
    @Column(name = "dt")
    @Temporal(TemporalType.DATE)
    private Date dt;

    public TxInstance() {
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

    public Integer getActivitycaptureser() {
        return activitycaptureser;
    }

    public void setActivitycaptureser(Integer activitycaptureser) {
        this.activitycaptureser = activitycaptureser;
    }

    public Integer getPatientser() {
        return patientser;
    }

    public void setPatientser(Integer patientser) {
        this.patientser = patientser;
    }

    public String getActivitycode() {
        return activitycode;
    }

    public void setActivitycode(String activitycode) {
        this.activitycode = activitycode;
    }

    public String getProcedurecode() {
        return procedurecode;
    }

    public void setProcedurecode(String procedurecode) {
        this.procedurecode = procedurecode;
    }

    public String getShortcomment() {
        return shortcomment;
    }

    public void setShortcomment(String shortcomment) {
        this.shortcomment = shortcomment;
    }

    public String getHospitalname() {
        return hospitalname;
    }

    public void setHospitalname(String hospitalname) {
        this.hospitalname = hospitalname;
    }

    public Integer getHospitalser() {
        return hospitalser;
    }

    public void setHospitalser(Integer hospitalser) {
        this.hospitalser = hospitalser;
    }

    public Integer getPhysId() {
        return physId;
    }

    public void setPhysId(Integer physId) {
        this.physId = physId;
    }

    public String getPhys() {
        return phys;
    }

    public void setPhys(String phys) {
        this.phys = phys;
    }

    public Date getDt() {
        return dt;
    }

    public void setDt(Date dt) {
        this.dt = dt;
    }
    
}
