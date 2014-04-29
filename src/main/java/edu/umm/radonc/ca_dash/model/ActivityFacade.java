/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.umm.radonc.ca_dash.model;

import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.*;

/**
 *
 * @author michaelmcgrath
 */
@Stateless
public class ActivityFacade extends AbstractFacade<Activity> {
    @PersistenceContext(unitName = "clinicalActivityPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ActivityFacade() {
        super(Activity.class);
    }
    
    public List<Activity> itemsDateRange(Date start, Date end, int[] range) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery(Activity.class);
        Root<Activity> rt = cq.from(Activity.class);
        cq.where(
                cb.and(rt.get(Activity_.fromdateofservice).isNotNull(),
                cb.and(
                        cb.notEqual(rt.get(Activity_.procedurecodeser).get(Procedure_.procedurecode), "00000"),
                        cb.between(rt.get(Activity_.fromdateofservice), start, end)
                ))
        );
        cq.orderBy(cb.asc(rt.get(Activity_.fromdateofservice)));
        Query q = em.createQuery(cq);
        q.setMaxResults(range[1] - range[0] + 1);
        q.setFirstResult(range[0]);
        return q.getResultList();
        
    }
    
    public int itemsDateRangeCount(Date start, Date end) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery(Activity.class);
        Root<Activity> rt = cq.from(Activity.class);
        cq.select(cb.count(rt.get(Activity_.actinstproccodeser)));
        cq.where(
                cb.and(rt.get(Activity_.fromdateofservice).isNotNull(),
                cb.and(
                        cb.notEqual(rt.get(Activity_.procedurecodeser).get(Procedure_.procedurecode), "00000"),
                        cb.between(rt.get(Activity_.fromdateofservice), start, end)
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
    
    public List<Object[]> getDailyCounts(Date start, Date end, boolean imrtOnly) {
        //CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        //cq.select(cq.from(Activity.class));cast result list
        
        //CriteriaBuilder cb = getEntityManager().getCriteriaBuilder()
        
        String imrtString = "";
        if(imrtOnly) { 
            imrtString = "AND a.procedurecodeser.shortcomment LIKE '%IMRT%' ";
        }
        
        javax.persistence.Query q = getEntityManager()
                .createQuery("SELECT a.fromdateofservice, count(a.actinstproccodeser) " + 
                        " FROM Activity a " +
                        "WHERE a.fromdateofservice IS NOT NULL " +
                        "AND a.fromdateofservice >= :start AND a.fromdateofservice <= :end " +
                        "AND a.procedurecodeser.procedurecode != '00000' " + imrtString +
                        "GROUP BY a.fromdateofservice " + 
                        "ORDER BY a.fromdateofservice ASC") 
                .setParameter("start", start)
                .setParameter("end", end);
        List<Object[]> retval = q.getResultList();
        return retval;
    }
    
    public List<Object[]> getDailyCounts(Date start, Date end, Long hospitalSer, boolean imrtOnly) {
        //CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        //cq.select(cq.from(Activity.class));cast result list
        
        //CriteriaBuilder cb = getEntityManager().getCriteriaBuilder()
        String imrtString = "";
        if(imrtOnly) { 
            imrtString = "AND a.procedurecodeser.shortcomment LIKE '%IMRT%' ";
        } 
            
        javax.persistence.Query q = getEntityManager()
                .createQuery("SELECT a.fromdateofservice, count(a.actinstproccodeser) " + 
                        " FROM Activity a " + 
                        "WHERE a.fromdateofservice IS NOT NULL " +
                        "AND a.fromdateofservice >= :start AND a.fromdateofservice <= :end " +
                        "AND a.departmentser.hospitalser.hospitalser = :hosp " +
                        "AND a.procedurecodeser.procedurecode != '00000' " + imrtString +
                        "GROUP BY a.fromdateofservice " + 
                        "ORDER BY a.fromdateofservice ASC") 
                .setParameter("start", start)
                .setParameter("end", end)
                .setParameter("hosp", hospitalSer);
        List<Object[]> retval = q.getResultList();
        return retval;
    }
    
    
    //TODO: Fix query
    public List<Object[]> getWeeklyCounts(Date start, Date end, Long hospital, boolean imrtOnly) {
        //CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        //cq.select(cq.from(Activity.class));cast result list
        String imrtString = "";
        String imrtSel = "";
        String hospString = "";
        String hospSel = "";
        if(imrtOnly) {
            imrtSel = ", procedurecode p ";
            imrtString = "AND a.procedurecodeser.shortcomment LIKE '%IMRT%' ";
        }
        
        if(hospital != null && hospital > 0) {
            hospSel = ", department d ";
            hospString =  "AND a.departmentser = d.departmentser AND d.hospitalser = ? ";
        }
        
        javax.persistence.Query q = getEntityManager().createNativeQuery(
                "SELECT date_part('year', a.fromdateofservice) AS yr, date_part('week', a.fromdateofservice) AS wk, count(a.actinstproccodeser) " +
                "FROM actinstproccode a " + imrtSel + hospSel +
                "WHERE a.fromdateofservice IS NOT NULL " +
                "AND a.fromdateofservice >= ? AND a.fromdateofservice <= ? " +
                "AND a.procedurecodeser.procedurecode != '00000' " + imrtString + hospString +
                "GROUP BY yr, wk ORDER BY yr,wk ASC;")
                .setParameter(1, start)
                .setParameter(2, end);
        
        if(hospital != null && hospital > 0) {
            q.setParameter(3, hospital);
        }
        
        
        List<Object[]> retval = q.getResultList();
        return retval;
    }
    
    //TODO: Fix query -- don't use native query
    public List<Object> getMonthlyCounts(Date start, Date end) {
        //CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        //cq.select(cq.from(Activity.class));cast result list
        
        /*javax.persistence.Query q = getEntityManager().createQuery(
                "SELECT FUNCTION('date_part', 'year', a.fromdateofservice) AS yr, FUNCTION('date_part', 'month', a.fromdateofservice) AS mn, COUNT(a.actinstproccodeser) " +
                "FROM Activity a WHERE a.fromdateofservice IS NOT NULL " +
                "GROUP BY yr, mn " +
                "ORDER BY yr, mn ASC");*/
        
        javax.persistence.Query q = getEntityManager().createNativeQuery(
                "SELECT date_part('year', a.fromdateofservice) AS yr, date_part('month', a.fromdateofservice) AS mn, count(a.actinstproccodeser) " +
                "FROM actinstproccode a " +
                "WHERE a.fromdateofservice IS NOT NULL " +
                "AND a.fromdateofservice >= ? AND a.fromdateofservice <= ? " +
                "GROUP BY yr, mn ORDER BY yr,mn ASC;")
                .setParameter(1, start)
                .setParameter(2, end);
        
        List<Object> retval = q.getResultList();
        return retval;
    }
        
        
    
}
