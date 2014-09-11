/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.umm.radonc.ca_dash.controllers;

import edu.umm.radonc.ca_dash.model.DoctorStats;
import edu.umm.radonc.ca_dash.model.FiscalDate;
import edu.umm.radonc.ca_dash.model.TxInstanceFacade;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.apache.commons.math.stat.descriptive.SynchronizedDescriptiveStatistics;
import org.json.JSONArray;
import org.primefaces.model.chart.PieChartModel;


/**
 *
 * @author michaelmcgrath
 */
@Named("pieChartController")
@SessionScoped
public class PieChartController implements Serializable{
    
    @EJB
    private edu.umm.radonc.ca_dash.model.TxInstanceFacade ejbFacade;
    
    @EJB
    private edu.umm.radonc.ca_dash.model.HospitalFacade hFacade;
    
    private Date startDate;
    private Date endDate;
    private DateFormat df;
    private Long selectedFacility;
    private SynchronizedDescriptiveStatistics dstats;
    private TreeMap<String, DoctorStats> dstatsPerDoc;
    private TreeMap<String, DoctorStats> dstatsPerRTM;
    private PieChartModel pieChart;
    private String interval;
    String selectedFilters;

    public PieChartController() {
        endDate = new Date();
        this.interval = "";
        
        GregorianCalendar gc = new GregorianCalendar();
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        Map<String, Object> sessionMap = externalContext.getSessionMap();
        
        if(sessionMap.containsKey("endDate")) {
            endDate = (Date) sessionMap.get("endDate");
        } else {
            endDate = new Date();
            sessionMap.put("endDate", endDate);
        }
        
        if(sessionMap.containsKey("startDate")) {
            startDate = (Date) sessionMap.get("startDate");
        } else {
            gc.setTime(endDate);
            gc.add(Calendar.MONTH, -1);
            startDate = gc.getTime();
            sessionMap.put("startDate", startDate);
            this.interval="1m";
        }

        this.df =  new SimpleDateFormat("MM/dd/YYYY");
        this.selectedFacility = new Long(-1);
        this.dstats = new SynchronizedDescriptiveStatistics();
        this.dstatsPerDoc = new TreeMap<>();
        this.dstatsPerRTM = new TreeMap<>();
        this.pieChart = new PieChartModel();

        selectedFilters = "all-tx";
    }

    
    
    public TxInstanceFacade getFacade() {
        return ejbFacade;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setStartDate(Date startDate) {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        Map<String, Object> sessionMap = externalContext.getSessionMap();
        this.startDate = startDate;
        sessionMap.put("startDate", startDate);
    }
    
    public void setEndDate(Date endDate) {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        Map<String, Object> sessionMap = externalContext.getSessionMap();
        this.endDate = endDate;
        sessionMap.put("endDate", endDate);
    }

    public Long getSelectedFacility() {
        return selectedFacility;
    }

    public void setSelectedFacility(Long selectedFacility) {
        this.selectedFacility = selectedFacility;
    }

    public String getSelectedFilters() {
        return selectedFilters;
    }

    public void setSelectedFilters(String selectedFilters) {
        this.selectedFilters = selectedFilters;
    }

    public String getInterval() {
        return interval;
    }

    public void setInterval(String interval) {
        this.interval = interval;
    }
    
    public SynchronizedDescriptiveStatistics getDstats() {
        return dstats;
    }

    public TreeMap<String, DoctorStats> getDstatsPerDoc() {
        return dstatsPerDoc;
    }

    public TreeMap<String, DoctorStats> getDstatsPerRTM() {
        return dstatsPerRTM;
    }
    
    public PieChartModel getPieChart() {
        return pieChart;
    }
    
    /*private String filterString() {
        String retval = "";
        for(String item : selectedFilters ) {
            retval += "," + item;
        }
        return retval;
    }*/
    
    public void updateData(String dataset){
    
    }
    
    public void updateChart(String dataSet){
        TreeMap<String, Long> mtxcounts;
        TreeMap<String, Long> dptcounts;
        TreeMap<String, SynchronizedDescriptiveStatistics> mptstats;
        TreeMap<String, SynchronizedDescriptiveStatistics> ptstats;
        pieChart.clear();
        dstats.clear();
        dstatsPerDoc.clear();
        dstatsPerRTM.clear();
        
        JSONArray labels = new JSONArray();
        
        if(dataSet.equals("DR")) {
            dptcounts = getFacade().doctorPtCounts(startDate, endDate, selectedFacility, selectedFilters);
            ptstats = getFacade().doctorStats(startDate, endDate, selectedFacility, selectedFilters);
            for(String doctor : dptcounts.keySet()) {
                Long count = dptcounts.get(doctor);
                DoctorStats newItem = new DoctorStats();
                newItem.setTotalPatients(count);
                newItem.setAverageDailyPatients(ptstats.get(doctor));
                dstatsPerDoc.put(doctor, newItem);
                pieChart.set(doctor, count);
                dstats.addValue(count);
                try{
                    String item = doctor + " (" + count + ")";
                    labels.put(item);
                }catch(Exception e){
                    //FIXME
                }
            }
            
            pieChart.setTitle("Physician Workload: " + df.format(startDate) + " - " + df.format(endDate));
        } else {
            mtxcounts = getFacade().machineTxCounts(startDate, endDate, selectedFacility, selectedFilters);
            mptstats = getFacade().machineStats(startDate, endDate, selectedFacility, selectedFilters);
            pieChart.setTitle("Tx per Machine: " + df.format(startDate) + " - " + df.format(endDate));
            
            for(String machine : mtxcounts.keySet()) {
                Long count = mtxcounts.get(machine);
                DoctorStats newItem = new DoctorStats();
                newItem.setTotalPatients(count);
                newItem.setAverageDailyPatients(mptstats.get(machine));
                dstatsPerRTM.put(machine, newItem);
                pieChart.set(machine, count);
                dstats.addValue(count);
                try{
                    String item = machine + " (" + count + ")";
                    labels.put(item);
                }catch(Exception e){
                    //FIXME
                }
            }
        }

        
        //pieChart.setLegendPosition("ne");
        pieChart.setShowDataLabels(true);
        pieChart.setShadow(false);
        //pieChart.setDataFormat("value");
        pieChart.setSeriesColors("8C3130, E0AB5D, 4984D0, 2C2A29, A2B85C, BBBEC3, D8C9B6, BD8A79, 3C857A, CD3935");
        pieChart.setExtender("function(){ this.cfg.seriesDefaults.rendererOptions.dataLabels = " + labels.toString() + "; " +
                    "this.cfg.seriesDefaults.rendererOptions.dataLabelPositionFactor = 1.2; " +
                    "this.cfg.seriesDefaults.rendererOptions.diameter = 600; " +
                    "this.cfg.seriesDefaults.rendererOptions.dataLabelThreshold = 0.5;" +
                    "this.cfg.sliceMargin = 3; " +
                    "this.legend = {show:false} }");
    }
    
    public void draw(String dataSet) {
        updateChart(dataSet);
    
    }
    
     public void onSelectTimePeriod(){
        endDate = new Date();
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(endDate);
        
        switch(interval) {
            case "1wk":
                gc.add(Calendar.DATE, -7);
                startDate = gc.getTime();
                break;
            case "1m":
                gc.add(Calendar.MONTH, -1);
                startDate = gc.getTime();
                break;
            case "3m":
                gc.add(Calendar.MONTH, -3);
                startDate = gc.getTime();
                break;
            case "6m":
                gc.add(Calendar.MONTH, -6);
                startDate = gc.getTime();
                break;
            case "1y":
                gc.add(Calendar.YEAR, -1);
                startDate = gc.getTime();
                break;
            case "2y":
                gc.add(Calendar.YEAR, -2);
                startDate = gc.getTime();
                break;
            case "3y":
                gc.add(Calendar.YEAR, -3);
                startDate = gc.getTime();
                break;
            case "Q1":
                gc.setTime(FiscalDate.getQuarter(1));
                startDate = gc.getTime();
                gc.add(Calendar.MONTH, 3);
                endDate = gc.getTime();
                break;
            case "Q2":
                gc.setTime(FiscalDate.getQuarter(2));
                startDate = gc.getTime();
                gc.add(Calendar.MONTH, 3);
                endDate = gc.getTime();
                break;
            case "Q3":
                gc.setTime(FiscalDate.getQuarter(3));
                startDate = gc.getTime();
                gc.add(Calendar.MONTH, 3);
                endDate = gc.getTime();
                break;
            case "Q4":
                gc.setTime(FiscalDate.getQuarter(4));
                startDate = gc.getTime();
                gc.add(Calendar.MONTH, 3);
                endDate = gc.getTime();
                break;
        }
    }
    
}
