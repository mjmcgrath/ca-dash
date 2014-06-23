/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.umm.radonc.ca_dash.controllers;

/**
 *
 * @author mmcgrath
 */



import edu.umm.radonc.ca_dash.model.ActivityAIPC;
import edu.umm.radonc.ca_dash.model.ActivityFacade;
import edu.umm.radonc.ca_dash.model.FiscalDate;
import edu.umm.radonc.ca_dash.model.Hospital;
import edu.umm.radonc.ca_dash.model.TxInstanceFacade;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Named;
import org.apache.commons.math.stat.descriptive.SynchronizedDescriptiveStatistics;
import org.json.*;
import org.primefaces.event.ItemSelectEvent;
import org.primefaces.model.SortOrder;
import org.primefaces.model.chart.CartesianChartModel;
import org.primefaces.model.chart.ChartSeries;

@Named("histogramController")
@SessionScoped
public class HistogramController implements Serializable {
    
    @EJB
    private edu.umm.radonc.ca_dash.model.TxInstanceFacade ejbFacade;
    
    @EJB
    private edu.umm.radonc.ca_dash.model.HospitalFacade hFacade;
    
    private CartesianChartModel histoChart;
    
    private ActivityAIPC selected;
    private Date startDate;
    private Date endDate;
    private DateFormat df;
    private Long selectedFacility;
    private Date selectedDate;
    private double percentile;
    private double percentileVal;
    private boolean imrtOnly;
    private boolean includeWeekends;
    private CartesianChartModel histogram;
    private String interval;
    private SynchronizedDescriptiveStatistics dstats;
            
    public HistogramController() {
        histogram = new CartesianChartModel();
        percentile = 50.0;
        dstats = new SynchronizedDescriptiveStatistics();
        endDate = new Date();
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(endDate);
        gc.add(Calendar.MONTH, -1);
        startDate = gc.getTime();
        interval="1m";

        selectedFacility = new Long(-1);
    }
    
    private TxInstanceFacade getFacade() {
        return ejbFacade;
    }
    
    private edu.umm.radonc.ca_dash.model.HospitalFacade getHospitalFacade(){
        return hFacade;
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
    
    public CartesianChartModel getHistogram() {
        return histogram;
    }

    public double getPercentile() {
        return percentile;
    }

    public void setPercentile(double percentile) {
        this.percentile = percentile;
    }
    
    public double getPercentileVal() {
        updatePercentile();
        return percentileVal;
    }
    
    public void updatePercentile() {
        Long hospital = new Long(-1);
        if( hospital != null && hospital > 0) {
            dstats = getFacade().getDailyStats(startDate, endDate, hospital, imrtOnly, includeWeekends);
        }
        else {
            dstats = getFacade().getDailyStats(startDate, endDate, new Long(-1), imrtOnly, includeWeekends);
        }
        percentileVal = dstats.getPercentile(percentile);
    }
    

    public boolean isImrtOnly() {
        return imrtOnly;
    }

    public void setImrtOnly(boolean imrtOnly) {
        this.imrtOnly = imrtOnly;
    }

    public boolean isIncludeWeekends() {
        return includeWeekends;
    }

    public void setIncludeWeekends(boolean includeWeekends) {
        this.includeWeekends = includeWeekends;
    }

    public String getInterval() {
        return interval;
    }

    public void setInterval(String interval) {
        this.interval = interval;
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
    
    public ChartSeries buildHistogram(Long hospital){
        ChartSeries histo = new ChartSeries();
        SynchronizedDescriptiveStatistics stats;
        stats = getFacade().getDailyStats(startDate, endDate, hospital, imrtOnly, includeWeekends);
        String label = "All";
        //Freedman-Diaconis bin width
        double interval = 2.0 * (stats.getPercentile(75.0) - stats.getPercentile(25.0)) * Math.pow(stats.getN(),(-1.0/3.0));
        
        double[] sortedValues = stats.getSortedValues();
        double currIntervalStart = 0.0;
        double currIntervalEnd = interval;
        int count = 0;
        for (int i = 0; i < sortedValues.length; i++) {
            if(sortedValues[i] < currIntervalEnd) {
                count++;
            } else {
                String intervalString = Math.ceil(currIntervalStart) + "-" +  Math.ceil(currIntervalEnd);
                histo.set(intervalString, count);
                currIntervalStart = currIntervalEnd;
                currIntervalEnd += interval;
                count = 1;
            }
        }
        if ( hospital > 0 ) {
            label = getHospitalFacade().find(hospital.intValue()).getHospitalname();
        }
        histo.setLabel(label);
        return histo;
    }
    
    public void drawHistogram() {
        this.histogram = new CartesianChartModel();
        histogram.addSeries(buildHistogram(selectedFacility));
    }
    
}
