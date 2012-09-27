package com.hp.jmx.qc.rest.entity;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.jmx.qc.rest.AbstractQCRestRequest;
import com.hp.jmx.qc.rest.QCRestRequest;
import com.hp.jmx.qc.rest.QCRestWSException;
import com.hp.jmx.qc.rest.model.Lists;

public class GetListValuesRequest extends AbstractQCRestRequest{

    private static final Logger log = LoggerFactory.getLogger(GetListValuesRequest.class);
    
    private String entityType;
    
    public GetListValuesRequest(String entityType) {
        this.entityType = entityType;
        this.httpMethod = QCRestRequest.GET_METHOD;
        
        String url = getBaseURL() + "customization/entities/" + this.entityType + "/lists";
        this.outputRequest = false;
        this.requestURL = url;
    }
    
    @Override
    public void writeBody(OutputStream out) throws QCRestWSException {
        return;
    }

    private Map<String, List<String>> idListMap;

    private Map<String, List<String>> nameListMap;
    
    @Override
    protected void parsetReponse(Object response) {
        try {
            Lists qcLists = (Lists) response;
            idListMap = new HashMap<String, List<String>>();
            nameListMap = new HashMap<String, List<String>>();
            for (Lists.List qcList : qcLists.getList()) {
                List<String> listValues = new ArrayList<String>();
                for (Lists.List.Items.Item qcListItem : qcList.getItems().getItem()) {
                    addItemValues(qcListItem, listValues);
                }
                idListMap.put(qcList.getId().toString(), listValues);
                nameListMap.put(qcList.getName(), listValues);
            }
            
        } catch (ClassCastException e) {
            logAndException("The unmarshall object is not the expected type: Fields.", e);
        }
    }

    private void addItemValues(Lists.List.Items.Item qcListItem, List<String> listValues) {
        listValues.add(qcListItem.getValue());
        for (Lists.List.Items.Item childItem : qcListItem.getItem()) {
            addItemValues(childItem, listValues);
        }
    }
    
    public Map<String, List<String>> getIdListMap() {
        return idListMap;
    }

    public void setIdListMap(Map<String, List<String>> idListMap) {
        this.idListMap = idListMap;
    }

    public Map<String, List<String>> getNameListMap() {
        return nameListMap;
    }

    public void setNameListMap(Map<String, List<String>> nameListMap) {
        this.nameListMap = nameListMap;
    }

    private void logAndException(String message, Exception e) {
        log.error(message, e);
        throw new RuntimeException(message, e);
    }

}

