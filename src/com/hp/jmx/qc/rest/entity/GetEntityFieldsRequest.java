package com.hp.jmx.qc.rest.entity;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.jmx.qc.model.Field;
import com.hp.jmx.qc.rest.AbstractQCRestRequest;
import com.hp.jmx.qc.rest.QCRestRequest;
import com.hp.jmx.qc.rest.QCRestWSException;
import com.hp.jmx.qc.rest.model.Fields;

public class GetEntityFieldsRequest extends AbstractQCRestRequest{

    private static final Logger log = LoggerFactory.getLogger(GetEntityFieldsRequest.class);
    
    private String entityType;
    
    public GetEntityFieldsRequest(String entityType) {
        this.entityType = entityType;
        this.httpMethod = QCRestRequest.GET_METHOD;
        
        String url = getBaseURL() + "customization/entities/" + this.entityType + "/fields";
        this.outputRequest = false;
        this.requestURL = url;
    }
    
    @Override
    public void writeBody(OutputStream out) throws QCRestWSException {
        return;
    }

    private List<Field> results;
    
    @Override
    protected void parsetReponse(Object response) {
        try {
            Fields qcFields = (Fields) response;
            results = new ArrayList<Field>();
            for (Fields.Field qcField : qcFields.getField()) {
                Field field = new Field();
                field.setName(qcField.getName());
                field.setLabel(qcField.getLabel());
                field.setPhysicalName(qcField.getPhysicalName());
                results.add(field);
            }
            
        } catch (ClassCastException e) {
            logAndException("The unmarshall object is not the expected type: Fields.", e);
        }
    }

    public List<Field> getEntities() {
        return this.results;
    }

    private void logAndException(String message, Exception e) {
        log.error(message, e);
        throw new RuntimeException(message, e);
    }

}

