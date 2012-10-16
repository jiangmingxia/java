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
import com.hp.jmx.qc.util.EntityUtil;
import com.hp.jmx.qc.util.InstanceUtil;

public class LogUtil {
	
	private final static String Log_DateTimePattern1= " ?([01]?\\d)(\\-|\\/|\\.)([0-3]?\\d)(\\-|\\/|\\.)(\\d{4}) +([012]?\\d):([0-5]\\d):([0-5]\\d) *(AM|PM|am|pm)? ?";
	private final static String Log_DateTimePattern2= " ?(\\d{4})(\\-|\\/|\\.)([01]?\\d)(\\-|\\/|\\.)([0-3]?\\d) +([012]?\\d):([0-5]\\d):([0-5]\\d) *(AM|PM|am|pm)? ?";
	
	
	//JSON related static string
	public final static String FIELD_NAME="field";
	public final static String FIELD_VALUE="value";
	public final static String TEST_NAME="test";
	public final static String TEST_RUNTIME="run time";
	public final static String TEST_RESULT="result";
	
	
	private Pattern testResultPattern;
	private Pattern dateTimePattern1;
	private Pattern dateTimePattern2;
	public DateFormat QCDateTimeFormat;
	public DateFormat QCDateFormat;
	
	//design:
	//{"test":"xxxx","run time":"MM\dd\yyyy HH:mm:ss AM|PM|am|pm","result":"Passed|Failed|Blocked|...."}
	//{"field":"xxxx","value":"xxxx"}
	//{"field":"xxxx","value":"xxxx"}
	
	
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
		dateTimePattern1 = Pattern.compile(Log_DateTimePattern1);
		dateTimePattern2 = Pattern.compile(Log_DateTimePattern2);
		QCDateTimeFormat= new SimpleDateFormat(EntityUtil.QCDateTimeFormatPattern);
		QCDateFormat= new SimpleDateFormat(EntityUtil.QCDateFormatPattern);
		QCDateTimeFormat.setLenient(false);
		QCDateFormat.setLenient(false);
	}
	
	//return pattern like: Passed|Failed|<other status in QC>|...
	private Pattern getTestResultPattern(){
		if (testResultPattern==null) {
			List<String> allStatus = InstanceUtil.getListByName("Status");
			if (allStatus==null || allStatus.size()<1) return null; //something wrong with the result
			StringBuffer sb = new StringBuffer();
			boolean first = true;
			for (String status:allStatus){
				if (!first) {
					sb.append("|");				
				}
				sb.append(status);
				first = false;
			}
			testResultPattern = Pattern.compile(sb.toString());
		}
		return testResultPattern;
	}
	
	//match run result with status in ALM/QC
	private boolean matchResultPattern(String runResult){
		Pattern resultPattern = getTestResultPattern();
		Matcher m = resultPattern.matcher(runResult);
		if (m.matches()) return true;
		return false;
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
                Map<String,String> data = readJSON(s);
                if (data!=null && data.size()<1) continue;
                String key = data.get(FIELD_NAME);
                String value = data.get(FIELD_VALUE);
                if (key==null ||value == null) continue;
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
	
	//{"test":"<test name>","run time":"<run time of this test>","result":"<run status>"}	
	//{"field":"platform","value":"Linux"}
	private Map<String,String> readJSON(String oneLine) {
		Map<String,String> data = new HashMap<String,String>();		
		if (!oneLine.startsWith("{")||!oneLine.endsWith("}")||oneLine.length()<3) {			
			return null;
		}
		oneLine = oneLine.substring(1,oneLine.length()-1); //remove {}
		
		String[] items = oneLine.split("\"\\s*,\\s*\"");		
		//add " to each items since lost it during split
		int length = items.length;
		if (length>1) {
			items[0] = items[0]+"\"";
			items[length-1] = "\""+items[length-1];
		}
		for (int i=1;i<length-1;i++){
			items[i] = "\""+items[i]+"\"";
		}
		
		String pattern = "^\"([^\"]+)\"\\s*:\\s*\"([^\"]+)\"$";
    	Pattern p=Pattern.compile(pattern);	
    	Matcher matcher = null;
		for (int i=0;i<length;i++) {
			matcher = p.matcher(items[i]);
			if (matcher.find()) {
				data.put(matcher.group(1), matcher.group(2));				
			} else {
				//System.out.println("It should match \"<name>\":\"<value>\" pattern. But now it is "+items[i]);
				return null;
			}
		}	
		return data;
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
            
            while ((s = br.readLine()) != null) {
                s=s.trim();                
                Map<String,String> data = readJSON(s);
                if (data==null ||data.size()<1) continue; //skip not JSON format line
                
                String testName = data.get(TEST_NAME);
                String runResult = data.get(TEST_RESULT);
                String runTime = data.get(TEST_RUNTIME);
                if (testName==null || runResult == null) continue; //skip not line when no test name/result info
                if (!matchResultPattern(runResult)) continue; //skip line when test run status is not consistent with ALM                              
                TestInfo ti = new TestInfo();
                ti.setName(testName);
                ti.setRunResult(runResult); 
                
                Date testExecDate = defaultDate;
                if (runTime!=null) {
                	//match date
                	Date d = getDateFromString(runTime);
                	if (d != null) {
                		testExecDate = d;
                	}
                }
                ti.setDate(testExecDate);               
                testRuns.put(testName, ti);
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
}
