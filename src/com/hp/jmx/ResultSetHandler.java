package com.hp.jmx;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface ResultSetHandler {
	public Object handleResultSet(ResultSet rs) throws SQLException ;
}
