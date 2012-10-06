package test;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hp.jmx.cmd.Command;
import com.hp.jmx.cmd.CommandFactory;
import com.hp.jmx.cmd.CommandReader;
import com.hp.jmx.qc.dao.QCEntityDAO;
import com.hp.jmx.qc.dao.impl.DAOFactory;
import com.hp.jmx.qc.model.EntityObject;
import com.hp.jmx.qc.model.KeyType;
import com.hp.jmx.qc.model.QCEntity;
import com.hp.jmx.qc.util.EntityUtil;
import com.hp.jmx.qc.util.FolderUtil;
import com.hp.jmx.qc.util.TestSetUtil;

public class TestQCEntityDAO {
    
    private static final QCEntityDAO entityDAO = DAOFactory.getQCEntityDAO();
    
    @SuppressWarnings("deprecation")
	public static void main(String[] args) {
    	testQuery();
    	//instanceQuery();
    	//createQuery();
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
        
        entityDAO.save(entity);
        
        System.out.println("The test id is : " + entity.getEntityId());
        
        return entity;
    }
    
    
}
