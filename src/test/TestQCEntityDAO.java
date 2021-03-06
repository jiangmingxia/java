package test;

import java.io.IOException;

import java.util.List;

import com.hp.jmx.qc.dao.QCEntityDAO;
import com.hp.jmx.qc.dao.impl.DAOFactory;
import com.hp.jmx.qc.model.EntityObject;
import com.hp.jmx.qc.model.KeyType;
import com.hp.jmx.qc.model.QCEntity;
import com.hp.jmx.qc.rest.QCRestClient;
import com.hp.jmx.qc.rest.QCRestConfig;
import com.hp.jmx.util.*;

public class TestQCEntityDAO {
    
    private static final QCEntityDAO entityDAO = DAOFactory.getQCEntityDAO();
    
    
	public static void main(String[] args) throws Exception {
		String pwd = "dela";
		MyEncode.encode(pwd);		
    }
	
	

    private static QCEntity testQuery() {
        QCEntity entity = new QCEntity(EntityObject.TEST_TYPE, KeyType.FIELD_NAME);
        entity.put("user-01", "apollo or a");       
        
        List<QCEntity> entities = entityDAO.query(entity,true);
        
        for (QCEntity item : entities) {
        	
            System.out.println("The queried test is : " + item);
        }
        
        return entity;
    }
    
    private static QCEntity testSetQuery() {
        QCEntity entity = new QCEntity(EntityObject.TEST_SET_TYPE, KeyType.FIELD_NAME);
        entity.put(QCEntity.ID_NAME, "2");       
        
        List<QCEntity> entities = entityDAO.query(entity,false);
        
        for (QCEntity item : entities) {
        	System.out.println("The queried test set name is "+ item.getEntityName());
        	System.out.println("The queried test set parent id is "+ item.getEntityParentId());
            System.out.println("The queried test is : " + item);
        }
        
        return entity;
    }
    
    private static QCEntity folderQuery() {
        QCEntity entity = new QCEntity(EntityObject.TEST_FOLDER_TYPE, KeyType.FIELD_NAME);
        //entity.put("parent-id", "2");
        entity.put("name", "Subject");
        
        
        List<QCEntity> entities = entityDAO.query(entity);
        
        for (QCEntity item : entities) {        	
            System.out.println("The queried test is : " + item);            
        }
        
       
        return entity;
    }
    
    private static QCEntity instanceQuery() {
        QCEntity entity = new QCEntity(EntityObject.TEST_INSTANCE_TYPE, KeyType.FIELD_NAME);
        //entity.put("parent-id", "2");
        entity.put("contains-test-set.id", "5");
        
        
        List<QCEntity> entities = entityDAO.query(entity);
        
        for (QCEntity item : entities) {        	
            System.out.println("The queried test is : " + item);            
        }
        try {
        	System.out.println("begin log out");
			QCRestClient.getInstance().logoutSession(QCRestConfig.getQCRestURL());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return entity;
    }
    
    private static QCEntity createQuery() {
        QCEntity entity = new QCEntity(EntityObject.RUN_TYPE, KeyType.FIELD_NAME);
        //entity.put("parent-id", "2");
        entity.put("name", "auto_9_29");
        entity.put("cycle-id", "5");
        entity.put("testcycl-id", "28");
        entity.put("status", "Failed");
        entity.put("test-id", "3");
        entity.put("owner", "dela");
        entity.put("subtype-id", "hp.qc.run.MANUAL");
        //entity.put("", "");
        entityDAO.save(entity);
        System.out.println("The run id is : " + entity.getEntityId());
        return entity;
       
    }

    private static QCEntity testCreate() {
        QCEntity entity = new QCEntity(EntityObject.TEST_TYPE, KeyType.FIELD_NAME);
        entity.put("name", "Created by EntityDAO");
        entity.put("parent-id", "1001");
        entity.put("subtype-id", "MANUAL");
        
        entityDAO.update(entity);
        
        System.out.println("The test id is : " + entity.getEntityId());
        
        return entity;
    }
    
    
}
