package com.hp.jmx.qc.dao.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.jmx.qc.dao.DAOException;
import com.hp.jmx.qc.dao.TestDAO;
import com.hp.jmx.qc.model.Test;

public class TestDAOImpl extends AbstractEntityDAOImpl<Test> implements TestDAO {

    private static final Logger log = LoggerFactory.getLogger(TestDAOImpl.class);
    
    public TestDAOImpl() {
        this.clazz = Test.class;
    }
    
    @Override
    public Test getTestByName(String testName) {
        String filterString = "query={name[" + testName + "]}";
        List<Test> results = this.queryInternal(filterString);
        if (results == null || results.size() == 0)
            return null;
        else if (results.size() != 1) {
            log.error("The expected results of getTestByName() size is 1, but got size is: " + results.size());
            throw new DAOException("The expected results of getTestByName() size is 1, but got size is: " + results.size());
        }
        else
            return results.get(0);
    }

}
