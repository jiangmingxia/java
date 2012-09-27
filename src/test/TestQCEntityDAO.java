package test;

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

public class TestQCEntityDAO {
    
    private static final QCEntityDAO entityDAO = DAOFactory.getQCEntityDAO();
    
    public static void main(String[] args) {
        //QCEntity entity = testCreate();
        //testQuery();
//    	String cmd = "create_testset -name myTS3 -file C:\\Users\\jiamingx\\Downloads\\ts1.txt";
//    	Command cmdor = CommandFactory.getCommand(CommandReader.getCommandName(cmd));
//    	if (cmdor == null) {
//    		return;
//    	}
//    	if (!cmdor.execute(CommandReader.getOptions(cmd))){
//    		System.out.println("Error occurs during creating testset!");
//    	} else {
//    		System.out.println("Create test set successfully done.");
//    	}
    	
    	String input = "9/18/2012";
    	//String pattern = "(^.*/s|^)([0-1]?/d)(\\-|\\/|\\.)([0-3]?/d)(\\-|\\/|\\.)(/d{4})/s.*";
    	String pattern = "^([01]?\\d)/([0123]\\d)/(\\d{4})$";
    	Pattern p = Pattern.compile(pattern);
    	Matcher matcher=p.matcher(input);
    	System.out.println(matcher.matches());
    	while (matcher.find()) {
			String option = matcher.group(1); //remove "-"
			System.out.println(option);
		}
    	
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
