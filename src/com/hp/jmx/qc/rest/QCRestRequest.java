package com.hp.jmx.qc.rest;

import java.io.InputStream;
import java.io.OutputStream;

public interface QCRestRequest {

	public static final String GET_METHOD = "GET";

    public static final String POST_METHOD = "POST";

    public static final String PUT_METHOD = "PUT";

    public static final String DELETE_METHOD = "DELETE";

    public boolean isOutputRequest();
    
	public String getOperationMethod();
	
	public String getRequestURL();
	
	/**
	 * A callback function to write the content to the REST body if needed.
	 * @param out
	 * @throws QCRestWSException 
	 */
	public void writeBody(OutputStream out) throws QCRestWSException;
	

    public boolean isSucceed();
    
    public void handleSuccessfulResponse(InputStream in);
    
    public void handleFailedResponse(InputStream in);
    
    public boolean isFinished();
    
    public String getErrorMessage();
}
