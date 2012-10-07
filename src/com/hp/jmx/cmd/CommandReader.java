package com.hp.jmx.cmd;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandReader {

	//get command part
	public static String getCommandName(String[] args){
	    StringBuffer command=new StringBuffer();
	    boolean first = true;
	    for (String arg:args) {
	        if (arg.startsWith("-")) break;
	        if (!first) command.append(" ");
	        first=false;
	        command.append(arg);
	    }
	    return command.toString();
	}
	
	//get option part of this command
	public static Hashtable<String,String> getOptions(String[] args){
	    Hashtable<String,String> options = new Hashtable<String,String>();
	    for (String arg:args) {
	        
	    }		
		
		//options: -a value1 -b -c value1, value2
		Hashtable<String,String> options = new Hashtable<String,String>();
		input = input.substring(index);
		String optionPattern = "-[a-zA-Z]*";
		String[] optionValues = input.split(optionPattern);
		
		Pattern p=Pattern.compile(optionPattern);		
		Matcher matcher = p.matcher(input);
		int i=1;//the first one of values is empty, should be skipped.
		while (matcher.find()&&i<optionValues.length) {
			String option = matcher.group().substring(1).trim(); //remove "-"
			options.put(option, optionValues[i].trim());
			i++;
		}
		return options;
	}
}
