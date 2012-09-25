package com.hp.jmx;

import java.io.*;

abstract class BaseDBDump implements DBDump {
	protected String dumpDir;
	protected String dumpName;
	protected String userName;
	protected String password;
	protected String sid;
	protected OracleDBAccessor dbAccessor;

	protected String getInfoCmdFile;
	protected String importCmdFile;
	protected static final String GETINFO_OUTPUT = "get_info.log";
	protected static final String IMPORT_OUTPUT = "import.log";

	protected String tableSpace;
	protected String oldSchema;
	protected String newSchema;
	//oracle data file system loacation
	protected String oracleDataDir;	
	
	private static final String RETURN_CHARS = "\r\n";
	//unit is M, so it is 50M
	private static final double minTableSpaceSize = 100;

	public BaseDBDump(String dumpFile, String url, String oracleDataSpace,OracleDBAccessor dbAccessor)
			throws Exception {
		// init private vars
		int index = dumpFile.lastIndexOf(File.separator);
		this.dumpDir = dumpFile.substring(0, index);
		this.dumpName = dumpFile.substring(index + 1);
		index = url.lastIndexOf(":");
		this.sid = url.substring(index + 1);
		this.oracleDataDir = oracleDataSpace;
		String serverAddr = AccessorFactory.getServerAddress(url);
		try {
			this.userName = AccessorFactory.getDBLoginProperties(serverAddr)[0];
			this.password = AccessorFactory.getDBLoginProperties(serverAddr)[1];
		} catch (Exception e) {
			System.out
					.println("Fail to find server login name and password, please check your url: "
							+ url + " and check your DB properties file");
			throw e;
		}
		this.getInfoCmdFile = dumpDir + File.separator + "get_info_"
				+ getNameNoExt(dumpName) + ".cmd";
		this.importCmdFile = dumpDir + File.separator + "import_"
				+ getNameNoExt(dumpName) + ".cmd";
		this.dbAccessor=dbAccessor;
	}

	// return xxx if input xxx.xx, xxx.xx if input xxx.xx.xx
	private String getNameNoExt(String name) {
		int index = name.lastIndexOf(".");
		return name.substring(0, index);
	}

	/**
	 * Issue getInfo command return true if succeed return false if failed
	 */
	public boolean getInfo() {
		// Check dump file exist
		String path = dumpDir + File.separator + dumpName;
		File dumpFile = new File(path);
		if (dumpFile.exists() == false) {
			System.out.println("Cannot find dump file:" + path);
			return false;
		}
		
		// write get_info cmd file
		// delete cmd file/log file if exists
		File cmdFile = new File(getInfoCmdFile);
		if (cmdFile.exists())
			cmdFile.delete();
		String getInfoLogName = dumpDir + File.separator
		+ GETINFO_OUTPUT;
		File logFile = new File(getInfoLogName);
		if (logFile.exists())
			logFile.delete();
		try {
			String cmd = getInfoCmd();
			FileWriter fw = new FileWriter(getInfoCmdFile);			
			fw.write(getInfoCmd() + RETURN_CHARS);
			fw.close();			

			// check if get info command succeed
			if (runOracleCmd(cmd, new File(dumpDir))) {
				if (!logFile.exists()) {
					System.out
							.println("Do not find log after exec get_info command: "
									+ getInfoLogName);
					return false;
				}

				if (isGetInfoSucceed(getInfoLogName))
					return true;
				return false;

			} else {
				return false;
			}
		} catch (IOException e) {
			System.out.println("Fail to create get_info file, get exception:");
			e.printStackTrace();
			return false;
		}
	}

	public boolean importDump() {
		
		//get required data(tablespace, schema name) from get_info output
		tableSpace = this.getTableSpaceFromLog();
		System.out.println("Table Space is: "+tableSpace);
		oldSchema = this.getSchemaFromLog();
		System.out.println("Schema name is: "+oldSchema);
		String dumpFile = dumpDir+File.separator+dumpName;
		double tableSpaceSize = getTableSpaceSize(dumpFile);		
		
		//prepare tablespace (make sure its existence, enough free space)
		if (!dbAccessor.createTableSpace(tableSpace, tableSpaceSize, oracleDataDir)) {
			return false;
		}		
		
		//prepare the target schema name
		newSchema=dbAccessor.getNoDuplicateDBName(oldSchema);
		if (newSchema == null) return false; //fail to get a proper schema name
		System.out.println("New schema name is: "+newSchema);
				
		if (!beforeImport()) return false; //normal dump needs to create user before import
	
		//import
		File cmdFile = new File(importCmdFile);
		if (cmdFile.exists())
			cmdFile.delete();
		String output = dumpDir + File.separator
		+ IMPORT_OUTPUT;
		File outputFile = new File(output);
		if (outputFile.exists())
			outputFile.delete();
		try {
			String cmd = getImportCmd();
			FileWriter fw = new FileWriter(importCmdFile);			
			fw.write(cmd + RETURN_CHARS);
			fw.close();
			System.out.println(importCmdFile + " is created.");

			// check if get info command succeed
			if (runOracleCmd(cmd,new File(dumpDir))) {
				if (!outputFile.exists()) {
					System.out
							.println("Do not find log after exec import command: "
									+ output);
					return false;
				}
				
				//Check import result.	
				if (!isImportSucceed(output)) return false;
				System.out.println("Data dump is imported.");
				if (!afterImport()) return false;
				return true;
			} else {
				return false;
			}
		} catch (IOException e) {
			System.out.println("Fail to create import command file, get exception:");
			e.printStackTrace();
			return false;
		}
	}

	// exec cmd, return true if cmd is executed. return false if any exception occurs
	private boolean runOracleCmd(String oracleCmd, File envDir) {

		String cmd = "cmd /c "+oracleCmd;
		
		Process proc = null;
		try {
			 proc = Runtime.getRuntime().exec(cmd, null, envDir);
			 //any error
			 StreamGobbler errorGobbler = new StreamGobbler(proc.getErrorStream(), "ORACLE OUTPUT");
             // any output?
	         StreamGobbler outputGobbler = new StreamGobbler(proc.getInputStream(), "OUTPUT");
             // kick them off
	         errorGobbler.start();
	         outputGobbler.start();
             
	         if (proc.waitFor() != 0) {
	        	 System.out.println("Exec "+cmd+" get return code: "+proc.exitValue());
	        	 return true;
	         } else {
	        	 return true;
	         }			

		} catch (Exception e) {
			System.out.println("Fail to exec " + cmd
					+ ", get exception:");
			e.printStackTrace();			
			return false;
		}
	}
	
	//unit is M
	//at least 100M, or dumpFileSize*110%
	private double getTableSpaceSize(String dumpFile) {
		File f = new File(dumpFile);
		double dumpSize = (double)f.length()/1024/1024;
		double optionSize = dumpSize*1.5;
		return minTableSpaceSize>optionSize?minTableSpaceSize:optionSize;
	}
	
	
	static class StreamGobbler extends Thread {
        InputStream is;
        String type;

        StreamGobbler(InputStream is, String type) {
            this.is = is;
            this.type = type;
        }

        public void run() {
            try {
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String line = null;
                while ((line = br.readLine()) != null)
                    System.out.println(type + ">" + line);
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }
	

	// Get get_info command string
	abstract String getInfoCmd();
	// Get import command string
	abstract String getImportCmd();
	// if get info command successfully done
	abstract boolean isGetInfoSucceed(String log);
	//get Table Space name from log
	abstract String getTableSpaceFromLog();
	//get schema name from log
	abstract String getSchemaFromLog();
	//import related
	abstract boolean beforeImport();	
	abstract boolean isImportSucceed(String log);
	abstract boolean afterImport();
	
}
