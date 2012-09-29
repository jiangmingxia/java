package com.hp.jmx.qc.util;

import java.util.List;

import com.hp.jmx.qc.dao.QCEntityDAO;
import com.hp.jmx.qc.dao.impl.DAOFactory;
import com.hp.jmx.qc.model.KeyType;
import com.hp.jmx.qc.model.QCEntity;

public class EntityUtil {
	private static final QCEntityDAO entityDAO = DAOFactory.getQCEntityDAO();
	
	public static QCEntity getEntityById(String type, String id){
		QCEntity entity = new QCEntity(type, KeyType.FIELD_NAME);
        entity.put(QCEntity.ID_NAME,id);        
        List<QCEntity> entities = entityDAO.query(entity,false);        
        if (entities.size()<1){        	
        	return null;
        }
        return entities.get(0);
	}
}
