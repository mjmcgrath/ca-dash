/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.umm.radonc.ca_dash.model;

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
    
     public List<Object[]> getDailyCounts(Date start, Date end, Long hospitalSer, boolean imrtOnly, boolean includeWeekends) {
        //CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        //cq.select(cq.from(Activity.class));cast result list
        
        //CriteriaBuilder cb = getEntityManager().getCriteriaBuilder()
        String imrtString = "";
        String weekendString = "";
        String hospString = "";
        if(imrtOnly) { 
            imrtString = "AND a.procedurecodeser.shortcomment LIKE '%IMRT%' ";
        }
        
        //if(!includeWeekends) {
           weekendString = " AND date_part('dow', dt) <> 0 AND date_part('dow', dt) <> 6 ";
        //}
        
        if(hospitalSer > 0) {
            hospString = "AND hospitalser = ? ";
        }
            
        javax.persistence.Query q = getEntityManager()
                .createNativeQuery("SELECT dt, COUNT (DISTINCT patientser) FROM tx_flat_s WHERE " +
                        " dt IS NOT NULL AND dt >= ? AND dt <= ? " +  
                        hospString +
                        " AND (procedurecode = '77403' OR " +
                        "procedurecode = '77408' OR " +
                        "procedurecode = '77413' OR " +
                        "procedurecode = '77404' OR " +
                        "procedurecode = '77409' OR " +
                        "procedurecode = '77414' OR " +
                        "procedurecode = '77418' OR " +
                        "procedurecode = '77416' OR " +
                        "procedurecode = 'G0251' OR " +
                        "procedurecode = 'G0173') " +
                         weekendString +
                        "GROUP BY dt " +
                        "ORDER BY dt ASC") 
                .setParameter(1, start)
                .setParameter(2, end);
        if(hospitalSer > 0) {
                q.setParameter(3, hospitalSer);
        }
        List<Object[]> retval = q.getResultList();
        return retval;
    }

    public SynchronizedDescriptiveStatistics getDailyStats(Date startDate, Date endDate, Long hospital, boolean imrtOnly, boolean includeWeekends) {
        SynchronizedDescriptiveStatistics stats = new SynchronizedDescriptiveStatistics();
        List<Object[]> counts = getDailyCounts(startDate, endDate, hospital, imrtOnly, includeWeekends);
        for(Object[] item : counts) {
            stats.addValue(((Long)item[1]).doubleValue());
        }
        return stats;
    }

    public TreeMap<String, SynchronizedDescriptiveStatistics> getWeeklySummaryStats(Date startDate, Date endDate, Long hospitalser, boolean imrtOnly, boolean includeWeekends) {
        Calendar cal = new GregorianCalendar();
        TreeMap<String,SynchronizedDescriptiveStatistics> retval = new TreeMap<>();
        
        List<Object[]> events  = getDailyCounts(startDate, endDate, hospitalser, imrtOnly, includeWeekends);
        
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
                retval.put(prevYrWk, currStats);
                currStats = new SynchronizedDescriptiveStatistics();
            }
            
            currStats.addValue(count);
            i++;
        }
        retval.put(prevYrWk, currStats);

        return retval;
    }

    public TreeMap<String, SynchronizedDescriptiveStatistics> getMonthlySummaryStats(Date startDate, Date endDate, Long hospitalser, boolean imrtOnly, boolean includeWeekends) {
        Calendar cal = new GregorianCalendar();
        TreeMap<String,SynchronizedDescriptiveStatistics> retval = new TreeMap<>();
        
        List<Object[]> events ;
        
        events = getDailyCounts(startDate, endDate, hospitalser, imrtOnly, includeWeekends);
        
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

    public List<Object[]> getWeeklyCounts(Date startDate, Date endDate, Long hospital, boolean imrtOnly, boolean includeWeekends) {
        String imrtString = "";
        String hospString = "";
        if(imrtOnly) {
            imrtString = "AND tf.shortcomment LIKE '%IMRT%' ";
        }
        
        if(hospital != null && hospital > 0) {
            hospString =  "AND tf.hospitalser = ? ";
        }
        
        javax.persistence.Query q = getEntityManager().createNativeQuery(
                "SELECT date_part('year', tf.dt) AS yr, date_part('month', tf.dt) AS mo, date_part('week', tf.dt) AS wk, count(DISTINCT tf.patientser) " +
                "FROM tx_flat_s tf " +
                "WHERE tf.dt IS NOT NULL AND tf.dt >= ? AND tf.dt <= ? " +
                "AND tf.procedurecode != '00000' " + imrtString + hospString +
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

    public List<Object[]> getMonthlyCounts(Date startDate, Date endDate, Long hospital, boolean imrtOnly, boolean includeWeekends) {
        String imrtString = "";
        String hospString = "";
        if(imrtOnly) {
            imrtString = "AND tf.shortcomment LIKE '%IMRT%' ";
        }
        
        if(hospital != null && hospital > 0) {
            hospString =  "AND tf.hospitalser = ? ";
        }
        
        javax.persistence.Query q = getEntityManager().createNativeQuery(
                "SELECT date_part('year', tf.dt) AS yr, date_part('month', tf.dt) AS mn, count(DISTINCT tf.patientser) " +
                "FROM tx_flat_s tf " +
                "WHERE tf.dt IS NOT NULL AND tf.dt >= ? AND tf.dt <= ? " +
                "AND tf.procedurecode != '00000' " + imrtString + hospString +
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

    public Map<Date, SynchronizedDescriptiveStatistics> getWeeklyTrailingSummaryStats(Date startDate, Date endDate, Long hospitalser, boolean imrtOnly, boolean includeWeekends) {
        TreeMap<Date,SynchronizedDescriptiveStatistics> retval = new TreeMap();
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(endDate);
        Date d;
        SynchronizedDescriptiveStatistics stats;
        while(gc.getTime().compareTo(startDate) > 0) {
            d = gc.getTime();
            gc.add(Calendar.DATE, -7);
            stats = getDailyStats(gc.getTime(), d, hospitalser, imrtOnly, includeWeekends);
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
        if(filter != null) {
            filterString += " AND (";
            if(filter.contains("imrt")) {
               filterString = filterString + "procedurecode = '77403' OR " +
                        "procedurecode = '77408' OR " +
                        "procedurecode = '77413' OR " +
                        "procedurecode = '77404' OR " +
                        "procedurecode = '77409' OR " +
                        "procedurecode = '77414' OR " +
                        "procedurecode = '77418' OR " +
                        "procedurecode = '77416' OR " +
                        "procedurecode = 'G0251' OR " +
                        "procedurecode = 'G0173' ";
            }
            if(filter.contains("igrt")) {
               if(!(filterString.equals(""))) {
                    filterString += " OR ";
                }
                filterString = filterString += "procedurecode = '77021' OR " +
                                               "procedurecode = '77014' OR " +
                                               "procedurecode = '0197T' ";
            }
            if (filter.contains("vmat")) {
                if(!(filterString.equals(""))) {
                    filterString += " OR ";
                }
            
            }
            filterString += ") ";
        }
        javax.persistence.Query q = getEntityManager().createNativeQuery(
                "select tf.phys, count(DISTINCT tf.patientser) " +
                "FROM tx_flat_s tf " +
                "WHERE tf.dt IS NOT NULL AND tf.dt >= ? AND tf.dt <= ? " +
                "AND tf.procedurecode != '00000' " + filterString + hospString +
                "GROUP BY tf.phys;")
                .setParameter(1, startDate)
                .setParameter(2, endDate);
        
        if(hospital != null && hospital > 0) {
            q.setParameter(3, hospital);
        }
        List<Object[]> retval = q.getResultList();
        return retval;
    }

    public SynchronizedDescriptiveStatistics getMonthlyStats(Date startDate, Date endDate, boolean imrtOnly, boolean includeWeekends) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
