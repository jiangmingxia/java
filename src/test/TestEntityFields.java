package test;

import java.util.Map;

import com.hp.jmx.qc.model.EntityObject;
import com.hp.jmx.qc.model.Field;
import com.hp.jmx.qc.rest.entity.EntityFieldHelper;

public class TestEntityFields {
    public static void main(String[] args) {
        
        Map<String, Field> runLabelFieldsMap = EntityFieldHelper.getLabelFieldMap(EntityObject.RUN_TYPE);
        printFieldMap(runLabelFieldsMap);

        Map<String, Field> testNameFieldsMap = EntityFieldHelper.getNameFieldMap(EntityObject.TEST_TYPE);
        printFieldMap(testNameFieldsMap);
    }
    
    private static void printFieldMap(Map<String, Field> map) {
        for (String key : map.keySet()) {
            Field field = map.get(key);
            System.out.println("Field " + key + " : " + field.getName() + ", " + field.getLabel() + ", " + field.getPhysicalName());
        }
    }
}
