package test;

import java.io.File;
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

public class TestQCEntityDAO {
    
    private static final QCEntityDAO entityDAO = DAOFactory.getQCEntityDAO();
    
    @SuppressWarnings("deprecation")
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
    	
    	String input = "2012/06/30 6:56:11 : Scenario Name: Scenario17 Deploy Mode: /Isolate/Common Proxy/WebServer Authentication: /Forward Proxy/NTLM Authentication/No WebServer Authentication IsSSL: True IsWebServer: False Result: True";    	
    	String input2 = "asdf test{1}:pass fasdf";
    	String pattern1 = " ?([01]?\\d)(\\-|\\/|\\.)([0-3]?\\d)(\\-|\\/|\\.)(\\d{4}) +([012]?\\d):([0-5]\\d):([0-5]\\d) *(AM|PM|am|pm)? ?";
    	String pattern2 = " ?(\\d{4})(\\-|\\/|\\.)([01]?\\d)(\\-|\\/|\\.)([0-3]?\\d) +([012]?\\d):([0-5]\\d):([0-5]\\d) *(AM|PM|am|pm)? ?";
    	
    	String pattern3 = "(\\S+) *: *(pass|fail|ini)($| +.*$)";
    	
    	Pattern p = Pattern.compile(pattern1);
    	Matcher matcher=p.matcher(input);
    	//System.out.println(matcher.matches());
    	
    	while (matcher.find()) {
    	    int count = matcher.groupCount();
    	    System.out.println("matcher.groupCount():"+count);
    	    for (int i=1;i<=count;i++){
    	        System.out.println("matcher.group("+i+"):"+matcher.group(i));
    	    }
			
		}
    	
    	p = Pattern.compile(pattern2);
        matcher=p.matcher(input);
        //System.out.println(matcher.matches());
        
        while (matcher.find()) {
            int count = matcher.groupCount();
            System.out.println("matcher.groupCount():"+count);
            for (int i=1;i<=count;i++){
                System.out.println("matcher.group("+i+"):"+matcher.group(i));
            }
            
        }
        
        p = Pattern.compile(pattern3);
        matcher=p.matcher(input2);
        //System.out.println(matcher.matches());
        
        while (matcher.find()) {
            int count = matcher.groupCount();
            System.out.println("matcher.groupCount():"+count);
            for (int i=1;i<=count;i++){
                System.out.println("matcher.group("+i+"):"+matcher.group(i));
            }
            
        }
        
        //QC:    18:11:51       2009-01-28
        DateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        dateTimeFormat.setLenient(false);
        Date date =null;
        try {
           
                date = dateTimeFormat.parse("2012-11-31 23:23:33");
                System.out.println("你输入的日期合法");
        } catch (Exception e) {
            System.out.println("你输入的日期不合法，请重新输入");
        }
        
        File file = new File("C:\\Users\\jiamingx\\Downloads\\ts1.txt");
        Date d1 = new Date(file.lastModified());
        Date d2 = new Date(2010,12,10,11,24,38);
        dateTimeFormat.format(d2);
        System.out.println(dateTimeFormat.format(d2));
    	
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
