/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.umm.radonc.ca_dash.controllers;

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
import javax.ejb.EJB;
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
@ViewScoped
public class PieChartController implements Serializable{
    
    @EJB
    private edu.umm.radonc.ca_dash.model.TxInstanceFacade ejbFacade;
    
    @EJB
    private edu.umm.radonc.ca_dash.model.HospitalFacade hFacade;
    
    private Date startDate;
    private Date endDate;
    private DateFormat df;
    private Long selectedFacility;
    private boolean imrtOnly;
    private SynchronizedDescriptiveStatistics dstats;
    private PieChartModel pieChart;
    private String interval;
    List<String> selectedFilters;

    public PieChartController() {
        endDate = new Date();
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(endDate);
        gc.add(Calendar.MONTH, -1);
        startDate = gc.getTime();
        interval="1m";
        this.df =  new SimpleDateFormat("MM/dd/YYYY");
        this.selectedFacility = new Long(-1);
        this.dstats = new SynchronizedDescriptiveStatistics();
        this.pieChart = new PieChartModel();
        this.interval = "";
        selectedFilters = new ArrayList<>();
    }

    
    
    public TxInstanceFacade getFacade() {
        return ejbFacade;
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

    public String getInterval() {
        return interval;
    }

    public void setInterval(String interval) {
        this.interval = interval;
    }
    
    public SynchronizedDescriptiveStatistics getDstats() {
        return dstats;
    }
    public PieChartModel getPieChart() {
        return pieChart;
    }
    
    private String filterString() {
        String retval = "";
        for(String item : selectedFilters ) {
            retval += "," + item;
        }
        return retval;
    }
    
    public void updateChart(String dataSet){
        List<Object[]> counts;
        pieChart.clear();
        dstats.clear();
        
        if(dataSet.equals("DR")) {
            counts = getFacade().DoctorPtCounts(startDate, endDate, selectedFacility, filterString());
            pieChart.setTitle("Physician Workload: " + df.format(startDate) + " - " + df.format(endDate));
        } else {
            counts = getFacade().MachineTxCounts(startDate, endDate, selectedFacility, filterString());
            pieChart.setTitle("Tx per Machine: " + df.format(startDate) + " - " + df.format(endDate));
        }
        JSONArray labels = new JSONArray();

        for(Object[] row : counts) {
            String item = "";
            String dr = (String) row[0];
            Long ptCount = (Long) row[1];
            pieChart.set(dr, ptCount);
            dstats.addValue(ptCount);
            try{
                item = dr + "<br/>(" + ptCount + ")";
                labels.put(item);
            }catch(Exception e){
                //FIXME
            }
        }
        
        //pieChart.setLegendPosition("ne");
        pieChart.setShowDataLabels(true);
        pieChart.setShadow(false);
        //pieChart.setDataFormat("value");
        
        pieChart.setExtender("function(){ this.cfg.seriesDefaults.rendererOptions.dataLabels = " + labels.toString() + "; " +
                    "this.cfg.seriesDefaults.rendererOptions.dataLabelPositionFactor = 1.22; " +
                    "this.cfg.seriesDefaults.rendererOptions.diameter = 525; " +
                    "this.cfg.seriesDefaults.rendererOptions.dataLabelThreshold = 0.5;" +
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
