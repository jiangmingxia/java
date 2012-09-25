package com.hp.jmx.qc.rest.entity;

import java.io.OutputStream;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.jmx.qc.model.Field;
import com.hp.jmx.qc.model.KeyType;
import com.hp.jmx.qc.model.QCEntity;
import com.hp.jmx.qc.rest.AbstractQCRestRequest;
import com.hp.jmx.qc.rest.QCRestRequest;
import com.hp.jmx.qc.rest.QCRestWSException;
import com.hp.jmx.qc.rest.model.Entity;

public class QCEntityRequest extends AbstractQCRestRequest {

    private static final Logger log = LoggerFactory.getLogger(QCEntityRequest.class);
    
    private QCEntity entity;
    
    public QCEntityRequest(QCEntity entity, String httpMethod) {
        this.entity = entity;
        this.httpMethod = httpMethod;
        String entityId = entity.getEntityId();
        
        init(entityId);
    }

    public QCEntityRequest(QCEntity entity, String httpMethod, long entityId) {
        this.entity = entity;
        this.httpMethod = httpMethod;
        
        init("" + entityId);
    }
    
    private void init(String entityId) {
        String entityType = this.entity.getEntityType();
        
        String url = getBaseURL() + entityType + "s";
        if (QCRestRequest.GET_METHOD.equals(this.httpMethod) || QCRestRequest.PUT_METHOD.equals(this.httpMethod) || QCRestRequest.DELETE_METHOD.equals(this.httpMethod)) {
            url = url + "/" + entityId;
        }
        if (QCRestRequest.GET_METHOD.equals(this.httpMethod) || QCRestRequest.DELETE_METHOD.equals(this.httpMethod)) {
            this.outputRequest = false;
        }
        this.requestURL = url; 
    }
    
    @Override
    public void writeBody(OutputStream out) throws QCRestWSException {
        if (this.entity == null)
            logAndException("The entity can't be null if the request need write the http request body.");       
        
        Entity qcEntity = new Entity();
        String entityType = this.entity.getEntityType();
        if (entityType == null) {
            logAndException("The entity doesn't have the qc entity type name.");
        }
        qcEntity.setType(entityType);
        
        Entity.Fields fields = new Entity.Fields();
        Map<String, Field> keyFieldMap = getKeyFieldMap();
        
        for (String key : this.entity.keySet()) {
            String qcFieldName = keyFieldMap.get(key).getName();
            String qcFieldValue = this.entity.get(key);
            if (qcFieldValue != null && !qcFieldValue.isEmpty()) {
                Entity.Fields.Field field = new Entity.Fields.Field();
                field.setName(qcFieldName);
                Entity.Fields.Field.Value value = new Entity.Fields.Field.Value();
                value.setValue(qcFieldValue);
                field.getValue().add(value);
                fields.getField().add(field);
            }            
        }
        
        qcEntity.setFields(fields);
        
        // TODO: should use singleton of JAXB context
        try {
            JAXBContext jaxb = JAXBContext.newInstance("com.hp.jmx.qc.rest.model");
            Marshaller marshaller = jaxb.createMarshaller();
            marshaller.marshal(qcEntity, out);
        } catch (JAXBException e) {
            log.error("Error happend when marshal the entity object.", e);
            throw new QCRestWSException("Error happend when marshal the entity object.", e);
        }
    }
    
    private Map<String, Field> getKeyFieldMap() {
        if (this.entity.getKeyType() == KeyType.FIELD_NAME)
            return EntityFieldHelper.getNameFieldMap(this.entity.getEntityType());
        else if (this.entity.getKeyType() == KeyType.FIELD_LABEL)
            return EntityFieldHelper.getLabelFieldMap(this.entity.getEntityType());
        else 
            return EntityFieldHelper.getPhisicalNameFieldMap(this.entity.getEntityType());
    }

    @Override
    protected void parsetReponse(Object response) {
        try {
            Entity qcEntity = (Entity) response;
            String qcEntityType = qcEntity.getType();
            String entityType = this.entity.getEntityType();
            if (!qcEntityType.equals(entityType))
                logAndException("The expected type: " + entityType + " is different from result type: " + qcEntityType);

            Map<String, Field> nameFieldMap = EntityFieldHelper.getNameFieldMap(this.entity.getEntityType());
            for (final Entity.Fields.Field qcField : qcEntity.getFields().getField()) {
                if (qcField.getValue() != null && qcField.getValue().size() == 1 && qcField.getValue().get(0) != null
                        && qcField.getValue().get(0).getValue() != null && !(qcField.getValue().get(0).getValue().isEmpty())) {
                    String valueString = qcField.getValue().get(0).getValue();
                    String key = getKeyFromName(nameFieldMap, qcField.getName());
                    this.entity.put(key, valueString);
                }
            }
            
        } catch (ClassCastException e) {
            logAndException("The unmarshall object is not the expected type: Entities.", e);
        } 
    }

    public QCEntity getEntity() {
        return this.entity;
    }

    private String getKeyFromName(Map<String, Field> nameFieldMap, String name) {
        Field field = nameFieldMap.get(name);
        if (this.entity.getKeyType() == KeyType.FIELD_NAME)
            return name;
        else if (this.entity.getKeyType() == KeyType.FIELD_LABEL)
            return field.getLabel();
        else 
            return field.getPhysicalName();
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
