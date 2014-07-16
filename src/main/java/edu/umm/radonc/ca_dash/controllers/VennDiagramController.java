/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.umm.radonc.ca_dash.controllers;

import edu.umm.radonc.ca_dash.model.TxInstanceFacade;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.apache.commons.math.stat.descriptive.SynchronizedDescriptiveStatistics;
import org.json.*;
import org.primefaces.model.chart.PieChartModel;

/**
 *
 * @author mmcgrath
 */
@Named("vennDiagramController")
@ViewScoped
public class VennDiagramController {
    
    private JSONObject vennData;
    private Date startDate;
    private Date endDate;
    private DateFormat df;
    private Long selectedFacility;
    List<String> selectedFilters;
    
    @EJB
    private edu.umm.radonc.ca_dash.model.TxInstanceFacade ejbFacade;
    
    public VennDiagramController() {
        vennData = new JSONObject();
    }

    public JSONObject getVennData() {
        return vennData;
    }

    public void setVennData(JSONObject vennData) {
        this.vennData = vennData;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public DateFormat getDf() {
        return df;
    }

    public void setDf(DateFormat df) {
        this.df = df;
    }

    public Long getSelectedFacility() {
        return selectedFacility;
    }

    public void setSelectedFacility(Long selectedFacility) {
        this.selectedFacility = selectedFacility;
    }

    public List<String> getSelectedFilters() {
        return selectedFilters;
    }

    public void setSelectedFilters(List<String> selectedFilters) {
        this.selectedFilters = selectedFilters;
    }
    
    private TxInstanceFacade getFacade() {
        return ejbFacade;
    }
    
    public void generateVennData(){
        JSONArray sets = new JSONArray();
        JSONArray overlaps = new JSONArray();
        //Get IMRT count
        List<Object[]> imrtCt = getFacade().getDailyCounts(startDate, endDate, selectedFacility, "imrt", true, false);
        //Get IGRT count
        List<Object[]> igrtCt = getFacade().getDailyCounts(startDate, endDate, selectedFacility, "igrt", true, false);
        //Get IMRT & IGRT count FIXME FIXME
        List<Object[]> overlapCt = getFacade().getDailyCounts(startDate, endDate, selectedFacility, "overlap", true, false);
        //Calc overlap here
        
        
        vennData.put("sets", sets);
        vennData.put("overlaps", overlaps);
    }
}
