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
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
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

    public List<Object[]> getDailyCounts(Date start, Date end, Long hospitalSer, String filter, boolean includeWeekends, boolean ptflag, boolean scheduledFlag) {
        //CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        //cq.select(cq.from(Activity.class));cast result list

        //CriteriaBuilder cb = getEntityManager().getCriteriaBuilder()
        String imrtString = buildFilterString(filter);
        String weekendString = "";
        String hospString = "";
        String ptString = "";
        String scheduledString = "completed";

        //if(!includeWeekends) {
        weekendString = " AND date_part('dow', " + scheduledString + " ) <> 0 AND date_part('dow', " + scheduledString + " ) <> 6 ";
        //}

        if (ptflag) {
            ptString = "DISTINCT";
        }
        
        if (scheduledFlag) {
            scheduledString = "scheduledstarttime";
            weekendString = " AND date_part('dow', " + scheduledString + " ) <> 0 AND date_part('dow', " + scheduledString + " ) <> 6 "; 
        }

        if (hospitalSer > 0) {
            hospString = "AND hospitalser = ? ";
        }

        javax.persistence.Query q = getEntityManager()
                .createNativeQuery("select date_trunc('day'," + scheduledString + ") as completed, COUNT(" + ptString + " patientser)"+
                    "from scheduledactivity  " +
                    imrtJoin +
                    "\n JOIN department dp ON dp.departmentser = activityinstance.departmentser\n" +
                    "where activitycode not in ('Physics QA')\n" +
                    "and patientser is not null and scheduledactivity.activityinstanceser\n" +
                    "in (\n" +
                    "select activityinstanceser from attendee where objectstatus='Active' \n" +
                    "and activityinstanceser in \n" +
                    "(select activityinstanceser from scheduledactivity where " + scheduledString  + " between ? and ? " + weekendString + " ) \n" +
                    "and resourceser in (1034,1564,1392,2285,4737,4736,2689,2692,2453,2398) \n" +
                    ")\n" +
                    hospString +
                    imrtString + 
                    "GROUP BY completed ORDER BY completed") 
                .setParameter(1, start)
                .setParameter(2, end);
        if (hospitalSer > 0) {
            q.setParameter(3, hospitalSer);
        }
        List<Object[]> retval = q.getResultList();
        return retval;
    }

    public SynchronizedDescriptiveStatistics getDailyStats(Date startDate, Date endDate, Long hospital, String filter, boolean includeWeekends, boolean ptflag, boolean scheduledFlag) {
        SynchronizedDescriptiveStatistics stats = new SynchronizedDescriptiveStatistics();
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(endDate);
        gc.add(Calendar.DATE, -1);
        List<Object[]> counts = getDailyCounts(startDate, gc.getTime(), hospital, filter, includeWeekends, ptflag, scheduledFlag);
        for (Object[] item : counts) {
            stats.addValue(((Long) item[1]).doubleValue());
        }
        return stats;
    }

    public TreeMap<Date, SynchronizedDescriptiveStatistics> getWeeklySummaryStatsTr(Date startDate, Date endDate, Long hospitalser, String filter, 
            boolean includeWeekends, boolean ptflag, boolean scheduledFlag) {
        Calendar cal = new GregorianCalendar();
        TreeMap<Date, SynchronizedDescriptiveStatistics> retval = new TreeMap<>();

        List<Object[]> events = getDailyCounts(startDate, endDate, hospitalser, filter, includeWeekends, ptflag, scheduledFlag);

        DateFormat df = new SimpleDateFormat("MM/dd/yy");
        cal.setTime(startDate);
        SynchronizedDescriptiveStatistics currStats = new SynchronizedDescriptiveStatistics();
        int i = 0;
        int dayCount = 0;
        while (cal.getTime().before(endDate) && i < events.size()) {

            Object[] event = events.get(i);
            Date d = (Date) event[0];
            Long count = (Long) event[1];

           //cal.setTime(d);
            if (dayCount == 6) {
                retval.put(cal.getTime(), currStats);
                currStats = new SynchronizedDescriptiveStatistics();
                dayCount = -1;
                cal.add(Calendar.DATE, 7);
            }

            currStats.addValue(count);
            dayCount++;
            i++;
        }
        retval.put(cal.getTime(), currStats);

        return retval;
    }

    public TreeMap<Date, SynchronizedDescriptiveStatistics> getWeeklySummaryStatsAbs(Date startDate, Date endDate, Long hospitalser, String filter, 
            boolean includeWeekends, boolean ptflag, boolean scheduledFlag) {
        Calendar cal = new GregorianCalendar();
        TreeMap<Date, SynchronizedDescriptiveStatistics> retval = new TreeMap<>();

        //SET TO BEGINNING OF WK FOR ABSOLUTE CALC
        cal.setTime(startDate);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        startDate = cal.getTime();

        List<Object[]> events = getDailyCounts(startDate, endDate, hospitalser, filter, includeWeekends, ptflag, scheduledFlag);

        DateFormat df = new SimpleDateFormat("MM/dd/yy");
        cal.setTime(startDate);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        int wk = cal.get(Calendar.WEEK_OF_YEAR);
        int mo = cal.get(Calendar.MONTH);
        int yr = cal.get(Calendar.YEAR);
        if (mo == Calendar.DECEMBER && wk == 1) {
            yr = yr + 1;
        } else if (mo == Calendar.JANUARY && wk == 52) {
            yr = yr - 1;
        }
        String currYrWk = yr + "-" + String.format("%02d", wk);
        //String prevYrWk = "";
        String prevYrWk = currYrWk;
        SynchronizedDescriptiveStatistics currStats = new SynchronizedDescriptiveStatistics();
        int i = 0;
        while (cal.getTime().before(endDate) && i < events.size()) {

            Object[] event = events.get(i);
            Date d = (Date) event[0];
            Long count = (Long) event[1];

            cal.setTime(d);
            cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            wk = cal.get(Calendar.WEEK_OF_YEAR);
            mo = cal.get(Calendar.MONTH);
            yr = cal.get(Calendar.YEAR);
            if (mo == Calendar.DECEMBER && wk == 1) {
                yr = yr + 1;
            } else if (mo == Calendar.JANUARY && wk == 52) {
                yr = yr - 1;
            }
            currYrWk = yr + "-" + String.format("%02d", wk);

            if (!(prevYrWk.equals(currYrWk))) {
                GregorianCalendar lastMon = new GregorianCalendar();
                lastMon.setTime(cal.getTime());
                lastMon.add(Calendar.DATE, -7);
                retval.put(lastMon.getTime(), currStats);
                currStats = new SynchronizedDescriptiveStatistics();
            }

            prevYrWk = currYrWk;

            currStats.addValue(count);
            i++;
        }
        retval.put(cal.getTime(), currStats);

        return retval;
    }

    public TreeMap<Date, SynchronizedDescriptiveStatistics> getMonthlySummaryStats(Date startDate, Date endDate, Long hospitalser, String filter, 
            boolean includeWeekends, boolean ptflag, boolean scheduledFlag) {
        Calendar cal = new GregorianCalendar();
        TreeMap<Date, SynchronizedDescriptiveStatistics> retval = new TreeMap<>();
        GregorianCalendar oc = new GregorianCalendar();
        List<Object[]> events;

        events = getDailyCounts(startDate, endDate, hospitalser, filter, includeWeekends, ptflag, scheduledFlag);

        cal.setTime(startDate);

        int mo = cal.get(Calendar.MONTH);
        int yr = cal.get(Calendar.YEAR);

        int currYr = yr;
        int currMo = mo;
        int prevMo = -1;
        int prevYr = -1;
        SynchronizedDescriptiveStatistics currStats = new SynchronizedDescriptiveStatistics();
        int i = 0;
        while (cal.getTime().before(endDate) && i < events.size()) {

            Object[] event = events.get(i);
            Date d = (Date) event[0];
            Long count = (Long) event[1];

            prevMo = currMo;
            prevYr = currYr;
            cal.setTime(d);
            mo = cal.get(Calendar.MONTH);
            yr = cal.get(Calendar.YEAR);

            currMo = mo;
            currYr = yr;

            if (prevMo != currMo || prevYr != currYr) {
                oc.set(Calendar.MONTH, prevMo);
                oc.set(Calendar.YEAR, prevYr);
                oc.set(Calendar.DAY_OF_MONTH, 1);
                retval.put(oc.getTime(), currStats);
                currStats = new SynchronizedDescriptiveStatistics();
            }

            currStats.addValue(count);
            i++;
        }
        oc.set(Calendar.MONTH, currMo);
        oc.set(Calendar.YEAR, currYr);
        oc.set(Calendar.DAY_OF_MONTH, 1);
        retval.put(oc.getTime(), currStats);

        return retval;
    }

    public List<Object[]> getWeeklyCounts(Date startDate, Date endDate, Long hospital, String filter, boolean includeWeekends, boolean ptflag, boolean scheduledFlag) {
        String imrtString = "";
        String hospString = "";
        String ptString = "";
        String scheduledString = "completed";
        
        imrtString = buildFilterString(filter);

        if (hospital != null && hospital > 0) {
            hospString = "AND tf.hospitalser = ? ";
        }
        
        if (ptflag) {
            ptString = "DISTINCT";
        }
        
        if(scheduledFlag) {
            scheduledString = "scheduled";
        }

        javax.persistence.Query q = getEntityManager().createNativeQuery(
                "SELECT date_part('year', tf.completed) AS yr, date_part('month', tf.completed) AS mo, date_part('week', tf.completed) AS wk, count(" + ptString +  " tf.patientser) "
                + "FROM tx_flat_v5 tf "
                + "WHERE tf.completed IS NOT NULL AND tf." + scheduledString + " >= ? AND tf." + scheduledString + " <= ? "
                + "AND tf.cpt != '00000' " + imrtString + hospString
                + "GROUP BY yr, mo, wk ORDER BY yr, mo, wk ASC")
                .setParameter(1, startDate)
                .setParameter(2, endDate);

        if (hospital != null && hospital > 0) {
            q.setParameter(3, hospital);
        }

        List<Object[]> retval = q.getResultList();
        return retval;
    }

    public List<TxInstance> itemsDateRange(Date startDate, Date endDate, int[] range) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery(TxInstance.class);
        Root<TxInstance> rt = cq.from(TxInstance.class);
        cq.where(
                cb.and(rt.get(TxInstance_.completed).isNotNull(),
                        cb.between(rt.get(TxInstance_.completed), startDate, endDate)
                    )
        );
        cq.orderBy(cb.asc(rt.get(TxInstance_.completed)));
        Query q = em.createQuery(cq);
        q.setMaxResults(range[1] - range[0] + 1);
        q.setFirstResult(range[0]);
        return q.getResultList();
    }

    public int itemsDateRangeCount(Date startDate, Date endDate) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery(TxInstance.class);
        Root<TxInstance> rt = cq.from(TxInstance.class);
        cq.select(cb.count(rt.get(TxInstance_.activityinstanceser)));
        cq.where(
                cb.and(rt.get(TxInstance_.completed).isNotNull(),
                       cb.between(rt.get(TxInstance_.completed), startDate, endDate)
                )
        );
 
        Query q = em.createQuery(cq);
        return ((Long)(q.getSingleResult())).intValue();
    }

    public List<Object[]> getMonthlyCounts(Date startDate, Date endDate, Long hospital, String filter, boolean includeWeekends, boolean ptFlag) {
        String imrtString = "";
        String hospString = "";
        String ptString = "";
        
        imrtString = buildFilterString(filter);

        if (hospital != null && hospital > 0) {
            hospString = "AND tf.hospitalser = ? ";
        }
        
        if (ptFlag) {
            ptString = "DISTINCT";
        }

        javax.persistence.Query q = getEntityManager().createNativeQuery(
                "SELECT date_part('year', tf.completed) AS yr, date_part('month', tf.completed) AS mn, count( " + ptString + " tf.patientser) "
                + "FROM tx_flat_v5 tf "
                + "WHERE tf.completed IS NOT NULL AND tf.completed >= ? AND tf.completed <= ? "
                + "AND tf.cpt != '00000' " + imrtString + hospString
                + "GROUP BY yr, mn ORDER BY yr,mn ASC;")
                .setParameter(1, startDate)
                .setParameter(2, endDate);

        if (hospital != null && hospital > 0) {
            q.setParameter(3, hospital);
        }

        List<Object[]> retval = q.getResultList();
        return retval;
    }

    public List<ActivityCount> getDailyActivities(Date selectedDate, long longValue, boolean imrtOnly) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Map<Date, SynchronizedDescriptiveStatistics> getWeeklyTrailingSummaryStats(Date startDate, Date endDate, Long hospitalser, String filter, 
            boolean includeWeekends, boolean ptflag, boolean scheduledFlag) {
        TreeMap<Date, SynchronizedDescriptiveStatistics> retval = new TreeMap();
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(endDate);
        Date d;
        SynchronizedDescriptiveStatistics stats;
        while (gc.getTime().compareTo(startDate) > 0) {
            d = gc.getTime();
            gc.add(Calendar.DATE, -7);
            stats = getDailyStats(gc.getTime(), d, hospitalser, filter, includeWeekends, ptflag, scheduledFlag);
            retval.put(gc.getTime(), stats);
        }

        return retval;
    }
    
    public TreeMap<String, SynchronizedDescriptiveStatistics> machineStats(Date startDate, Date endDate, Long hospital, String filter) {
        TreeMap<String, SynchronizedDescriptiveStatistics> retval = new TreeMap<>();
                String hospString = "";
        String filterString = "";
        if (hospital != null && hospital > 0) {
            hospString = " AND tf.hospitalser = ? ";
        }

        filterString = buildFilterString(filter);

        javax.persistence.Query q = getEntityManager().createNativeQuery(
                "select machine, COUNT(DISTINCT tf.activityinstanceser) "
                + "FROM tx_flat_v5 tf "
                + "WHERE tf.completed IS NOT NULL AND tf.completed >= ? AND tf.completed <= ? "
                + filterString + hospString
                + "GROUP BY tf.machine, completed;")
                .setParameter(1, startDate)
                .setParameter(2, endDate);

        if (hospital != null && hospital > 0) {
            q.setParameter(3, hospital);
        }
        List<Object[]> results = q.getResultList();
        
        String currMachine = "";
        if(!(results.isEmpty())) {
            currMachine = (String)results.get(0)[0];
        }
        SynchronizedDescriptiveStatistics mStats = new SynchronizedDescriptiveStatistics();
        for(Object[] row : results) {
            String m = (String)row[0];
            if( !(m.equals(currMachine)) ) {
                retval.put(currMachine, mStats);
                mStats = new SynchronizedDescriptiveStatistics();
            }
            mStats.addValue((Long)row[1]);
            currMachine = m;
        }
        retval.put(currMachine, mStats);
        
        return retval;
    }
    
    
    public TreeMap<String, SynchronizedDescriptiveStatistics> doctorStats (Date startDate, Date endDate, Long hospital, String filter) {
        TreeMap<String, SynchronizedDescriptiveStatistics> retval = new TreeMap<>();
           String hospString = "";
        String filterString = "";
        if (hospital != null && hospital > 0) {
            hospString = " AND tf.hospitalser = ? ";
        }

        filterString = buildFilterString(filter);

        javax.persistence.Query q = getEntityManager().createNativeQuery(
                "select (dr.lastname || ', ' || dr.firstname) AS doctor, completed, COUNT(DISTINCT tf.patientser) "
                + "FROM tx_flat_v5 tf "
                + "INNER JOIN patientdoctor ptdr ON tf.patientser = ptdr.patientser "
                + "INNER JOIN doctor dr ON ptdr.resourceser = dr.resourceser "
                + "WHERE tf.completed IS NOT NULL "
                + "AND ptdr.primaryflag = 'TRUE' AND ptdr.oncologistflag = 'TRUE' "
                + "AND tf.completed IS NOT NULL AND tf.completed >= ? AND tf.completed <= ? "
                + filterString + hospString
                + "GROUP BY dr.lastname, dr.firstname, completed;")
                .setParameter(1, startDate)
                .setParameter(2, endDate);

        if (hospital != null && hospital > 0) {
            q.setParameter(3, hospital);
        }
        List<Object[]> results = q.getResultList();
        
        String currDoc = "";
        if(!(results.isEmpty())) {
            currDoc = (String)results.get(0)[0];
        }
        SynchronizedDescriptiveStatistics drStats = new SynchronizedDescriptiveStatistics();
        for(Object[] row : results) {
            String dr = (String)row[0];
            if( !(dr.equals(currDoc)) ) {
                retval.put(currDoc, drStats);
                drStats = new SynchronizedDescriptiveStatistics();
            }
            drStats.addValue((Long)row[2]);
            currDoc = dr;
        }
        retval.put(currDoc, drStats);
        
        return retval;
    }

    public TreeMap<String, Long>doctorPtCounts(Date startDate, Date endDate, Long hospital, String filter) {
        String hospString = "";
        String filterString = "";
        TreeMap<String,Long> retval = new TreeMap<>();
        
        if (hospital != null && hospital > 0) {
            hospString = " AND tf.hospitalser = ? ";
        }

        filterString = buildFilterString(filter);

        javax.persistence.Query q = getEntityManager().createNativeQuery(
                "select (dr.lastname || ', ' || dr.firstname) AS doctor, COUNT(DISTINCT tf.patientser) "
                + "FROM tx_flat_v5 tf "
                + "INNER JOIN patientdoctor ptdr ON tf.patientser = ptdr.patientser "
                + "INNER JOIN doctor dr ON ptdr.resourceser = dr.resourceser "
                + "WHERE tf.completed IS NOT NULL "
                + "AND ptdr.primaryflag = 'TRUE' AND ptdr.oncologistflag = 'TRUE' "
                + "AND tf.completed IS NOT NULL AND tf.completed >= ? AND tf.completed <= ? "
                + filterString + hospString
                + "GROUP BY dr.lastname, dr.firstname;")
                .setParameter(1, startDate)
                .setParameter(2, endDate);

        if (hospital != null && hospital > 0) {
            q.setParameter(3, hospital);
        }
        List<Object[]> result = q.getResultList();
        for(Object[] row : result ) {
            retval.put((String)row[0], (Long)row[1]);
        }
        return retval;
    }

    public TreeMap<String, Long> machineTxCounts(Date startDate, Date endDate, Long hospital, String filter) {
        String hospString = "";
        String filterString = "";
        TreeMap<String,Long> retval = new TreeMap<>();
        
        if (hospital != null && hospital > 0) {
            hospString = " AND tf.hospitalser = ? ";
        }

        filterString = buildFilterString(filter);

        javax.persistence.Query q = getEntityManager().createNativeQuery(
                "select machine, COUNT(DISTINCT tf.activityinstanceser) "
                + "FROM tx_flat_v5 tf "
                + "WHERE tf.completed IS NOT NULL AND tf.completed >= ? AND tf.completed <= ? "
                + filterString + hospString
                + "GROUP BY tf.machine;")
                .setParameter(1, startDate)
                .setParameter(2, endDate);

        if (hospital != null && hospital > 0) {
            q.setParameter(3, hospital);
        }
        List<Object[]> result = q.getResultList();
        for(Object[] row : result ) {
            retval.put((String)row[0], (Long)row[1]);
        }
        return retval;
    }

    private String buildFilterString(String filter) {
        String filterString = "";
        if (filter != null && !"".equals(filter)) {
            filterString += " AND (";
            if (filter.contains("imrt")) {
                filterString = filterString + "(cpt = '77418')";
            }
            else if (filter.contains("non")) {
                filterString = filterString + "(cpt <> '77418' " +
                    "AND cpt LIKE '774%' " +
                    "AND cpt <> '77421' " +
                    "AND cpt <> '77417' " +
                    "AND codetype = 'Technical') OR cpt LIKE 'G0%'";
            }
            else if (filter.contains("all-tx")) {
                filterString = filterString + "(cpt LIKE '774%' " +
                    "AND cpt <> '77421' " +
                    "AND cpt <> '77417' " +
                    "AND codetype = 'Technical') OR cpt LIKE 'G0%'";
            }
            
            else if (filter.contains("xray") || filter.contains("conebeam") || filter.contains("visionrt")) {
                if (!(filterString.endsWith("("))) {
                    filterString += " OR ";
                }
                if (filter.contains("xray")) {
                    filterString = filterString += "cpt = '77421'";
                }
                if( filter.contains("conebeam") ) {
                    if(filterString.endsWith("'")) {
                        filterString += " OR ";
                    }
                    filterString += "cpt = '77014'";
                }
                if(filter.contains("visionrt")) {
                    if(filterString.endsWith("'")) {
                        filterString += " OR ";
                    }
                    filterString += "cpt = '0197T' ";
                }
            }
            if (filter.contains("vmat")) {
                if (!(filterString.endsWith("("))) {
                    //FIXME
                    filterString += " OR ";
                }
                filterString += "(aria_pm = 'Rapid Arc' AND cpt = '77418') "; //FIXME!
            }
            if (filter.equals("overlap")) {
                //FIXME -- what are the CPT codes that indicate IGRT AND IMRT
                filterString = filterString + "(cpt = '77414')";
            }
            filterString += ") ";
        }
        return filterString;
    }

    public SynchronizedDescriptiveStatistics getMonthlyStats(Date startDate, Date endDate, boolean imrtOnly, boolean includeWeekends) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
