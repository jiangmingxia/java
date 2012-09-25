package com.hp.jmx;

import java.sql.SQLException;
import java.util.List;

public interface DBAccessor {
	/*
	 * SiteAdmin
	 * */
	public List<Object> getProjectServers() throws SQLException;
	
	public int[] upgradeSiteAdmin();
	public int[] downgradeSiteAdmin();
	
	/*
	 * Project 
	 */
	public int[] prepareProjectsForUpgrade();
	public int[] prepareProjectsForDowngrade();

	public int dropCurrentDB();
	public int isCurrentDBExist();
	
	public List<Object> getAllDBName(String nameKeyWord) throws SQLException;
	
	public String getAppServerName() throws SQLException;
	public String getServerVersion() throws SQLException;
	public String getNoDuplicateDBName(String namePrefix);
	
	
}
