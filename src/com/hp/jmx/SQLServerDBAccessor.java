package com.hp.jmx;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SQLServerDBAccessor extends BaseDBAccessor implements DBAccessor {
	public SQLServerDBAccessor(String jdbcURL, String username, String password, String dbName) {
		super(jdbcURL, username, password, dbName);
	}
	
	protected String QCSchema="td";

	@Override
	String getProjectServersSQL() {
		String wholeName=this.dbName+"."+QCSchema+".projects";
		String sql = "Select DB_NAME, DB_CONNSTR_FORMAT from "+wholeName;
		return sql;
	}
	
	@Override
	String getUpgradeSiteAdminSQL() {
		String sql= "Update "+this.dbName+"."+QCSchema+".ADMIN set DOMS_VERSION='11.51' where DOMS_VERSION='11.50'";
		return sql;		
	}
	
	@Override
	//if exists (select * from sysobjects where id = object_id('td.PROJECT_EXTENSIONS')) update td.PROJECT_EXTENSIONS set PE_EXTENSION_VERSION='1.00' where PE_EXTENSION_NAME = 'ALM_SAMPLE_EXTENSION'");
	int downgradeSAExtensions() {		
		String project_extension_tb = this.dbName+"."+QCSchema+".PROJECT_EXTENSIONS";		
		 
		String sql = "if exists (select * from "+this.dbName+"..sysobjects where id = object_id('"+project_extension_tb+"')) update "+project_extension_tb+" set PE_EXTENSION_VERSION='1.00' where PE_EXTENSION_NAME = 'ALM_SAMPLE_EXTENSION'";
		return this.executeSQL(sql);
	}
	
	@Override
	String getDowngradeExtensionSQL() {
		String extensions_tb=this.dbName+"."+QCSchema+".extensions";
		String sql = "if exists (select * from "+this.dbName+"..sysobjects where id = object_id('"+extensions_tb+"')) update "+extensions_tb+" set EX_VERSION='1.00' where EX_NAME='ALM_SAMPLE_EXTENSION'";
		return sql;
	}
	
	
	
	@Override
	int deleteTable(String tableName) {		
		String wholeName = this.dbName+"."+QCSchema+"."+tableName;
		String sql = "if exists (select * from "+this.dbName+"..sysobjects where id = object_id('"+wholeName+"')) drop table "+wholeName ;
		return this.executeSQL(sql);		
	}

	

	@Override
	List<String> getDowngradeSiteAdminSQL() {
		List<String> sqls = new ArrayList<String>();
		String project_tb = this.dbName+"."+QCSchema+".projects";
		String admin_tb = this.dbName+"."+QCSchema+".ADMIN";
		sqls.add("UPDATE "+project_tb+" set PR_VERSION='11.50' where PR_VERSION='11.51'");
		sqls.add("UPDATE "+admin_tb+" set DOMS_VERSION='11.50' where DOMS_VERSION='11.51'");		
		return sqls;
	}

	@Override
	String getDowngradeProjectVersionSQL() {
		return "update "+this.dbName+"."+QCSchema+".DATACONST set DC_VALUE='11.50' where DC_CONST_NAME='version' and DC_VALUE='11.51'";     
	}
	
	/**
	 * Check if given user exists 
	 * @param user(database name)
	 * @return 1 when user exists
	 *         0 when user not exist
	 *         -1 when get error or exception
	 */
	@Override
	int isDBExist(String dbName) {
		String sql= "select count(*) from master.dbo.sysdatabases where name='"+dbName+"'";
		String result = atomicResultQuery(sql);
		if (result == null) return -1;
		int isExist = Integer.parseInt(result);
		if (isExist > 0) {
			return 1;
		} else {				
			return 0;
		}		
	}
	
	/**
	 * Check if given table exists 
	 * @param table whole name
	 * @return 1 when table exists
	 *         0 when table not exist
	 *         -1 when get error or exception
	 */	
	protected int isTableExist(String db, String tableWholeName) {
		String sql= "select count(*) from "+db+"..sysobjects where id = object_id('"+tableWholeName+"')";
		try {
			List<Object> result=query(sql);
			if (result.size()!=1) {
				System.out.println("select count (*) result row number should be 1, but now it is "+result.size());
				return -1;
			}
			String[] r = (String [])result.get(0);
			if (r.length!=1) {
				System.out.println("select count (*) result column number should be 1, but now it is "+r.length);
				return -1;
			}
			
			int tbExist = Integer.parseInt(r[0]);
			if (tbExist > 0) {
				return 1;
			} else {				
				return 0;
			}
		} catch (SQLException e) {			
			e.printStackTrace();
			return -1;
		}
	}

	@Override
	public int dropCurrentDB() {		
		int dbExist = isCurrentDBExist();
		if (dbExist == 1) {
			List<String> sqls = new ArrayList<String>();			
			sqls.add("ALTER DATABASE "+this.dbName+" SET SINGLE_USER");
			sqls.add("drop database "+this.dbName);		
			int[] results=this.executeBatch(sqls);
			if (results != null) {
				return 1;
			}
		}
		if (dbExist == 0) {
			System.out.println(this.dbName+" does not exist. No need to delete it.");
			return 0;
		}
		//Any other cases
		return -1;		
	}

	@Override
	public int isCurrentDBExist() {			
		return isDBExist(this.dbName);
	}

	@Override
	String getAllDBNameSQL(String nameKeyWord) {
		String sql = "select name from master.dbo.sysdatabases where name like '%"+nameKeyWord+"%'";
		return sql;
	}

	@Override
	//select DOMS_VERSION from appsvm83_qcsiteadmin_db.td.ADMIN
	String getServerVersionSQL() {		
		String sql = "select DOMS_VERSION from "+this.dbName+"."+QCSchema+".ADMIN";
		return sql;
	}

	@Override
	//select tdserver_name from appsvm83_qcsiteadmin_db.td.APPSERVERS
	String getAppServerNameSQL() {		
		String sql = "select tdserver_name from "+this.dbName+"."+QCSchema+".APPSERVERS";		
		return sql;
	}
	
}
