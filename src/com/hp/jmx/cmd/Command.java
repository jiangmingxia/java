package com.hp.jmx.cmd;

import java.util.Hashtable;

public interface Command {
	public boolean execute(Hashtable<String,String> options);	 

}
