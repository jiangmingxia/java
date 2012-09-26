package com.hp.jmx;

import java.io.File;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.hp.jmx.util.FileAccessor;

public class OracleDBAccessor extends BaseDBAccessor implements DBAccessor {
	public OracleDBAccessor(String jdbcURL, String username, String password, String dbName) {
		super(jdbcURL, username, password, dbName);
	}

	@Override
	String getProjectServersSQL() {
		String sql = "Select DB_NAME, DB_CONNSTR_FORMAT from "+this.dbName+".projects";
		return sql;
	}
	
	@Override
	String getUpgradeSiteAdminSQL() {			
		String sql = "Update "+this.dbName+".ADMIN set DOMS_VERSION='11.51' where DOMS_VERSION='11.50'";
		return sql;
	}
	
	@Override
	int downgradeSAExtensions() {
		//"Begin Update " +this.dbName + ".PROJECT_EXTENSIONS set PE_EXTENSION_VERSION='1.00' where exists (select * from all_tables where table_name='PROJECT_EXTENSIONS' and owner=upper('"+this.dbName+"')) and PE_EXTENSION_NAME = 'ALM_SAMPLE_EXTENSION';end;";
		String tableName = "PROJECT_EXTENSIONS";
		int tableExist = isTableExist(tableName);
		if (tableExist == 1) {
			return this.executeSQL("Update " +this.dbName+"."+tableName+" set PE_EXTENSION_VERSION='1.00' where PE_EXTENSION_NAME = 'ALM_SAMPLE_EXTENSION'");
		}
		if (tableExist == 0) {
			System.out.println(tableName+" does not exist. Not update it.");
			return 0;
		}
		return -1;		
	}
	
	@Override
	String getDowngradeExtensionSQL() {
		String sql = "begin update "+this.dbName+".extensions set EX_VERSION='1.00' where exists (select * from all_tables where table_name='extensions' and owner=upper('"+this.dbName+"')) and EX_NAME='ALM_SAMPLE_EXTENSION';end;";
		return sql;
	}
	
	/**
	 * Check if given table exists in current database/user_schema
	 * @param tableName
	 * @return 1 when table exists
	 *         0 when table not exist
	 *         -1 when get error or exception
	 */
	private int isTableExist(String tableName) {
		String sql= "select count(*) from all_tables where table_name=upper('"+tableName+"') and owner=upper('"+this.dbName+"')";
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
	 * Check if given user exists 
	 * @param user(database name)
	 * @return 1 when user exists
	 *         0 when user not exist
	 *         -1 when get error or exception
	 */
	@Override
	int isDBExist(String dbName) {
		String sql= "select count(*) from dba_users where username=upper('"+dbName+"')";
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
	 * Check if given tablespace exists 
	 * @param tablespace name
	 * @return 1 when tablespace exists
	 *         0 when tablespace not exist
	 *         -1 when get error or exception
	 */
	private int isTableSpaceExist(String tbSpaceName) {
		String sql= "select count(*) from dba_data_files where tablespace_name=upper('"+tbSpaceName+"')";
		String result = atomicResultQuery(sql);
		if (result == null) return -1;
		int isExist = Integer.parseInt(result);
		if (isExist > 0) {
			return 1;
		} else {				
			return 0;
		}
	}
	
	@Override
	int deleteTable(String tableName) {		
		int tableExist = isTableExist(tableName);
		if (tableExist == 1) {
			return this.executeSQL("drop table "+this.dbName+"."+tableName);
		}
		if (tableExist == 0) {
			System.out.println(tableName+" does not exist. Do not delete it.");
			return 0;
		}
		return -1;
	}

	

	@Override
	List<String> getDowngradeSiteAdminSQL() {
		List<String> sqls = new ArrayList<String>();
		sqls.add("UPDATE "+this.dbName+".PROJECTS set PR_VERSION='11.50' where PR_VERSION='11.51'");
		sqls.add("UPDATE "+this.dbName+".ADMIN set DOMS_VERSION='11.50' where DOMS_VERSION='11.51'");		
		return sqls;
	}

	@Override
	String getDowngradeProjectVersionSQL() {		 
        return "begin update "+this.dbName+".DATACONST set DC_VALUE='11.50' where DC_CONST_NAME='version' and DC_VALUE='11.51';end;";     
	}

	@Override
	public int dropCurrentDB() {
		int userExist = isDBExist(this.dbName);
		if (userExist == 1) {
			String sql = "drop user "+this.dbName+" cascade";
			return this.executeSQL(sql);
		}
		if (userExist == 0) {
			System.out.println(this.dbName+" does not exist. Do not delete it.");
			return 0;
		}
		return -1;
	}
	
	@Override
	public int isCurrentDBExist() {		
		int dbExist = isDBExist(this.dbName);		
		return dbExist;
	}

	@Override
	String getAllDBNameSQL(String nameKeyWord) {		
		String sql = "SELECT * FROM dba_users WHERE username LIKE '%"+nameKeyWord.toUpperCase()+"%'";
		return sql;
	}

	@Override
	String getServerVersionSQL() {
		String sql = "select DOMS_VERSION from "+this.dbName+".ADMIN";
		return sql;
	}

	@Override
	String getAppServerNameSQL() {
		String sql = "select tdserver_name from "+this.dbName+".APPSERVERS";
		return sql;
	}
	
	/**
	 * 
	 * @param spaceName: the space name to be created if not exists
	 * @param spaceSize: the space free space size, if not exists create a space with this size, if exists make sure free space >= spaceSize, if not add space
	 * @param spaceDataDir: the tablespace datafile location
	 * @return true if successfully done
	 * false if any error/exception occurs
	 *  
	 */
	public boolean createTableSpace(String spaceName,double spaceSize,String spaceDataDir) {
		spaceName=spaceName.toUpperCase();
		
		//check if give data file location valid, if not ,set it to null.		
		if (spaceDataDir!=null) {
			File dir = new File(spaceDataDir);
			if (!dir.isDirectory()) spaceDataDir=null;
		}
		
		int tbSpaceExist = isTableSpaceExist(spaceName);
		if (tbSpaceExist == 1) {
			//check current space free space
			String freeSpaceSql = "SELECT trunc(sum(bytes) / (1024 * 1024)) as free_m FROM dba_free_space where tablespace_name=\'"+spaceName+"\'";       
			String result = atomicResultQuery(freeSpaceSql);
			if (result == null) {
				System.out.println("Fail to check free space of this table space: "+spaceName);
				return false;
			}
			if (Double.parseDouble(result) >= spaceSize) {
				System.out.println("Table Space Exists and has enough free space left.");
				return true;
			}
			
			System.out.println("Table Space exists and not have enough free space.");
			
			//Add new data file
			//select dir:
			if (spaceDataDir==null) {
				String sql = "SELECT FILE_NAME FROM dba_data_files where rownum = 1 and tablespace_name=\'"+spaceName+"\'";    
				result = atomicResultQuery(sql);
				if (result == null) {
					System.out.println("Fail to get oracle table space data file location: "+spaceName);
					return false;
				}
				
				File dataFileLocation = (new File(result)).getParentFile();
				if (!dataFileLocation.isDirectory()) {
					System.out.println("The data file location does not exist: "+result);
					return false;
				}
				
				spaceDataDir = (new File(result)).getParent();
			}
			String dataFilePrefix="MYORACLEDBF";
			//full path
			String dataFileName = FileAccessor.getNoDuplicateFileName(dataFilePrefix,spaceDataDir);
			if (dataFileName == null) {
				return false;
			}
			
			//Add datafile to tablespace
			int size = (int)spaceSize;
			String sql = "alter tablespace \""+spaceName+"\" add DATAFILE '"+dataFileName+"' SIZE "+size+"M REUSE AUTOEXTEND ON NEXT 10M MAXSIZE UNLIMITED";
			if (this.executeSQL(sql) == Statement.EXECUTE_FAILED) {
				System.out.println("Fail to add file to table space.");
				return false;
			}
			System.out.println("Data file is added to the table space.");
			return true;
		} else {
			//select datafile dir
			if (spaceDataDir==null) {
				String sql = "SELECT FILE_NAME FROM dba_data_files where rownum = 1 and tablespace_name = 'SYSTEM'";    
				String result = atomicResultQuery(sql);
				if (result == null) {
					System.out.println("Fail to get SYSTEM tablespace data file location.");
					return false;
				}
				
				File dataFileLocation = (new File(result)).getParentFile();
				if (!dataFileLocation.isDirectory()) {
					System.out.println("The data file location does not exist: "+result);
					return false;
				}
				
				spaceDataDir = (new File(result)).getParent();
			}
			String dataFilePrefix="MYORACLEDBF";
			//full path
			String dataFileName = FileAccessor.getNoDuplicateFileName(dataFilePrefix,spaceDataDir);
			if (dataFileName == null) {
				return false;
			}
			
			//CREATE TABLE SPACE
			int size = (int)spaceSize;
			String sql = "create tablespace \""+spaceName+"\" DATAFILE '"+dataFileName+"' SIZE "+size+"M REUSE AUTOEXTEND ON NEXT 10M MAXSIZE UNLIMITED";
			if (this.executeSQL(sql) == Statement.EXECUTE_FAILED) {
				System.out.println("Fail to create tablespace "+spaceName);
				return false;
			}
			System.out.println("Tablespace "+spaceName+" is created.");
			return true;
		}
	}
	
	/**
	 * CREATE USER XXXX profile "DEFAULT" IDENTIFIED BY "tdtdtd" DEFAULT TABLESPACE TTTT  TEMPORARY TABLESPACE "TEMP" ;
	 * GRANT UNLIMITED TABLESPACE TO XXXX;
	   GRANT CONNECT,CTXAPP TO XXXX;
       GRANT CREATE PROCEDURE to XXXX;
       GRANT CREATE SEQUENCE to XXXX;
       GRANT CREATE SESSION to XXXX;
       GRANT CREATE TABLE to XXXX;
       GRANT CREATE TRIGGER to XXXX;
       GRANT CREATE VIEW to XXXX;
	 * @param schemaName
	 * @param defaultTableSpace
	 * @return
	 */
	public boolean createQCUser(String schemaName,String defaultTableSpace) {
		List<String> sqls = new ArrayList<String>();
		String sql = "CREATE USER \""+schemaName+"\" profile \"DEFAULT\" IDENTIFIED BY \"tdtdtd\" DEFAULT TABLESPACE \""+defaultTableSpace+"\" TEMPORARY TABLESPACE \"TEMP\"";
		sqls.add(sql);
		sqls.add("GRANT UNLIMITED TABLESPACE TO \""+schemaName+"\"");
		sqls.add("GRANT CONNECT,CTXAPP TO \""+schemaName+"\"");
		sqls.add("GRANT CREATE PROCEDURE TO \""+schemaName+"\"");
		sqls.add("GRANT CREATE SEQUENCE TO \""+schemaName+"\"");
		sqls.add("GRANT CREATE SESSION TO \""+schemaName+"\"");
		sqls.add("GRANT CREATE TABLE TO \""+schemaName+"\"");
		sqls.add("GRANT CREATE TRIGGER TO \""+schemaName+"\"");
		sqls.add("GRANT CREATE VIEW TO \""+schemaName+"\"");
		
		int[] results = executeBatch(sqls);
		if (results == null) {
			System.out.println("Error when creating QC user "+schemaName);
			return false;
		}
		
		//Check user after creation
		if (isDBExist(schemaName)==1) {
			return true;
		} else {
			System.out.println("User "+schemaName+" is not created. Please check SQL results.");
			return false;
		}		
	}
	
	/**
	 * create Oracle dir
	 * @param dir: dump file location
	 * @return true if succeed
	 */
	public boolean createDir(String dir) {
		String sql = "create or replace directory qc_dir as '"+dir+"'";
		if (executeSQL(sql) == Statement.EXECUTE_FAILED) return false;
		return true;
	}
	
	/**
	 * update existing user with QC password: tdtdtd and corresponding privilege
	 * @param schemaName
	 * @return
	 */
	public boolean updateQCUser(String schemaName) {
		List<String> sqls = new ArrayList<String>();
		String sql = "ALTER USER \""+schemaName+"\" IDENTIFIED BY \"tdtdtd\" ";
		sqls.add(sql);
		sqls.add("GRANT UNLIMITED TABLESPACE TO \""+schemaName+"\"");
		sqls.add("GRANT CONNECT,CTXAPP TO \""+schemaName+"\"");
		sqls.add("GRANT CREATE PROCEDURE TO \""+schemaName+"\"");
		sqls.add("GRANT CREATE SEQUENCE TO \""+schemaName+"\"");
		sqls.add("GRANT CREATE SESSION TO \""+schemaName+"\"");
		sqls.add("GRANT CREATE TABLE TO \""+schemaName+"\"");
		sqls.add("GRANT CREATE TRIGGER TO \""+schemaName+"\"");
		sqls.add("GRANT CREATE VIEW TO \""+schemaName+"\"");
		
		int[] results = executeBatch(sqls);
		if (results == null) {
			System.out.println("Error when altering QC user "+schemaName);
			return false;
		}
		
		return true;		
	}

}
