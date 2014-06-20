/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.umm.radonc.ca_dash.model;

import java.util.Date;
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
        
        if(!includeWeekends) {
           weekendString = "AND FUNC('date_part', 'dow', a.fromdateofservice) <> 0 AND FUNC('date_part', 'dow', a.fromdateofservice) <> 6 ";
        }
        
        if(hospitalSer > 0) {
            hospString = " hospitalser = ? ";
        }
            
        javax.persistence.Query q = getEntityManager()
                .createNativeQuery("SELECT COUNT (DISTINCT patientser) , hospitalname, dt FROM tx_flat_t WHERE " +
                        "dt >= ? AND dt <= ? " +  
                        hospString +
                        "(procedurecode = '77403' OR " +
                        "procedurecode = '77408' OR " +
                        "procedurecode = '77413' OR " +
                        "procedurecode = '77404' OR " +
                        "procedurecode = '77409' OR " +
                        "procedurecode = '77414' OR " +
                        "procedurecode = '77418' OR " +
                        "procedurecode = '77416' OR " +
                        "procedurecode = 'G0251' OR " +
                        "procedurecode = 'G0173') " +
                        "GROUP BY dt" +
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public TreeMap<String, SynchronizedDescriptiveStatistics> getWeeklySummaryStats(Date startDate, Date endDate, Long aLong, boolean imrtOnly, boolean includeWeekends) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public TreeMap<String, SynchronizedDescriptiveStatistics> getMonthlySummaryStats(Date startDate, Date endDate, Long aLong, boolean imrtOnly, boolean includeWeekends) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public List<Object[]> getWeeklyCounts(Date startDate, Date endDate, Long index, boolean imrtOnly, boolean includeWeekends) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public List<TxInstance> itemsDateRange(Date startDate, Date endDate, int[] range) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public int itemsDateRangeCount(Date startDate, Date endDate) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public List<Object[]> getMonthlyCounts(Date startDate, Date endDate, Long index, boolean imrtOnly, boolean includeWeekends) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public List<ActivityCount> getDailyActivities(Date selectedDate, long longValue, boolean imrtOnly) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Map<Date, SynchronizedDescriptiveStatistics> getWeeklyTrailingSummaryStats(Date startDate, Date endDate, Long aLong, boolean imrtOnly, boolean includeWeekends) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public SynchronizedDescriptiveStatistics getMonthlyStats(Date startDate, Date endDate, boolean imrtOnly, boolean includeWeekends) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
