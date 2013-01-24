package com.hp.jmx;


import java.util.Hashtable;

import com.hp.jmx.cmd.Command;
import com.hp.jmx.cmd.CommandFactory;
import com.hp.jmx.cmd.CommandReader;

public class Test {	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		//Test test = new Test();
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
			String commandName=CommandReader.getCommandName(args);
			Hashtable<String, String> options = CommandReader.getOptions(args);
			Command command = CommandFactory.getCommand(commandName);
			if (command!=null) {
				command.execute(options);
				System.out.println("Successfully done.");
			} else {
				System.out.println("Error");
			}
			
		}		
	}
	
	
}
