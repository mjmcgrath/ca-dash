/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.umm.radonc.ca_dash.model.util;

import java.util.TreeMap;

/**
 *
 * @author mmcgrath
 */
public class ColorMap {
    private static TreeMap<String, String[]> map = null;
    public static TreeMap getMap() {
        if (map == null) {
            synchronized (ColorMap.class) {
                map = new TreeMap<>();
                map.put("All", new String[]{"EA1300","F53800","DE5A0B","F57F00","EA9600"});
                map.put("University of Maryland Medical Center", new String[]{"8C3130","994D2D","A34230","A33065","992D95"});
                map.put("Central Maryland Radiation Oncology", new String[]{"E0AB5D","ED8556","F7A25A","F7C75B","EDCE56"});
                map.put("Upper Chesapeake Health System", new String[]{"4984D0","5142DD","4662E7","46B6E7","42D7DD"});
                map.put("Baltimore Washington Medical Center", new String[]{"2C2A29", "393632", "433F3B", "433D3B", "393232"});
                map.put("University of Maryland", new String[]{"8C3130","994D2D","A34230","A33065","992D95"});
                map.put("Upper Chesapeake Health", new String[]{"4984D0","5142DD","4662E7","46B6E7","42D7DD"});
            }
        }
        return map;
    }
}
