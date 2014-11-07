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
import edu.umm.radonc.ca_dash.model.util.ColorMap;
import java.io.Serializable;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
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
@ViewScoped
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
    private String selectedFilters;
    private boolean includeWeekends;
    private BarChartModel histogram;
    private String interval;
    private SynchronizedDescriptiveStatistics dstats;
    private boolean patientsFlag;
    private boolean scheduledFlag;
    private boolean relativeModeFlag;
            
    public HistogramController() {
        histogram = new BarChartModel();
        percentile = 50.0;
        dstats = new SynchronizedDescriptiveStatistics();
        endDate = new Date();
        interval="";
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

        selectedFilters = "all-tx";
        selectedFacility = new Long(-1);
        patientsFlag = true;
        scheduledFlag = false;
        relativeModeFlag = false;
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
        dstats = getFacade().getDailyStats(startDate, endDate, selectedFacility, selectedFilters, includeWeekends, patientsFlag, scheduledFlag);
        percentileVal = dstats.getPercentile(percentile);
    }

    public String getSelectedFilters() {
        return selectedFilters;
    }

    public void setSelectedFilters(String selectedFilters) {
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

    public boolean isScheduledFlag() {
        return scheduledFlag;
    }

    public void setScheduledFlag(boolean scheduledFlag) {
        this.scheduledFlag = scheduledFlag;
    }

    public boolean isRelativeModeFlag() {
        return relativeModeFlag;
    }

    public void setRelativeModeFlag(boolean relativeModeFlag) {
        this.relativeModeFlag = relativeModeFlag;
    }
    
    public void handleModeSelect() {
        this.selectedFilters = "all-tx";
    }
    
    public void onSelectTimePeriod(){
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(startDate);
        
        switch(interval) {
            case "1wk":
                gc.add(Calendar.DATE, 7);
                endDate = gc.getTime();
                break;
            case "1m":
                gc.add(Calendar.MONTH, 1);
                endDate = gc.getTime();
                break;
            case "3m":
                gc.add(Calendar.MONTH, 3);
                endDate = gc.getTime();
                break;
            case "6m":
                gc.add(Calendar.MONTH, 6);
                endDate = gc.getTime();
                break;
            case "1y":
                gc.add(Calendar.YEAR, 1);
                endDate = gc.getTime();
                break;
            case "2y":
                gc.add(Calendar.YEAR, 2);
                endDate = gc.getTime();
                break;
            case "3y":
                gc.add(Calendar.YEAR, 3);
                endDate = gc.getTime();
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
    
    /*private String filterString() {
        String retval = "";
        for(String item : selectedFilters ) {
            retval += "," + item;
        }
        return retval;
    }*/
    
    public ChartSeries buildHistogram(Long hospital){
        double divisor = 1.0;
        ChartSeries histo = new ChartSeries();
        dstats = getFacade().getDailyStats(startDate, endDate, hospital, selectedFilters, includeWeekends, patientsFlag, scheduledFlag);
        String label = "All";
        //Freedman-Diaconis bin width
        double binInterval = Math.floor(2.0 * (dstats.getPercentile(75.0) - dstats.getPercentile(25.0)) * Math.pow(dstats.getN(),(-1.0/3.0)));
        if(relativeModeFlag) {
            divisor = dstats.getN();
        }
        binInterval = (binInterval < 1.0) ? 1.0 : binInterval;
        double[] sortedValues = dstats.getSortedValues();
        double currIntervalStart = 0.0;
        double currIntervalEnd = binInterval;
        double count = 0.0;
        for (int i = 0; i < sortedValues.length; i++) {
            if(sortedValues[i] < currIntervalEnd) {
                count += 1.0;
            } else {
                String intervalString = String.format("%d", (int)Math.ceil(currIntervalStart)) + " - " +  String.format("%d", (int)Math.floor(currIntervalEnd) - 1 );
                histo.set(intervalString, count / divisor);
                currIntervalStart = currIntervalEnd;
                currIntervalEnd += binInterval;
                count = 0.0;
                i--;
            }
        }
        String intervalString = String.format("%d", (int)Math.ceil(currIntervalStart)) + " - " +  String.format("%d", (int)(dstats.getMax()));
        histo.set(intervalString, count / divisor);
        if ( hospital > 0 ) {
            label = getHospitalFacade().find(hospital.intValue()).getHospitalname();
        }
        histo.setLabel(label);
        String[] colorset = (String[])ColorMap.getMap().get(label);
        histogram.setSeriesColors(colorset[0]); 
        return histo;
    }
    
    public void drawHistogram() {
        this.histogram.clear();
        Axis xAx = histogram.getAxis(AxisType.X);
        Axis yAx = histogram.getAxis(AxisType.Y);
        histogram.setLegendPosition("ne");
        if(patientsFlag) {
            histogram.setTitle("Patient Frequency Distribution");
            xAx.setLabel("Patients Treated");
        } else {
            histogram.setTitle("Treatment Frequency Distribution");
            xAx.setLabel("Treatments Completed");
        }

        xAx.setMin(0);
        xAx.setTickAngle(45);
        yAx.setLabel("Frequency");
        histogram.setShadow(false);
        histogram.addSeries(buildHistogram(selectedFacility));
    }
    
}
