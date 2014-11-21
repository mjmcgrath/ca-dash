/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.umm.radonc.ca_dash.model.util;

import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author mmcgrath
 */
@Named("sessionBean")
@SessionScoped
public class SessionBean implements Serializable {
        public static void logout() {
            String path = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();
            try {
                ((HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest()).logout();
                FacesContext.getCurrentInstance().getExternalContext().redirect(path + "/faces/main/index.xhtml");
            } catch (Exception e) {
            }
    }
}
