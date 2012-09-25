package com.hp.jmx;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

abstract class BaseDBAccessor implements DBAccessor {
	
	private String jdbcURL;
	private String username;
	private String password;
	protected String dbName;

	public BaseDBAccessor(String jdbcURL, String username, String password, String dbName) {
		this.jdbcURL = jdbcURL;
		this.username = username;
		this.password = password;
		this.dbName = dbName;
	}	

 	private int[] executeBatch(Connection con, List<String> sqlList) {
		Statement stmt = null;
		if (con != null) {
			try {
				//con.setAutoCommit(false);
				stmt = con.createStatement();				
				for (String sql : sqlList) {
					stmt.addBatch(sql);
					System.out.println("EXEC SQL>"+sql);
				}					
				int[] results = stmt.executeBatch();
				//con.commit();
				return results;
			} catch (SQLException e) {
				try {
					con.rollback();
				} catch (SQLException e1) {
					System.out.println("Error when rollback the transaction.");
				}
				System.out.println("Error when execute batch SQLs.");
			} finally {
				try {
					if (stmt != null)
						stmt.close();
				} catch (SQLException e1) {
					System.out.println("Error when close the statement.");
				}
			}
		}
		return null;	
	}
	
	protected int[] executeBatch(List<String> sqlList) {
		Connection con = getConnection();
		if (con != null) {
			try {
				int[] results = executeBatch(con, sqlList);
				return results;
			} finally {
				try {
					if (con != null)
						con.close();
				} catch (SQLException e1) {
					System.out.println("Error when close the connection.");
				}
			}
		}
		return null;
	}

	protected int executeBatch(String sql) {
		List<String> sqlList = new ArrayList<String>();
		sqlList.add(sql);
		int[] results = this.executeBatch(sqlList);
		if (results != null)
			return results[0];
		else
			return Statement.EXECUTE_FAILED;
		
	}
	
	protected int executeSQL (String sql) {
		Connection con = getConnection();
		Statement stmt = null;
		if (con != null) {
			try {	
				stmt = con.createStatement();
				System.out.println("EXEC SQL>"+ sql);
				int count = stmt.executeUpdate(sql);				
				return count;
			} catch (SQLException e) {
				System.out.println (e.getMessage());				
			} finally {
				try {
					if (stmt != null)
						stmt.close();
				} catch (SQLException e1) {
					System.out.println("Error when close the statement.");
				}
			}
		}
		return Statement.EXECUTE_FAILED;
	}
	
	protected Connection getConnection() {
		Connection con = null;
		try {
			con = DriverManager.getConnection(jdbcURL, username, password);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			System.out.println(e.getStackTrace());
			System.out.println("Error when getting JDBC connection.");
		}
		return con;
	}

	private List<Object> query(Connection con, String sql, ResultSetHandler handler) throws SQLException {
		Statement stmt = null;
		ResultSet rs = null;
		if (con != null) {
			try {
				stmt = con.createStatement();
				rs = stmt.executeQuery(sql);
				List<Object> results = new ArrayList<Object>();
				while (rs.next()) {
					Object result = handler.handleResultSet(rs);
					results.add(result);
				}
				return results;
			} catch (SQLException e) {
				System.out.println("Error when query the database." + sql );
				throw e;
			} finally {
				try {
					if (rs != null)
						rs.close();
					if (stmt != null)
						stmt.close();
				} catch (SQLException e1) {
					System.out.println("Error when close the statement.");
				}
			}
		}
		return null;
	}	
		
	protected List<Object> query(String sql) throws SQLException {
		Connection con = getConnection();
		if (con != null) {
			try {
				List<Object> results =query(con, sql, new BaseResultSetHandler());
				return results;
			} finally {
				try {
					if (con != null)
						con.close();
				} catch (SQLException e1) {
					System.out.println("Error when close the connection.");
				}
			}
		}
		return null;
	}

	public List<Object> getProjectServers() throws SQLException {
		Connection con = getConnection();		
		if (con != null) {
			try {
				String sql = getProjectServersSQL();
				List<Object> results = query(con, sql, new BaseResultSetHandler());
				return results;
			} catch (SQLException e) {
				System.out.println("Error when getProjectServers.");
				throw e;
			} finally {
				try {
					if (con != null)
						con.close();
				} catch (SQLException e1) {
					System.out.println("Error when close the connection.");
				}
			}
		}
		return null;
	}
	
	//"Update td.ADMIN set DOMS_VERSION='12.01' where DOMS_VERSION='12.00'";
    //"if exists (select * from sysobjects where id = object_id('td.PROJECT_EXTENSIONS')) update td.PROJECT_EXTENSIONS set PE_EXTENSION_VERSION='1.00' where PE_EXTENSION_NAME = 'ALM_SAMPLE_EXTENSION'";
	public int[] upgradeSiteAdmin () {
		int[] results = new int[2];
		String sql = getUpgradeSiteAdminSQL();
		results[0] = this.executeSQL(sql);
		results[1] = downgradeSAExtensions();		
		return results;
	}
	
	//begin update "+this.dbName+".extensions set EX_VERSION='1.00' where exists (select * from all_tables where table_name='extensions' and owner=upper('"+this.dbName+"')) and EX_NAME='ALM_SAMPLE_EXTENSION';end;
	public int[] prepareProjectsForUpgrade() {
		int[] results = new int[1];
		String sql = getDowngradeExtensionSQL();
		results[0] = executeBatch(sql);
		return results;
	}
	
	//UPDATE td.PROJECTS set PR_VERSION='11.50' where PR_VERSION='11.51'
	//UPDATE td.ADMIN set DOMS_VERSION='11.50' where DOMS_VERSION='11.51'		
	public int[] downgradeSiteAdmin() {
		List<String> sqls = getDowngradeSiteAdminSQL();		
		return executeBatch(sqls);
	}
	
	//delete added tables
	//update td.DATACONST set DC_VALUE='11.50' where DC_CONST_NAME='version' and DC_VALUE='11.51'
	public int[] prepareProjectsForDowngrade() {
		int[] results = new int[3];
		results[0] = deleteTable("FPE");
		results[1] = deleteTable("SAMPLE_FPE");
		String sql = getDowngradeProjectVersionSQL();
		results[2] = executeBatch(sql);
		return results;
	}
	
	
	
	//get all database names which contain specific characters	
	public List<Object> getAllDBName(String nameKeyWord) throws SQLException {
		String sql = getAllDBNameSQL(nameKeyWord);
		return query(sql);		
	}
	
	//get ALM APP server Name
	public String getAppServerName() {		
		String sql = getAppServerNameSQL();
		try {
			List<Object> result = query(sql);
			if (result.size()!=1) {
				System.out.println("select count (*) result row number should be 1, but now it is "+result.size());
				return null;
			}
			String[] r = (String [])result.get(0);
			if (r.length!=1) {
				System.out.println("select count (*) result column number should be 1, but now it is "+r.length);
				return null;
			}	
			return r[0];
			
		} catch (SQLException e) {			
			return null;
		}		
	}
	
	//get ALM version
	public String getServerVersion() {
		String sql = getServerVersionSQL();
		return atomicResultQuery(sql);		
	}
	
	/**
	 * This sql only has one row and one column result.
	 * @param sql
	 * @return this one row & column result or null if no results or exception occur
	 */
	protected String atomicResultQuery(String sql) {
		try {
			List<Object> result=query(sql);
			if (result.size()!=1) {
				System.out.println (sql);
				System.out.println("This sql should has only one row returned, but now it is "+result.size());				
				return null;
			}
			String[] r = (String [])result.get(0);
			if (r.length!=1) {
				System.out.println (sql);
				System.out.println("This sql should has only one column returned, but now it is "+r.length);
				return null;
			}			
			return r[0];
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	/**
	 * Check if DB/user with given name exists, if exists check again with given name +"0", .....
	 * Example, given name ABC, search ABC, ABC0, ABC1, ... until the named DB/user not exists, return this name.
	 * Or it comes to ABC100, return null
	 */
	public String getNoDuplicateDBName(String namePrefix) {
		int count = 0;
		int maxCount = 100;
		String dbName=namePrefix;
		int isExist = isDBExist(dbName);
		while (count<maxCount && isExist==1) {			
			dbName= namePrefix+count;
			isExist = isDBExist(dbName);
			count++;
		}
		
		if (count == maxCount ) {
			System.out.println("Please change another preffred DB/User name or delete some existing DB/User. This prefix '"+namePrefix+"' already has too much DBs/Users.");
			return null;
		}
		
		//Exception/error occurs when query DB
		if (isExist == -1) return null;
		return dbName;
	}
	
	abstract String getProjectServersSQL();	
	//upgrade siteadmin
	abstract String getUpgradeSiteAdminSQL();
	abstract int downgradeSAExtensions();
	//project
	abstract String getDowngradeExtensionSQL();
	
	//downgrade siteadmin
	abstract List<String> getDowngradeSiteAdminSQL();
	//downgrade project
	abstract String getDowngradeProjectVersionSQL();
	abstract int deleteTable(String tableName);
	
	abstract String getAllDBNameSQL(String nameKeyWord);
	abstract String getServerVersionSQL();
	abstract String getAppServerNameSQL();
	
	
	abstract public int dropCurrentDB();
	abstract public int isCurrentDBExist();	
	abstract int isDBExist(String dbName);
	
}
