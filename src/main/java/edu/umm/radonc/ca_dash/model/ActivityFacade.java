/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.umm.radonc.ca_dash.model;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author mmcgrath
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
    
    public List<Object> getActivities() {
        em.createQuery("SELECT d from Activity d");
        return null;
    }
    
}
