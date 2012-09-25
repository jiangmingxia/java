package com.hp.jmx.qc.dao;

public interface BaseDAO<T> {
	
	public void save(T t);
	
	public void update(T t);
	
	public T get(long id);
	
	public void delete(T t);
		
}
