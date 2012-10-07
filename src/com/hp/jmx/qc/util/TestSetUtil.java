package com.hp.jmx.qc.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.hp.jmx.qc.dao.QCEntityDAO;
import com.hp.jmx.qc.dao.impl.DAOFactory;
import com.hp.jmx.qc.model.EntityObject;
import com.hp.jmx.qc.model.KeyType;
import com.hp.jmx.qc.model.QCEntity;

public class TestSetUtil {
	private static final QCEntityDAO entityDAO = DAOFactory.getQCEntityDAO();
	public static final String defaultSubType="hp.qc.test-set.default";	
	public static final String OPEN_DATE_FIELD="open-date";
	public static final String CLOSE_DATE_FIELD="close-date";
	
	
	public static boolean isSourceTestSetExist(String name){
		return isTestSetExist(name,FolderUtil.getTestSetSourceFolderId());
	}
	
	public static boolean isTestSetExist(String name,String parentId){
		QCEntity entity = new QCEntity(EntityObject.TEST_SET_TYPE, KeyType.FIELD_NAME);
		entity.put(EntityUtil.NAME_FIELD, name);
		entity.put(EntityUtil.PARENT_ID_FIELD, parentId);
		List<QCEntity> entities = entityDAO.query(entity);
		if (entities.size()<1){        	
        	return false;
        } else {
        	return true;
        }
	}
	
	public static QCEntity createTestSetByName(String name){
		String folderId = FolderUtil.getTestSetSourceFolderId();
		QCEntity entity = new QCEntity(EntityObject.TEST_SET_TYPE, KeyType.FIELD_NAME);
		entity.put("name", name);
		entity.put("parent-id", folderId);
		entity.put("subtype-id", defaultSubType);
		entityDAO.save(entity);
		return entity;
	}
	
	public static QCEntity getTestSetById(String id){
		return EntityUtil.getEntityById(EntityObject.TEST_SET_TYPE, id);
	}
	
	public static List<QCEntity> getAllSourceTestSets() {
	    List<QCEntity> allTestSets = new ArrayList<QCEntity>();
	    List<String> testSetFolders=FolderUtil.getSourceTestSetFolders();
	    for (String folderId: testSetFolders) {
	        QCEntity entity = new QCEntity(EntityObject.TEST_SET_TYPE, KeyType.FIELD_NAME);
	        entity.put("parent-id", folderId);
	        List<QCEntity> entities = entityDAO.query(entity);
	        allTestSets.addAll(entities);
	    }
	    return allTestSets;
	}
	
	/**
     * check if given date is in this test set's period according to testset's open/close date
     * @param date : the date to check
     * @param testSet : the test set to check
     * @return true if given date is in this testset's period
     * @throws ParseException
     */
    public static boolean isInTestSetPeriod(Date date,QCEntity testSet) throws ParseException {
        String openDateString=testSet.get(OPEN_DATE_FIELD);
        String closeDateString=testSet.get(CLOSE_DATE_FIELD);
        DateFormat QCDateFormat= new SimpleDateFormat(EntityUtil.QCDateFormatPattern);
        Date openDate = QCDateFormat.parse(openDateString);
        if (openDate.after(date)) return false;
        if (closeDateString ==null) {
            return true; //no need to check close date
        }
        Date closeDate = QCDateFormat.parse(closeDateString);
        if (closeDate.before(date)) return false;
        return true;
        
    }
    public static boolean isInTestSetPeriod(String dateString,QCEntity testSet) throws ParseException {
        DateFormat QCDateFormat= new SimpleDateFormat(EntityUtil.QCDateFormatPattern);
        Date date = QCDateFormat.parse(dateString);
        return isInTestSetPeriod(date,testSet);
    }    
}
