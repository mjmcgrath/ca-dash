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
import org.apache.commons.math.stat.descriptive.SummaryStatistics;
import org.apache.commons.math.stat.descriptive.SynchronizedSummaryStatistics;
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
    private SynchronizedSummaryStatistics stats;
    private boolean imrtOnly;
    private boolean hideDailyTab;
    private boolean hideWeeklyTab;
    private boolean hideMonthlyTab;
    private boolean disableDailyCheckbox;
    private boolean disableWeeklyCheckbox;
    private boolean disableMonthlyCheckbox;
    private boolean disableYearlyCheckbox;
    private JSONArray errorBars;
    private JSONArray errorLabels;
    private String weeklyDisplayMode;
    
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
        hideDailyTab = false;
        hideWeeklyTab = true;
        hideMonthlyTab = true;
        disableDailyCheckbox = false;
        disableWeeklyCheckbox = true;
        disableMonthlyCheckbox = true;
        disableYearlyCheckbox = true;
        weeklyDisplayMode = "Summary";
    }

    public Activity getSelected() {
        return selected;
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

    public void setSelectedFacilities(List<String> selectedFacilities) {
        this.selectedFacilities = new ArrayList<>();
        for(String fac : selectedFacilities) {
            this.selectedFacilities.add(Integer.parseInt(fac));
        }
    }

    public List<String> getSelectedTimeIntervals() {
        return selectedTimeIntervals;
    }

    public String getErrorBars() {
        return errorBars.toString();
    }

    public String getErrorLabels() {
        return errorLabels.toString();
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

    public SynchronizedSummaryStatistics getStats() {
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
                public List<Activity> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
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
    
    public SynchronizedSummaryStatistics getDailySummary(){
        return getFacade().getDailyStats(startDate, endDate, imrtOnly, false);
    }
    
    public TreeMap<String,SynchronizedSummaryStatistics> getWeeklySummary(){
        return getFacade().getWeeklySummaryStats(startDate, endDate, new Long(-1), imrtOnly, true);
    }
    
    public List<Object[]> getWeeklyCounts(Long index) {
        ArrayList<Date> allDates = new ArrayList<>();
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(startDate);
        while(gc.getTime().compareTo(endDate) < 0) {
            allDates.add(gc.getTime());
            gc.add(Calendar.WEEK_OF_YEAR, 1);
        }
        
        List<Object[]> items;
        List<Object[]> itemsMerged = new ArrayList<>();
      
        items = getFacade().getWeeklyCounts(startDate, endDate, index, imrtOnly, true);
        //FIXME FIXME FIXME
        DateFormat wdf = new SimpleDateFormat("yyyy ww");
        int i;
        //outer:
        for(Date d : allDates) {
            i = 0;
            Long val = new Long(0); 
            while(i < items.size()) {
                String yrAndWk = ((Double)items.get(i)[0]).intValue() + " " + String.format("%02d", ((Double)items.get(i)[1]).intValue());
                String thisWk = wdf.format(d);
                if(thisWk.equals(yrAndWk)) {
                    val = (Long)items.get(i)[2];
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
    
    public List<Object> getMonthlyCounts() {
        return getFacade().getMonthlyCounts(null, null);
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
    
    public void draw(){
        int curSeries = 0;
        List<Object[]> events;
        this.hospitalChartSeriesMapping = new HashMap<>();
        this.dailyChart = new CartesianChartModel();

        DateFormat wdf = new SimpleDateFormat("yyyy 'Week' ww");
        
        if(this.selectedTimeIntervals.contains("Weekly") && (this.weeklyDisplayMode.equals("Raw"))){
            this.weeklyChart = new CartesianChartModel();
            if(selectedFacilities.contains(-1)) {
                events = getWeeklyCounts(new Long(-1));
                ChartSeries series = new ChartSeries();
                series.setLabel("All");
                for (Object[] event : events) {
                    String xval = wdf.format((Date)event[0]); // + " " + event[1]; //df.format((Date)event[0]);
                    Long yval = (Long)event[1];
                    series.set(xval, yval);
                }
                weeklyChart.addSeries(series);
                hideWeeklyTab = false;
            }
        } else {
          //hideWeeklyTab = true;  
        }
         
        if(this.selectedTimeIntervals.contains("Weekly") && (this.weeklyDisplayMode.equals("Summary"))) {
                    this.weeklyChart = new CartesianChartModel();
                    ChartSeries wSumSeries = new ChartSeries();
                    wSumSeries.setLabel("Mean Weekly Treatments All Facilities");
                    Map<String,SynchronizedSummaryStatistics> wSumStats = this.getWeeklySummary();
                    JSONArray errorData = new JSONArray();
                    JSONArray errorTextData = new JSONArray();

                    for(String key : wSumStats.keySet()) {
                        String xval = key;
                        Double yval = wSumStats.get(key).getMean();
                        Double twoSigma = (2 * (wSumStats.get(key).getStandardDeviation())) / wSumStats.get(key).getMean();
                        JSONObject errorItem = new JSONObject();
                        try {
                            errorItem.put("min", twoSigma);
                            errorItem.put("max", twoSigma);
                            errorData.put(errorItem);
                            errorTextData.put("");
                        } catch (Exception e) {
                        }
                        
                        wSumSeries.set(xval,yval);
                    }
                    this.errorBars = new JSONArray();
                    this.errorBars.put(errorData);
                    this.errorLabels = new JSONArray();
                    this.errorLabels.put(errorTextData);
                    weeklyChart.addSeries(wSumSeries);
                    hideWeeklyTab = false;
                }
        
        //decide whether or not to display totals or by location
        //iterate over lists
        if(this.selectedTimeIntervals.contains("Daily") && selectedFacilities.contains(-1)) {
            events = getDailyCounts(-1);
            ChartSeries series = new ChartSeries();
            series.setLabel("All");
            for (Object[] event : events) {
                String xval = df.format((Date)event[0]);
                Long yval = (Long)event[1];
                series.set(xval, yval);
            }
            dailyChart.addSeries(series);
            //TODO: monthly
            hospitalChartSeriesMapping.put(curSeries,-1);
            curSeries++;
            hideDailyTab = false;
        } else if ( !(this.selectedTimeIntervals.contains("Daily")) ) {
            hideDailyTab = true;
        }
        
        for (Integer fac: selectedFacilities) {
            if( fac > 0) {
                Hospital h =  hFacade.find(fac);
                
                if(this.selectedTimeIntervals.contains("Daily")) {
                    ChartSeries series = new ChartSeries();
                    series.setLabel(h.getHospitalname());
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
                
                //TODO: weekly
                if (this.selectedTimeIntervals.contains("Weekly")) {
                    ChartSeries wSeries = new ChartSeries();
                    wSeries.setLabel(h.getHospitalname());
                    events = this.getWeeklyCounts(new Long(fac));
                    for (Object[] event : events) {
                        String xval = wdf.format((Date)event[0]);
                        Long yval = (Long)event[1];
                        wSeries.set(xval, yval);
                    }
                    hideWeeklyTab = false;
                    weeklyChart.addSeries(wSeries);
                }
                
                
                //TODO: monthly
                if (this.selectedTimeIntervals.contains("Weekly")) {
                    ChartSeries mSeries = new ChartSeries();
                    mSeries.setLabel(h.getHospitalname());
                    /*events = this.getWeeklyCounts(new Long(fac));
                    for (Object[] event : events) {
                        String xval = df.format((Date)event[0]);
                        Long yval = (Long)event[1];
                        wSeries.set(xval, yval);
                    }*/
                    monthlyChart.addSeries(mSeries);
                }
                
                curSeries++;
            }
        }
        
        
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
