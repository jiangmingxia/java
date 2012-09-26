package com.hp.jmx.qc.model;

/**
 * Just a label interface.
 * @author JMX
 *
 */
public interface EntityObject {
    
    public static final String TEST_TYPE = "test";

    public static final String TEST_INSTANCE_TYPE = "test-instance";
    
    public static final String TEST_FOLDER_TYPE = "test-folder";
    
    public static final String TEST_SET_TYPE = "test-set";
    
    public static final String RUN_TYPE = "run";
    
    public static final String TEST_SET_FOLDER_TYPE="test-set-folder";
    
    public long getId();
    public void setId(long id);
}
