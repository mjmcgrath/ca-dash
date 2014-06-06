package edu.umm.radonc.ca_dash.controllers;

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

@Named("activityController")
@SessionScoped
public class ActivityController implements Serializable {

    @EJB
    private edu.umm.radonc.ca_dash.model.ActivityFacade ejbFacade;
    
    @EJB
    private edu.umm.radonc.ca_dash.model.HospitalFacade hFacade;
    
    private List<Activity> items = null;
    private LazyDataModel<Activity> lazyItems = null;
    private CartesianChartModel dailyChart;
    private CartesianChartModel weeklyChart;
    private CartesianChartModel monthlyChart;
    private List<ActivityCount> dailyActivities;
    private Activity selected;
    private Date startDate;
    private Date endDate;
    private DateFormat df;
    private List<Integer> selectedFacilities;
    private List<String> selectedTimeIntervals;
    private Date selectedDate;
    private HashMap<Integer,Integer> hospitalChartSeriesMapping;
    private SynchronizedDescriptiveStatistics stats;
    private boolean imrtOnly;
    private boolean includeWeekends;
    private boolean hideDailyTab;
    private boolean hideWeeklyTab;
    private boolean hideMonthlyTab;
    private boolean disableDailyCheckbox;
    private boolean disableWeeklyCheckbox;
    private boolean disableMonthlyCheckbox;
    private boolean disableYearlyCheckbox;
    private JSONArray weeklyErrorBars;
    private JSONArray weeklyErrorLabels;
    private JSONArray monthlyErrorBars;
    private JSONArray monthlyErrorLabels;
    private String weeklyDisplayMode;
    private String weeklySegmentationMode;
    private String monthlyDisplayMode;
    private Integer weeklyChartmax;
    private Integer monthlyChartmax;
    
    public ActivityController() {
        df = new SimpleDateFormat("E, dd MMM yyyy");
        GregorianCalendar gc = new GregorianCalendar();
        endDate = new Date();
        gc.setTime(endDate);
        gc.add(Calendar.MONTH, -1);
        startDate = gc.getTime();
        dailyChart = new CartesianChartModel();
        weeklyChart = new CartesianChartModel();
        monthlyChart = new CartesianChartModel();
        selectedFacilities = new ArrayList<Integer>();
        selectedFacilities.add(-1);
        selectedTimeIntervals = new ArrayList<>();
        selectedTimeIntervals.add("Daily");
        hospitalChartSeriesMapping = new HashMap<>();
        imrtOnly = false;
        includeWeekends = false;
        hideDailyTab = false;
        hideWeeklyTab = true;
        hideMonthlyTab = true;
        disableDailyCheckbox = false;
        weeklyDisplayMode = "Summary";
        weeklySegmentationMode = "Absolute";
        monthlyDisplayMode = "Raw";
        weeklyChartmax = 0;
        monthlyChartmax = 0;
        handleDateSelect();
    }

    public Activity getSelected() {
        return selected;
    }

    public Integer getWeeklyChartmax() {
       return weeklyChartmax;
    }
    
    public Integer getMonthlyChartmax() {
       return weeklyChartmax;
    }

    public boolean isIncludeWeekends() {
        return includeWeekends;
    }

    public void setIncludeWeekends(boolean includeWeekends) {
        this.includeWeekends = includeWeekends;
    }
    
    public List<Integer> getSelectedFacilities() {
        return selectedFacilities;
    }

    public String getSelectedDate() {
        if (selectedDate != null) {
            return df.format(selectedDate);
        } else {
            return "";
        }
    }

    public String getWeeklyDisplayMode() {
        return weeklyDisplayMode;
    }

    public void setWeeklyDisplayMode(String weeklyDisplayMode) {
        this.weeklyDisplayMode = weeklyDisplayMode;
    }

    public String getWeeklySegmentationMode() {
        return weeklySegmentationMode;
    }

    public void setWeeklySegmentationMode(String weeklySegmentationMode) {
        this.weeklySegmentationMode = weeklySegmentationMode;
    }

    public String getMonthlyDisplayMode() {
        return monthlyDisplayMode;
    }

    public void setMonthlyDisplayMode(String monthlyDisplayMode) {
        this.monthlyDisplayMode = monthlyDisplayMode;
    }
    
    public void setSelectedFacilities(List<String> selectedFacilities) {
        this.selectedFacilities = new ArrayList<>();
        for(String fac : selectedFacilities) {
            this.selectedFacilities.add(Integer.parseInt(fac));
        }
    }

    public List<String> getSelectedTimeIntervals() {
        return selectedTimeIntervals;
    }

    public String getWeeklyErrorBars() {
        return weeklyErrorBars.toString();
    }

    public String getWeeklyErrorLabels() {
        return weeklyErrorLabels.toString();
    }

    public JSONArray getMonthlyErrorBars() {
        return monthlyErrorBars;
    }

    public JSONArray getMonthlyErrorLabels() {
        return monthlyErrorLabels;
    }

    public void setSelectedTimeIntervals(List<String> selectedTimeSpans) {
        this.selectedTimeIntervals = selectedTimeSpans;
    }

    public void setSelected(Activity selected) {
        this.selected = selected;
    }

    public boolean isImrtOnly() {
        return imrtOnly;
    }

    public void setImrtOnly(boolean imrtOnly) {
        this.imrtOnly = imrtOnly;
    } 

    public boolean isHideDailyTab() {
        return hideDailyTab;
    }

    public void setHideDailyTab(boolean hideDailyTab) {
        this.hideDailyTab = hideDailyTab;
    }

    public boolean isHideWeeklyTab() {
        return hideWeeklyTab;
    }

    public void setHideWeeklyTab(boolean hideWeeklyTab) {
        this.hideWeeklyTab = hideWeeklyTab;
    }

    public boolean isHideMonthlyTab() {
        return hideMonthlyTab;
    }

    public void setHideMonthlyTab(boolean hideMonthlyTab) {
        this.hideMonthlyTab = hideMonthlyTab;
    }

    public boolean isDisableDailyCheckbox() {
        return disableDailyCheckbox;
    }

    public void setDisableDailyCheckbox(boolean disableDailyCheckbox) {
        this.disableDailyCheckbox = disableDailyCheckbox;
    }

    public boolean isDisableWeeklyCheckbox() {
        return disableWeeklyCheckbox;
    }

    public void setDisableWeeklyCheckbox(boolean disableWeeklyCheckbox) {
        this.disableWeeklyCheckbox = disableWeeklyCheckbox;
    }

    public boolean isDisableMonthlyCheckbox() {
        return disableMonthlyCheckbox;
    }

    public void setDisableMonthlyCheckbox(boolean disableMonthlyCheckbox) {
        this.disableMonthlyCheckbox = disableMonthlyCheckbox;
    }

    public boolean isDisableYearlyCheckbox() {
        return disableYearlyCheckbox;
    }

    public void setDisableYearlyCheckbox(boolean disableYearlyCheckbox) {
        this.disableYearlyCheckbox = disableYearlyCheckbox;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private ActivityFacade getFacade() {
        return ejbFacade;
    }

    public Activity prepareCreate() {
        selected = new Activity();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("ActivityCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("ActivityUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("ActivityDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Activity> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }

    public SynchronizedDescriptiveStatistics getStats() {
        return stats;
    }
    
    private void recalcStats(List<Object[]> values, int valIndex) {
        stats.clear();
        for(Object[] item : values) {
            stats.addValue(((Long)item[valIndex]).doubleValue());
        }
    }
    
    public void handleDateSelect() {
        long diff = endDate.getTime() - startDate.getTime();
        long diffDays = diff / (24 * 60 * 60 * 1000);
        
        System.out.println(diffDays);
        
        this.disableDailyCheckbox = (diffDays < 1);
        this.disableWeeklyCheckbox = (diffDays < 7);
        this.disableMonthlyCheckbox = (diffDays < 28);
        this.disableYearlyCheckbox = (diffDays < 365);
    }
    
    public LazyDataModel<Activity> getLazyItems() {
        if (lazyItems == null) {
            this.lazyItems = new LazyDataModel<Activity>(){
                private static final long serialVersionUID    = 1L;
                @Override
                public List<Activity> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                    int[] range = {first, first + pageSize};
                    List<Activity> result = getFacade().itemsDateRange(startDate, endDate, range);
                    lazyItems.setRowCount(getFacade().itemsDateRangeCount(startDate, endDate));
                    return result;
                }
            };
        }
        return lazyItems;
    }
    
    private void persist(PersistAction persistAction, String successMessage) {
        if (selected != null) {
            setEmbeddableKeys();
            try {
                if (persistAction != PersistAction.DELETE) {
                    getFacade().edit(selected);
                } else {
                    getFacade().remove(selected);
                }
                JsfUtil.addSuccessMessage(successMessage);
            } catch (EJBException ex) {
                String msg = "";
                Throwable cause = ex.getCause();
                if (cause != null) {
                    msg = cause.getLocalizedMessage();
                }
                if (msg.length() > 0) {
                    JsfUtil.addErrorMessage(msg);
                } else {
                    JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            }
        }
    }

    public Activity getActivity(java.lang.Integer id) {
        return getFacade().find(id);
    }

    public List<ActivityCount> getDailyActivities() {
        return dailyActivities;
    }

    public List<Activity> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Activity> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }
    
    public List<Object[]> getDailyCounts(int index) {
        ArrayList<Date> allDates = new ArrayList<>();
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(startDate);
        while(gc.getTime().compareTo(endDate) < 0) {
            allDates.add(gc.getTime());
            gc.add(Calendar.DATE, 1);
        }
        
        List<Object[]> items;
        List<Object[]> itemsMerged = new ArrayList<>();
        if(index < 0) {
            //FIXME: change last parameter to checkbox value
            items = getFacade().getDailyCounts(startDate, endDate, imrtOnly, true);
        } else {
            items = getFacade().getDailyCounts(startDate, endDate, new Long(index), imrtOnly, true);
        }
        
        int i;
        outer:
        for(Date d : allDates) {
            i = 0;
            while(i < items.size()) {
                if(df.format(d).equals(df.format((Date)items.get(i)[0]))) {
                    itemsMerged.add(items.get(i));
                    continue outer;
                }
                i++;
            }
            ArrayList<Object> o = new  ArrayList<>();
            o.add(d);
            o.add(new Long(0));
            itemsMerged.add(o.toArray());
        }
        return itemsMerged;
    }
    
    public SynchronizedDescriptiveStatistics getDailySummary(){
        return getFacade().getDailyStats(startDate, endDate, imrtOnly, includeWeekends);
    }
    
    public TreeMap<String,SynchronizedDescriptiveStatistics> getWeeklySummary(int hospital){
        return getFacade().getWeeklySummaryStats(startDate, endDate, new Long(hospital), imrtOnly, includeWeekends);
    }
    
    public TreeMap<String,SynchronizedDescriptiveStatistics> getMonthlySummary(int hospital){
        return getFacade().getMonthlySummaryStats(startDate, endDate, new Long(hospital), imrtOnly, includeWeekends);
    }
    
    public List<Object[]> getWeeklyCounts(Long index) {
        ArrayList<Date> allDates = new ArrayList<>();
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(startDate);
        int dow = gc.get(Calendar.DAY_OF_WEEK);
        int offset = gc.getFirstDayOfWeek() - dow;
        gc.add(Calendar.DATE, offset);
        //gc.setTime(new Date(gc.);
        while(gc.getTime().compareTo(endDate) < 0) {
            allDates.add(gc.getTime());
            gc.add(Calendar.WEEK_OF_YEAR, 1);
        }
        
        List<Object[]> items;
        List<Object[]> itemsMerged = new ArrayList<>();
      
        items = getFacade().getWeeklyCounts(startDate, endDate, index, imrtOnly, includeWeekends);
        //FIXME FIXME FIXME
        DateFormat wdf = new SimpleDateFormat("yyyy ww");
        int i;
        //outer:
        for(Date d : allDates) {
            i = 0;
            Long val = new Long(0);
            Double wk;
            Double yr = 0.0;
            Double mo = 0.0;
            int gcy = 0;
            int gcm = 0;
            int gcw = 0;
            Long partialwk = new Long(0);
            while(i < items.size()) {
;
                yr = ((Double)items.get(i)[0]);
                mo = ((Double)items.get(i)[1]);
                wk = ((Double)items.get(i)[2]);
                if( wk == 1.0 && mo == 12.0) {
                    //yr = yr + 1.0;
                    partialwk = (Long)items.get(i)[3];
                }
                String yrAndWk = (yr.intValue()) + " " + String.format("%02d", wk.intValue());
                
                gc.setTime(d);
                gcy = gc.get(Calendar.YEAR);
                gcm = gc.get(Calendar.MONTH);
                gcw = gc.get(Calendar.WEEK_OF_YEAR);
                if (gcm == Calendar.DECEMBER && gcw == 1 ){
                    gcy = gcy + 1;
                }
                String thisWk = gcy + " " +  String.format("%02d", gcw);
                if(thisWk.equals(yrAndWk)) {
                    val = (Long)items.get(i)[3];
                    if (gc.get(Calendar.MONTH) == Calendar.DECEMBER && gcw == 1) {
                            val = val + partialwk;
                            partialwk = new Long(0);
                    }
                    //continue outer;
                    break;
                }
                
                i++;
            }
            ArrayList<Object> o = new  ArrayList<>();
            o.add(d);
            o.add(val);
            itemsMerged.add(o.toArray());
        }
        return itemsMerged;
    }
    
    public List<Object[]> getMonthlyCounts(Long index) {
        return getFacade().getMonthlyCounts(startDate, endDate, index, imrtOnly, includeWeekends);
    }
    
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
    
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
    
    public Date getStartDate() {
        return this.startDate;
    }
    
    public Date getEndDate() {
        return this.endDate;
    }
    
    public CartesianChartModel getDailyBarChart() {
        return this.dailyChart;
    }

    public CartesianChartModel getWeeklyBarChart() {
        return weeklyChart;
    }
    
    public CartesianChartModel getMonthlyBarChart() {
        return this.monthlyChart;
    }
   
    public void itemSelect(ItemSelectEvent event) { 
       int series = event.getSeriesIndex();
       int item = event.getItemIndex();
       
       String dateRaw = (dailyChart.getSeries().get(series).getData().keySet().toArray()[item].toString());
       
       try {
            this.selectedDate = df.parse(dateRaw);
       }
       catch (ParseException ex) {
           //TODO: Log parse failure
       }
       
       Integer hospitalID = hospitalChartSeriesMapping.get(series);
       if( hospitalID < 0) {
            dailyActivities = getFacade().getDailyActivities(this.selectedDate, imrtOnly);
            //recalcStats(dailyCounts,1);
       } else {
           dailyActivities = getFacade().getDailyActivities(this.selectedDate, hospitalID.longValue(), imrtOnly);
       }
       
    }
    
    public void drawDaily(DateFormat df) {
        this.dailyChart = new CartesianChartModel();
        int curSeries = 0;
        List<Object[]> events;
        
        for (Integer fac: selectedFacilities) {
            String hospital = "All";
            if( fac > 0 ) {
                hospital =  hFacade.find(fac).getHospitalname();
            }
            if(this.selectedTimeIntervals.contains("Daily")) {
                ChartSeries series = new ChartSeries();
                series.setLabel(hospital);
                events = getDailyCounts(fac);
                for (Object[] event : events) {
                    String xval = df.format((Date)event[0]);
                    Long yval = (Long)event[1];
                    series.set(xval, yval);
                }
                hospitalChartSeriesMapping.put(curSeries,fac);

                hideDailyTab = false;
                dailyChart.addSeries(series);
            }
            
            curSeries++;
        }
    }
    
    public void drawWeekly(DateFormat df) {
        this.weeklyChart = new CartesianChartModel();
        this.weeklyChart = new CartesianChartModel();
        this.weeklyErrorBars = new JSONArray();
        this.weeklyErrorLabels = new JSONArray();
        weeklyChartmax = 0;
        //int curSeries = 0;
        List<Object[]> events;
        
        for (Integer fac: selectedFacilities) {
            String hospital = "All";
            if( fac > 0 ) {
                hospital =  hFacade.find(fac).getHospitalname();
            }
            
            GregorianCalendar gc = new GregorianCalendar();

            if (this.selectedTimeIntervals.contains("Weekly") && this.weeklyDisplayMode.equals("Raw") &&  this.weeklySegmentationMode.equals("Absolute") ) {
                ChartSeries wSeries = new ChartSeries();
                wSeries.setLabel(hospital);
                events = this.getWeeklyCounts(new Long(fac));
                
                for (Object[] event : events) {
                    Date d = (Date)event[0];
                    gc.setTime(d);
                    String xval = df.format(d);
                    if( gc.get(Calendar.MONTH) == Calendar.DECEMBER && gc.get(Calendar.WEEK_OF_YEAR) == 1){
                        xval = (gc.get(Calendar.YEAR) + 1) + " Week " + gc.get(Calendar.WEEK_OF_YEAR);
                    }
                    Long yval = (Long)event[1];
                    wSeries.set(xval, yval);
                    if(yval > weeklyChartmax){
                        weeklyChartmax = yval.intValue();
                    }
                }
                hideWeeklyTab = false;
                weeklyChart.addSeries(wSeries);
            }
            
            if(this.weeklyDisplayMode.equals("Summary") &&  this.weeklySegmentationMode.equals("Absolute")) {
                ChartSeries wSumSeries = new ChartSeries();
                wSumSeries.setLabel("Mean Daily Treatments " + hospital);
                Map<String,SynchronizedDescriptiveStatistics> wSumStats = this.getWeeklySummary(fac);
                JSONArray errorData = new JSONArray();
                JSONArray errorTextData = new JSONArray();
                
                for(String key : wSumStats.keySet()) {
                    String xval = key;
                    Double yval = wSumStats.get(key).getMean();
                    Double twoSigma = (2 * (wSumStats.get(key).getStandardDeviation())) / wSumStats.get(key).getMean();
                    if( (yval + (yval * twoSigma)) > weeklyChartmax ){
                        weeklyChartmax = calcChartMax(yval, twoSigma);
                    }
                    JSONObject errorItem = new JSONObject();
                    try {
                        errorItem.put("min", twoSigma);
                        errorItem.put("max", twoSigma);
                        errorData.put(errorItem);
                        errorTextData.put("");
                    } catch (Exception e) {
                    }
                    if(Double.isNaN(yval)) {
                        yval = 0.0;
                    }
                    wSumSeries.set(xval,yval);
                }
                
                this.weeklyErrorBars.put(errorData);
                this.weeklyErrorLabels.put(errorTextData);
                weeklyChart.addSeries(wSumSeries);
                hideWeeklyTab = false;
            }
            
            if(this.weeklySegmentationMode.equals("Trailing")) {
                ChartSeries wTrSumSeries = new ChartSeries();
                Map<Date,SynchronizedDescriptiveStatistics> wTrSumStats = this.getTrailingWeeklySummary(fac);
                JSONArray errorData = new JSONArray();
                JSONArray errorTextData = new JSONArray();
                this.weeklyErrorBars = new JSONArray();
                this.weeklyErrorBars = new JSONArray();
                
                for(Date key : wTrSumStats.keySet()) {
                    String xval = this.df.format(key);
                    Double yval;
                    if(this.weeklyDisplayMode.equals("Summary")) {
                        wTrSumSeries.setLabel("Mean Daily Treatments (Trailing) " + hospital);
                        yval = wTrSumStats.get(key).getMean();
                        Double twoSigma = errorBar(wTrSumStats.get(key).getStandardDeviation(), wTrSumStats.get(key).getMean());
                        if( (yval + (yval * twoSigma)) > weeklyChartmax ){
                            weeklyChartmax = calcChartMax(yval, twoSigma);
                        }
                        JSONObject errorItem = new JSONObject();
                        try {
                            errorItem.put("min", twoSigma);
                            errorItem.put("max", twoSigma);
                            errorData.put(errorItem);
                            errorTextData.put("");
                        } catch (Exception e) {
                        }
                    } else {
                        //FIXME
                        if( wTrSumStats.get(key).getMax() > weeklyChartmax){
                            weeklyChartmax = new Double(wTrSumStats.get(key).getMax()).intValue();
                        }
                        wTrSumSeries.setLabel("Total Treatments (Trailing) " + hospital);
                        yval = wTrSumStats.get(key).getSum();
                    }
                    wTrSumSeries.set(xval,yval);
                }
                
                this.weeklyErrorBars.put(errorData);
                this.weeklyErrorLabels.put(errorTextData);
                weeklyChart.addSeries(wTrSumSeries);
                hideWeeklyTab = false;
            }
            
            //curSeries++;
        }
    }
    
    private Integer roundUpToNearestMultipleOfSix(Integer i) {
      if(i%6 != 0) {
         int multiple = i / 6;
         i = 6 * (multiple + 1);
      }
      return i;
   }
    
    private Integer calcChartMax(Double yval, Double twoSigma) {
        //return Math.floor((Math.ceil(yval + (yval * twoSigma)) + 50.0 / 100.0));
        if(twoSigma != null && twoSigma != 0.0) {
            return roundUpToNearestMultipleOfSix( (new Double(Math.ceil(yval + (yval * twoSigma)))).intValue() );
        }
        else {
            return roundUpToNearestMultipleOfSix(yval.intValue());
        }
    }
    
    public void drawMonthly(DateFormat df) {
        this.monthlyChart = new CartesianChartModel();
        this.monthlyErrorBars = new JSONArray();
        this.monthlyErrorLabels = new JSONArray();
        ChartSeries mSeries;
        List<Object[]> events;
        
        if (this.monthlyDisplayMode.equals("Raw")) {
            for (Integer fac: selectedFacilities) {
                mSeries = new ChartSeries();
                String hospital = "All";
                if( fac > 0 ) {
                    hospital =  hFacade.find(fac).getHospitalname();
                }
            
                //Monthly total
                events = this.getMonthlyCounts(new Long(fac));
  
                mSeries.setLabel(hospital);

                for (Object[] event : events) {
                    String xval = ((Double)(event[0])).intValue() + " " + ((Double) event[1]).intValue();
                    Long yval = (Long)event[2];
                    mSeries.set(xval, yval);
                    if(yval > monthlyChartmax){
                        monthlyChartmax = yval.intValue();
                    }
                }
                monthlyChart.addSeries(mSeries);
            }
        } else {
            //monthly summary

            for (Integer fac: selectedFacilities) {
                String hospital = "All";
                if( fac > 0 ) {
                    hospital =  hFacade.find(fac).getHospitalname();
                }
                
                ChartSeries mSumSeries = new ChartSeries();
                
                mSumSeries.setLabel("Mean Daily Treatments " + hospital);
                Map<String,SynchronizedDescriptiveStatistics> mSumStats = this.getMonthlySummary(fac);
                
                JSONArray errorData = new JSONArray();
                JSONArray errorTextData = new JSONArray();
                
                for(String key : mSumStats.keySet()) {
                    String xval = key;
                    Double yval = mSumStats.get(key).getMean();
                    Double twoSigma = errorBar(mSumStats.get(key).getStandardDeviation(), mSumStats.get(key).getMean());
                    if( (yval + (yval * twoSigma)) > monthlyChartmax ){
                        monthlyChartmax = calcChartMax(yval, twoSigma);
                    }
                    JSONObject errorItem = new JSONObject();
                    try {
                        errorItem.put("min", twoSigma);
                        errorItem.put("max", twoSigma);
                        errorData.put(errorItem);
                        errorTextData.put("");
                    } catch (JSONException e) {
                        System.out.println("Error building JSON object containing monthly error bar data");
                    }

                    mSumSeries.set(xval,yval);
                }
                
                this.monthlyErrorBars.put(errorData);
                this.monthlyErrorLabels.put(errorTextData);
                monthlyChart.addSeries(mSumSeries);
 
            }
           
       }
       hideMonthlyTab = false;
    }
    
    private Double errorBar(Double stddev, Double mean) {
        return (2 * stddev) / mean;
    }
    
    public void draw(){
        int curSeries = 0;
        List<Object[]> events;
        this.hospitalChartSeriesMapping = new HashMap<>();
        this.dailyChart = new CartesianChartModel();
        this.weeklyChart = new CartesianChartModel();
        this.weeklyErrorBars = new JSONArray();
        this.weeklyErrorLabels = new JSONArray();
        this.monthlyErrorBars = new JSONArray();
        this.monthlyErrorLabels = new JSONArray();
        weeklyChartmax = 0;
        monthlyChartmax = 0;
        
        DateFormat wdf = new SimpleDateFormat("yyyy 'Week' ww");
        DateFormat mdf = new SimpleDateFormat("yyyy MMM");
        
        if(this.selectedTimeIntervals.contains("Daily")) {
            drawDaily(df);
        }
        if(this.selectedTimeIntervals.contains("Weekly")) {
            drawWeekly(wdf);
        }
        if(this.selectedTimeIntervals.contains("Monthly")) {
            drawMonthly(mdf);
        }
    }

    private Map<Date, SynchronizedDescriptiveStatistics> getTrailingWeeklySummary(Integer hospital) {
        return getFacade().getWeeklyTrailingSummaryStats(startDate, endDate, new Long(hospital), imrtOnly, includeWeekends);
    }
    
    public SynchronizedDescriptiveStatistics getMonthlySummary() {
        return getFacade().getMonthlyStats(startDate, endDate, imrtOnly, includeWeekends);
    }

    @FacesConverter(forClass = Activity.class)
    public static class ActivityControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            ActivityController controller = (ActivityController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "activityController");
            return controller.getActivity(getKey(value));
        }

        java.lang.Integer getKey(String value) {
            java.lang.Integer key;
            key = Integer.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Integer value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }
        
        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Activity) {
                Activity o = (Activity) object;
                return getStringKey(o.getActinstproccodeser());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Activity.class.getName()});
                return null;
            }
        }

    }

}
