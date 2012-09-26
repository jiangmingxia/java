package com.hp.jmx;

import java.io.File;

import com.hp.jmx.util.FileAccessor;

public class DPump extends BaseDBDump implements DBDump {
	public DPump(String dumpFile, String url, String oracleDataSpace,OracleDBAccessor dbAccessor) throws Exception  {
		super(dumpFile, url, oracleDataSpace,dbAccessor);
		createOracleDir ();
	}
	
	private static final String GET_INFO_PASS_PATTERN="^Job .* successfully completed at .*";
	private static final String IMPORT_PASS_PATTERN="^Job .* successfully completed at .*";
	private static final String ORACLE_DIRECTORY_NAME = "qc_dir";
	private static final String GET_INFO_SQL_OUTPUT = "get_info.sql";
	private static final String TABLESPACE_PATTERN = ".*DEFAULT TABLESPACE \"(.+)\".*";
	private static final String SCHEMA_PATTERN = ".*CREATE USER \"(.+)\" IDENTIFIED.*";
	
	private void createOracleDir () {
		if (!dbAccessor.createDir(this.dumpDir)) {
			System.out.println("Fail to create oracle dir:"+this.dumpDir);
		} else {
			System.out.println("Oracle dir is created.");
		}		
	}
	
	@Override
	String getImportCmd() {
		// impdp "'/ as sysdba'" directory=qc_dir
		// dumpfile=QAP_SPRINT_LAB_PROJECT.dmp logfile=sprint.log schemas=
		// remap_schema=:sprint_lab
		String userid = "userid=" + userName + "/" + password + "@" + sid;
		String directory = "directory=" + ORACLE_DIRECTORY_NAME;
		String dumpFile = "dumpfile=" + dumpName;
		String options = "logfile=" + IMPORT_OUTPUT;
		String fromUser = "schemas=" + oldSchema;
		String reMap = "remap_schema=" + oldSchema + ":" + newSchema;

		//return "impdp \"\'/ as sysdba\'\" " + directory + " " + dumpFile + " "
		//		+ options + " " + fromUser + " " + reMap;
		return "impdp "+userid+" "+ directory + " " + dumpFile + " "
				+ options + " " + fromUser + " " + reMap;
	}

	@Override
	String getInfoCmd() {
		// impdp "'/ as sysdba'" directory=qc_dir
		// dumpfile=QAP_SPRINT_LAB_PROJECT.dmp sqlfile=sprint.sql full=y
		// logfile=get_info.log
		String userid = "userid=" + userName + "/" + password + "@" + sid;
		String directory = "directory=" + ORACLE_DIRECTORY_NAME;
		String dumpFile = "dumpfile=" + dumpName;
		String options = "sqlfile=" + GET_INFO_SQL_OUTPUT + " full=y logfile="
				+ GETINFO_OUTPUT;

		return "impdp "+userid+" "+directory+ " " + dumpFile + " "
				+ options;
	}

	@Override
	boolean isGetInfoSucceed(String log) {
		
		if (FileAccessor.isFileMatchFromEnd(log,GET_INFO_PASS_PATTERN)) {
			return true;
		}		
		return false;
	}

	@Override
	String getTableSpaceFromLog() {
		String sqlFile = dumpDir+File.separator+GET_INFO_SQL_OUTPUT;
		String[] results = FileAccessor.fileMatch(sqlFile,TABLESPACE_PATTERN);
		if (results != null) return results[0];
		return null;
	}

	@Override
	String getSchemaFromLog() {
		String sqlFile = dumpDir+File.separator+GET_INFO_SQL_OUTPUT;
		String[] results = FileAccessor.fileMatch(sqlFile,SCHEMA_PATTERN);
		if (results != null) return results[0];
		return null;
	}

	@Override
	boolean beforeImport() {
		//Nothing needs to do
		return true;
	}

	@Override
	boolean isImportSucceed(String log) {
		if (FileAccessor.isFileMatchFromEnd(log,IMPORT_PASS_PATTERN)) {
			return true;
		}		
		return false;		
	}

	/**
	 * update QC user's password to tdtdtd and add necessary privileges after import. 
	 */
	@Override
	boolean afterImport() {
		if (dbAccessor.updateQCUser(newSchema)) {
			System.out.println("User "+newSchema+" is altered.");
			return true;
		}
		return false;
	}
	
}
