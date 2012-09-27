package test;

import java.util.List;

import com.hp.jmx.qc.model.EntityObject;
import com.hp.jmx.qc.rest.entity.EntityListHelper;

public class TestEntityLists {
    public static void main(String[] args) {

        List<String> listValues = EntityListHelper.getEntityListById(EntityObject.TEST_TYPE, "1");
        printFieldMap(listValues);

        List<String> listValues2 = EntityListHelper.getEntityListByName(EntityObject.TEST_TYPE, "status");
        printFieldMap(listValues2);

    }
    
    private static void printFieldMap(List<String> values) {
        System.out.println(values);
    }
}
