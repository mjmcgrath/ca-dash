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
import edu.umm.radonc.ca_dash.model.Hospital;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.time.*;
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
import org.primefaces.event.ItemSelectEvent;
import org.primefaces.model.SortOrder;
import org.primefaces.model.chart.CartesianChartModel;
import org.primefaces.model.chart.ChartSeries;
import org.json.*;

@Named("histogramController")
@SessionScoped
public class HistogramController implements Serializable {
    
    @EJB
    private edu.umm.radonc.ca_dash.model.ActivityFacade ejbFacade;
    
    @EJB
    private edu.umm.radonc.ca_dash.model.HospitalFacade hFacade;
    
    private CartesianChartModel histoChart;
    
    private ActivityAIPC selected;
    private Date startDate;
    private Date endDate;
    private DateFormat df;
    private List<Integer> selectedFacilities;
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
    }
    
    private ActivityFacade getFacade() {
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

    public List<Integer> getSelectedFacilities() {
        return selectedFacilities;
    }

    public void setSelectedFacilities(List<Integer> selectedFacilities) {
        this.selectedFacilities = selectedFacilities;
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
            dstats = getFacade().getDailyStats(startDate, endDate, imrtOnly, includeWeekends);
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
                break;
            case "1m":
                gc.add(Calendar.MONTH, -1);
                break;
            case "3m":
                gc.add(Calendar.MONTH, -3);
                break;
            case "6m":
                gc.add(Calendar.MONTH, -6);
                break;
            case "1y":
                gc.add(Calendar.YEAR, -1);
                break;
            case "2y":
                gc.add(Calendar.YEAR, -2);
                break;
            case "3y":
                gc.add(Calendar.YEAR, -3);
                break;
        }
        startDate = gc.getTime();
    }
    
    public ChartSeries buildHistogram(Long hospital){
        ChartSeries histo = new ChartSeries();
        SynchronizedDescriptiveStatistics stats;
        if( hospital != null && hospital > 0) {
            stats = getFacade().getDailyStats(startDate, endDate, hospital, imrtOnly, includeWeekends);
        }
        else {
            stats = getFacade().getDailyStats(startDate, endDate, imrtOnly, includeWeekends);
        }
        
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
        return histo;
    }
    
    public void drawHistogram() {
        this.histogram = new CartesianChartModel();
        histogram.addSeries(buildHistogram(new Long(-1)));
    }
    
}
