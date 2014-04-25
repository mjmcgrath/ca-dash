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
import javax.persistence.SqlResultSetMapping;
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
                cb.and(
                        rt.get(Activity_.fromdateofservice).isNotNull(),
                        cb.between(rt.get(Activity_.fromdateofservice), start, end)
                )
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
                cb.and(
                        rt.get(Activity_.fromdateofservice).isNotNull(),
                        cb.between(rt.get(Activity_.fromdateofservice), start, end)
                )
        );
 
        Query q = em.createQuery(cq);
        return ((Long)(q.getSingleResult())).intValue();
        
    }
    
    
    
    //TODO: Add parameters to query
    public List<Object[]> getDailyCounts(Date start, Date end) {
        //CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        //cq.select(cq.from(Activity.class));cast result list
        
        javax.persistence.Query q = getEntityManager()
                .createQuery("SELECT a.fromdateofservice, count(a.actinstproccodeser) " + 
                        " FROM Activity a " + 
                        "WHERE a.fromdateofservice IS NOT NULL " +
                        "AND a.fromdateofservice >= :start AND a.fromdateofservice <= :end " +
                        "GROUP BY a.fromdateofservice " + 
                        "ORDER BY a.fromdateofservice ASC") 
                .setParameter("start", start)
                .setParameter("end", end);
        List<Object[]> retval = q.getResultList();
        return retval;
    }
    
    //TODO: Fix query
    public List<Object> getWeeklyCounts(Date start, Date end) {
        //CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        //cq.select(cq.from(Activity.class));cast result list
        
        javax.persistence.Query q = getEntityManager()
                .createQuery("SELECT a.fromdateofservice, count(a.actinstproccodeser) " + 
                        " FROM Activity a " + 
                        "WHERE a.fromdateofservice IS NOT NULL " + 
                        "GROUP BY a.fromdateofservice " + 
                        "ORDER BY a.fromdateofservice ASC");
        List<Object> retval = q.getResultList();
        return retval;
    }
    
    //TODO: Fix query -- accept parameters & probably someday don't use native query
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
