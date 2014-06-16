/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.umm.radonc.ca_dash.controllers;

import java.io.IOException;
import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;

/**
 *
 * @author mmcgrath
 */
@Named("navigationController")
@SessionScoped
public class NavigationController implements Serializable {
    
    private String selectedReport;

    public String getSelectedReport() {
        return selectedReport;
    }

    public void setSelectedReport(String selectedReport) {
        this.selectedReport = selectedReport;
    }
    
    public void redirectReportSelector() {
        String redirect="";
        switch(selectedReport) {
            case "histograms":
                redirect = "faces/histograms.xhtml";
                break;
            case "trends":
                redirect = "faces/trends.xhtml";
                break;
        }
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect(redirect);
        } catch (IOException e) {
        }
    }
    
    
    
}
