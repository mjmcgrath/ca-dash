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

import edu.umm.radonc.ca_dash.model.Activity;
import edu.umm.radonc.ca_dash.model.ActivityCount;
import edu.umm.radonc.ca_dash.model.ActivityFacade;
import edu.umm.radonc.ca_dash.model.Hospital;
import edu.umm.radonc.ca_dash.model.util.JsfUtil;
import edu.umm.radonc.ca_dash.model.util.JsfUtil.PersistAction;
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
import org.primefaces.model.LazyDataModel;
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
    
    private Activity selected;
    private Date startDate;
    private Date endDate;
    private DateFormat df;
    private List<Integer> selectedFacilities;
    private List<String> selectedTimeIntervals;
    private Date selectedDate;
    private double percentile;
    boolean imrtOnly;
    boolean includeWeekends;
           
    public HistogramController() {
    
    }
    
    private ActivityFacade getFacade() {
        return ejbFacade;
    }
    
    public ChartSeries histogram(Long hospital){
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
    
}
