package com.hp.jmx.qc.dao.impl;

import com.hp.jmx.qc.dao.QCEntityDAO;
import com.hp.jmx.qc.dao.TestDAO;

public class DAOFactory {

    private static final TestDAO testDAO = new TestDAOImpl();

    private static final QCEntityDAO qcEntityDAO = new QCEntityDAOImpl();
    
    public static TestDAO getTestDAO() {
        return testDAO;
    }
    
    public static QCEntityDAO getQCEntityDAO() {
        return qcEntityDAO;
    }
}
