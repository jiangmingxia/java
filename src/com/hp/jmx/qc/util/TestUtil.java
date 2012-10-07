package com.hp.jmx.qc.util;

import java.util.List;

import com.hp.jmx.qc.dao.QCEntityDAO;
import com.hp.jmx.qc.dao.impl.DAOFactory;
import com.hp.jmx.qc.model.EntityObject;
import com.hp.jmx.qc.model.KeyType;
import com.hp.jmx.qc.model.QCEntity;

public class TestUtil {
	private static final QCEntityDAO entityDAO = DAOFactory.getQCEntityDAO();
	public static final String defaultSubType="MANUAL";
	
	public static boolean isTestExists(String name){
		String parentId = FolderUtil.getTestSourceFolderId();
		return isTestExistsByParentId(name,parentId);
	}

	private static boolean isTestExistsByParentId(String name, String parentId){
		QCEntity entity = new QCEntity(EntityObject.TEST_TYPE, KeyType.FIELD_NAME);
		entity.put("name", name);
		entity.put("parent-id", parentId);
		List<QCEntity> entities = entityDAO.query(entity);
		if (entities.size()<1){        	
        	return false;
        } else {
        	return true;
        }
	}
	
	public static QCEntity createTestByName(String name){
		String folderId = FolderUtil.getTestSourceFolderId();
		QCEntity entity = new QCEntity(EntityObject.TEST_TYPE, KeyType.FIELD_NAME);
		entity.put("name", name);
		entity.put("parent-id", folderId);
		entity.put("subtype-id", defaultSubType);
		entityDAO.save(entity);
		return entity;
	}
	
	//search test with specified name and check if it is source test.
	//if it is source test return the result
	//Note: we have assumption that test names under source folders are all different. 
	public static QCEntity getTestByName(String name) {		
		QCEntity entity = new QCEntity(EntityObject.TEST_TYPE, KeyType.FIELD_NAME);
		entity.put("name", name);
		
		List<QCEntity> entities = entityDAO.query(entity);
		if (entities.size()<1) return null;
		List<String> folderIds = FolderUtil.getSourceTestFolders();
		for (QCEntity test: entities) {
		    String parentId = test.getEntityParentId();
		    for (String testFolderId : folderIds) {
		        if (parentId.equals(testFolderId)) {
		            return test;
		        }
		    }
		}
		return null;
	}
	
	public static QCEntity getTestById(String id){
		return EntityUtil.getEntityById(EntityObject.TEST_TYPE, id);
	}

}
