package com.hp.jmx.qc.dao;

import com.hp.jmx.qc.model.Test;

public interface TestDAO extends BaseDAO<Test> {

	public Test getTestByName(String testName);
	
}
