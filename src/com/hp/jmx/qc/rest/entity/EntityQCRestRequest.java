package com.hp.jmx.qc.rest.entity;

import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.jmx.qc.annotation.EntityUtil;
import com.hp.jmx.qc.model.EntityObject;
import com.hp.jmx.qc.rest.AbstractQCRestRequest;
import com.hp.jmx.qc.rest.QCRestRequest;
import com.hp.jmx.qc.rest.QCRestWSException;
import com.hp.jmx.qc.rest.QCValueHelper;
import com.hp.jmx.qc.rest.model.Entity;

public class EntityQCRestRequest<E extends EntityObject> extends AbstractQCRestRequest {

    private static final Logger log = LoggerFactory.getLogger(EntityQCRestRequest.class);
    
    private E entity;
    
    public EntityQCRestRequest(E entity, String httpMethod) {
        this.entity = entity;
        this.httpMethod = httpMethod;
        
        String entityType = EntityUtil.getEntityType(this.entity.getClass());
        
        String url = getBaseURL() + entityType + "s";
        if (QCRestRequest.GET_METHOD.equals(this.httpMethod) || QCRestRequest.PUT_METHOD.equals(this.httpMethod) || QCRestRequest.DELETE_METHOD.equals(this.httpMethod)) {
            url = url + "/" + this.entity.getId();
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
	    String entityType = EntityUtil.getEntityType(this.entity.getClass());
	    if (entityType == null) {
	        logAndException("The entity doesn't have the qc entity type name.");
	    }
	    qcEntity.setType(entityType);
	    
	    Entity.Fields fields = new Entity.Fields();
	    
	    Map<String, Method> toQCFieldInfoMap = EntityUtil.getToQCFieldInfo(this.entity.getClass());
	    if (toQCFieldInfoMap == null){
            logAndException("The entity doesn't have the qc field info.");
        }
	    
	    for (String qcFieldName : toQCFieldInfoMap.keySet()) {
	        String entityValue = QCValueHelper.invokeGetterMethod(toQCFieldInfoMap.get(qcFieldName), this.entity);
	        if (entityValue != null) {
    	        Entity.Fields.Field field = new Entity.Fields.Field();
    	        field.setName(qcFieldName);
    	        Entity.Fields.Field.Value value = new Entity.Fields.Field.Value();
    	        value.setValue(entityValue);
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

    @Override
    protected void parsetReponse(Object response) {
        try {
            Entity qcEntity = (Entity) response;
            String qcEntityType = qcEntity.getType();
            String entityType = EntityUtil.getEntityType(entity.getClass());
            if (!qcEntityType.equals(entityType))
                logAndException("The expected type: " + entityType + " is different from result type: " + qcEntityType);
            
            Map<String, Method> fromQCFieldInfo = EntityUtil.getFromQCFieldInfo(entity.getClass());

            for (final Entity.Fields.Field qcField : qcEntity.getFields().getField()) {
                if (fromQCFieldInfo.containsKey(qcField.getName()) && qcField.getValue() != null && qcField.getValue().size() == 1 && qcField.getValue().get(0) != null
                        && qcField.getValue().get(0).getValue() != null && !(qcField.getValue().get(0).getValue().isEmpty())) {
                    String valueString = qcField.getValue().get(0).getValue();
                    Method setter = fromQCFieldInfo.get(qcField.getName());
                    QCValueHelper.invokeSetterMethod(setter, this.entity, valueString);
                }
            }
            
        } catch (ClassCastException e) {
            logAndException("The unmarshall object is not the expected type: Entities.", e);
        } catch (Exception e) {
            logAndException("Error when set the value to the entity from QC.", e);
        } 
    }
    
    public E getEntity() {
        return this.entity;
    }

    private void logAndException(String message, Exception e) {
        log.error(message, e);
        throw new RuntimeException(message, e);
    }

	private void logAndException(String message) {
	    log.error(message);
	    throw new RuntimeException(message);
	}
}
