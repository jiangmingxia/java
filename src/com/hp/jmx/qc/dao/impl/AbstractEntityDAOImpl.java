package com.hp.jmx.qc.dao.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.jmx.qc.dao.BaseDAO;
import com.hp.jmx.qc.dao.DAOException;
import com.hp.jmx.qc.model.EntityObject;
import com.hp.jmx.qc.rest.EntityNotFoundException;
import com.hp.jmx.qc.rest.QCRestClient;
import com.hp.jmx.qc.rest.QCRestRequest;
import com.hp.jmx.qc.rest.QCRestWSException;
import com.hp.jmx.qc.rest.entity.EntityQCRestRequest;
import com.hp.jmx.qc.rest.entity.GetEntitiesQCRestRequest;

public abstract class AbstractEntityDAOImpl<E extends EntityObject> implements BaseDAO<E> {

    private static final Logger log = LoggerFactory.getLogger(AbstractEntityDAOImpl.class);
    
    protected Class<E> clazz;
    
    @Override
    public void delete(E t){
        EntityQCRestRequest<E> request = new EntityQCRestRequest<E>(t, QCRestRequest.DELETE_METHOD);
        try {
            QCRestClient.getInstance().run(request);
        } catch (QCRestWSException e) {
            logAndException("Error happend when deleting the entity : " + t.getId(), e);
        }
        
        if (!request.isSucceed()) {
            logAndException(request.getErrorMessage());
        }
    }

    @Override
    public void save(E t) {
        EntityQCRestRequest<E> request = new EntityQCRestRequest<E>(t, QCRestRequest.POST_METHOD);
        try {
            QCRestClient.getInstance().run(request);
        } catch (QCRestWSException e) {
            logAndException("Error happend when creating the entity." + t, e);
        }
        
        if (!request.isSucceed()) {
            logAndException(request.getErrorMessage());
        }
    }

    @Override
    public void update(E t) {
        EntityQCRestRequest<E> request = new EntityQCRestRequest<E>(t, QCRestRequest.PUT_METHOD);
        try {
            QCRestClient.getInstance().run(request);
        } catch (QCRestWSException e) {
            logAndException("Error happend when updating the entity : " + t.getId(), e);
        }
        
        if (!request.isSucceed()) {
            logAndException(request.getErrorMessage());
        }
    }

    @Override
    public E get(long id) {
        if (clazz == null)
            logAndException("The entity class type must be injected before using this DAO.");
        E t = null;
        try {
            t = clazz.newInstance();
        } catch (Exception e) {
            logAndException("Error happened when new the entity instance.", e);
        }
        t.setId(id);
        EntityQCRestRequest<E> request = new EntityQCRestRequest<E>(t, QCRestRequest.GET_METHOD);
        try {
            QCRestClient.getInstance().run(request);
        } catch (EntityNotFoundException e) {
            log.info("The entity is not found with id : " + t.getId(), e);
            return null;
        }
        
        if (!request.isSucceed()) {
            logAndException(request.getErrorMessage());
        }
        
        return request.getEntity();
    }
    
    protected List<E> queryInternal(String filterString) {
        if (clazz == null)
            logAndException("The entity class type must be injected before using this DAO.");
        GetEntitiesQCRestRequest<E> request = new GetEntitiesQCRestRequest<E>(clazz, filterString);
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
