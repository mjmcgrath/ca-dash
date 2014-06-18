package edu.umm.radonc.ca_dash.controllers;

import edu.umm.radonc.ca_dash.model.Activityinstance;
import edu.umm.radonc.ca_dash.controllers.util.JsfUtil;
import edu.umm.radonc.ca_dash.controllers.util.JsfUtil.PersistAction;
import edu.umm.radonc.ca_dash.model.ActivityinstanceFacade;

import java.io.Serializable;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@Named("activityinstanceController")
@SessionScoped
public class ActivityinstanceController implements Serializable {

    @EJB
    private edu.umm.radonc.ca_dash.model.ActivityinstanceFacade ejbFacade;
    private List<Activityinstance> items = null;
    private Activityinstance selected;

    public ActivityinstanceController() {
    }

    public Activityinstance getSelected() {
        return selected;
    }

    public void setSelected(Activityinstance selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private ActivityinstanceFacade getFacade() {
        return ejbFacade;
    }

    public Activityinstance prepareCreate() {
        selected = new Activityinstance();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("ActivityinstanceCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("ActivityinstanceUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("ActivityinstanceDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Activityinstance> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }

    private void persist(PersistAction persistAction, String successMessage) {
        if (selected != null) {
            setEmbeddableKeys();
            try {
                if (persistAction != PersistAction.DELETE) {
                    getFacade().edit(selected);
                } else {
                    getFacade().remove(selected);
                }
                JsfUtil.addSuccessMessage(successMessage);
            } catch (EJBException ex) {
                String msg = "";
                Throwable cause = ex.getCause();
                if (cause != null) {
                    msg = cause.getLocalizedMessage();
                }
                if (msg.length() > 0) {
                    JsfUtil.addErrorMessage(msg);
                } else {
                    JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            }
        }
    }

    public Activityinstance getActivityinstance(java.lang.Integer id) {
        return getFacade().find(id);
    }

    public List<Activityinstance> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Activityinstance> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = Activityinstance.class)
    public static class ActivityinstanceControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            ActivityinstanceController controller = (ActivityinstanceController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "activityinstanceController");
            return controller.getActivityinstance(getKey(value));
        }

        java.lang.Integer getKey(String value) {
            java.lang.Integer key;
            key = Integer.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Integer value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Activityinstance) {
                Activityinstance o = (Activityinstance) object;
                return getStringKey(o.getActivityinstanceser());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Activityinstance.class.getName()});
                return null;
            }
        }

    }

}
