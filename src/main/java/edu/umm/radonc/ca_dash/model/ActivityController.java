package edu.umm.radonc.ca_dash.model;

import edu.umm.radonc.ca_dash.model.util.JsfUtil;
import edu.umm.radonc.ca_dash.model.util.JsfUtil.PersistAction;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.enterprise.context.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Named;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

@Named("activityController")
@SessionScoped
public class ActivityController implements Serializable {

    @EJB
    private edu.umm.radonc.ca_dash.model.ActivityFacade ejbFacade;
    private List<Activity> items = null;
    private LazyDataModel<Activity> lazyItems = null;
    private Activity selected;
    private Date startDate;
    private Date endDate;

    public ActivityController() {
    }

    public Activity getSelected() {
        return selected;
    }

    public void setSelected(Activity selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private ActivityFacade getFacade() {
        return ejbFacade;
    }

    public Activity prepareCreate() {
        selected = new Activity();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("ActivityCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("ActivityUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("ActivityDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Activity> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }
    
    public LazyDataModel<Activity> getLazyItems() {
        if (lazyItems == null) {
            this.lazyItems = new LazyDataModel<Activity>(){
                private static final long serialVersionUID    = 1L;
                @Override
                public List<Activity> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
                    int[] range = {first, first + pageSize};
                    List<Activity> result = getFacade().findRange(range);
                    lazyItems.setRowCount(getFacade().count());
                    return result;
                }
            };
        }
        return lazyItems;
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

    public Activity getActivity(java.lang.Integer id) {
        return getFacade().find(id);
    }

    public List<Activity> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Activity> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }
    
    public List<Object> getDailyCounts() {
        return getFacade().getDailyCounts(null, null);
    }
    
    public void setStartDate(Date startDate) {
        System.out.println(startDate.toString());
        this.startDate = startDate;
    }
    
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
    
    public Date getStartDate() {
        return this.startDate;
    }
    
    public Date getEndDate() {
        return this.endDate;
    }

    @FacesConverter(forClass = Activity.class)
    public static class ActivityControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            ActivityController controller = (ActivityController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "activityController");
            return controller.getActivity(getKey(value));
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
            if (object instanceof Activity) {
                Activity o = (Activity) object;
                return getStringKey(o.getActinstproccodeser());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Activity.class.getName()});
                return null;
            }
        }

    }

}
