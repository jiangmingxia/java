package com.hp.jmx.qc.util;

import java.util.List;
import com.hp.jmx.qc.dao.QCEntityDAO;
import com.hp.jmx.qc.dao.impl.DAOFactory;
import com.hp.jmx.qc.model.KeyType;
import com.hp.jmx.qc.model.QCEntity;

public class EntityUtil {
	private static final QCEntityDAO entityDAO = DAOFactory.getQCEntityDAO();
	public static final String NAME_FIELD="name";
    public static final String PARENT_ID_FIELD="parent-id";
    
    public final static String QCDateTimeFormatPattern="yyyy-MM-dd HH:mm:ss";
    public final static String QCDateFormatPattern="yyyy-MM-dd";
    public final static String QCTimeFormatPattern="HH:mm:ss";    
    
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
