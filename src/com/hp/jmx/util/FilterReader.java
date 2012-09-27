package com.hp.jmx.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

public abstract class FilterReader extends BufferedReader{

	public FilterReader(Reader reader) {
		super(reader);
	}
	
	@Override
	public String readLine(){
		try {
			String s=null;
			while ((s = super.readLine()) != null) {
				if (skipLine(s)) continue;
				return s;
			}
			return null;
		} catch (IOException e) {			
			e.printStackTrace();
			return null;
		}
	} 
	
	abstract boolean skipLine(String s);
}
