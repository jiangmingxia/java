package test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.hp.jmx.cmd.LogUtil;
import com.hp.jmx.cmd.LogUtil.TestInfo;

public class TestRegMatch {
	public static void main(String[] args) throws Exception {    	
		
     	LogUtil logUtil = new LogUtil();
     	ArrayList<TestInfo> info = logUtil.getTestRunInfo("C:\\Users\\jiamingx\\Downloads\\CSD_Automation_Result.txt", new Date());
     	if (info!=null) printTestInfo(info);
    }
	
	
	private static void printTestInfo(List<TestInfo> info) {
		for (TestInfo ti: info){						
			System.out.println("ti.getName():"+ti.getName());
			System.out.println("ti.getTestId():"+ti.getTestId());
			System.out.println("ti.getTestSetName():"+ti.getTestSetName());
			System.out.println("ti.getRunResult():"+ti.getRunResult());
			System.out.println("ti.getDate().toString():"+ti.getDate().toString());
		}
	    
	}
}
