package com.hp.jmx.qc.dao;

import java.util.List;

import com.hp.jmx.qc.model.QCEntity;

public interface QCEntityDAO extends BaseDAO<QCEntity> {

    QCEntity get(QCEntity entity, long id);

    public List<QCEntity> query(QCEntity entity);
    
    public List<QCEntity> query(QCEntity entity, boolean isComplexQuery);
    
    public List<QCEntity> query(QCEntity entity, String queryString);
}
