package com.hp.jmx.cmd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hp.jmx.qc.model.QCEntity;
import com.hp.jmx.qc.util.InstanceUtil;

public class TestSetsInfo {
    private Map<String,QCEntity> testSetMap;
    private Map<String,String> instanceTestSetMap;
    private Map<String,List<String>> testInstancesMap;
    private List<QCEntity> testSetEntities;
    
    public TestSetsInfo(List<QCEntity> testSets) {
        this.testSetEntities=testSets;
    }
    
    private void initMaps() {        
        instanceTestSetMap=new HashMap<String,String>(); //instanceId->testsetId
        testInstancesMap=new HashMap<String,List<String>>(); //testId->InstanceIds
        
        for (QCEntity testSet:testSetEntities){
            String testSetId=testSet.getEntityId();                      
            List<QCEntity> instances=InstanceUtil.getTestSetAllInstances(testSetId);
            for (QCEntity instance: instances) {
                String instanceId=instance.getEntityId();
                instanceTestSetMap.put(instanceId, testSetId);
                String testId=instance.get(InstanceUtil.TEST_ID_FIELD);                
                if (testInstancesMap.get(testId)==null) {
                    List<String> instanceIds = new ArrayList<String>();
                    instanceIds.add(instanceId);
                    testInstancesMap.put(testId, instanceIds);
                } else {
                    testInstancesMap.get(testId).add(instanceId);
                }
            }
        } 
    }
    
    public Map<String,QCEntity> getTestSetMap(){
        if (testSetMap==null) {
            testSetMap=new HashMap<String,QCEntity>(); //testsetId->testset entity
            for (QCEntity testSet:testSetEntities){
                String testSetId=testSet.getEntityId();
                testSetMap.put(testSetId, testSet);
            } 
        }
        return testSetMap;
    }
    
    public Map<String,String> getInstanceTestSetMap(){
        if (instanceTestSetMap==null) {
            initMaps();
        }
        return instanceTestSetMap;
    }
    
    public Map<String,List<String>> getTestInstancesMap(){
        if (testInstancesMap==null) {
            initMaps();
        }
        return testInstancesMap;
    }

}
