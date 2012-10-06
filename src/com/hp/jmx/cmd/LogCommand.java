package com.hp.jmx.cmd;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import com.hp.jmx.qc.dao.QCEntityDAO;
import com.hp.jmx.qc.dao.impl.DAOFactory;
import com.hp.jmx.qc.rest.QCRestConfig;
import com.hp.jmx.qc.util.InstanceUtil;


public class LogCommand implements Command {
	private final static String commandSample = "log -file <log file> -details <details file>";	 
	public final static String command_name = "log";	
	private final static String FileOption = "file";
	private final static String DetailsOption = "details";	
	private static boolean SaveToSourceTestSetWhenNotFound = true;
	private static final QCEntityDAO entityDAO = DAOFactory.getQCEntityDAO();

	@Override
	public boolean execute(Hashtable<String,String> options) {
		if (!validateInput(options)) return false;		
		String logFileName = options.get(FileOption);
		String detailsFileName = options.get(DetailsOption);
			
		
		//get log file modified time
		File logFile = new File(logFileName);
		Date logDate = new Date(logFile.lastModified());
		
		//read log file, get test result		
		LogUtil logUtil=LogUtilFactory.getLogUtil();
		Map<String, LogUtil.TestInfo> tests = new HashMap<String, LogUtil.TestInfo>(); 
		Date maxDate=null,minDate=null;
		try {
			BufferedReader br = new BufferedReader(new FileReader(logFileName));
			String s = null;
			String logMode=QCRestConfig.getLogMode();
			String logPrefix=QCRestConfig.getLogPrefix();
			
			while ((s = br.readLine()) != null) {
				s=s.trim();
				if (skipLine(s,logMode,logPrefix)) continue; //skip line according to log config
				
				//match: test name : PASSED|FAILED|
				//if match store the result
				String[] test = logUtil.getTestResult(s);
				if (test==null) continue; //skip line when not match <test name>:<run status>
				LogUtil.TestInfo ti = logUtil.new TestInfo();
				ti.setName(test[0]);
				ti.setRunResult(test[1]);				
				
				//match: date time
				Date testExecDate = logUtil.getDateFromString(s);
				if (testExecDate==null) {
				    testExecDate = logDate;
				} 
				ti.setDate(testExecDate);				
				tests.put(test[0], ti);
				
				//update min,max date				
				if (maxDate == null) {
	                maxDate=testExecDate;
	            } else {
	                if (testExecDate.after(maxDate)) {
	                    maxDate=testExecDate;
	                }
	            }
				
				if (minDate == null) {
				    minDate=testExecDate;
                } else {
                    if (testExecDate.before(minDate)) {
                        minDate=testExecDate;
                    }
                }
			}			
			br.close();
		} catch (FileNotFoundException e1) {			
			CommandOutput.errorOutput(e1.getMessage());
			return false;
		} catch (IOException e2) {
			CommandOutput.errorOutput(e2.getMessage());
			return false;
		}
		
		//read run details, match all pattern like xxxx=xxx
		Map<String,String> details = new HashMap<String,String>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(detailsFileName));
			String s = null;			
			while ((s = br.readLine()) != null) {
				s=s.trim();
				//if (skipLine(s,logMode,logPrefix)) continue;
				if (s.indexOf("=")<0) continue;
				if (s.split("=").length!=2) continue;
				String key = s.split("=")[0].trim();
				String value = s.split("=")[1].trim();
				if (key == null || key.isEmpty()) continue;
				details.put(key, value);
			}			
			br.close();
		} catch (FileNotFoundException e1) {			
			CommandOutput.errorOutput(e1.getMessage());
			return false;
		} catch (IOException e2) {
			CommandOutput.errorOutput(e2.getMessage());
			return false;
		}
		
		//insert run result to QC
		//1.select testset match details and max/min test run time
		//1) foreach details either = value or empty
		//2) testset start date earlier than max run date, end date either empty or later than min run date
		//3) testset is not under source folder/subfolder
		
		
		//2.get all instances of these test set, save to testsetId->testset entity, instanceId->testsetId
		//3.get all instances' test entity, save to instance Id->test entity
		
		//4.select all source test sets (Y/N)
		//5.get all instances of these test set, save to instanceId->testsetId
		//6.get all instances' test id: save to instanceId->testId
		
		//foreach test run: testname:status
		//1) according to testname, find corresponding test entity, test instanceIds, testset Ids: List<String[3]>
		//2) according to testset Id get test set entities, check if its close date, start date meet the test run date
		//3) if find one result: insert this run
		//   if not find one: try to save it to source test set (the first find one)
		
		return true;
	}
	
	
	
	
	//determine whether to skip this line or not
	private boolean skipLine(String line, String logMode,String logPrefix){		
		if (line.isEmpty()) return true;			
		
		if (!logMode.equals(QCRestConfig.EXCLUDE)&&!logMode.equals(QCRestConfig.INCLUDE)) return false;
		
		if (logMode.equals(QCRestConfig.EXCLUDE)) {
			//exclude the line start with the prefix
			if (line.startsWith(logPrefix)) return true;
			return false;
		} else {
			//include the line start with the prefix
			if (line.startsWith(logPrefix)) return false;
			return true;
		}
	}	
	
	private boolean validateInput(Hashtable<String,String> options)	{
		if (options.get(DetailsOption)!=null&&options.get(FileOption)!=null) {
			if (checkFileExists(options.get(FileOption))&&checkFileExists(options.get(DetailsOption))) return true;
			return false;			
		}
		
		CommandOutput.errorOutput("The command should be like:");
		CommandOutput.errorOutput("		"+commandSample);
		return false;
	}
	
	private boolean checkFileExists(String path){		
		File file = new File(path);
		if (!file.exists()){
			CommandOutput.errorOutput("File does not exists:"+path);
			return false;
		}
		if(!file.isFile()){
			CommandOutput.errorOutput("It is not a file: "+path);
			return false;
		}
		return true;
	}
}
