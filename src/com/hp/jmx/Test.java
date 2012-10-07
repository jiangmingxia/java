package com.hp.jmx;


import java.io.FileInputStream;
import java.io.FileReader;
import java.io.RandomAccessFile;

import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import java.io.Console;

public class Test {

	private static final String SITE_ADMIN_PROP_FILE = "site_admin.properties";

	private static final String SITE_ADMIN_JDBC_URL_KEY = "site.admin.jdbc.url";
	private static final String SITE_ADMIN_JDBC_DB_NAME_KEY = "site.admin.jdbc.db.name";
	
	private static final String CONFIG_FILE="config";
	private static final String ORACLE_JDBC_URL_KEY="oracle.jdbc.url";
	private static final String DUMP_FILE="dumpFile";
	private static final String ORACLE_DATA_SPACE="oracleDataSpace";

	private static Properties siteAdminProperties;
	private static Configure configs;

	public void upgrade() throws Exception {
		try {
			AccessorFactory.init();
		} catch (ClassNotFoundException e) {
			System.out.println("The AccessorFactory init failed.");
			throw e;
		}
		/**************************************
		 * Site Admin DB
		 **************************************/
		String siteAdminJDBCUrl = getSiteAdminProperties().getProperty(
				SITE_ADMIN_JDBC_URL_KEY);
		String siteAdminJDBCDBName = getSiteAdminProperties().getProperty(
				SITE_ADMIN_JDBC_DB_NAME_KEY);
		DBAccessor adminAccessor = AccessorFactory.getDBAccessor(
				siteAdminJDBCUrl, siteAdminJDBCDBName, true);

		// 1. Upgrade Site admin version to 12.01
		// 2. Downgrade Site admin ALM_SAMPLE_EXTENSION version to ¡°1.00¡±
		int[] results = adminAccessor.upgradeSiteAdmin();
		for (int i : results) {
			System.out.println("ROWS AFFECTED: " + i);
		}

		/*******************************************
		 * Update project schemas
		 *******************************************/
		List<Object> servers = adminAccessor.getProjectServers();
		for (Object server : servers) {
			String[] serverInfo = (String[]) server;
			String dbName = serverInfo[0];
			String dbURL = serverInfo[1];

			DBAccessor projectAccessor = AccessorFactory.getDBAccessor(dbURL,
					dbName, false);
			int[] pResults = projectAccessor.prepareProjectsForUpgrade();
			for (int i : pResults) {
				System.out.println("ROWS AFFECTED: " + i);
			}
		}
		System.out.println("Upgrade done!!");
	}

	public void downgrade() throws Exception {
		try {
			AccessorFactory.init();
		} catch (ClassNotFoundException e) {
			System.out.println("The AccessorFactory init failed.");
			throw e;
		}

		/**************************************
		 * Site Admin DB
		 **************************************/
		String siteAdminJDBCUrl = getSiteAdminProperties().getProperty(
				SITE_ADMIN_JDBC_URL_KEY);
		String siteAdminJDBCDBName = getSiteAdminProperties().getProperty(
				SITE_ADMIN_JDBC_DB_NAME_KEY);
		DBAccessor adminAccessor = AccessorFactory.getDBAccessor(
				siteAdminJDBCUrl, siteAdminJDBCDBName, true);

		// 1. Downgrade Siteadmin version to 12.00
		// 2. Downgrade table "projects" version to 12.00
		int[] results = adminAccessor.downgradeSiteAdmin();
		for (int i : results) {
			System.out.println("ROWS AFFECTED: " + i);
		}

		/*******************************************
		 * Downgrade project schemas
		 *******************************************/
		List<Object> servers = adminAccessor.getProjectServers();
		for (Object server : servers) {
			String[] serverInfo = (String[]) server;
			String dbName = serverInfo[0];
			String dbURL = serverInfo[1];

			DBAccessor projectAccessor = AccessorFactory.getDBAccessor(dbURL,
					dbName, false);
			int[] pResults = projectAccessor.prepareProjectsForDowngrade();
			for (int i : pResults) {
				System.out.println("ROWS AFFECTED: " + i);
			}
		}

		System.out.println("Downgrade done!!");

	}

	public void dropDBs() throws Exception {
		try {
			AccessorFactory.init();
		} catch (ClassNotFoundException e) {
			System.out.println("The AccessorFactory init failed.");
			throw e;
		}

		String siteAdminJDBCUrl = getSiteAdminProperties().getProperty(
				SITE_ADMIN_JDBC_URL_KEY);
		String siteAdminJDBCDBName = getSiteAdminProperties().getProperty(
				SITE_ADMIN_JDBC_DB_NAME_KEY);
		DBAccessor adminAccessor = AccessorFactory.getDBAccessor(
				siteAdminJDBCUrl, siteAdminJDBCDBName, true);

		int adminDBExist = adminAccessor.isCurrentDBExist();
		if (adminDBExist == 0) {
			System.out.println("Site Admin DB '" + siteAdminJDBCDBName
					+ "' does not exists, exit.");
			return;
		}

		if (adminDBExist == -1) {
			System.out.println("Got exception when checking site Admin DB '"
					+ siteAdminJDBCDBName + "', exit.");
			return;
		}

		/*******************************************
		 * Drop project DBs
		 *******************************************/
		List<Object> servers = adminAccessor.getProjectServers();
		for (Object server : servers) {
			String[] serverInfo = (String[]) server;
			String dbName = serverInfo[0];
			String dbURL = serverInfo[1];

			DBAccessor projectAccessor = AccessorFactory.getDBAccessor(dbURL,
					dbName, false);
			projectAccessor.dropCurrentDB();
		}

		System.out.println();
		System.out.println();
		Console console = System.console();
		if (console != null) {
			String deleteSA = new String(console.readLine(
					"Do you want to delete siteadmin DB '"
							+ siteAdminJDBCDBName + "' (yes or no): ",
					new Object[0]));
			if (deleteSA.equalsIgnoreCase("y")
					|| deleteSA.equalsIgnoreCase("yes")) {
				adminAccessor.dropCurrentDB();
			} else {
				console.printf("Site admin DB is not deleted.");
			}
		} else {
			System.out.println("No Console!");
		}
	}

	public void checkAdminDB() throws Exception {
		try {
			AccessorFactory.init();
		} catch (ClassNotFoundException e) {
			System.out.println("The AccessorFactory init failed.");
			throw e;
		}

		String JDBCUrl = getSiteAdminProperties().getProperty(
				SITE_ADMIN_JDBC_URL_KEY);
		// only for sqlserver
		String dbName = "master";
		DBAccessor dbAccessor = AccessorFactory.getDBAccessor(JDBCUrl, dbName,
				true);

		List<Object> adminDBNames = dbAccessor.getAllDBName("admin_db");

		for (Object name : adminDBNames) {
			String adminDBName = ((String[]) name)[0];
			DBAccessor adminDBAccessor = AccessorFactory.getDBAccessor(JDBCUrl,
					adminDBName, true);
			String appServerName = adminDBAccessor.getAppServerName();
			String appVersion = adminDBAccessor.getServerVersion();
			System.out.print(adminDBName + "   ");
			if (appServerName != null) {
				System.out.print(appServerName + "   ");
			}

			if (appVersion != null) {
				System.out.print(appVersion + "   ");
			}
			System.out.println();
		}
	}

	public void restoreDB() throws Exception {
//		Console console = System.console();
//		if (console != null) {
//			String deleteSA = new String(console.readLine(
//					"Input any key when ready:",
//					new Object[0]));
//			System.out.println("You are pressing:"+deleteSA);
//		} else {
//			System.out.println("No Console!");
//		}
		
		try {
			AccessorFactory.init();
		} catch (ClassNotFoundException e) {
			System.out.println("The AccessorFactory init failed.");
			throw e;
		}
		String url = getConfigs().get(ORACLE_JDBC_URL_KEY);
		String dumpFile = getConfigs().get(DUMP_FILE);	
		String oracleSpace = getConfigs().get(ORACLE_DATA_SPACE);
		DBAccessor dbAccessor = AccessorFactory.getDBAccessor(url, null,
				true);	
		
		DBDump dump = DBDumpFactory.getDBDump(dumpFile,url,oracleSpace,(OracleDBAccessor)dbAccessor);
		if (dump==null) {
			return;
		}
			
		dump.importDump();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		Test test = new Test();
		// test.upgrade();
		// String dbURL = "jdbc:oracle:thin:@16.156.254.95:1521:ALMQA1";
		// String dbName = "DEFAULT_006_CP_P3_2_DB";
		// String
		// dbURL="jdbc:sqlserver://appsvm19:1433;databaseName=default_vm24_p3_db";
		// String dbName = "default_vm24_p3_db";
		// String URL =
		// getSiteAdminProperties().getProperty(SITE_ADMIN_JDBC_URL_KEY);
		// DBAccessor projectAccessor = AccessorFactory.getDBAccessor(URL,
		// "master", false);

		if (args.length > 0 ) {
			if (args[0].equalsIgnoreCase("dropDB")) {
				test.dropDBs();
				return;
			}

			if (args[0].equalsIgnoreCase("checkAdmin")) {
				test.checkAdminDB();
				return;
			}
			
			if (args[0].equalsIgnoreCase("importDump")) {
				test.restoreDB();
				return;
			}
			
			String 
			
		}		
	}
	


	public static synchronized Properties getSiteAdminProperties()
			throws Exception {
		if (siteAdminProperties == null) {
			siteAdminProperties = new Properties();
			try {
				siteAdminProperties.load(new FileInputStream(
						SITE_ADMIN_PROP_FILE));
			} catch (Exception e) {
				System.out.println("The site admin properties load failed.");
				throw e;
			}
		}
		return siteAdminProperties;
	}

	//load config file 
	public static Configure getConfigs() throws Exception {
		if (configs == null) {
			configs = new Configure();
			try {
				configs.load(new FileReader(CONFIG_FILE));
			} catch (Exception e) {
				System.out.println("The config file load failed.");
				throw e;
			}
		}
		return configs;
	}

}
