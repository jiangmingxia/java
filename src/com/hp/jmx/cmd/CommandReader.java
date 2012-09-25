package com.hp.jmx.cmd;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandReader {

	//get command part
	public static String getCommandName(String input){
		input = input.trim();
		
		//remove options which are begin with "-"
		int index = input.indexOf("-");
		String command = input;
		if (index >0)	command = input.substring(0,index);
		
		//remove extra blank spaces between command words
		String[] words = command.split(" ");
		command = "";
		for (String word : words){
			word = word.trim();
			if (word.equals("")) continue;			
			command+=word+" ";
		}
		return command.trim();		
	}
	
	//get option part of this command
	public static Hashtable<String,String> getOptions(String input){
		input = input.trim();	
		//get options part
		int index = input.indexOf("-");
		if (index <0){
			return null;
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
