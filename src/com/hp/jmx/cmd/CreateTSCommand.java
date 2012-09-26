package com.hp.jmx.cmd;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hp.jmx.qc.model.QCEntity;
import com.hp.jmx.qc.rest.QCRestConfig;
import com.hp.jmx.qc.util.TestSetUtil;
import com.hp.jmx.qc.util.TestUtil;

public class CreateTSCommand implements Command {
	
	private final static String commandSample = "create testset -name <testset name> -file <testset file>";	 
	public final static String command_name = "create testset";
	private final static String NameOption = "name";
	private final static String FileOption = "file";
	

	@Override
	public boolean execute(Hashtable<String,String> options) {
		if (!validateInput(options)) return false;
		
		//check if test set exists in ALM
		String testSetName = options.get(NameOption);
		if (!TestSetUtil.isSourceTestSetExist(testSetName)) {
			CommandOutput.errorOutput("Test set "+testSetName+" already exists!");
			return false;
		}
		
		//create test set
		QCEntity testSetEntity = TestSetUtil.createTestSetByName(testSetName);		
		
		//get all test names
		List<String> testNames=new ArrayList<String>();	
		String fileName = options.get(FileOption);
		try {
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			String s = null;			
			while ((s = br.readLine()) != null) {
				s=s.trim();
				if (s.length()<1) continue;
				testNames.add(s);
			}
			br.close();
		} catch (FileNotFoundException e1) {
			CommandOutput.errorOutput("File not exists: " + fileName);
			CommandOutput.errorOutput(e1.getMessage());
			return false;
		} catch (IOException e2) {
			CommandOutput.errorOutput(e2.getMessage());
			return false;
		}
		
		//create test if not exists and add it to test set
		QCEntity testEntity;
		for (String testName: testNames){
			//if not exists create one
			if (!TestUtil.isTestExists(testName)) {
				testEntity=TestUtil.createTestByName(testName);
				if (testEntity==null) {
					CommandOutput.errorOutput("Fail to create test:" +testName);
					return false;
				}
			} else {
				testEntity = TestUtil.getTestByName(testName);
			}
			
			//add it to test set
			
		}
		
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
