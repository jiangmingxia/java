package com.hp.jmx.qc.dao.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.jmx.qc.dao.DAOException;
import com.hp.jmx.qc.dao.QCEntityDAO;
import com.hp.jmx.qc.model.QCEntity;
import com.hp.jmx.qc.rest.EntityNotFoundException;
import com.hp.jmx.qc.rest.QCRestClient;
import com.hp.jmx.qc.rest.QCRestRequest;
import com.hp.jmx.qc.rest.QCRestWSException;
import com.hp.jmx.qc.rest.entity.GetEntitiesQCRestRequest;
import com.hp.jmx.qc.rest.entity.GetQCEntitiesRequest;
import com.hp.jmx.qc.rest.entity.QCEntityRequest;

public class QCEntityDAOImpl implements QCEntityDAO {

    private static final Logger log = LoggerFactory.getLogger(AbstractEntityDAOImpl.class);
        
    @Override
    public void delete(QCEntity entity){
        QCEntityRequest request = new QCEntityRequest(entity, QCRestRequest.DELETE_METHOD);
        try {
            QCRestClient.getInstance().run(request);
        } catch (QCRestWSException e) {
            logAndException("Error happend when deleting the entity : " + entity.getEntityId(), e);
        }
        
        if (!request.isSucceed()) {
            logAndException(request.getErrorMessage());
        }
    }

    @Override
    public void save(QCEntity entity) {
        QCEntityRequest request = new QCEntityRequest(entity, QCRestRequest.POST_METHOD);
        try {
            QCRestClient.getInstance().run(request);
        } catch (QCRestWSException e) {
            logAndException("Error happend when creating the entity." + entity, e);
        }
        
        if (!request.isSucceed()) {
            logAndException(request.getErrorMessage());
        }
    }

    @Override
    public void update(QCEntity entity) {
        QCEntityRequest request = new QCEntityRequest(entity, QCRestRequest.PUT_METHOD);
        try {
            QCRestClient.getInstance().run(request);
        } catch (QCRestWSException e) {
            logAndException("Error happend when updating the entity : " + entity, e);
        }
        
        if (!request.isSucceed()) {
            logAndException(request.getErrorMessage());
        }
    }


    @Override
    public QCEntity get(long id) {
        log.error("Unsupported get method. Please use the ");
        throw new DAOException("Unsupported get method. Please use the ");
    }
    
    @Override
    public QCEntity get(QCEntity entity, long id) {
        QCEntityRequest request = new QCEntityRequest(entity, QCRestRequest.GET_METHOD, id);
        try {
            QCRestClient.getInstance().run(request);
        } catch (EntityNotFoundException e) {
            log.info("The entity is not found with id : " + entity, e);
            return null;
        }
        
        if (!request.isSucceed()) {
            logAndException(request.getErrorMessage());
        }
        
        return request.getEntity();
    }
    
    public List<QCEntity> query(QCEntity entity) {
        GetQCEntitiesRequest request = new GetQCEntitiesRequest(entity);
        QCRestClient.getInstance().run(request);
        
        if (!request.isSucceed()) {
            logAndException(request.getErrorMessage());
        }
        
        return request.getEntities();
        
    }

    public List<QCEntity> query(QCEntity entity, String queryString) {
        GetQCEntitiesRequest request = new GetQCEntitiesRequest(entity,queryString);
        QCRestClient.getInstance().run(request);
        
        if (!request.isSucceed()) {
            logAndException(request.getErrorMessage());
        }
        
        return request.getEntities();
    }
    
    protected void logAndException(String message) {
        log.error(message);
        throw new DAOException(message);
    }

    protected void logAndException(String message, Exception e) {
        log.error(message, e);
        throw new DAOException(message, e);
    }

}
