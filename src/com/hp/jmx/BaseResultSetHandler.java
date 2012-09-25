package com.hp.jmx;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BaseResultSetHandler implements ResultSetHandler{

	@Override
	public Object handleResultSet(ResultSet rs) throws SQLException {
		int columnNumber = rs.getMetaData().getColumnCount();
		String[] result = new String[columnNumber];
		for (int i = 1; i <= columnNumber; i++) {
			result[i-1] = rs.getString(i);
		}
		return result;
	}

}
