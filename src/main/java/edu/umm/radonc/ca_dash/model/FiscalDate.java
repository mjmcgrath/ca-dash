/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.umm.radonc.ca_dash.model;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 *
 * @author mmcgrath
 */
public class FiscalDate {
    
    //Start of FY - July 1st
    public static GregorianCalendar FY_START;
    private Date internal_date;
    private GregorianCalendar gc;
    
    public FiscalDate() {
        FY_START.set(Calendar.DAY_OF_MONTH, 1);
        FY_START.set(Calendar.MONTH, Calendar.JULY);
        internal_date = new Date();
        gc = new GregorianCalendar();
        gc.setTime(internal_date);
    }
    
    public FiscalDate(Date d) {
        FY_START.set(Calendar.DAY_OF_MONTH, 1);
        FY_START.set(Calendar.MONTH, Calendar.JULY);
        internal_date = d;
        gc = new GregorianCalendar();
        gc.setTime(internal_date);
    }
    
    public void setDate(Date d) {
        this.internal_date = d;
        gc.setTime(d);
    }
    
    public int getFYQuarter() {
        //FIXME : Should this calculation be based on Week of FY rather than month??
        int mo = gc.get(Calendar.MONTH);
        int fymo = FY_START.get(Calendar.MONTH);
        if(mo == fymo || mo == fymo + 1 || mo == fymo + 2) {
            return 1;
        }
        else if (mo == fymo + 3 || mo == fymo + 4 || mo == fymo + 5) {
            return 2;
        }
        else if (mo == fymo + 6 || mo == fymo + 7 || mo == fymo + 8) {
            return 3;
        }
        else if (mo == fymo + 9 || mo == fymo + 10 || mo == fymo + 11) {
            return 4;
        }
        else {
            return -1;
        }
    }
    
    public int getFY() {
        int mo = gc.get(Calendar.MONTH);
        int yr = gc.get(Calendar.YEAR);
        //FIXME
        if( mo >= Calendar.JULY && mo <= Calendar.DECEMBER) {
            return yr;
        } else {
            return yr + 1;
        }
    }
    
    public static Date getQuarter(int quarter) {
        Date today = new Date();
        GregorianCalendar todaygc = new GregorianCalendar();
        todaygc.setTime(today);
        int yr = todaygc.get(Calendar.YEAR);
        int mo = todaygc.get(Calendar.MONTH);
        int day = todaygc.get(Calendar.DATE);
        
        if(mo < Calendar.JULY) {
            yr = yr - 1;
        }
        GregorianCalendar fy = new GregorianCalendar();
        fy.set(Calendar.YEAR, yr);
        fy.set(Calendar.DAY_OF_MONTH, 1);
        fy.set(Calendar.MONTH, Calendar.JULY);
        for(int i = 1; i < quarter; i++) {
            fy.add(Calendar.MONTH, 3);
        }
        return fy.getTime();
    }
    
    public int getFYWeek(Date date) {
        int wk = gc.get(Calendar.WEEK_OF_YEAR);
        GregorianCalendar gcfy = new GregorianCalendar();
        gcfy.set(Calendar.MONTH, FY_START.get(Calendar.MONTH));
        gcfy.set(Calendar.DATE, FY_START.get(Calendar.DATE));
        gcfy.set(Calendar.YEAR, gc.get(Calendar.YEAR));
        int wkfy = gcfy.get(Calendar.WEEK_OF_YEAR);
        int offset = 52 - wkfy;
        int retval = (wk % wkfy);
        if(wk < wkfy) {
            retval += offset;
        }
        
        return retval + 1;
    }
}
