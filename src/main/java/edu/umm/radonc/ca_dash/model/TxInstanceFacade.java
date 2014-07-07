/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.umm.radonc.ca_dash.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.commons.math.stat.descriptive.SynchronizedDescriptiveStatistics;

/**
 *
 * @author mmcgrath
 */
@Stateless
public class TxInstanceFacade extends AbstractFacade<TxInstance> {
    @PersistenceContext(unitName = "clinicalActivityPU")
    private EntityManager em;
    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TxInstanceFacade() {
        super(TxInstance.class);
    }
    
    public List<Object[]> getDailyCounts(Date start, Date end, Long hospitalSer, String filter, boolean includeWeekends, boolean ptflag) {
        //CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        //cq.select(cq.from(Activity.class));cast result list
        
        //CriteriaBuilder cb = getEntityManager().getCriteriaBuilder()
        String imrtString = buildFilterString(filter);
        String weekendString = "";
        String hospString = "";
        String ptString = "";
        
        //if(!includeWeekends) {
        weekendString = " AND date_part('dow', completed) <> 0 AND date_part('dow', completed) <> 6 ";
        //}
        
        if(ptflag) {
            ptString = "DISTINCT";
        }
        
        if(hospitalSer > 0) {
            hospString = "AND hospitalser = ? ";
        }
            
        javax.persistence.Query q = getEntityManager()
                .createNativeQuery("SELECT completed, COUNT ("+ ptString + " patientser) FROM tx_flat_v2 WHERE " +
                        " completed IS NOT NULL AND completed >= ? AND completed <= ? " +  
                        hospString +
                        imrtString +
                         weekendString +
                        "GROUP BY completed " +
                        "ORDER BY completed ASC") 
                .setParameter(1, start)
                .setParameter(2, end);
        if(hospitalSer > 0) {
                q.setParameter(3, hospitalSer);
        }
        List<Object[]> retval = q.getResultList();
        return retval;
    }

    public SynchronizedDescriptiveStatistics getDailyStats(Date startDate, Date endDate, Long hospital, String filter, boolean includeWeekends, boolean ptflag) {
        SynchronizedDescriptiveStatistics stats = new SynchronizedDescriptiveStatistics();
        List<Object[]> counts = getDailyCounts(startDate, endDate, hospital, filter, includeWeekends, ptflag);
        for(Object[] item : counts) {
            stats.addValue(((Long)item[1]).doubleValue());
        }
        return stats;
    }

    public TreeMap<Date, SynchronizedDescriptiveStatistics> getWeeklySummaryStats(Date startDate, Date endDate, Long hospitalser, String filter, boolean includeWeekends, boolean ptflag) {
        Calendar cal = new GregorianCalendar();
        TreeMap<Date,SynchronizedDescriptiveStatistics> retval = new TreeMap<>();
        
        List<Object[]> events  = getDailyCounts(startDate, endDate, hospitalser, filter, includeWeekends, ptflag);
        
        DateFormat df = new SimpleDateFormat("MM/dd/yy");
        cal.setTime(startDate);
        int wk = cal.get(Calendar.WEEK_OF_YEAR);
        int mo = cal.get(Calendar.MONTH);
        int yr = cal.get(Calendar.YEAR);
        if(mo == Calendar.DECEMBER && wk == 1) {
            yr = yr + 1;
        } else if (mo == Calendar.JANUARY && wk == 52) {
            yr = yr - 1;
        }
        String currYrWk = yr + "-" + String.format("%02d", wk);
        String prevYrWk = "";
        SynchronizedDescriptiveStatistics currStats = new SynchronizedDescriptiveStatistics();
        int i = 0;
        while(cal.getTime().before(endDate) && i < events.size()) {
            
            Object[] event = events.get(i);
            Date d = (Date)event[0];
            Long count = (Long)event[1];
            
            prevYrWk = currYrWk;
            cal.setTime(d);
            cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            wk = cal.get(Calendar.WEEK_OF_YEAR);
            mo = cal.get(Calendar.MONTH);
            yr = cal.get(Calendar.YEAR);
            if(mo == Calendar.DECEMBER && wk == 1) {
                yr = yr + 1;
            } else if (mo == Calendar.JANUARY && wk == 52) {
                yr = yr - 1;
            }
            currYrWk = yr + "-" + String.format("%02d", wk);

            
            if( !(prevYrWk.equals(currYrWk)) ) {
                retval.put(cal.getTime(), currStats);
                currStats = new SynchronizedDescriptiveStatistics();
            }
            
            currStats.addValue(count);
            i++;
        }
        retval.put(cal.getTime(), currStats);

        return retval;
    }

    public TreeMap<String, SynchronizedDescriptiveStatistics> getMonthlySummaryStats(Date startDate, Date endDate, Long hospitalser, String filter, boolean includeWeekends, boolean ptflag) {
        Calendar cal = new GregorianCalendar();
        TreeMap<String,SynchronizedDescriptiveStatistics> retval = new TreeMap<>();
        
        List<Object[]> events ;
        
        events = getDailyCounts(startDate, endDate, hospitalser, filter, includeWeekends, ptflag);
        
        cal.setTime(startDate);

        int mo = cal.get(Calendar.MONTH);
        int yr = cal.get(Calendar.YEAR);
        
        String currMoYr = yr + "-" + String.format("%02d", mo + 1);
        String prevMoYr = "";
        SynchronizedDescriptiveStatistics currStats = new SynchronizedDescriptiveStatistics();
        int i = 0;
        while(cal.getTime().before(endDate) && i < events.size()) {
            
            Object[] event = events.get(i);
            Date d = (Date)event[0];
            Long count = (Long)event[1];
            
            prevMoYr = currMoYr;
            cal.setTime(d);
            mo = cal.get(Calendar.MONTH);
            yr = cal.get(Calendar.YEAR);

            currMoYr = yr + "-" + String.format("%02d", mo + 1);

            
            if( !(prevMoYr.equals(currMoYr)) ) {
                retval.put(prevMoYr, currStats);
                currStats = new SynchronizedDescriptiveStatistics();
            }
            
            currStats.addValue(count);
            i++;
        }
        retval.put(prevMoYr, currStats);

        return retval;
    }

    public List<Object[]> getWeeklyCounts(Date startDate, Date endDate, Long hospital, String filter, boolean includeWeekends) {
        String imrtString = "";
        String hospString = "";
        
        imrtString = buildFilterString(filter);
        
        if(hospital != null && hospital > 0) {
            hospString =  "AND tf.hospitalser = ? ";
        }
        
        javax.persistence.Query q = getEntityManager().createNativeQuery(
                "SELECT date_part('year', tf.completed) AS yr, date_part('month', tf.completed) AS mo, date_part('week', tf.completed) AS wk, count(DISTINCT tf.patientser) " +
                "FROM tx_flat_v2 tf " +
                "WHERE tf.completed IS NOT NULL AND tf.completed >= ? AND tf.completed <= ? " +
                "AND tf.cpt != '00000' " + imrtString + hospString +
                "GROUP BY yr, mo, wk ORDER BY yr, mo, wk ASC")
                .setParameter(1, startDate)
                .setParameter(2, endDate);
        
        if(hospital != null && hospital > 0) {
            q.setParameter(3, hospital);
        }
        
        
        List<Object[]> retval = q.getResultList();
        return retval;
    }

    public List<TxInstance> itemsDateRange(Date startDate, Date endDate, int[] range) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public int itemsDateRangeCount(Date startDate, Date endDate) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public List<Object[]> getMonthlyCounts(Date startDate, Date endDate, Long hospital, String filter, boolean includeWeekends) {
        String imrtString = "";
        String hospString = "";
        
        imrtString = buildFilterString(filter);
        
        if(hospital != null && hospital > 0) {
            hospString =  "AND tf.hospitalser = ? ";
        }
        
        javax.persistence.Query q = getEntityManager().createNativeQuery(
                "SELECT date_part('year', tf.completed) AS yr, date_part('month', tf.completed) AS mn, count(DISTINCT tf.patientser) " +
                "FROM tx_flat_v2 tf " +
                "WHERE tf.completed IS NOT NULL AND tf.completed >= ? AND tf.completed <= ? " +
                "AND tf.cpt != '00000' " + imrtString + hospString +
                "GROUP BY yr, mn ORDER BY yr,mn ASC;")
                .setParameter(1, startDate)
                .setParameter(2, endDate);
        
        if(hospital != null && hospital > 0) {
            q.setParameter(3, hospital);
        }
        
        List<Object[]> retval = q.getResultList();
        return retval;
    }

    public List<ActivityCount> getDailyActivities(Date selectedDate, long longValue, boolean imrtOnly) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Map<Date, SynchronizedDescriptiveStatistics> getWeeklyTrailingSummaryStats(Date startDate, Date endDate, Long hospitalser, String filter, boolean includeWeekends, boolean ptflag) {
        TreeMap<Date,SynchronizedDescriptiveStatistics> retval = new TreeMap();
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(endDate);
        Date d;
        SynchronizedDescriptiveStatistics stats;
        while(gc.getTime().compareTo(startDate) > 0) {
            d = gc.getTime();
            gc.add(Calendar.DATE, -7);
            stats = getDailyStats(gc.getTime(), d, hospitalser, filter, includeWeekends, ptflag);
            retval.put(gc.getTime(), stats);
        }
       
        return retval;
    }
    
    public List<Object[]> DoctorCounts(Date startDate, Date endDate, Long hospital, String filter) {
        String hospString = "";
        String filterString = ""; 
        if(hospital != null && hospital > 0) {
           hospString = " AND tf.hospitalser = ? ";
        }
        
        filterString = buildFilterString(filter);
        
        javax.persistence.Query q = getEntityManager().createNativeQuery(
                "select (dr.lastname || ', ' || dr.firstname) AS doctor, COUNT(DISTINCT tf.patientser) " +
"FROM tx_flat_v2 tf " +
"INNER JOIN patientdoctor ptdr ON tf.patientser = ptdr.patientser " +
"INNER JOIN doctor dr ON ptdr.resourceser = dr.resourceser " +
"WHERE tf.completed IS NOT NULL " +
"AND ptdr.primaryflag = 'TRUE' AND ptdr.oncologistflag = 'TRUE' " +
                "AND tf.completed IS NOT NULL AND tf.completed >= ? AND tf.completed <= ? " +
                filterString + hospString +
                "GROUP BY dr.lastname, dr.firstname;")
                .setParameter(1, startDate)
                .setParameter(2, endDate);
        
        if(hospital != null && hospital > 0) {
            q.setParameter(3, hospital);
        }
        List<Object[]> retval = q.getResultList();
        return retval;
    }
    
    private String buildFilterString(String filter){
        String filterString = "";
        if(filter != null && !"".equals(filter)) {
            filterString += " AND (";
            if(filter.contains("imrt")) {
               filterString = filterString + "((cpt = '77403' OR " +
                        "cpt = '77408' OR " +
                        "cpt = '77413' OR " +
                        "cpt = '77404' OR " +
                        "cpt = '77409' OR " +
                        "cpt = '77414' OR " +
                        "cpt = '77418' OR " +
                        "cpt = '77416' OR " +
                        "cpt = 'G0251' OR " +
                        "cpt = 'G0173' ) AND (aria_pm LIKE '%IMRT%'))";
            }
            if(filter.contains("igrt")) {
               if(!(filterString.endsWith("("))) {
                    filterString += " OR ";
                }
                filterString = filterString += "cpt = '77021' OR " +
                                               "cpt = '77014' OR " +
                                               "cpt = '0197T' ";
            }
            if (filter.contains("vmat")) {
                if(!(filterString.endsWith("("))) {
                    filterString += " OR ";
                }
                filterString += "(aria_pm = 'Rapid Arc' AND cpt = '77418') "; //FIXME!
            
            }
            filterString += ") ";
        }
        return filterString;
    }

    public SynchronizedDescriptiveStatistics getMonthlyStats(Date startDate, Date endDate, boolean imrtOnly, boolean includeWeekends) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
