/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.umm.radonc.ca_dash.controllers;

/**
 *
 * @author mmcgrath
 */

import java.io.Serializable;  
import java.util.List;  
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import org.primefaces.model.chart.CartesianChartModel;  
import org.primefaces.model.chart.ChartSeries;  

@Named("barChartBean")
@SessionScoped
public class BarChartBean implements Serializable {
    
    private CartesianChartModel chartModel;
    
    public BarChartBean() {
        testData();
    }
    
    public CartesianChartModel getChartModel() {
        return chartModel;
    }
    
    public void setData(List<Object> items) {
        
    }
    
    private void testData() {
        chartModel = new CartesianChartModel();

        ChartSeries ser_a = new ChartSeries();
        ser_a.setLabel("A");

        ser_a.set("2004", 120);
        ser_a.set("2005", 100);
        ser_a.set("2006", 44);
        ser_a.set("2007", 150);
        ser_a.set("2008", 25);

        ChartSeries ser_b = new ChartSeries();
        ser_b.setLabel("B");

        ser_b.set("2004", 52);
        ser_b.set("2005", 60);
        ser_b.set("2006", 110);
        ser_b.set("2007", 135);
        ser_b.set("2008", 120);

        chartModel.addSeries(ser_a);
        chartModel.addSeries(ser_b);
    }
    
}
