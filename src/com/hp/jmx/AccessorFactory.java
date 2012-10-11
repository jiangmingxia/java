package com.hp.jmx;

import java.io.FileInputStream;
import java.util.Properties;

//jdbc:mercury:sqlserver://16.186.72.93:1433
//jdbc:mercury:oracle://16.186.72.93:1521;sid=abcd

//jdbc:oracle:thin:@16.156.254.95:1521:ALMQA1
//jdbc:sqlserver://16.186.72.93:1433;databaseName=appsvm83_141_qcsiteadmin_db

public class AccessorFactory {

	public static final String ORACLE_DRIVER_NAME = "oracle.jdbc.driver.OracleDriver";
	public static final String SQL_SERVER_DRIVER_NAME = "com.microsoft.sqlserver.jdbc.SQLServerDriver";

	private static final String SQL_SERVER_TOKEN = "sqlserver";
	private static final String ORACLE_TOKEN = "oracle";

	private static final String QC_SITEADMIN_DB = "admin_db";

	public static void init() throws ClassNotFoundException {
		Class.forName(ORACLE_DRIVER_NAME);
		Class.forName(SQL_SERVER_DRIVER_NAME);
	}

	private static Properties dbConfigProperties;

	private static final String DB_CONFIG_PROP_FILE = "db_config.properties";

	private static synchronized Properties getDBConfigProperties()
			throws Exception {
		if (dbConfigProperties == null) {
			dbConfigProperties = new Properties();
			try {
				dbConfigProperties
						.load(new FileInputStream(DB_CONFIG_PROP_FILE));
			} catch (Exception e) {
				System.out.println("The db config properties load failed.");
				throw e;
			}
		}
		return dbConfigProperties;
	}
	
	//get DB login name/password by server Addr
	public static String[] getDBLoginProperties (String serverAddr) throws Exception {
		String valueString = getDBConfigProperties().getProperty(serverAddr);
		// when sqlserver address with instance name, and cannot find
		// corresponding username/pwd
		// try to find user/pwd again after removing instance name
		if (valueString == null && serverAddr.indexOf("\\") > 0) {
			// 16.186.72.93\instance:1433 => 16.186.72.93:1433
			int indexStart = serverAddr.indexOf("\\");
			int indexEnd = serverAddr.indexOf(":");
			String s1 = serverAddr.substring(0, indexStart);
			String s2 = serverAddr.substring(indexEnd);
			serverAddr = s1 + s2;
			valueString = getDBConfigProperties().getProperty(serverAddr);
		}
		if (valueString == null) return null;
		return valueString.split(":");
	}
	
	//Get DB server address from URL, include host,instance name (for sqlserver), port
	// Oracle url: jdbc:mercury:oracle://apps002:1521;sid=ALMQA1
	// Oracle url: jdbc:oracle:thin:@16.156.254.95:1521:ALMQA1
	// SQL url:
	// jdbc:sqlserver://16.186.72.93:1433;databaseName=appsvm83_qcsiteadmin_db;
	// SQL url: jdbc:sqlserver://16.186.72.93:1433
	public static String getServerAddress(String url) {
		int startIndex = url.indexOf("//") + 2;
		int lastIndex;
		String serverAddr;

		if (startIndex < 2) {
			// Oracle jdbc url: jdbc:oracle:thin:@16.156.254.95:1521:ALMQA1
			startIndex = url.indexOf("@") + 1;
			lastIndex = url.lastIndexOf(":");
			serverAddr = url.substring(startIndex, lastIndex);
		} else {
			serverAddr = url.substring(startIndex);
			// after "//"
			// serverAddr = 16.186.72.93:1433
			// serverAddr = apps002:1521;sid=ALMQA1
			// serverAddr =
			// 16.186.72.93:1433;databaseName=appsvm83_qcsiteadmin_db;
			// serverAddr = 16.186.72.93\instance:1433

			// remove the string after ";"
			lastIndex = serverAddr.indexOf(";");
			if (lastIndex > 0) {
				// serverAddr = apps002:1521;sid=ALMQA1
				// serverAddr =
				// 16.186.72.93:1433;databaseName=appsvm83_qcsiteadmin_db;
				serverAddr = serverAddr.substring(0, lastIndex);
			}			
		}
		
		//change server name to lower case
		if (serverAddr.indexOf("\\")>0) {
			//sqlserver with instance name in address
			String serverName = serverAddr.substring(0, serverAddr.indexOf("\\"));
			serverName=serverName.toLowerCase();
			serverAddr = serverName+serverAddr.substring(serverAddr.indexOf("\\"));
		} else {
			serverAddr = serverAddr.toLowerCase();
		}
		
		return serverAddr;		
	}

	public static DBAccessor getDBAccessor(String url, String dbName,
			boolean isJdbcURL) throws Exception {
		
		String serverAddr = getServerAddress(url);
		
		String[] values = getDBLoginProperties(serverAddr);
		
		if (values != null) {
			String username = values[0];
			String password = values[1];

			if (url.contains(ORACLE_TOKEN)) {
				if (isJdbcURL)
					return new OracleDBAccessor(url, username, password, dbName);
				else {
					String jdbcURL = url.replace("mercury:oracle://",
							"oracle:thin:@");
					jdbcURL = jdbcURL.replace(";sid=", ":");
					return new OracleDBAccessor(jdbcURL, username, password,
							dbName);
				}
			} else if (url.contains(SQL_SERVER_TOKEN)) {

				// remove ;databaseName=.....
				if (url.indexOf(";databaseName=") > 0) {
					int index = url.indexOf(";databaseName=");
					url = url.substring(0, index);
				}

				// change string to JDBC connection string
				if (!isJdbcURL) {
					url = url.replace("mercury:sqlserver", "sqlserver");
				}

				if (dbName.toLowerCase().contains(QC_SITEADMIN_DB)) {
					return new SQLServerAdminDBAccessor(url, username,
							password, dbName);
				} else {
					return new SQLServerDBAccessor(url, username, password,
							dbName);
				}
			}
		}
		if (dbName==null){
			System.out.println("Cannot find username and password for server: "+serverAddr);
		} else {
			System.out.println("Cannot find username and password for server: "+serverAddr+" with database "+dbName);
		}		
		System.out.println("Please ensure it is in file "+DB_CONFIG_PROP_FILE);
		return null;
	}
}
