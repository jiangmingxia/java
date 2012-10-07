package com.hp.jmx.qc.rest.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.jmx.qc.model.EntityObject;
import com.hp.jmx.qc.model.Field;
import com.hp.jmx.qc.rest.QCRestClient;

public class EntityFieldHelper {

    private static final Logger log = LoggerFactory.getLogger(EntityFieldHelper.class);

    private static final ConcurrentHashMap<String, List<Field>> fieldsMapCache = new ConcurrentHashMap<String, List<Field>>();

    /** QC field label and the related field map. We can find the name, phisical name via this map. */
    private static final ConcurrentHashMap<String, Map<String, Field>> labelFieldMapCache = new ConcurrentHashMap<String, Map<String, Field>>();

    /** QC field name and the related field map. We can find the label, phisical name via this map. */
    private static final ConcurrentHashMap<String, Map<String, Field>> nameFieldMapCache = new ConcurrentHashMap<String, Map<String, Field>>();

    private static final ConcurrentHashMap<String, Map<String, Field>> physicalNameFieldMapCache = new ConcurrentHashMap<String, Map<String, Field>>();
    
    private static List<Field> getFields(String entityType) {
        if (fieldsMapCache.containsKey(entityType)) 
            return fieldsMapCache.get(entityType);
        else {
            GetEntityFieldsRequest request = new GetEntityFieldsRequest(entityType);
            QCRestClient.getInstance().run(request);
            
            if (!request.isSucceed()) {
                logAndException(request.getErrorMessage());
            }
            
            List<Field> fields = request.getEntities();
            if (fields != null)
                fieldsMapCache.putIfAbsent(entityType, fields);
            return fields;
        }        
    }
    
    public static Map<String, Field> getLabelFieldMap(String entityType) {
        if (labelFieldMapCache.containsKey(entityType)) 
            return labelFieldMapCache.get(entityType);
        else {
            List<Field> fields = getFields(entityType);
            Map<String, Field> labelFieldMap = new HashMap<String, Field>();
            
            for (Field field : fields) {
                if (field.getLabel() != null && !field.getLabel().isEmpty())
                    labelFieldMap.put(field.getLabel(), field);
            }
            
            labelFieldMapCache.putIfAbsent(entityType, labelFieldMap);
            return labelFieldMap;
        }
        
    }

    public static Map<String, Field> getNameFieldMap(String entityType) {
        if (nameFieldMapCache.containsKey(entityType)) 
            return nameFieldMapCache.get(entityType);
        else {
            List<Field> fields = getFields(entityType);
            Map<String, Field> nameFieldMap = new HashMap<String, Field>();
            
            for (Field field : fields) {
                if (field.getLabel() != null && !field.getLabel().isEmpty())
                    nameFieldMap.put(field.getName(), field);
            }
            
            nameFieldMapCache.putIfAbsent(entityType, nameFieldMap);
            return nameFieldMap;
        }
        
    }

    public static Map<String, Field> getPhysicalNameFieldMap(String entityType) {
        if (physicalNameFieldMapCache.containsKey(entityType)) 
            return physicalNameFieldMapCache.get(entityType);
        else {
            List<Field> fields = getFields(entityType);
            Map<String, Field> phisicalNameFieldMap = new HashMap<String, Field>();
            
            for (Field field : fields) {
                if (field.getLabel() != null && !field.getLabel().isEmpty())
                    phisicalNameFieldMap.put(field.getPhysicalName(), field);
            }
            
            physicalNameFieldMapCache.putIfAbsent(entityType, phisicalNameFieldMap);
            return phisicalNameFieldMap;
        }
        
    }
        
    public static String getFieldNameByLabel(String entityType,String label) {
        Map<String, Field> labelFieldMap = getLabelFieldMap(entityType);
        Field field = labelFieldMap.get(label);
        if (field == null) return null;
        return field.getName();
    }
    
    public static String getFieldNameByPhysicalName(String entityType,String physicialName) {
        Map<String, Field> physicalFieldMap = getPhysicalNameFieldMap(entityType);
        Field field = physicalFieldMap.get(physicialName);
        if (field == null) return null;
        return field.getName();
    }
    
    private static void logAndException(String message) {
        log.error(message);
        throw new RuntimeException(message);
    }
}
