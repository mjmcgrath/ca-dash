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
import edu.umm.radonc.ca_dash.model.FiscalDate;
import edu.umm.radonc.ca_dash.model.TxInstanceFacade;
import java.io.Serializable;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import org.apache.commons.math.stat.descriptive.SynchronizedDescriptiveStatistics;
import org.json.*;
import org.primefaces.event.ItemSelectEvent;
import org.primefaces.model.SortOrder;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
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
    private List<String> selectedFilters;
    private boolean includeWeekends;
    private BarChartModel histogram;
    private String interval;
    private SynchronizedDescriptiveStatistics dstats;
    private boolean patientsFlag;
            
    public HistogramController() {
        histogram = new BarChartModel();
        percentile = 50.0;
        dstats = new SynchronizedDescriptiveStatistics();
        endDate = new Date();
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(endDate);
        gc.add(Calendar.MONTH, -1);
        startDate = gc.getTime();
        interval="1m";
        selectedFilters = new ArrayList<>();
        selectedFacility = new Long(-1);
        patientsFlag = true;
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

    public boolean isPatientsFlag() {
        return patientsFlag;
    }

    public void setPatientsFlag(boolean patientsFlag) {
        this.patientsFlag = patientsFlag;
    }
    
    
    public void updatePercentile() {
        Long hospital = new Long(-1);
        if( hospital != null && hospital > 0) {
            dstats = getFacade().getDailyStats(startDate, endDate, hospital, filterString(), includeWeekends, patientsFlag);
        }
        else {
            dstats = getFacade().getDailyStats(startDate, endDate, new Long(-1),filterString(), includeWeekends, patientsFlag);
        }
        percentileVal = dstats.getPercentile(percentile);
    }

    public List<String> getSelectedFilters() {
        return selectedFilters;
    }

    public void setSelectedFilters(List<String> selectedFilters) {
        this.selectedFilters = selectedFilters;
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
    
    private String filterString() {
        String retval = "";
        for(String item : selectedFilters ) {
            retval += "," + item;
        }
        return retval;
    }
    
    public ChartSeries buildHistogram(Long hospital){
        ChartSeries histo = new ChartSeries();
        SynchronizedDescriptiveStatistics stats;
        stats = getFacade().getDailyStats(startDate, endDate, hospital, filterString(), includeWeekends, patientsFlag);
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
        this.histogram.clear();
        histogram.setLegendPosition("ne");
        histogram.setTitle("Frequency Distribution");
        Axis xAx = histogram.getAxis(AxisType.X);
        xAx.setMin(0);
        xAx.setTickAngle(45); 
        histogram.setShadow(false);
        histogram.setSeriesColors("C8102E, FFCD00, 007698, 2C2A29, 33460D,49182D"); 
        histogram.addSeries(buildHistogram(selectedFacility));
    }
    
}
