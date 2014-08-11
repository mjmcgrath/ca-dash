package edu.umm.radonc.ca_dash.controllers;

import edu.umm.radonc.ca_dash.model.ActivityCount;
import edu.umm.radonc.ca_dash.model.ActivityFacade;
import edu.umm.radonc.ca_dash.model.FiscalDate;
import edu.umm.radonc.ca_dash.model.Hospital;
import edu.umm.radonc.ca_dash.model.TxInstance;
import edu.umm.radonc.ca_dash.model.TxInstanceFacade;
import edu.umm.radonc.ca_dash.model.util.JsfUtil;
import edu.umm.radonc.ca_dash.model.util.JsfUtil.PersistAction;
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
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.apache.commons.math.stat.descriptive.SynchronizedDescriptiveStatistics;
import org.json.*;
import org.primefaces.event.ItemSelectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.CategoryAxis;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.DateAxis;

@Named("txInstanceController")
@ViewScoped
public class TxInstanceController implements Serializable {

    @EJB
    private edu.umm.radonc.ca_dash.model.TxInstanceFacade ejbFacade;
    
    @EJB
    private edu.umm.radonc.ca_dash.model.HospitalFacade hFacade;
    
    private List<TxInstance> items = null;
    private LazyDataModel<TxInstance> lazyItems = null;
    private BarChartModel dailyChart;
    private BarChartModel weeklyChart;
    private BarChartModel monthlyChart;
    private List<ActivityCount> dailyActivities;
    private TxInstance selected;
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
    private String interval;
    private ArrayList<String> selectedFilters;
    //private String selectedFilters;
    private boolean patientsFlag;
    private boolean scheduledFlag;
    
    public TxInstanceController() {
        df = new SimpleDateFormat("MM/dd/yy");
        GregorianCalendar gc = new GregorianCalendar();
        endDate = new Date();
        gc.setTime(endDate);
        gc.add(Calendar.MONTH, -1);
        startDate = gc.getTime();
        dailyChart = new BarChartModel();

        weeklyChart = new BarChartModel();
        monthlyChart = new BarChartModel();
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
        monthlyDisplayMode = "Summary";
        weeklyChartmax = 0;
        monthlyChartmax = 0;
        selectedFilters =  new ArrayList<>();
        selectedFilters.add("all-tx");
        patientsFlag = true;
        scheduledFlag = false;
        handleDateSelect();
    }

    public TxInstance getSelected() {
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

    public boolean isPatientsFlag() {
        return patientsFlag;
    }

    public void setPatientsFlag(boolean patientsFlag) {
        this.patientsFlag = patientsFlag;
    }

    public boolean isScheduledFlag() {
        return scheduledFlag;
    }

    public void setScheduledFlag(boolean scheduledFlag) {
        this.scheduledFlag = scheduledFlag;
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

    public String getInterval() {
        return interval;
    }

    public void setInterval(String interval) {
        this.interval = interval;
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

    public List<String> getSelectedFilters() {
        return selectedFilters;
    }

    public void setSelectedFilters(ArrayList<String> selectedFilters) {
        this.selectedFilters = selectedFilters;
    }

    public void setSelected(TxInstance selected) {
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

    private TxInstanceFacade getFacade() {
        return ejbFacade;
    }

    public TxInstance prepareCreate() {
        selected = new TxInstance();
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

    public List<TxInstance> getItems() {
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
        
        this.disableDailyCheckbox = (diffDays < 1);
        this.disableWeeklyCheckbox = (diffDays < 7);
        this.disableMonthlyCheckbox = (diffDays < 28);
        this.disableYearlyCheckbox = (diffDays < 365);
    }
    
    public LazyDataModel<TxInstance> getLazyItems() {
        if (lazyItems == null) {
            this.lazyItems = new LazyDataModel<TxInstance>(){
                private static final long serialVersionUID    = 1L;
                @Override
                public List<TxInstance> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                    int[] range = {first, first + pageSize};
                    List<TxInstance> result = getFacade().itemsDateRange(startDate, endDate, range);
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

    public TxInstance getActivity(java.lang.Integer id) {
        return getFacade().find(id);
    }

    public List<ActivityCount> getDailyActivities() {
        return dailyActivities;
    }

    public List<TxInstance> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<TxInstance> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }
    
    public List<Object[]> getDailyCounts(int index, String filter) {
        ArrayList<Date> allDates = new ArrayList<>();
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(startDate);
        while(gc.getTime().compareTo(endDate) < 0) {
            allDates.add(gc.getTime());
            gc.add(Calendar.DATE, 1);
        }
        
        List<Object[]> items;
        List<Object[]> itemsMerged = new ArrayList<>();
        //String fs = filterString();
        
        items = getFacade().getDailyCounts(startDate, endDate, new Long(index), filter, true, patientsFlag, scheduledFlag);
        
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
    
    public SynchronizedDescriptiveStatistics getDailySummary(String filter) {
        return getFacade().getDailyStats(startDate, endDate, new Long (-1), filter, includeWeekends, patientsFlag, scheduledFlag);
        
    }
    
        public TreeMap<Date,SynchronizedDescriptiveStatistics> getWeeklySummaryTr(int hospital, String filter){
        return getFacade().getWeeklySummaryStatsTr(startDate, endDate, new Long(hospital), filter, includeWeekends, patientsFlag, scheduledFlag);
    }
    
    public TreeMap<Date,SynchronizedDescriptiveStatistics> getWeeklySummaryAbs(int hospital, String filter){
        return getFacade().getWeeklySummaryStatsAbs(startDate, endDate, new Long(hospital), filter, includeWeekends, patientsFlag, scheduledFlag);
    }
    
    public TreeMap<Date,SynchronizedDescriptiveStatistics> getMonthlySummary(int hospital, String filter){
        ArrayList<Date> allDates = new ArrayList<>();
        GregorianCalendar gc = new GregorianCalendar();
        GregorianCalendar dc = new GregorianCalendar();
        gc.setTime(startDate);
        gc.set(Calendar.DAY_OF_MONTH, 1);
        //gc.setTime(new Date(gc.);
        while(gc.getTime().compareTo(endDate) < 0) {
            allDates.add(gc.getTime());
            gc.add(Calendar.MONTH, 1);
        }
        TreeMap<Date,SynchronizedDescriptiveStatistics> items;
        TreeMap<Date,SynchronizedDescriptiveStatistics> itemsMerged = new TreeMap<>();
        
        items = getFacade().getMonthlySummaryStats(startDate, endDate, new Long(hospital), filter, includeWeekends, patientsFlag, scheduledFlag);
        //FIXME FIXME FIXME
        DateFormat wdf = new SimpleDateFormat("yyyy mm");
        int i;
        //outer:
        for(Date d : allDates) {
            i = 0;
            SynchronizedDescriptiveStatistics val = new SynchronizedDescriptiveStatistics();
            val.addValue(0.0);
            int yr = 0;
            int mo = 0;
            int gcy = 0;
            int gcm = 0;
            for(Date dt : items.keySet()){
                dc.setTime(dt);
                yr = dc.get(Calendar.YEAR);
                mo = dc.get(Calendar.MONTH);
                gc.setTime(d);
                gcy = gc.get(Calendar.YEAR);
                gcm = gc.get(Calendar.MONTH);
                if(yr == gcy && mo == gcm) {
                   val = items.get(dt);
                   break;
                } 
                i++;
            }
            itemsMerged.put(d,val);
        }
        return itemsMerged; 
    }
    
    public List<Object[]> getWeeklyCounts(Long index, String filter) {
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
      
        items = getFacade().getWeeklyCounts(startDate, endDate, index, filter, includeWeekends, patientsFlag, scheduledFlag);
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
    
    public List<Object[]> getMonthlyCounts(Long index, String filter) {
        ArrayList<Date> allDates = new ArrayList<>();
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(startDate);
        gc.set(Calendar.DAY_OF_MONTH, 1);
        //gc.setTime(new Date(gc.);
        while(gc.getTime().compareTo(endDate) < 0) {
            allDates.add(gc.getTime());
            gc.add(Calendar.MONTH, 1);
        }
        List<Object[]> items;
        List<Object[]> itemsMerged = new ArrayList<>();
        
        items = getFacade().getMonthlyCounts(startDate, endDate, index, filter, includeWeekends, patientsFlag);
        //FIXME FIXME FIXME
        DateFormat wdf = new SimpleDateFormat("yyyy mm");
        int i;
        //outer:
        for(Date d : allDates) {
            i = 0;
            Long val = new Long(0);
            Double yr = 0.0;
            Double mo = 0.0;
            int gcy = 0;
            int gcm = 0;
            while(i < items.size()) {
                yr = ((Double)items.get(i)[0]);
                mo = ((Double)items.get(i)[1]);
                gc.setTime(d);
                gcy = gc.get(Calendar.YEAR);
                gcm = gc.get(Calendar.MONTH) + 1; //stupid java zero-indexed months
                if(yr.intValue() == gcy && mo.intValue() == gcm) {
                   val = (Long)items.get(i)[2];
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
    
    public BarChartModel getDailyBarChart() {
        return this.dailyChart;
    }

    public BarChartModel getWeeklyBarChart() {
        return weeklyChart;
    }
    
    public BarChartModel getMonthlyBarChart() {
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
       
       dailyActivities = getFacade().getDailyActivities(this.selectedDate, hospitalID.longValue(), imrtOnly);
       
       
    }
    
    public void drawDaily(DateFormat df) {
        //this.dailyChart.clear();
        dailyChart.setLegendPosition("ne");
        dailyChart.setSeriesColors("8C3130, E0AB5D, 4984D0, 2C2A29, 33460D,49182D");
        dailyChart.setShadow(false);
        Axis yAx = dailyChart.getAxis(AxisType.Y);
        Axis xAx = dailyChart.getAxis(AxisType.X);
        yAx.setMin(0);
        xAx.setLabel("Date");
        if(patientsFlag) {
            yAx.setLabel("Patients");
            if (scheduledFlag) {
                this.dailyChart.setTitle("Patients Scheduled");
            } else {
                this.dailyChart.setTitle("Patients Treated");
            }
        } else {
            yAx.setLabel("Treatments");
            if (scheduledFlag) {
                this.dailyChart.setTitle("Scheduled Treatments");
            } else {
                this.dailyChart.setTitle("Completed Treatments");
            }
        }
        xAx.setTickAngle(45);
        xAx.setTickFormat("%m/%d/%y");
        dailyChart.setZoom(false);
        dailyChart.setExtender("function(){"
                + " var interval = 1; "
                + " var items = this.cfg.axes.xaxis.ticks.length; "
                + "if( items > 730) { interval = 365; } "
                + "else if( items > 180) { interval = 30; } "
                + "else if( items > 42) { interval = 7; } "
                + "for(var i = 0; i < this.cfg.axes.xaxis.ticks.length; i++) { "
                + "   if(i % interval != 0) { "
                + "     this.cfg.axes.xaxis.ticks[i] = \" \"; "
                + "   } "
                + " } "
                + "}");

        /*dailyChart.setExtender("function(){"
                    +"this.cfg.axes.xaxis.renderer = $.jqplot.DateAxisRenderer;"
                    +"console.log(this);"
                + "}");*/
        dailyChart.setStacked(true);
        int curSeries = 0;
        List<Object[]> events;
        hideDailyTab = true;
        for (Integer fac: selectedFacilities) {
            for(String filter : selectedFilters) {
                String hospital = "All";
                if( fac > 0 ) {
                    hospital =  hFacade.find(fac).getHospitalname();
                }
                if(this.selectedTimeIntervals.contains("Daily")) {
                    ChartSeries series = new ChartSeries();
                    series.setLabel(hospital + "\n" + filter);
                    events = getDailyCounts(fac, filter);
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
    }
    
    /*private String filterString() {
        String retval = "";
        for(String item : selectedFilters ) {
            retval += "," + item;
        }
        return retval;
    }*/
    
    public void drawWeekly(DateFormat df) {
        this.weeklyChart.clear();
        weeklyChart.setLegendPosition("ne");
        weeklyChart.setSeriesColors("8C3130, E0AB5D, 4984D0, 2C2A29, 33460D,49182D");
        weeklyChart.setShadow(false);
        Axis yAx = weeklyChart.getAxis(AxisType.Y);
        Axis xAx = weeklyChart.getAxis(AxisType.X);
        if(patientsFlag) {
            yAx.setLabel("Patients");
            if (scheduledFlag) {
                 this.weeklyChart.setTitle("Patients Scheduled");
            } else {
                this.weeklyChart.setTitle("Patients Treated"); 
            }
        } else {
            yAx.setLabel("Treatments");
            if (scheduledFlag) {
                this.weeklyChart.setTitle("Scheduled Treatments");
            } else {
                this.weeklyChart.setTitle("Completed Treatments");
            }
        }
        yAx.setMin(0);
        xAx.setLabel("Date");
        xAx.setTickAngle(45);
        this.weeklyErrorBars = new JSONArray();
        this.weeklyErrorLabels = new JSONArray();
        weeklyChartmax = 0;
        //int curSeries = 0;
        List<Object[]> events;
        
        for (Integer fac: selectedFacilities) {
            for(String filter : selectedFilters) {
                String hospital = "All";
                if( fac > 0 ) {
                    hospital =  hFacade.find(fac).getHospitalname();
                }

                GregorianCalendar gc = new GregorianCalendar();

                if (this.selectedTimeIntervals.contains("Weekly") && this.weeklyDisplayMode.equals("Raw") &&  this.weeklySegmentationMode.equals("Absolute") ) {
                    ChartSeries wSeries = new ChartSeries();
                    wSeries.setLabel(hospital + "\n" + filter);
                    events = this.getWeeklyCounts(new Long(fac), filter);

                    for (Object[] event : events) {
                        Date d = (Date)event[0];
                        gc.setTime(d);
                        gc.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                        String xval = df.format(gc.getTime());
                        //if( gc.get(Calendar.MONTH) == Calendar.DECEMBER && gc.get(Calendar.WEEK_OF_YEAR) == 1){
                        //    xval = (gc.get(Calendar.YEAR) + 1) + " Week " + gc.get(Calendar.WEEK_OF_YEAR);
                        //}
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
                    wSumSeries.setLabel(hospital);
                    Map<Date,SynchronizedDescriptiveStatistics> wSumStats = this.getWeeklySummaryAbs(fac, filter);
                    JSONArray errorData = new JSONArray();
                    JSONArray errorTextData = new JSONArray();
                    ArrayList<Date> allMondays = new ArrayList<>();
                    GregorianCalendar wgc = new GregorianCalendar();
                    wgc.setTime(startDate);
                    wgc.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                    while(wgc.getTime().before(endDate)) {
                        allMondays.add(wgc.getTime());
                        wgc.add(Calendar.DATE, 7);
                    }

                    wklyOuter:
                    for(Date mon : allMondays) {
                        String xval = df.format(mon);
                        Double yval = 0.0; 
                        JSONObject errorItem = new JSONObject();
                        for(Date key : wSumStats.keySet()) {
                            if(mon.equals(key)) {
                                xval = df.format(key);
                                yval = wSumStats.get(key).getMean();
                                Double twoSigma = (2 * (wSumStats.get(key).getStandardDeviation())) / wSumStats.get(key).getMean();
                                if( (yval + (yval * twoSigma)) > weeklyChartmax){
                                    weeklyChartmax = calcChartMax(yval, twoSigma);
                                    yAx.setMax(weeklyChartmax);
                                }

                                try {
                                    errorItem.put("min", twoSigma);
                                    errorItem.put("max", twoSigma);
                                    errorData.put(errorItem);
                                    errorTextData.put("");
                                } catch (Exception e) {
                                    System.out.println("error bar generation failed");
                                }
                                if(Double.isNaN(yval)) {
                                    yval = 0.0;
                                }
                                wSumSeries.set(xval,yval);
                                continue wklyOuter;
                            }
                        }
                        try {
                                    errorItem.put("min", 0.0);
                                    errorItem.put("max", 0.0);
                                    errorData.put(errorItem);
                                    errorTextData.put("");
                                } catch (Exception e) {
                                    System.out.println("error bar generation failed");
                                }
                        wSumSeries.set(xval,yval);
                    }

                    this.weeklyErrorBars.put(errorData);
                    this.weeklyErrorLabels.put(errorTextData);
                    weeklyChart.setExtender("function(){ this.cfg.seriesDefaults.rendererOptions.fillToZero = true; this.cfg.seriesDefaults.rendererOptions.errorBarWidth = 2; " +
                        "this.cfg.axes.yaxis.numberTicks = 7; " +
                        "this.cfg.seriesDefaults.rendererOptions.errorBarColor = 'black';" + 
                        "this.cfg.seriesDefaults.rendererOptions.errorData = " + weeklyErrorBars.toString() + "; " +
                        "this.cfg.seriesDefaults.rendererOptions.errorTextData = " + weeklyErrorLabels.toString() + ";}");
                    weeklyChart.addSeries(wSumSeries);
                    hideWeeklyTab = false;
                }

                if(this.weeklySegmentationMode.equals("Trailing")) {
                    ChartSeries wTrSumSeries = new ChartSeries();
                    Map<Date,SynchronizedDescriptiveStatistics> wTrSumStats = this.getTrailingWeeklySummary(fac, filter);
                    JSONArray errorData = new JSONArray();
                    JSONArray errorTextData = new JSONArray();

                    for(Date key : wTrSumStats.keySet()) {
                        String xval = this.df.format(key);
                        Double yval;
                        if(this.weeklyDisplayMode.equals("Summary")) {
                            wTrSumSeries.setLabel(hospital + "\n" + filter);
                            yval = wTrSumStats.get(key).getMean();
                            Double twoSigma = errorBar(wTrSumStats.get(key).getStandardDeviation(), wTrSumStats.get(key).getMean());
                            if( (yval + (yval * twoSigma)) > weeklyChartmax ){
                                weeklyChartmax = calcChartMax(yval, twoSigma);
                                yAx.setMax(weeklyChartmax);
                            }
                            JSONObject errorItem = new JSONObject();
                            try {
                                errorItem.put("min", twoSigma);
                                errorItem.put("max", twoSigma);
                                errorData.put(errorItem);
                                errorTextData.put("");
                            } catch (Exception e) {
                                System.out.println("error bar generation failed");
                            }
                        } else {
                            //FIXME
                            if( wTrSumStats.get(key).getMax() > weeklyChartmax){

                                //yAx.setMax(new Double(wTrSumStats.get(key).getMax()).intValue());
                            }
                            wTrSumSeries.setLabel(hospital);
                            yval = wTrSumStats.get(key).getSum();
                        }
                        wTrSumSeries.set(xval,yval);
                    }

                    this.weeklyErrorBars.put(errorData);
                    this.weeklyErrorLabels.put(errorTextData);
                    weeklyChart.setExtender("function(){ this.cfg.seriesDefaults.rendererOptions.fillToZero = true; this.cfg.seriesDefaults.rendererOptions.errorBarWidth = 2; " +
                        "this.cfg.axes.yaxis.numberTicks = 7; " +
                        "this.cfg.seriesDefaults.rendererOptions.errorBarColor = 'black';" + 
                        "this.cfg.seriesDefaults.rendererOptions.errorData = " + weeklyErrorBars.toString() + "; " +
                        "this.cfg.seriesDefaults.rendererOptions.errorTextData = " + weeklyErrorLabels.toString() + ";}");
                    weeklyChart.addSeries(wTrSumSeries);
                    hideWeeklyTab = false;
                }

                //curSeries++;
            }
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
        this.monthlyChart.clear();
        monthlyChart.setLegendPosition("ne");
        monthlyChart.setSeriesColors("8C3130, E0AB5D, 4984D0, 2C2A29, 33460D,49182D");
        monthlyChart.setShadow(false);
        Axis yAx = monthlyChart.getAxis(AxisType.Y);
        Axis xAx = monthlyChart.getAxis(AxisType.X);
        yAx.setMin(0);
        xAx.setLabel("Month");
        xAx.setTickAngle(45);
        if(patientsFlag) {
            yAx.setLabel("Patients");
            if(scheduledFlag) {
                this.monthlyChart.setTitle("Patients Scheduled");
            } else {
                this.monthlyChart.setTitle("Patients Treated");
            }
        } else {
            yAx.setLabel("Treatments");
            if(scheduledFlag) {
                this.monthlyChart.setTitle("ScheduledTreatments");
            } else {
                this.monthlyChart.setTitle("Completed Treatments");
            }
        }
        this.monthlyErrorBars = new JSONArray();
        this.monthlyErrorLabels = new JSONArray();
        ChartSeries mSeries;
        List<Object[]> events;
        
        if (this.monthlyDisplayMode.equals("Raw")) {
            this.monthlyErrorBars = new JSONArray();
            this.monthlyErrorLabels = new JSONArray();
            
            for (Integer fac: selectedFacilities) {
                for(String filter : selectedFilters) {
                    mSeries = new ChartSeries();
                    String hospital = "All";
                    if( fac > 0 ) {
                        hospital =  hFacade.find(fac).getHospitalname();
                    }

                    //Monthly total
                    events = this.getMonthlyCounts(new Long(fac), filter);

                    mSeries.setLabel(hospital);

                    for (Object[] event : events) {
                        String xval = df.format((Date)(event[0]));
                        Long yval = (Long)event[1];
                        mSeries.set(xval, yval);
                        if(yval > monthlyChartmax){
                            yAx = monthlyChart.getAxis(AxisType.Y);
                            monthlyChartmax = calcChartMax(yval.doubleValue(), 0.0);
                            yAx.setMax(monthlyChartmax);
                        }
                    }
                    monthlyChart.setExtender("function(){ this.cfg.seriesDefaults.rendererOptions.fillToZero = true; this.cfg.seriesDefaults.rendererOptions.errorBarWidth = 2; " +
                        "this.cfg.axes.yaxis.numberTicks = 7; " +
                        "this.cfg.seriesDefaults.rendererOptions.errorBarColor = 'black';" + 
                        "this.cfg.seriesDefaults.rendererOptions.errorData = " + monthlyErrorBars.toString() + "; " +
                        "this.cfg.seriesDefaults.rendererOptions.errorTextData = " + monthlyErrorLabels.toString() + ";}");
                    monthlyChart.addSeries(mSeries);
                }
            }
        } else {
            //monthly summary
            for (Integer fac: selectedFacilities) {
                for(String filter : selectedFilters)  {
                    String hospital = "All";
                    if( fac > 0 ) {
                        hospital =  hFacade.find(fac).getHospitalname();
                    }

                    ChartSeries mSumSeries = new ChartSeries();

                    mSumSeries.setLabel(hospital);
                    Map<Date,SynchronizedDescriptiveStatistics> mSumStats = this.getMonthlySummary(fac, filter);

                    JSONArray errorData = new JSONArray();
                    JSONArray errorTextData = new JSONArray();

                    for(Date key : mSumStats.keySet()) {
                        String xval = df.format(key);
                        Double yval = mSumStats.get(key).getMean();
                        Double twoSigma = errorBar(mSumStats.get(key).getStandardDeviation(), mSumStats.get(key).getMean());
                        if( (yval + (yval * twoSigma)) > monthlyChartmax ){
                            yAx = monthlyChart.getAxis(AxisType.Y);
                            monthlyChartmax = calcChartMax(yval, twoSigma);
                            yAx.setMax(monthlyChartmax);
                        }
                        JSONObject errorItem = new JSONObject();
                        try {
                            if(twoSigma.isNaN()) { twoSigma = 0.0; }
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
                    monthlyChart.setExtender("function(){ this.cfg.seriesDefaults.rendererOptions.fillToZero = true; this.cfg.seriesDefaults.rendererOptions.errorBarWidth = 2; " +
                        "this.cfg.axes.yaxis.numberTicks = 7; " +
                        "this.cfg.seriesDefaults.rendererOptions.errorBarColor = 'black';" + 
                        "this.cfg.seriesDefaults.rendererOptions.errorData = " + monthlyErrorBars.toString() + "; " +
                        "this.cfg.seriesDefaults.rendererOptions.errorTextData = " + monthlyErrorLabels.toString() + ";}");
                    monthlyChart.addSeries(mSumSeries);
                }
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
        this.dailyChart = new BarChartModel();
        this.weeklyChart = new BarChartModel();
        this.weeklyErrorBars = new JSONArray();
        this.weeklyErrorLabels = new JSONArray();
        this.monthlyErrorBars = new JSONArray();
        this.monthlyErrorLabels = new JSONArray();
        weeklyChartmax = 0;
        monthlyChartmax = 0;
        
        DateFormat wdf = new SimpleDateFormat("yyyy 'Week' ww");
        DateFormat mdf = new SimpleDateFormat("yyyy MMM");
        hideDailyTab = true;
        
        if(this.selectedTimeIntervals.contains("Daily")) {
            drawDaily(df);
        }
        if(this.selectedTimeIntervals.contains("Weekly")) {
            drawWeekly(df);
        }
        if(this.selectedTimeIntervals.contains("Monthly")) {
            drawMonthly(mdf);
        }
    }

    private Map<Date, SynchronizedDescriptiveStatistics> getTrailingWeeklySummary(Integer hospital, String filter) {
        return getFacade().getWeeklyTrailingSummaryStats(startDate, endDate, new Long(hospital), filter, includeWeekends, patientsFlag, scheduledFlag);
    }
    
    public SynchronizedDescriptiveStatistics getMonthlySummary() {
        return getFacade().getMonthlyStats(startDate, endDate, imrtOnly, includeWeekends);
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

    @FacesConverter(forClass = TxInstance.class)
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
            if (object instanceof TxInstance) {
                TxInstance o = (TxInstance) object;
                return getStringKey(o.getActivityinstanceser());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), TxInstance.class.getName()});
                return null;
            }
        }

    }

}
