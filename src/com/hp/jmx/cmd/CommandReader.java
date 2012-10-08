package com.hp.jmx.cmd;

import java.util.*;

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
	    int i=0;
	    for (;i<args.length;i++) {
	    	if (args[i].startsWith("-")) break;
	    }
	    for (;i<args.length;){
	    	String option = args[i].substring(1).trim(); //remove "-"
    		StringBuffer value = new StringBuffer();
    		i++;
    		while(i<args.length&&args[i].startsWith("-")==false) {
    			value.append(args[i]+" ");
    			i++;
    		}
    		options.put(option, value.toString().trim());
	    }
		
//		//options: -a value1 -b -c value1, value2		
//		input = input.substring(index);
//		String optionPattern = "-[a-zA-Z]*";
//		String[] optionValues = input.split(optionPattern);
//		
//		Pattern p=Pattern.compile(optionPattern);		
//		Matcher matcher = p.matcher(input);
//		int i=1;//the first one of values is empty, should be skipped.
//		while (matcher.find()&&i<optionValues.length) {
//			String option = matcher.group().substring(1).trim(); //remove "-"
//			options.put(option, optionValues[i].trim());
//			i++;
//		}
		return options;
	}
}
