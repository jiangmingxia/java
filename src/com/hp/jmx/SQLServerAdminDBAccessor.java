package com.hp.jmx;

import java.sql.SQLException;
import java.util.List;

public class SQLServerAdminDBAccessor extends SQLServerDBAccessor implements DBAccessor {
	
	public SQLServerAdminDBAccessor(String jdbcURL, String username, String password, String dbName) {
		super(jdbcURL, username, password, dbName);
		this.QCSchema=getQCSchema(dbName);
	}	
	
	//Get schema for QC siteadmin DB, usually it is td, but it could be different.
	private String getQCSchema(String adminDB) {
		String sql= "select name from "+adminDB+".sys.schemas where schema_id<10 and name <> 'guest' and name <> 'sys' and name not like 'INFORMATION%'";
		try {
			List<Object> result=query(sql);
			for (Object o : result){
				String[] r  = (String[]) o;				
				String schema = r[0];
				String tbName=adminDB+"."+schema+".ADMIN";
				int tbExist=isTableExist(adminDB, tbName);
				if (tbExist == 1){
					return schema;					
				}
			}
		} catch (SQLException e) {			
			e.printStackTrace();
		}		
		return null;
	}	
}
	
