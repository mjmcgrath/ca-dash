/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.umm.radonc.ca_dash.model;

/**
 *
 * @author mmcgrath
 */
public class ActivityCount {
    
    private String shortcomment;
    private String procedurecode;
    private Long activityCount;

    public ActivityCount(String shortcomment, String procedurecode, Long activityCount) {
        this.shortcomment = shortcomment;
        this.procedurecode = procedurecode;
        this.activityCount = activityCount;
    }
    
    public String getShortcomment() {
        return shortcomment;
    }

    public void setShortcomment(String shortcomment) {
        this.shortcomment = shortcomment;
    }

    public String getProcedurecode() {
        return procedurecode;
    }

    public void setProcedurecode(String procedurecode) {
        this.procedurecode = procedurecode;
    }

    public Long getActivityCount() {
        return activityCount;
    }

    public void setActivityCount(Long activityCount) {
        this.activityCount = activityCount;
    }
    
}
