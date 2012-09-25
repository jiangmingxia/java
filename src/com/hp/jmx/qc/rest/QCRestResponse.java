package com.hp.jmx.qc.rest;

import java.io.InputStream;

public interface QCRestResponse {
	public void handleSuccessfulResponse(InputStream in);
	
	public void handleFailedResponse(InputStream in);
}
