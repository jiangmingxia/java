package com.hp.jmx.cmd;

import java.io.File;
import java.util.Hashtable;

public class CreateTSCommand implements Command {
	
	private final static String commandSample = "create testset -name <testset name> -file <testset file>";	 
	public final static String command_name = "create testset";
	private final static String NameOption = "name";
	private final static String FileOption = "file";
	

	@Override
	public boolean execute(Hashtable<String,String> options) {
		if (!validateInput(options)) return false;
		return true;
	}
	
	private boolean validateInput(Hashtable<String,String> options)	{
		if (options.get(NameOption)!=null&&options.get(FileOption)!=null) {
			String filePath = options.get(FileOption);
			File file = new File(filePath);
			if (!file.exists()){
				CommandOutput.errorOutput("File does not exists:"+filePath);
				return false;
			}
			if(!file.isFile()){
				CommandOutput.errorOutput("It is not a file: "+filePath);
				return false;
			}
			return true;
		}		
		
		CommandOutput.errorOutput("The command should be like:");
		CommandOutput.errorOutput("		"+commandSample);
		return false;
	}

}
