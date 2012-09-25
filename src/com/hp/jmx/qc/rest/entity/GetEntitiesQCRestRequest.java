package com.hp.jmx.qc.rest.entity;

import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.jmx.qc.annotation.EntityUtil;
import com.hp.jmx.qc.model.EntityObject;
import com.hp.jmx.qc.rest.AbstractQCRestRequest;
import com.hp.jmx.qc.rest.QCRestRequest;
import com.hp.jmx.qc.rest.QCRestWSException;
import com.hp.jmx.qc.rest.QCValueHelper;
import com.hp.jmx.qc.rest.model.Entities;

public class GetEntitiesQCRestRequest<E extends EntityObject> extends AbstractQCRestRequest{

    private static final Logger log = LoggerFactory.getLogger(GetEntitiesQCRestRequest.class);
    
    private Class<E> clazz;
    
    public GetEntitiesQCRestRequest(Class<E> clazz, String filterString) {
        this.clazz = clazz;
        this.httpMethod = QCRestRequest.GET_METHOD;
        
        String entityType = EntityUtil.getEntityType(this.clazz);
        
        String url = getBaseURL() + entityType + "s";
        if (filterString != null && !filterString.isEmpty()) {
            if (filterString.startsWith("?"))
                url = url + filterString;
            else
                url = url + "?" + filterString;
        }
        this.outputRequest = false;
        this.requestURL = url;
    }
    
    @Override
    public void writeBody(OutputStream out) throws QCRestWSException {
        return;
    }

    private List<E> results;
    
    @Override
    protected void parsetReponse(Object response) {
        try {
            Entities qcEntities = (Entities) response;
            results = new ArrayList<E>();
            for (Entities.Entity qcEntity : qcEntities.getEntity()) {
                String qcEntityType = qcEntity.getType();
                String entityType = EntityUtil.getEntityType(clazz);
                if (!qcEntityType.equals(entityType))
                    logAndException("The expected type: " + entityType + " is different from result type: " + qcEntityType);
                
                Map<String, Method> fromQCFieldInfo = EntityUtil.getFromQCFieldInfo(clazz);
    
                E entity = clazz.newInstance();
                for (final Entities.Entity.Fields.Field qcField : qcEntity.getFields().getField()) {
                    if (fromQCFieldInfo.containsKey(qcField.getName()) && qcField.getValue() != null && qcField.getValue().size() == 1 && qcField.getValue().get(0) != null
                            && qcField.getValue().get(0).getValue() != null && !(qcField.getValue().get(0).getValue().isEmpty())) {
                        String valueString = qcField.getValue().get(0).getValue();
                        Method setter = fromQCFieldInfo.get(qcField.getName());
                        QCValueHelper.invokeSetterMethod(setter, entity, valueString);
                    }
                }
                results.add(entity);
            }
            
        } catch (ClassCastException e) {
            logAndException("The unmarshall object is not the expected type: Entities.", e);
        } catch (Exception e) {
            logAndException("Error when set the value to the entity from QC.", e);
        } 
    }

    public List<E> getEntities() {
        return this.results;
    }

    private void logAndException(String message) {
        log.error(message);
        throw new RuntimeException(message);
    }

    private void logAndException(String message, Exception e) {
        log.error(message, e);
        throw new RuntimeException(message, e);
    }

}
