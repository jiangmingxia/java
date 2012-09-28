package com.hp.jmx.cmd;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hp.jmx.qc.util.InstanceUtil;

public class LogUtil {
	
	private final static String Log_DateTimePattern1= " ?([01]?\\d)(\\-|\\/|\\.)([0-3]?\\d)(\\-|\\/|\\.)(\\d{4}) +([012]?\\d):([0-5]\\d):([0-5]\\d) *(AM|PM|am|pm)? ?";
	private final static String Log_DateTimePattern2= " ?(\\d{4})(\\-|\\/|\\.)([01]?\\d)(\\-|\\/|\\.)([0-3]?\\d) +([012]?\\d):([0-5]\\d):([0-5]\\d) *(AM|PM|am|pm)? ?";
	private final static String QCDateFormatPattern="yyyy-MM-dd HH:mm:ss";
	
	private Pattern testResultPattern;
	private Pattern dateTimePattern1;
	private Pattern dateTimePattern2;
	private DateFormat QCDateFormat;
	
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
		QCDateFormat= new SimpleDateFormat(QCDateFormatPattern);
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
	public String[] getTestResult(String input) {
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
	
	public Date getDateFromString(String input) {				
		Date resultDate;
		int year,month,date,hour,min,sec;
		String AMPMtag="";
		
		//match the first date pattern
		Matcher m = dateTimePattern1.matcher(input);
		if (m.find()) {			
			year = Integer.parseInt(m.group(5));
			month =Integer.parseInt(m.group(3));
			date = Integer.parseInt(m.group(1));
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
		System.out.println("dateString:"+dateString);
		try {
			resultDate = QCDateFormat.parse(dateString);
			return resultDate;
		} catch (ParseException e) {			
			e.printStackTrace();
			return null;
		} 
	}
	
	
}
