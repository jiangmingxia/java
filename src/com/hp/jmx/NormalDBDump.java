package com.hp.jmx;

import java.io.File;

import com.hp.jmx.util.FileAccessor;

public class NormalDBDump extends BaseDBDump implements DBDump {
	public NormalDBDump(String dumpFile, String url, String oracleDataSpace, OracleDBAccessor dbAccessor) throws Exception {
		super(dumpFile, url, oracleDataSpace,dbAccessor);
	}
	
	//error message when the dump format not correct
	private static final String ERROR_FORMAT_PATTERN = ".* header failed verification$";	
	private static final String GET_INFO_PASS_PATTERN="^Import terminated successfully .*";
	private static final String IMPORT_PASS_PATTERN="^Import terminated successfully .*";
	
	//Table space, schema match pattern
	private static final String TABLESPACE_PATTERN = ".*TABLESPACE \"(.+)\" .*";
	private static final String SCHEMA_PATTERN = ".* SET CURRENT_SCHEMA=\\s*\"(.[^\"]+)\".*";

	@Override
	String getImportCmd() {
		String userid = "userid=" + userName + "/" + password + "@" + sid;
		String file = "file=" + dumpName;
		String options = "log=" + IMPORT_OUTPUT
				+ " IGNORE=Y GRANTS=Y BUFFER=20000 FEEDBACK=1000";
		String fromUser = "fromuser=" + oldSchema;
		String toUser = "touser=" + newSchema;

		return "imp " + userid + " " + file + " " + options + " " + fromUser
				+ " " + toUser;
	}

	@Override
	String getInfoCmd() {
		
		// imp file=<dump file name>.dmp userid=system/manager full=y rows=n
		// show=y log=<log file name- better to type a recognizable name>.log
		String file = "file=" + dumpName;
		String userid = "userid=" + userName + "/" + password + "@" + sid;
		String options = "full=y rows=n show=y log=" + GETINFO_OUTPUT;

		return "imp " + file + " " + userid + " " + options;
		
		
	}

	@Override
	boolean isGetInfoSucceed(String log) {
		if (FileAccessor.isFileMatch(log,ERROR_FORMAT_PATTERN)) {
			return false;
		}
		if (FileAccessor.isFileMatchFromEnd(log,GET_INFO_PASS_PATTERN)) {
			return true;
		}
		return false;
	}

	@Override
	String getTableSpaceFromLog() {
		String log = dumpDir+File.separator+GETINFO_OUTPUT;
		String[] results = FileAccessor.fileMatch(log,TABLESPACE_PATTERN);
		if (results != null) return results[0];
		return null;
	}

	@Override
	String getSchemaFromLog() {
		String log = dumpDir+File.separator+GETINFO_OUTPUT;
		String[] results = FileAccessor.fileMatch(log,SCHEMA_PATTERN);
		if (results != null) return results[0];
		return null;
	}

	/**
	 * before import we need to create QC user
	 */
	@Override
	boolean beforeImport() {
		if (dbAccessor.createQCUser(newSchema, tableSpace)) {
			System.out.println("User "+newSchema+" is created.");
			return true;
		}
		return false;
	}

	@Override
	boolean isImportSucceed(String log) {
		if (FileAccessor.isFileMatchFromEnd(log,IMPORT_PASS_PATTERN)) {
			return true;
		}
		return false;
	}

	@Override
	boolean afterImport() {
	    //Do nothing
		return true;
	}
	
}
