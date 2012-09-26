package com.hp.jmx.qc.util;

import java.util.List;

import com.hp.jmx.qc.dao.QCEntityDAO;
import com.hp.jmx.qc.dao.impl.DAOFactory;
import com.hp.jmx.qc.model.EntityObject;
import com.hp.jmx.qc.model.KeyType;
import com.hp.jmx.qc.model.QCEntity;

public class InstanceUtil {
	private static final QCEntityDAO entityDAO = DAOFactory.getQCEntityDAO();
	private static final String defaultSubType="hp.qc.test-instance.MANUAL";
	
	public static QCEntity addTestToTestSet(String testId,String testSetId){
		QCEntity entity = new QCEntity(EntityObject.TEST_INSTANCE_TYPE, KeyType.FIELD_NAME);
		entity.put("cycle-id",testSetId);
		entity.put("test-id", testId);
		entity.put("subtype-id",defaultSubType);
		entityDAO.save(entity);
		return entity;
	}

}
