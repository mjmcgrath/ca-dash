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
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.BarChartSeries;
import org.primefaces.model.chart.CategoryAxis;
import org.primefaces.model.chart.DateAxis;
import org.primefaces.model.chart.LineChartModel;
import org.primefaces.model.chart.LineChartSeries;
 
@Named("dstest")
@ViewScoped
public class DSTest implements Serializable {
 
    private BarChartModel dateModel;
 
    @PostConstruct
    public void init() {
        createDateModel();
    }
 
    public BarChartModel getDateModel() {
        return dateModel;
    }
     
    private void createDateModel() {
        dateModel = new BarChartModel();
        BarChartSeries series1 = new BarChartSeries();
        series1.setLabel("Series 1");
 
        series1.set("2014-01-01", 51);
        series1.set("2014-01-06", 22);
        series1.set("2014-01-12", 65);
        series1.set("2014-01-18", 74);
        series1.set("2014-01-24", 24);
        series1.set("2014-01-30", 51);
 
        BarChartSeries series2 = new BarChartSeries();
        series2.setLabel("Series 2");
 
        series2.set("2014-01-01", 32);
        series2.set("2014-01-06", 73);
        series2.set("2014-01-12", 24);
        series2.set("2014-01-18", 12);
        series2.set("2014-01-24", 74);
        series2.set("2014-01-30", 62);
 
        dateModel.addSeries(series1);
        dateModel.addSeries(series2);
         
        dateModel.setTitle("Zoom for Details");
        dateModel.setZoom(true);
        dateModel.getAxis(AxisType.Y).setLabel("Values");
        CategoryAxis axis = new CategoryAxis("Dates");
        axis.setTickAngle(-50);
        axis.setTickFormat("%b %#d, %y");
        
        dateModel.setExtender("function(){"
                + " var interval = 1; "
                + " var items = this.cfg.axes.xaxis.ticks.length; "
                + "if( items > 21) { interval = 7; } "
                + "else if( items > 45) { interval = 30; } "
                + "else if( items > 500) { interval = 365; } "
                + "for(var i = 0; i < this.cfg.axes.xaxis.ticks.length; i++) { "
                + "   if(i % inteval != 0) { "
                + "     this.cfg.axes.xaxis.ticks[i] = \" \"; "
                + "   } "
                + " } "
                + "}");
         
        dateModel.getAxes().put(AxisType.X, axis);
    }
}
