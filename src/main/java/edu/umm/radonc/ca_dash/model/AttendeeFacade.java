/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.umm.radonc.ca_dash.model;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author mmcgrath
 */
@Stateless
public class AttendeeFacade extends AbstractFacade<Attendee> {
    @PersistenceContext(unitName = "securityPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public AttendeeFacade() {
        super(Attendee.class);
    }
    
}
