package com.hp.jmx;

import java.io.*;
import java.util.Hashtable;


public class Configure {
	
	private Hashtable<String,String> configureTable; 
	private static final String SPLITER = "=";	
	
	public Configure ()  {
		configureTable = new Hashtable<String, String>();
	}
	
	public synchronized void load (FileReader fr) throws IOException {
		BufferedReader br = new BufferedReader(fr);
		String s = null;
		while ((s=br.readLine())!= null) {
			s = s.trim();
			//skip empty line
			if (s.equals("")) continue; 
			
			//skip line begin with #
			if (s.startsWith("#")) continue;  
			
			//split by "="
			String[] keyValue = s.split(SPLITER);
			if (keyValue==null || keyValue.length<1) {
				System.out.println("Config file is not well formed. Must be one \"=\" a line!");
			}			
			if (keyValue[1]==null || keyValue[1].equals("")) continue;
			
			configureTable.put(keyValue[0], keyValue[1]);
		}
		br.close();		
	}
	
	public String get(String key) {
		return configureTable.get(key);
	}	
}
