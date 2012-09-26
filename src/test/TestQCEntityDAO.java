package test;

import java.util.List;

import com.hp.jmx.qc.dao.QCEntityDAO;
import com.hp.jmx.qc.dao.impl.DAOFactory;
import com.hp.jmx.qc.model.EntityObject;
import com.hp.jmx.qc.model.KeyType;
import com.hp.jmx.qc.model.QCEntity;

import com.hp.jmx.qc.util.FolderUtil;
import com.hp.jmx.qc.util.InstanceUtil;
import com.hp.jmx.qc.util.TestUtil;

public class TestQCEntityDAO {
    
    private static final QCEntityDAO entityDAO = DAOFactory.getQCEntityDAO();
    
    public static void main(String[] args) {
        //QCEntity entity = testCreate();
        //testQuery();
    	    	
    	System.out.println(InstanceUtil.addTestToTestSet("4", "2"));    	
    	
    	
    	
    }

    private static QCEntity testQuery() {
        QCEntity entity = new QCEntity(EntityObject.TEST_TYPE, KeyType.FIELD_NAME);
        entity.put("parent-id", "1001");
        entity.put("subtype-id", "MANUAL");
        
        List<QCEntity> entities = entityDAO.query(entity);
        
        for (QCEntity item : entities) {
        	
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

    private static QCEntity testCreate() {
        QCEntity entity = new QCEntity(EntityObject.TEST_TYPE, KeyType.FIELD_NAME);
        entity.put("name", "Created by EntityDAO");
        entity.put("parent-id", "1001");
        entity.put("subtype-id", "MANUAL");
        
        entityDAO.save(entity);
        
        System.out.println("The test id is : " + entity.getEntityId());
        
        return entity;
    }
}
