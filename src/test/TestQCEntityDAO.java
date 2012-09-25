package test;

import java.util.List;

import com.hp.jmx.qc.dao.QCEntityDAO;
import com.hp.jmx.qc.dao.impl.DAOFactory;
import com.hp.jmx.qc.model.EntityObject;
import com.hp.jmx.qc.model.KeyType;
import com.hp.jmx.qc.model.QCEntity;

public class TestQCEntityDAO {
    
    private static final QCEntityDAO entityDAO = DAOFactory.getQCEntityDAO();
    
    public static void main(String[] args) {
        //QCEntity entity = testCreate();
        testQuery();
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
