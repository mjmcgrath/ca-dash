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
@Table(name = "tx_flat_v2")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TxInstance.findAll", query = "SELECT t FROM TxInstance t"),
    @NamedQuery(name = "TxInstance.findByActivityinstanceser", query = "SELECT t FROM TxInstance t WHERE t.activityinstanceser = :activityinstanceser"),
    @NamedQuery(name = "TxInstance.findByActivitycaptureser", query = "SELECT t FROM TxInstance t WHERE t.activitycaptureser = :activitycaptureser"),
    @NamedQuery(name = "TxInstance.findByPatientser", query = "SELECT t FROM TxInstance t WHERE t.patientser = :patientser"),
    @NamedQuery(name = "TxInstance.findByCompleted", query = "SELECT t FROM TxInstance t WHERE t.completed = :completed"),
    @NamedQuery(name = "TxInstance.findByCodetype", query = "SELECT t FROM TxInstance t WHERE t.codetype = :codetype"),
    @NamedQuery(name = "TxInstance.findByCpt", query = "SELECT t FROM TxInstance t WHERE t.cpt = :cpt"),
    @NamedQuery(name = "TxInstance.findByShortcomment", query = "SELECT t FROM TxInstance t WHERE t.shortcomment = :shortcomment"),
    @NamedQuery(name = "TxInstance.findByComment", query = "SELECT t FROM TxInstance t WHERE t.comment = :comment"),
    @NamedQuery(name = "TxInstance.findByAriaPm", query = "SELECT t FROM TxInstance t WHERE t.ariaPm = :ariaPm"),
    @NamedQuery(name = "TxInstance.findByMachine", query = "SELECT t FROM TxInstance t WHERE t.machine = :machine"),
    @NamedQuery(name = "TxInstance.findByDoctor", query = "SELECT t FROM TxInstance t WHERE t.doctor = :doctor"),
    @NamedQuery(name = "TxInstance.findByHospitalser", query = "SELECT t FROM TxInstance t WHERE t.hospitalser = :hospitalser"),
    @NamedQuery(name = "TxInstance.findByHospital", query = "SELECT t FROM TxInstance t WHERE t.hospital = :hospital")})
public class TxInstance implements Serializable {
    private static final long serialVersionUID = 1L;
     @Id
    @Column(name = "activityinstanceser")
    private Integer activityinstanceser;
    @Column(name = "activitycaptureser")
    private Integer activitycaptureser;
    @Column(name = "patientser")
    private Integer patientser;
    @Column(name = "completed")
    @Temporal(TemporalType.TIMESTAMP)
    private Date completed;
    @Size(max = 255)
    @Column(name = "codetype")
    private String codetype;
    @Size(max = 255)
    @Column(name = "cpt")
    private String cpt;
    @Size(max = 255)
    @Column(name = "shortcomment")
    private String shortcomment;
    @Size(max = 255)
    @Column(name = "comment")
    private String comment;
    @Size(max = 255)
    @Column(name = "aria_pm")
    private String ariaPm;
    @Size(max = 64)
    @Column(name = "machine")
    private String machine;
    @Size(max = 2147483647)
    @Column(name = "doctor")
    private String doctor;
    @Column(name = "hospitalser")
    private Integer hospitalser;
    @Size(max = 255)
    @Column(name = "hospital")
    private String hospital;

    public TxInstance() {
    }

    public Integer getActivityinstanceser() {
        return activityinstanceser;
    }

    public void setActivityinstanceser(Integer activityinstanceser) {
        this.activityinstanceser = activityinstanceser;
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

    public Date getCompleted() {
        return completed;
    }

    public void setCompleted(Date completed) {
        this.completed = completed;
    }

    public String getCodetype() {
        return codetype;
    }

    public void setCodetype(String codetype) {
        this.codetype = codetype;
    }

    public String getCpt() {
        return cpt;
    }

    public void setCpt(String cpt) {
        this.cpt = cpt;
    }

    public String getShortcomment() {
        return shortcomment;
    }

    public void setShortcomment(String shortcomment) {
        this.shortcomment = shortcomment;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getAriaPm() {
        return ariaPm;
    }

    public void setAriaPm(String ariaPm) {
        this.ariaPm = ariaPm;
    }

    public String getMachine() {
        return machine;
    }

    public void setMachine(String machine) {
        this.machine = machine;
    }

    public String getDoctor() {
        return doctor;
    }

    public void setDoctor(String doctor) {
        this.doctor = doctor;
    }

    public Integer getHospitalser() {
        return hospitalser;
    }

    public void setHospitalser(Integer hospitalser) {
        this.hospitalser = hospitalser;
    }

    public String getHospital() {
        return hospital;
    }

    public void setHospital(String hospital) {
        this.hospital = hospital;
    }

    
    
}
