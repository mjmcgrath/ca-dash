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
import java.util.TreeMap;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.apache.commons.math.stat.descriptive.SynchronizedDescriptiveStatistics;
import org.json.JSONArray;
import org.primefaces.model.chart.PieChartModel;
import edu.umm.radonc.ca_dash.model.DoctorStats;


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
    private TreeMap<String, DoctorStats> dstatsPerDoc;
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
        this.dstatsPerDoc = new TreeMap<>();
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

    public TreeMap<String, DoctorStats> getDstatsPerDoc() {
        return dstatsPerDoc;
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
    
    public void updateData(String dataset){
    
    }
    
    public void updateChart(String dataSet){
        List<Object[]> machinecounts = new ArrayList<>();
        TreeMap<String, Long> ptcounts;
        TreeMap<String, SynchronizedDescriptiveStatistics> ptstats;
        pieChart.clear();
        dstats.clear();
        dstatsPerDoc.clear();
        JSONArray labels = new JSONArray();
        
        if(dataSet.equals("DR")) {
            ptcounts = getFacade().doctorPtCounts(startDate, endDate, selectedFacility, filterString());
            ptstats = getFacade().doctorStats(startDate, endDate, selectedFacility, filterString());
            for(String doctor : ptcounts.keySet()) {
                Long count = ptcounts.get(doctor);
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
            machinecounts = getFacade().MachineTxCounts(startDate, endDate, selectedFacility, filterString());
            pieChart.setTitle("Tx per Machine: " + df.format(startDate) + " - " + df.format(endDate));
            
            for(Object[] row : machinecounts) {
                String item = "";
                String dr = (String) row[0];
                Long ptCount = (Long) row[1];
                pieChart.set(dr, ptCount);
                dstats.addValue(ptCount);
                try{
                    item = dr + " (" + ptCount + ")";
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
