package test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hp.jmx.cmd.LogUtil;
import com.hp.jmx.cmd.LogUtil.TestInfo;
import com.hp.jmx.qc.util.EntityUtil;


public class TestRegMatch {
	public static void main(String[] args) throws Exception {    	
		
     	LogUtil logUtil = new LogUtil();
     	Map<String, TestInfo> info = logUtil.getTestRunInfo("C:\\Users\\jiamingx\\Downloads\\CSD_Automation_Result.txt", new Date());
     	if (info!=null) printTestInfo(info);
    }
	
	private static void printMap(Map<String,String> map) {
		for (String key: map.keySet()){
			System.out.println(key+":"+map.get(key));
		}
	    
	}
	
	private static void printTestInfo(Map<String,TestInfo> map) {
		for (String key: map.keySet()){
			TestInfo info = map.get(key);			
			System.out.println("info.getName():"+info.getName());
			System.out.println("info.getRunResult():"+info.getRunResult());
			System.out.println("info.getDate().toString():"+info.getDate().toString());
		}
	    
	}
}
