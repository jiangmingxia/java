package test;

import com.hp.jmx.qc.dao.TestDAO;
import com.hp.jmx.qc.dao.impl.DAOFactory;
import com.hp.jmx.qc.model.Test;

public class TestTestDAO {
    
    private static TestDAO testDAO = DAOFactory.getTestDAO();
    
    public static void main(String[] args) {
        Test created = testCreate();
        Test got = testDAO.get(created.getId());
        System.out.println("The got test name is:" + got.getName());
        got.setName("test2");
        testDAO.update(got);
        
        Test updated = testDAO.get(created.getId());
        System.out.println("The updated test name is: " + updated.getName());
        
        testDAO.delete(created);
        Test got2 = testDAO.get(created.getId());
        if (got2 == null)
            System.out.println("Successful delete the test: " + created.getId());
        
        Test gotByName = testDAO.getTestByName("sampel");
        System.out.println("Successful get the test: " + gotByName.getId());
    }
    
    private static Test testCreate() {
        Test test = new Test();
        test.setParentId(1001);
        test.setName("test1");
        test.setSubTypeId("MANUAL");
        
        testDAO.save(test);
        
        System.out.println("The test id is : " + test.getId());
        
        return test;
    }
}
