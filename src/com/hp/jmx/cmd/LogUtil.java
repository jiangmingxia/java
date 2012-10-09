package com.hp.jmx.cmd;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hp.jmx.qc.rest.QCRestConfig;
import com.hp.jmx.qc.util.EntityUtil;
import com.hp.jmx.qc.util.InstanceUtil;

public class LogUtil {
	
	private final static String Log_DateTimePattern1= " ?([01]?\\d)(\\-|\\/|\\.)([0-3]?\\d)(\\-|\\/|\\.)(\\d{4}) +([012]?\\d):([0-5]\\d):([0-5]\\d) *(AM|PM|am|pm)? ?";
	private final static String Log_DateTimePattern2= " ?(\\d{4})(\\-|\\/|\\.)([01]?\\d)(\\-|\\/|\\.)([0-3]?\\d) +([012]?\\d):([0-5]\\d):([0-5]\\d) *(AM|PM|am|pm)? ?";	
	
	private Pattern testResultPattern;
	private Pattern dateTimePattern1;
	private Pattern dateTimePattern2;
	public DateFormat QCDateTimeFormat;
	public DateFormat QCDateFormat;
	
	//design:
	//{"test name":"xxxx","run time":"MM\dd\yyyy HH:mm:ss","result":""}
	
	
	public class TestInfo {
		private String name;
		private String runResult;
		private Date date;
		
		public void setName(String name){
			this.name = name;
		}
		public void setRunResult(String result){
			this.runResult = result;
		}
		public void setDate(Date date){
			this.date=date;
		}
		
		public String getName(){
			return this.name;
		}
		public String getRunResult(){
			return this.runResult;
		}
		public Date getDate(){
			return this.date;
		}
	}
	
	public LogUtil(){
		//compile all pattern
		testResultPattern = Pattern.compile(LogUtil.getLogTestPattern());
		dateTimePattern1 = Pattern.compile(Log_DateTimePattern1);
		dateTimePattern2 = Pattern.compile(Log_DateTimePattern2);
		QCDateTimeFormat= new SimpleDateFormat(EntityUtil.QCDateTimeFormatPattern);
		QCDateFormat= new SimpleDateFormat(EntityUtil.QCDateFormatPattern);
		QCDateTimeFormat.setLenient(false);
		QCDateFormat.setLenient(false);
	}
	
	private static String getLogTestPattern(){
		List<String> allStatus = InstanceUtil.getListByName("Status");
		StringBuffer sb = new StringBuffer();
		sb.append("(\\S+) *: *(");
		boolean first = true;
		for (String status:allStatus){
			if (!first) {
				sb.append("|");				
			}
			sb.append(status);
			first = false;
		}
		sb.append(")($| +.*$)");
		return sb.toString();
	} 
	
	//if not match return null
	//if match String[0] is test name, String[1] is test run result
	private String[] getTestResult(String input) {
		String[] result = new String[2];
		Matcher m = testResultPattern.matcher(input);
		if (m.find()) {
    	    result[0] = m.group(1);
    	    result[1] = m.group(2);
    	    return result;    	    
		} else {
			return null;
		}
	}	
	
	private Date getDateFromString(String input) {				
		Date resultDate;
		int year,month,date,hour,min,sec;
		String AMPMtag="";
		
		//match the first date pattern
		Matcher m = dateTimePattern1.matcher(input);
		if (m.find()) {			
			year = Integer.parseInt(m.group(5));
			month =Integer.parseInt(m.group(1));
			date = Integer.parseInt(m.group(3));
			hour = Integer.parseInt(m.group(6));
			min = Integer.parseInt(m.group(7));
			sec = Integer.parseInt(m.group(8));
			AMPMtag = m.group(9);
		} else {
			//match the second date pattern
			m = dateTimePattern2.matcher(input);
			if (m.find()){
				year = Integer.parseInt(m.group(1));
				month =Integer.parseInt(m.group(3));
				date = Integer.parseInt(m.group(5));				
				hour = Integer.parseInt(m.group(6));
				min = Integer.parseInt(m.group(7));
				sec = Integer.parseInt(m.group(8));
				AMPMtag = m.group(9);
			} else {
				//no date time match
				return null;
			}
		}
		if (AMPMtag!=null && AMPMtag.equalsIgnoreCase("PM")) {
			hour=hour+12;				
		}
		String dateString = year+"-"+month+"-"+date+" "+hour+":"+min+":"+sec;		
		try {
			resultDate = QCDateTimeFormat.parse(dateString);
			return resultDate;
		} catch (ParseException e) {			
			e.printStackTrace();
			return null;
		} 
	}
	
	/**
	 * retrieve run details information from given file(name), save as <key,value>
	 * @param fileName
	 * @return details map or null if exception occurs
	 */
	public Map<String,String> getDetails(String fileName){
	    Map<String,String> details = new HashMap<String,String>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            String s = null;            
            while ((s = br.readLine()) != null) {
                s=s.trim();             
                if (s.indexOf("=")<0) continue;
                if (s.split("=").length!=2) continue;
                String key = s.split("=")[0].trim();
                String value = s.split("=")[1].trim();
                if (key == null || key.isEmpty()) continue;
                details.put(key, value);
            }           
            br.close();
            return details;
        } catch (FileNotFoundException e1) {            
            CommandOutput.errorOutput(e1.getMessage());
            return null;
        } catch (IOException e2) {
            CommandOutput.errorOutput(e2.getMessage());
            return null;
        }        
	}
	
	/**
	 * retrieve test run result and run time info from given file
	 * @param fileName
	 * @param defaultDate : if run time info is not got use default date
	 * @return test run result/info, test name as key
	 * return null if any exception occurs
	 */
	public Map<String, TestInfo> getTestRunInfo(String fileName, Date defaultDate){
	    Map<String, TestInfo> testRuns = new HashMap<String, TestInfo>(); 
	    try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            String s = null;
            String logMode=QCRestConfig.getLogMode();
            String logPrefix=QCRestConfig.getLogPrefix();
            
            while ((s = br.readLine()) != null) {
                s=s.trim();
                if (skipLine(s,logMode,logPrefix)) continue; //skip line according to log config
                
                //match: test name : PASSED|FAILED|
                //if match store the result
                String[] test = getTestResult(s);
                if (test==null) continue; //skip line when not match <test name>:<run status>
                TestInfo ti = new TestInfo();
                ti.setName(test[0]);
                ti.setRunResult(test[1]);               
                
                //match: date time
                Date testExecDate = getDateFromString(s);
                if (testExecDate==null) {
                    testExecDate = defaultDate;
                } 
                ti.setDate(testExecDate);               
                testRuns.put(test[0], ti);
            }           
            br.close();
            return testRuns;
        } catch (FileNotFoundException e1) {            
            CommandOutput.errorOutput(e1.getMessage());
            return null;
        } catch (IOException e2) {
            CommandOutput.errorOutput(e2.getMessage());
            return null;
        }
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
	
	
}
