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
import java.util.TreeMap;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.*;
import org.apache.commons.math.stat.descriptive.*;

/**
 *
 * @author michaelmcgrath
 */
@Stateless
public class ActivityFacade extends AbstractFacade<ActivityAIPC> {
    @PersistenceContext(unitName = "clinicalActivityPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ActivityFacade() {
        super(ActivityAIPC.class);
    }
    
    public List<ActivityAIPC> itemsDateRange(Date start, Date end, int[] range) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery(ActivityAIPC.class);
        Root<ActivityAIPC> rt = cq.from(ActivityAIPC.class);
        cq.where(
                cb.and(rt.get(ActivityAIPC_.fromdateofservice).isNotNull(),
                cb.and(
                        cb.notEqual(rt.get(ActivityAIPC_.procedurecodeser), 528),
                        cb.notEqual(rt.get(ActivityAIPC_.procedurecodeser), 529),
                        cb.notEqual(rt.get(ActivityAIPC_.procedurecodeser), 530),
                        cb.between(rt.get(ActivityAIPC_.fromdateofservice), start, end)
                ))
        );
        cq.orderBy(cb.asc(rt.get(ActivityAIPC_.fromdateofservice)));
        Query q = em.createQuery(cq);
        q.setMaxResults(range[1] - range[0] + 1);
        q.setFirstResult(range[0]);
        return q.getResultList();
        
    }
    
    public int itemsDateRangeCount(Date start, Date end) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery(ActivityAIPC.class);
        Root<ActivityAIPC> rt = cq.from(ActivityAIPC.class);
        cq.select(cb.count(rt.get(ActivityAIPC_.actinstproccodeser)));
        cq.where(
                cb.and(rt.get(ActivityAIPC_.fromdateofservice).isNotNull(),
                cb.and(
                        cb.notEqual(rt.get(ActivityAIPC_.procedurecodeser), 528),
                        cb.notEqual(rt.get(ActivityAIPC_.procedurecodeser), 529),
                        cb.notEqual(rt.get(ActivityAIPC_.procedurecodeser), 530),
                        cb.between(rt.get(ActivityAIPC_.fromdateofservice), start, end)
                ))
        );
 
        Query q = em.createQuery(cq);
        return ((Long)(q.getSingleResult())).intValue();
        
    }
    
    public List<ActivityCount> getDailyActivities(Date day, boolean imrtOnly){
        String imrtString = "";
        if(imrtOnly) { imrtString = "AND p.shortcomment LIKE '%IMRT%' ";}
        Query q = getEntityManager().createNativeQuery("SELECT p.shortcomment, p.procedurecode, COUNT (p.procedurecode) as activityCount " + 
                "FROM actinstproccode a, procedurecode p " + 
                "WHERE p.procedurecodeser = a.procedurecodeser AND a.fromdateofservice = ? " +
                "AND p.procedurecode <> '00000' " + imrtString +
                "GROUP BY p.procedurecode, p.shortcomment ORDER BY p.procedurecode ASC", "dailyActivities")
                .setParameter(1, day);
        
        
        //CriteriaBuilder cb = em.getCriteriaBuilder();
        //CriteriaQuery cq = cb.createQuery(Activity.class);
        //Query q = getEntityManager().. .createNamedQuery("Activity.getDailyActivities","dailyActivities").setParameter(1, day);
        
        
        return q.getResultList();
    }
    
    
        public List<ActivityCount> getDailyActivities(Date day, Long hospitalser, boolean imrtOnly){
        String imrtString = "";
        if(imrtOnly) { imrtString = "AND p.shortcomment LIKE '%IMRT%' ";}
        
        String qString =  "SELECT p.shortcomment, p.procedurecode, COUNT (p.procedurecode) as activityCount " + 
                "FROM actinstproccode a, procedurecode p, department d " + 
                "WHERE p.procedurecodeser = a.procedurecodeser AND a.fromdateofservice = ? " +
                "AND a.departmentser = d.departmentser " + 
                "AND d.hospitalser = ? " +
                "AND p.procedurecode <> '00000' " + imrtString +
                "GROUP BY p.procedurecode, p.shortcomment ORDER BY p.procedurecode ASC"; 
            
        Query q = getEntityManager().createNativeQuery(qString, "dailyActivities")
                .setParameter(1, day)
                .setParameter(2, hospitalser);
        
        
        //CriteriaBuilder cb = em.getCriteriaBuilder();
        //CriteriaQuery cq = cb.createQuery(Activity.class);
        //Query q = getEntityManager().. .createNamedQuery("Activity.getDailyActivities","dailyActivities").setParameter(1, day);
        
        
        return q.getResultList();
    }
        
    public SynchronizedDescriptiveStatistics getDailyStats(Date start, Date end, boolean imrtOnly, boolean includeWeekends){
        SynchronizedDescriptiveStatistics stats = new SynchronizedDescriptiveStatistics();
        List<Object[]> counts = getDailyCounts(start, end, imrtOnly, includeWeekends);
        for(Object[] item : counts) {
            stats.addValue(((Long)item[1]).doubleValue());
        }
        return stats;
    }
    
    public SynchronizedDescriptiveStatistics getDailyStats(Date start, Date end, Long hospitalser, boolean imrtOnly, boolean includeWeekends){
        SynchronizedDescriptiveStatistics stats = new SynchronizedDescriptiveStatistics();
        List<Object[]> counts = getDailyCounts(start, end, hospitalser, imrtOnly, includeWeekends);
        for(Object[] item : counts) {
            stats.addValue(((Long)item[1]).doubleValue());
        }
        return stats;
    }
    
    public TreeMap<Date,SynchronizedDescriptiveStatistics> getWeeklyTrailingSummaryStats(Date start, Date end, Long hospitalser, boolean imrtOnly, boolean includeWeekends) {
        TreeMap<Date,SynchronizedDescriptiveStatistics> retval = new TreeMap();
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(end);
        Date d;
        SynchronizedDescriptiveStatistics stats;
        while(gc.getTime().compareTo(start) > 0) {
            d = gc.getTime();
            gc.add(Calendar.DATE, -7);
            if(hospitalser <= 0) {
                stats = getDailyStats(gc.getTime(), d, imrtOnly, includeWeekends);
            } else {
                stats = getDailyStats(gc.getTime(), d, hospitalser, imrtOnly, includeWeekends);
            }
            retval.put(gc.getTime(), stats);
        }
       
        return retval;
        
    }
    
    public SynchronizedDescriptiveStatistics getWeeklyStats(Date start, Date end, Long hospitalser, boolean imrtOnly, boolean includeWeekends) {
        SynchronizedDescriptiveStatistics stats = new SynchronizedDescriptiveStatistics();
        List<Object[]> counts = getWeeklyCounts(start, end, hospitalser, imrtOnly, includeWeekends);
        for(Object[] item : counts) {
            stats.addValue(((Long)item[2]).doubleValue());
        }
        return stats;
    }
    
    public TreeMap<String,SynchronizedDescriptiveStatistics> getWeeklySummaryStats(Date start, Date end, Long hospitalser, boolean imrtOnly, boolean includeWeekends){
        Calendar cal = new GregorianCalendar();
        TreeMap<String,SynchronizedDescriptiveStatistics> retval = new TreeMap<>();
        
        List<Object[]> events ;
        
        if(hospitalser < 0) {
            events = getDailyCounts(start, end, imrtOnly, includeWeekends);
        } else {
           events = getDailyCounts(start, end, hospitalser, imrtOnly, includeWeekends);
        }
        cal.setTime(start);
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
        while(cal.getTime().before(end) && i < events.size()) {
            
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
    
    public SynchronizedDescriptiveStatistics getMonthlyStats(Date start, Date end, boolean imrtOnly, boolean includeWeekends) {
        //TODO: implement me
        return null;
    }
    
    public List<Object[]> getDailyCounts(Date start, Date end, boolean imrtOnly, boolean includeWeekends) {
        //CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        //cq.select(cq.from(Activity.class));cast result list
        
        //CriteriaBuilder cb = getEntityManager().getCriteriaBuilder()
        
        String imrtString = "";
        String weekendString = "";
        
        if(imrtOnly) { 
            imrtString = "AND a.procedurecodeser.shortcomment LIKE '%IMRT%' ";
        }
        
        if(!includeWeekends) {
            weekendString = "AND FUNC('date_part', 'dow', a.fromdateofservice) <> 0 AND FUNC('date_part', 'dow', a.fromdateofservice) <> 6 ";
        }
        
        javax.persistence.Query q = getEntityManager()
                .createQuery("SELECT a.fromdateofservice, count(a.actinstproccodeser) " + 
                        " FROM ActivityAIPC a " +
                        "WHERE a.fromdateofservice IS NOT NULL " +
                        "AND a.fromdateofservice >= :start AND a.fromdateofservice <= :end " +
                        "AND a.procedurecodeser.procedurecode != '00000' " + imrtString + weekendString +
                        "GROUP BY a.fromdateofservice " + 
                        "ORDER BY a.fromdateofservice ASC") 
                .setParameter("start", start)
                .setParameter("end", end);
        List<Object[]> retval = q.getResultList();
        return retval;
    }
    
    public List<Object[]> getDailyCounts(Date start, Date end, Long hospitalSer, boolean imrtOnly, boolean includeWeekends) {
        //CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        //cq.select(cq.from(Activity.class));cast result list
        
        //CriteriaBuilder cb = getEntityManager().getCriteriaBuilder()
        String imrtString = "";
        String weekendString = "";
        if(imrtOnly) { 
            imrtString = "AND a.procedurecodeser.shortcomment LIKE '%IMRT%' ";
        }
        
        if(!includeWeekends) {
           weekendString = "AND FUNC('date_part', 'dow', a.fromdateofservice) <> 0 AND FUNC('date_part', 'dow', a.fromdateofservice) <> 6 ";
        }
            
        javax.persistence.Query q = getEntityManager()
                .createQuery("SELECT a.fromdateofservice, count(a.actinstproccodeser) " + 
                        " FROM ActivityAIPC a " + 
                        "WHERE a.fromdateofservice IS NOT NULL " +
                        "AND a.fromdateofservice >= :start AND a.fromdateofservice <= :end " +
                        "AND a.departmentser.hospitalser.hospitalser = :hosp " +
                        "AND a.procedurecodeser.procedurecode != '00000' " + imrtString + weekendString +
                        "GROUP BY a.fromdateofservice " + 
                        "ORDER BY a.fromdateofservice ASC") 
                .setParameter("start", start)
                .setParameter("end", end)
                .setParameter("hosp", hospitalSer);
        List<Object[]> retval = q.getResultList();
        return retval;
    }
    
    public List<Object[]> getWeeklyCountsTrailing(Date start, Date end, Long hospital, boolean imrtOnly, boolean includeWeekends) {
        String imrtString = "";
        String imrtSel = "";
        String hospString = "";
        String hospSel = "";
        if(imrtOnly) {
            imrtSel = "";
            imrtString = "AND p.shortcomment LIKE '%IMRT%' ";
        }
        
        if(hospital != null && hospital > 0) {
            hospSel = ", department d ";
            hospString =  "AND a.departmentser = d.departmentser AND d.hospitalser = ? ";
        }
        
        javax.persistence.Query q = getEntityManager().createNativeQuery(
                "SELECT date_part('year', a.fromdateofservice) AS yr, date_part('month', a.fromdateofservice) AS mo, date_part('week', a.fromdateofservice) AS wk, count(a.actinstproccodeser) " +
                "FROM actinstproccode a, procedurecode p " + hospSel +
                "WHERE a.fromdateofservice IS NOT NULL " +
                "AND a.fromdateofservice >= ? AND a.fromdateofservice <= ? " +
                "AND a.procedurecodeser = p.procedurecodeser " +
                "AND p.procedurecode != '00000' " + imrtString + hospString +
                "GROUP BY yr, mo, wk ORDER BY yr, mo, wk ASC;")
                .setParameter(1, start)
                .setParameter(2, end);
        
        if(hospital != null && hospital > 0) {
            q.setParameter(3, hospital);
        }
        
        
        List<Object[]> retval = q.getResultList();
        return retval;
    }
    
    public List<Object> getQuarterlyCounts(){
        return null;
    }
    
     //TODO: Fix query
    public List<Object[]> getWeeklyCounts(Date start, Date end, Long hospital, boolean imrtOnly, boolean includeWeekends) {
        //CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        //cq.select(cq.from(Activity.class));cast result list
        String imrtString = "";
        String imrtSel = "";
        String hospString = "";
        String hospSel = "";
        if(imrtOnly) {
            imrtSel = "";
            imrtString = "AND p.shortcomment LIKE '%IMRT%' ";
        }
        
        if(hospital != null && hospital > 0) {
            hospSel = ", department d ";
            hospString =  "AND a.departmentser = d.departmentser AND d.hospitalser = ? ";
        }
        
        javax.persistence.Query q = getEntityManager().createNativeQuery(
                "SELECT date_part('year', a.fromdateofservice) AS yr, date_part('month', a.fromdateofservice) AS mo, date_part('week', a.fromdateofservice) AS wk, count(a.actinstproccodeser) " +
                "FROM actinstproccode a, procedurecode p " + hospSel +
                "WHERE a.fromdateofservice IS NOT NULL " +
                "AND a.fromdateofservice >= ? AND a.fromdateofservice <= ? " +
                "AND a.procedurecodeser = p.procedurecodeser " +
                "AND p.procedurecode != '00000' " + imrtString + hospString +
                "GROUP BY yr, mo, wk ORDER BY yr, mo, wk ASC;")
                .setParameter(1, start)
                .setParameter(2, end);
        
        if(hospital != null && hospital > 0) {
            q.setParameter(3, hospital);
        }
        
        
        List<Object[]> retval = q.getResultList();
        return retval;
    }
    
    //TODO: Fix query -- don't use native query
    public List<Object[]> getMonthlyCounts(Date start, Date end, Long hospital, boolean imrtOnly, boolean includeWeekends) {
        String imrtString = "";
        String imrtSel = "";
        String hospString = "";
        String hospSel = "";
        if(imrtOnly) {
            imrtSel = "";
            imrtString = "AND p.shortcomment LIKE '%IMRT%' ";
        }
        
        if(hospital != null && hospital > 0) {
            hospSel = ", department d ";
            hospString =  "AND a.departmentser = d.departmentser AND d.hospitalser = ? ";
        }
        
        javax.persistence.Query q = getEntityManager().createNativeQuery(
                "SELECT date_part('year', a.fromdateofservice) AS yr, date_part('month', a.fromdateofservice) AS mn, count(a.actinstproccodeser) " +
                "FROM actinstproccode a, procedurecode p " + hospSel +
                "WHERE a.fromdateofservice IS NOT NULL " +
                "AND a.fromdateofservice >= ? AND a.fromdateofservice <= ? " +
                "AND a.procedurecodeser = p.procedurecodeser " +
                "AND p.procedurecode != '00000' " + imrtString + hospString +
                "GROUP BY yr, mn ORDER BY yr,mn ASC;")
                .setParameter(1, start)
                .setParameter(2, end);
        
        if(hospital != null && hospital > 0) {
            q.setParameter(3, hospital);
        }
        
        List<Object[]> retval = q.getResultList();
        return retval;
    }

    public TreeMap<String, SynchronizedDescriptiveStatistics> getMonthlySummaryStats(Date start, Date end, Long hospitalser, boolean imrtOnly, boolean includeWeekends) {
        Calendar cal = new GregorianCalendar();
        TreeMap<String,SynchronizedDescriptiveStatistics> retval = new TreeMap<>();
        
        List<Object[]> events ;
        
        if(hospitalser < 0) {
            events = getDailyCounts(start, end, imrtOnly, includeWeekends);
        } else {
           events = getDailyCounts(start, end, hospitalser, imrtOnly, includeWeekends);
        }
        cal.setTime(start);

        int mo = cal.get(Calendar.MONTH);
        int yr = cal.get(Calendar.YEAR);
        
        String currMoYr = yr + "-" + String.format("%02d", mo + 1);
        String prevMoYr = "";
        SynchronizedDescriptiveStatistics currStats = new SynchronizedDescriptiveStatistics();
        int i = 0;
        while(cal.getTime().before(end) && i < events.size()) {
            
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
        
        
    
}
