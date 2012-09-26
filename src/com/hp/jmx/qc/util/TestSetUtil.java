package com.hp.jmx.qc.util;

import java.util.List;

import com.hp.jmx.qc.dao.QCEntityDAO;
import com.hp.jmx.qc.dao.impl.DAOFactory;
import com.hp.jmx.qc.model.EntityObject;
import com.hp.jmx.qc.model.KeyType;
import com.hp.jmx.qc.model.QCEntity;

public class TestSetUtil {
	private static final QCEntityDAO entityDAO = DAOFactory.getQCEntityDAO();
	private static final String defaultSubType="hp.qc.test-set.default";
	
	public static boolean isSourceTestSetExist(String name){
		return isTestSetExist(name,FolderUtil.getTestSetSourceFolderId());
	}
	
	public static boolean isTestSetExist(String name,String parentId){
		QCEntity entity = new QCEntity(EntityObject.TEST_SET_TYPE, KeyType.FIELD_NAME);
		entity.put("name", name);
		entity.put("parent-id", parentId);
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
}
