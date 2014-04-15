/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.umm.radonc.ca.dash.model;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author mmcgrath
 */
@Stateless
public class DataFacade extends AbstractFacade<Data> {
    @PersistenceContext(unitName = "dataPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public DataFacade() {
        super(Data.class);
    }
    
}
