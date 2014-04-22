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
import javax.persistence.criteria.CriteriaQuery;

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
    
    public List<Object> getDailyCounts(Date start, Date end) {
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
    
}
