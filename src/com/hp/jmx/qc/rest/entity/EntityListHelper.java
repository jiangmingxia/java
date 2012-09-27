package com.hp.jmx.qc.rest.entity;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.jmx.qc.rest.QCRestClient;

public class EntityListHelper {

    private static final Logger log = LoggerFactory.getLogger(EntityListHelper.class);

    private static final ConcurrentHashMap<String, Map<String, List<String>>> entityIdListMapCache = new ConcurrentHashMap<String, Map<String, List<String>>>();

    private static final ConcurrentHashMap<String, Map<String, List<String>>> entityNameListMapCache = new ConcurrentHashMap<String, Map<String, List<String>>>();

    private static Map<String, List<String>> getIdEntityListMap(String entityType) {
        if (entityIdListMapCache.containsKey(entityType)) 
            return entityIdListMapCache.get(entityType);
        else {
            GetListValuesRequest request = new GetListValuesRequest(entityType);
            QCRestClient.getInstance().run(request);
            
            if (!request.isSucceed()) {
                logAndException(request.getErrorMessage());
            }
            
            Map<String, List<String>> entityIdListMap = request.getIdListMap();
            Map<String, List<String>> entityNameListIdMap = request.getNameListMap();
            if (entityIdListMap != null)
                entityIdListMapCache.putIfAbsent(entityType, entityIdListMap);
            if (entityNameListIdMap != null)
                entityNameListMapCache.putIfAbsent(entityType, entityNameListIdMap);
            return entityIdListMap;
        }        
    }

    private static Map<String, List<String>> getNameEntityListMap(String entityType) {
        if (entityNameListMapCache.containsKey(entityType)) 
            return entityNameListMapCache.get(entityType);
        else {
            GetListValuesRequest request = new GetListValuesRequest(entityType);
            QCRestClient.getInstance().run(request);
            
            if (!request.isSucceed()) {
                logAndException(request.getErrorMessage());
            }
            
            Map<String, List<String>> entityIdListMap = request.getIdListMap();
            Map<String, List<String>> entityNameListIdMap = request.getNameListMap();
            if (entityIdListMap != null)
                entityIdListMapCache.putIfAbsent(entityType, entityIdListMap);
            if (entityNameListIdMap != null)
                entityNameListMapCache.putIfAbsent(entityType, entityNameListIdMap);
            return entityNameListIdMap;
        }        
    }
    
    public static List<String> getEntityListById(String entityType, String listId) {
        Map<String, List<String>> entityIdListMap = getIdEntityListMap(entityType);
        if (entityIdListMap != null)
            return entityIdListMap.get(listId);
        return null;
    }

    public static List<String> getEntityListByName(String entityType, String listName) {
        Map<String, List<String>> entityNameListMap = getNameEntityListMap(entityType);
        if (entityNameListMap != null)
            return entityNameListMap.get(listName);
        return null;
    }
    
    private static void logAndException(String message) {
        log.error(message);
        throw new RuntimeException(message);
    }
}
