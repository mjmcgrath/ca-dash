/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.umm.radonc.ca_dash.model.util;

import java.util.Arrays;
import java.util.List;

/**
 *
 * @author mmcgrath
 */
public class TxConsts {
    private static final List<String> txcodes =  Arrays.asList("77403","77408","77413","77404",
            "77409","77414","77418","77416","G0251","G0173");
    
    private static final List<String> igrtcodes = Arrays.asList("77421","77014","0197T");
    
    public static List<String> getTxcodes() {
        return txcodes;
    }

    public static List<String> getIgrtcodes() {
        return igrtcodes;
    }
}
