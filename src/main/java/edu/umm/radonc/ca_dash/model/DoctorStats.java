/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.umm.radonc.ca_dash.model;

import org.apache.commons.math.stat.descriptive.SynchronizedDescriptiveStatistics;

/**
 *
 * @author mmcgrath
 */
public class DoctorStats {
    private SynchronizedDescriptiveStatistics averageDailyPatients;
    private Long totalPatients;

    public DoctorStats() {
        this.averageDailyPatients = new SynchronizedDescriptiveStatistics();
        this.totalPatients = new Long(0);
    }

    public SynchronizedDescriptiveStatistics getAverageDailyPatients() {
        return averageDailyPatients;
    }

    public void setAverageDailyPatients(SynchronizedDescriptiveStatistics averageDailyPatients) {
        this.averageDailyPatients = averageDailyPatients;
    }

    public Long getTotalPatients() {
        return totalPatients;
    }

    public void setTotalPatients(Long totalPatients) {
        this.totalPatients = totalPatients;
    }
  
}
