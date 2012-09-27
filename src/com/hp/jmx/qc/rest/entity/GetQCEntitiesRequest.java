package com.hp.jmx.qc.rest.entity;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.jmx.qc.model.Field;
import com.hp.jmx.qc.model.KeyType;
import com.hp.jmx.qc.model.QCEntity;
import com.hp.jmx.qc.rest.AbstractQCRestRequest;
import com.hp.jmx.qc.rest.QCRestRequest;
import com.hp.jmx.qc.rest.QCRestWSException;
import com.hp.jmx.qc.rest.model.Entities;

public class GetQCEntitiesRequest extends AbstractQCRestRequest{

    private static final Logger log = LoggerFactory.getLogger(GetQCEntitiesRequest.class);

    private String entityType;
    
    private KeyType keyType;
    
    private List<QCEntity> results;
    
    private QCEntity entity;
    
    private void init(QCEntity entity){
        this.httpMethod = QCRestRequest.GET_METHOD;
        this.entityType = entity.getEntityType();
        this.keyType = entity.getKeyType();
        this.entity = entity;        
        this.outputRequest = false;
        
    }
    
    public GetQCEntitiesRequest(QCEntity entity) {
        init(entity);        
                
        String url = getBaseURL() + entityType + "s";        
        String filterString = generateFilterString(false);
        if (filterString != null && !filterString.isEmpty()) {
            if (filterString.startsWith("?"))
                url = url + filterString;
            else
                url = url + "?" + filterString;
        }        
        this.requestURL = url;
    }
    
    public GetQCEntitiesRequest(QCEntity entity, boolean isComplexQuery) {
        init(entity);        
                
        String url = getBaseURL() + entityType + "s";        
        String filterString = generateFilterString(isComplexQuery);
        if (filterString != null && !filterString.isEmpty()) {
            if (filterString.startsWith("?"))
                url = url + filterString;
            else
                url = url + "?" + filterString;
        }        
        this.requestURL = url;
    }
    
    public GetQCEntitiesRequest(QCEntity entity,String internalString) {
        init(entity);        
                
        String url = getBaseURL() + entityType + "s";        
        String filterString = internalString;
        if (filterString != null && !filterString.isEmpty()) {
            if (filterString.startsWith("?"))
                url = url + filterString;
            else
                url = url + "?" + filterString;
        }        
        this.requestURL = url;
    }
    
    @Override
    public void writeBody(OutputStream out) throws QCRestWSException {
        return;
    }

    @Override
    protected void parsetReponse(Object response) {
        try {
            Entities qcEntities = (Entities) response;
            results = new ArrayList<QCEntity>();
            for (Entities.Entity qcEntity : qcEntities.getEntity()) {
                String qcEntityType = qcEntity.getType();
                if (!qcEntityType.equals(entityType))
                    logAndException("The expected type: " + entityType + " is different from result type: " + qcEntityType);

                QCEntity entity = new QCEntity(entityType, keyType);
                Map<String, Field> nameFieldMap = EntityFieldHelper.getNameFieldMap(entityType);
                for (final Entities.Entity.Fields.Field qcField : qcEntity.getFields().getField()) {
                    if (qcField.getValue() != null && qcField.getValue().size() == 1 && qcField.getValue().get(0) != null
                            && qcField.getValue().get(0).getValue() != null && !(qcField.getValue().get(0).getValue().isEmpty())) {
                        String valueString = qcField.getValue().get(0).getValue();
                        String key = getKeyFromName(nameFieldMap, qcField.getName());
                        entity.put(key, valueString);
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

    private String getKeyFromName(Map<String, Field> nameFieldMap, String name) {
        Field field = nameFieldMap.get(name);
        if (this.keyType == KeyType.FIELD_NAME)
            return name;
        else if (this.keyType == KeyType.FIELD_LABEL)
            return field.getLabel();
        else 
            return field.getPhysicalName();
    }

    public List<QCEntity> getEntities() {
        return this.results;
    }

    private String generateFilterString(boolean isComplexQuery) {

        if (entity.size() <= 0)
            return null;
        
        
        StringBuffer sb = new StringBuffer("query={");

        Map<String, Field> keyFieldMap = getKeyFieldMap();
        boolean first = true;
        
        for (String key : entity.keySet()) {
            Field field = keyFieldMap.get(key);
            String queryName = field.getName();
            String queryValue = entity.get(key);
            if (queryValue != null && !queryValue.isEmpty()) {
                if (!first) {
                    sb.append(";");
                }        
                if (isComplexQuery){
                	sb.append(queryName + "[");
                    sb.append(queryValue + "]");
                } else {
                	sb.append(queryName + "['");
                    sb.append(queryValue + "']");                	
                }                
                first = false;
            }
        }
        
        sb.append("}");
        String filterString = sb.toString();
        log.info("The query string is: " + filterString);
        
        return filterString;
    }

    private Map<String, Field> getKeyFieldMap() {
        if (this.entity.getKeyType() == KeyType.FIELD_NAME)
            return EntityFieldHelper.getNameFieldMap(this.entity.getEntityType());
        else if (this.entity.getKeyType() == KeyType.FIELD_LABEL)
            return EntityFieldHelper.getLabelFieldMap(this.entity.getEntityType());
        else 
            return EntityFieldHelper.getPhisicalNameFieldMap(this.entity.getEntityType());
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
