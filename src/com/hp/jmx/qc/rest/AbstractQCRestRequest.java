package com.hp.jmx.qc.rest;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.jmx.qc.rest.model.QCRestException;
import com.sun.org.apache.xerces.internal.dom.ElementNSImpl;
import com.sun.org.apache.xerces.internal.dom.NodeImpl;

public abstract class AbstractQCRestRequest implements QCRestRequest {

    private static final Logger log = LoggerFactory.getLogger(AbstractQCRestRequest.class);
    
    protected String httpMethod;
    protected String requestURL;
    protected boolean outputRequest = true;

    @Override
    public abstract void writeBody(OutputStream out) throws QCRestWSException;

    @Override
    public boolean isOutputRequest() {
    	return this.outputRequest;
    }

    @Override
    public String getOperationMethod() {
        return this.httpMethod;
    }

    @Override
    public String getRequestURL() {
        return this.requestURL;
    }

    protected String getBaseURL() {
        String qcURL = QCRestConfig.getQCRestURL();
        String domain = QCRestConfig.getQCRestDomain();
        String project = QCRestConfig.getQCRestProject();
        
        String url = qcURL + "/rest/domains/" + domain + "/projects/" + project + "/";
        return url;
    }
    

    private boolean succeed = false; 
    
    private boolean finished = false;
    
    private String errorMessage;
        
    @Override
    public void handleFailedResponse(InputStream in) {
        finished = true;

        QCRestException exception = null;
        try {
            JAXBContext jaxb = JAXBContext.newInstance("com.hp.jmx.qc.rest.model");
            Unmarshaller unmarshall = jaxb.createUnmarshaller();
            exception = (QCRestException)unmarshall.unmarshal(in);
        } catch (final JAXBException je) {
            // Can't parse the exception just throw the content.
            StringBuffer sb = new StringBuffer();
            try {
                Reader reader = new InputStreamReader(new BufferedInputStream(in), "UTF-8");
                char[] buffer = new char[1024];
                int len = reader.read(buffer);
                while (len != -1) {
                    sb.append(buffer, 0, len);
                    len = reader.read(buffer);
                }
                throw new QCRestWSException(sb.toString());
            } catch (UnsupportedEncodingException e) {
                logAndException("UTF-8 encoding isn't supported.", e);
            } catch (IOException e) {
                logAndException("Error happend when reading the error repsonse.", e);
            }
        }
        final Object errorTitle = exception.getTitle();//

        String errorMsg = "";
        if (errorTitle != null) {
            final NodeImpl errorNode = (NodeImpl)((ElementNSImpl)errorTitle).getFirstChild();
            if (errorNode != null) {
                errorMsg = errorNode.getNodeValue();
            }
        }

        // get the errorId
        String errorId = "";
        final Object errorIdObj = exception.getId();
        if (errorIdObj != null) {
            final NodeImpl errorNode = (NodeImpl)((ElementNSImpl)errorIdObj).getFirstChild();
            if (errorNode != null) {
                errorId = errorNode.getNodeValue();
                if (QCRestWSException.QC_REST_EXCEPTION_ENTITY_NOT_FOUND.equals(errorId)) {
                    throw new EntityNotFoundException(errorMsg);
                }
            }
        }

        throw new QCRestWSException(errorId, errorMsg);
        
    }

    @Override
    public void handleSuccessfulResponse(InputStream in) {
        try {
            finished = true;
            JAXBContext jaxb = JAXBContext.newInstance("com.hp.jmx.qc.rest.model");
            Unmarshaller unmarshall = jaxb.createUnmarshaller();
            Object response = unmarshall.unmarshal(in);
            parsetReponse(response);
            succeed = true;
        } catch (JAXBException e) {
            logAndException("Error happend when unmarshall the response.", e);
        }
    }

    protected abstract void parsetReponse(Object response);
    
    @Override
    public boolean isSucceed() {
        if (!finished)
            logAndException("The request is not finished yet.");
        return succeed;
    }

    private void logAndException(String message) {
        log.error(message);
        throw new RuntimeException(message);
    }

    private void logAndException(String message, Exception e) {
        log.error(message, e);
        throw new RuntimeException(message, e);
    }

    @Override
    public boolean isFinished() {
        return finished;
    }

    @Override
    public String getErrorMessage() {
        return this.errorMessage;
    }
}
