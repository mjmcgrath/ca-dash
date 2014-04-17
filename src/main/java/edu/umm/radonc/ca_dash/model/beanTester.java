/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.umm.radonc.ca_dash.model;

import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 *
 * @author mmcgrath
 */
@Named("beanTester")
@SessionScoped
public class beanTester implements Serializable {
    public beanTester () {
    }
    
    public String getStuff() {
        return "It works";
    }
}
