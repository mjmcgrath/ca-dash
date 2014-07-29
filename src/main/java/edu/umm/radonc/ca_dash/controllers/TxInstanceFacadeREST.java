/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.umm.radonc.ca_dash.controllers;

import edu.umm.radonc.ca_dash.model.TxInstance;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author mmcgrath
 */
@ApplicationPath("rest")
@Stateless
@Path("txinstance")
public class TxInstanceFacadeREST extends AbstractFacade<TxInstance> {
    @PersistenceContext(unitName = "clinicalActivityPU")
    private EntityManager em;

    public TxInstanceFacadeREST() {
        super(TxInstance.class);
    }

    @POST
    @Override
    @Consumes({"application/xml", "application/json"})
    public void create(TxInstance entity) {
        super.create(entity);
    }

    @PUT
    @Path("{id}")
    @Consumes({"application/xml", "application/json"})
    public void edit(@PathParam("id") Integer id, TxInstance entity) {
        super.edit(entity);
    }

    @DELETE
    @Path("{id}")
    public void remove(@PathParam("id") Integer id) {
        super.remove(super.find(id));
    }

    @GET
    @Path("{id}")
    @Produces({"application/xml", "application/json"})
    public TxInstance find(@PathParam("id") Integer id) {
        return super.find(id);
    }

    @GET
    @Override
    @Produces({"application/xml", "application/json"})
    public List<TxInstance> findAll() {
        return super.findAll();
    }

    @GET
    @Path("{from}/{to}")
    @Produces({"application/xml", "application/json"})
    public List<TxInstance> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to) {
        return super.findRange(new int[]{from, to});
    }

    @GET
    @Path("count")
    @Produces("text/plain")
    public String countREST() {
        return String.valueOf(super.count());
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    
    private String buildFilterString(String filter) {
        String filterString = "";
        if (filter != null && !"".equals(filter)) {
            filterString += " AND (";
            if (filter.contains("imrt")) {
                filterString = filterString + "(cpt = '77403' OR "
                        + "cpt = '77408' OR "
                        + "cpt = '77413' OR "
                        + "cpt = '77404' OR "
                        + "cpt = '77409' OR "
                        + "cpt = '77414' OR "
                        + "cpt = '77418' OR "
                        + "cpt = '77416' OR "
                        + "cpt = 'G0251' OR "
                        + "cpt = 'G0173')";
            }
            if (filter.contains("igrt")) {
                if (!(filterString.endsWith("("))) {
                    filterString += " OR ";
                }
                filterString = filterString += "cpt = '77021' OR "
                        + "cpt = '77014' OR "
                        + "cpt = '0197T' ";
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
    
    @GET
    @Path("drpt")
    @Produces("application/json")
    public String drPtCounts(@QueryParam("data") String dataSet, @QueryParam("from") Date startDate, @QueryParam("to") Date endDate, 
            @QueryParam("site") Long hospital, @QueryParam("filters") String filter){
        List<Object[]> counts;
        JSONArray jsonData = new JSONArray();
        String hospString = "";
        javax.persistence.Query q;
        if (hospital != null && hospital > 0) {
            hospString = " AND tf.hospitalser = ? ";
        }
        
        //return dataSet + " " + startDate.toString() + "" + endDate.toString() + " "  + hospital + " " + filter;
        String filterString = buildFilterString(filter);

        
        if(dataSet.equals("DR")) {
                q = getEntityManager().createNativeQuery(
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
            counts = q.getResultList();
        } else {
            q = getEntityManager().createNativeQuery(
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
            counts = q.getResultList();
        }
        JSONArray labels = new JSONArray();

        for(Object[] row : counts) {
            String item = "";
            String dr = (String) row[0];
            Long ptCount = (Long) row[1];
            //pieChart.set(dr, ptCount);
            JSONObject dataItem = new JSONObject();

            try{
                dataItem.put("label", dr);
                dataItem.put("value", ptCount);
                jsonData.put(dataItem);
            }catch(Exception e){
                //FIXME
            }
        }
        return jsonData.toString();
    }
    
}
