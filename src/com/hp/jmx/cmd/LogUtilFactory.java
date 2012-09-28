package com.hp.jmx.cmd;


public class LogUtilFactory {
	
	private static final LogUtil logUtil = new LogUtil();
	public static LogUtil getLogUtil(){
		return logUtil;
	}
	
}
