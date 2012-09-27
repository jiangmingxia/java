package com.hp.jmx.cmd;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hp.jmx.qc.rest.QCRestConfig;


public class LogCommand implements Command {
	private final static String commandSample = "log -file <log file> -details <details file>";	 
	public final static String command_name = "log";	
	private final static String FileOption = "file";
	private final static String DetailsOption = "details";
	
	

	@Override
	public boolean execute(Hashtable<String,String> options) {
		if (!validateInput(options)) return false;
		
		//read log file
		String fileName = options.get(FileOption);
		String logMode=QCRestConfig.getLogMode();
		String logPrefix=QCRestConfig.getLogPrefix();
		try {
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			String s = null;	
			String testResultPattern = "";
			Pattern p = Pattern.compile(testResultPattern);
			Matcher matcher;
			while ((s = br.readLine()) != null) {
				s=s.trim();
				if (skipLine(s,logMode,logPrefix)) continue; //skip line according to log config
				
				//try to match datetime
				matcher=p.matcher(s);
			}
			br.close();
		} catch (FileNotFoundException e1) {			
			CommandOutput.errorOutput(e1.getMessage());
			return false;
		} catch (IOException e2) {
			CommandOutput.errorOutput(e2.getMessage());
			return false;
		}		
		
		return true;
	}
	
	//determine whether to skip this line or not
	private boolean skipLine(String line, String logMode,String prefix){		
		if (line.isEmpty()) return true;
		
		if (!logMode.equals(QCRestConfig.EXCLUDE)&&!logMode.equals(QCRestConfig.INCLUDE)) return false;
		
		if (logMode.equals(QCRestConfig.EXCLUDE)) {
			//exclude the line start with the prefix
			if (line.startsWith(prefix)) return true;
			return false;
		} else {
			//include the line start with the prefix
			if (line.startsWith(prefix)) return false;
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
