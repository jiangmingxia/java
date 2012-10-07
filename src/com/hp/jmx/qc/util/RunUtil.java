package com.hp.jmx.qc.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.hp.jmx.qc.dao.QCEntityDAO;
import com.hp.jmx.qc.dao.impl.DAOFactory;
import com.hp.jmx.qc.model.EntityObject;
import com.hp.jmx.qc.model.KeyType;
import com.hp.jmx.qc.model.QCEntity;

public class RunUtil {
    private static final QCEntityDAO entityDAO = DAOFactory.getQCEntityDAO();
        
    public static final String defaultSubType="hp.qc.run.MANUAL";    
    public static final String TEST_ID_FIELD="test-id";
    public static final String INSTANCE_ID_FIELD="testcycl-id";
    public static final String TESTSET_ID_FIELD="cycle-id";
    public static final String STATUS_FIELD="status";
    public static final String OWNER_FIELD="owner";
    public static final String SUBTYPE_ID_FIELD="subtype-id";
    public static final String EXECDATE_FIELD="execution-date";
    public static final String EXECTIME_FIELD="execution-time";
    
    private static QCEntity createRun(String testId,String instanceId,String testSetId,String status,String owner,String execDate,String execTime){  
        String name = getRunName();
        if (name==null) return null;
        QCEntity entity = new QCEntity(EntityObject.RUN_TYPE, KeyType.FIELD_NAME);
        entity.put(EntityUtil.NAME_FIELD, "");
        entity.put(TEST_ID_FIELD, testId);
        entity.put(INSTANCE_ID_FIELD,instanceId);
        entity.put(TESTSET_ID_FIELD,testSetId);
        entity.put(OWNER_FIELD,owner);
        entity.put(STATUS_FIELD,status);
        entity.put(SUBTYPE_ID_FIELD,defaultSubType);
        entity.put(EXECDATE_FIELD,execDate);
        entity.put(EXECTIME_FIELD,execTime);
        entityDAO.save(entity);
        return entity;
    }
    
    private static QCEntity createRun(String testId,String instanceId,String testSetId,String status,String owner,String execDate,String execTime,Map<String,String> otherDetails){  
        String name = getRunName();
        if (name==null) return null;
        QCEntity entity = new QCEntity(EntityObject.RUN_TYPE, KeyType.FIELD_NAME);
        entity.put(EntityUtil.NAME_FIELD, "");
        entity.put(TEST_ID_FIELD, testId);
        entity.put(INSTANCE_ID_FIELD,instanceId);
        entity.put(TESTSET_ID_FIELD,testSetId);
        entity.put(OWNER_FIELD,owner);
        entity.put(STATUS_FIELD,status);
        entity.put(SUBTYPE_ID_FIELD,defaultSubType);
        entity.put(EXECDATE_FIELD,execDate);
        entity.put(EXECTIME_FIELD,execTime);
        
        for (String key:otherDetails.keySet()) {
            entity.put(key, otherDetails.get(key));
        }
        entityDAO.save(entity);
        return entity;
    }
    
    public static QCEntity createRun(String testId,String instanceId,String testSetId,String status,String owner,Date runDate){ 
        DateFormat QCDateFormat=new SimpleDateFormat(EntityUtil.QCDateFormatPattern);
        DateFormat QCTimeFormat=new SimpleDateFormat(EntityUtil.QCTimeFormatPattern);
        String execDate = QCDateFormat.format(runDate);
        String execTime = QCTimeFormat.format(runDate);
        return createRun(testId,instanceId,testSetId,status,owner,execDate,execTime);
    }
    
    public static QCEntity createRun(String testId,String instanceId,String testSetId,String status,String owner,Date runDate,Map<String,String> otherDetails){ 
        DateFormat QCDateFormat=new SimpleDateFormat(EntityUtil.QCDateFormatPattern);
        DateFormat QCTimeFormat=new SimpleDateFormat(EntityUtil.QCTimeFormatPattern);
        String execDate = QCDateFormat.format(runDate);
        String execTime = QCTimeFormat.format(runDate);
        return createRun(testId,instanceId,testSetId,status,owner,execDate,execTime,otherDetails);
    }

    
    private static String getUniqueRunName(String prefix) {
        String runName = prefix;        
        QCEntity entity = new QCEntity(EntityObject.RUN_TYPE, KeyType.FIELD_NAME);
        entity.put(EntityUtil.NAME_FIELD, runName);
        List<QCEntity> entities = entityDAO.query(entity);
        int i=1;
        final int maxNum=100;
        while (entities.size()>0&&i<=maxNum) {
            runName = prefix+"_"+i; 
            entity = new QCEntity(EntityObject.RUN_TYPE, KeyType.FIELD_NAME);
            entity.put(EntityUtil.NAME_FIELD, runName);
            entities = entityDAO.query(entity);
            i++;
        }
        if (i>50){
            System.out.println("Please try another prefix for run name.");
            return null;
        }
        return runName;
    }
    
    
    //return name like AUTO_RUN_2012-10-07_13-48-03_1, AUTO_RUN_2012-10-07_13-48-03_2, ....
    private static String getRunName(){
        Date date = new Date();        
        DateFormat df=new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String namePrefix = "AUTO_RUN_"+df.format(date);        
        return getUniqueRunName(namePrefix);
    }    
}
