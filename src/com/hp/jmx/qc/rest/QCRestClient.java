package com.hp.jmx.qc.rest;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.jmx.util.Base64;
import com.hp.jmx.util.MyEncode;

public class QCRestClient {

    protected static final int QC_CONNECTION_TIMEOUT = 60 * 1000;

    protected static final int QC_READ_TIMEOUT = 60 * 1000;

	private static final Logger log = LoggerFactory.getLogger(QCRestClient.class);
	
	private static final QCRestClient instance = new QCRestClient();
	
	private String ssoCookieValue;
	
	private String qcSessionCookieValue;
	
	
	
	private QCRestClient() {
	    
	}
	
	public static final QCRestClient getInstance() {
	    return instance;
	}
	
	public void run(QCRestRequest request) throws QCRestWSException {
        HttpURLConnection conn = null;

        try {
            // (1) authenticate
            if (ssoCookieValue == null)
                this.authenticate();

            // (2) build the request
            conn = this.sendRequest(request);

            final int responseCode = conn.getResponseCode();
            // give another try
            if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                this.authenticate();
                conn = this.sendRequest(request);
            }
            
            // (3) handle the response
            this.handleResponse(request, conn);
            
        }  catch (final QCRestWSException e) {
            log.error("REST Web Service error(QCRestWSException):", e);
            throw e;
        } catch (final Exception e) {
            log.error("REST Web Service error(Other Exception):", e);
            throw new QCRestWSException("Error to call the rest API.", e);
        } finally {
            // release current connection
            if (conn != null) {
                conn.disconnect();
            }

            // logout needs set the 2 cookies: LWSSO_COOKIE_KEY & QCSession
            // in the request.
            if (ssoCookieValue != null) {  
            	String qcSessionCookieValue1 = this.getCookieValue(conn, "QCSession");
            	if (qcSessionCookieValue1!=null && !qcSessionCookieValue1.isEmpty() && !qcSessionCookieValue1.equals(qcSessionCookieValue)) {
            		qcSessionCookieValue= qcSessionCookieValue1;
            	}                
                // get return cookie
                log.debug("Cookie for QCSession: " + qcSessionCookieValue);
            }
        }
	}

    /**
     * This method is used to authenticate the QC with the given user
     * name/password. support LWSSO authenticate, if set cookie key in parameter
     * qc, it will return ssoCookieValue directly
     * @param QCServerURL
     * @param username
     * @param password
     * @return
     * @throws IOException 
     * @throws IOException
     */
    protected String authenticate() throws QCRestWSException, IOException {
        log.debug("Begin authenticate QC");
        // encode the base64 codeing for username and password        
        final String userPassword = QCRestConfig.getQCRestUsername() + ":" + QCRestConfig.getQCRestPassword();   
        //final String encoding = new sun.misc.BASE64Encoder().encode(userPassword.getBytes());
        final String encoding = new String(Base64.encodeToByte(userPassword.getBytes(), true));
        

        String QCRestURL = QCRestConfig.getQCRestURL();
        if (!QCRestURL.endsWith("/")) QCRestURL = QCRestURL+"/";              
        final URL url = new URL(QCRestURL + "authentication-point/authenticate");
        final HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        // add the timeout to the connection and read
        this.setTimeout(conn);
        // set the basic authentication
        conn.setRequestProperty("Authorization", "Basic " + encoding);

        // do get
        conn.connect();

        // try to authenticate the qc user and password
        final int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
            throw new QCRestWSException("Authorization failed. " + userPassword);
        } else if (responseCode == HttpURLConnection.HTTP_NOT_FOUND) {
            throw new QCRestWSException("The request url is not found. " + url.getPath());
        }

        // get return cookie
        ssoCookieValue = this.getCookieValue(conn, "LWSSO_COOKIE_KEY");
        log.debug("Cookie for LWSSO_COOKIE_KEY: " + ssoCookieValue);

        // disconnect
        conn.disconnect();
        return ssoCookieValue;
    }

    protected HttpURLConnection sendRequest(QCRestRequest request) throws IOException, QCRestWSException
    {
        // post the output stream
        final URL url = new URL(request.getRequestURL());        
        final HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        // add the timeout to the connection and read
        this.setTimeout(conn);
        this.buildHeader(request, conn);
        this.buildBody(request, conn);
        conn.connect();
        return conn;
    }
    
    protected void handleResponse(QCRestRequest request, HttpURLConnection conn) throws IOException {
        final int responseCode = conn.getResponseCode();
        if (this.isSuccessful(request, responseCode)) {
            request.handleSuccessfulResponse(conn.getInputStream());
        } else {
            request.handleFailedResponse(conn.getErrorStream());
        }
    }
    
    protected void setTimeout(HttpURLConnection conn) {
        conn.setConnectTimeout(QC_CONNECTION_TIMEOUT);
        conn.setReadTimeout(QC_READ_TIMEOUT);
    }

    /**
     * Build the request header. POST/PUT: we need to set the output of the
     * connection; DELETE/GET: we needn't to set the output of the connection.
     * @param conn
     * @throws IOException
     */
    protected void buildHeader(QCRestRequest request, HttpURLConnection conn) throws IOException {
        // set the cookie
        setCookie(conn);
        conn.addRequestProperty("Content-Type", "application/xml;charset=UTF-8");
        conn.addRequestProperty("Accept", "application/xml");
        conn.setRequestMethod(request.getOperationMethod());
    }
    
    private void setCookie(HttpURLConnection conn) {
    	String cookieValue;
    	if (qcSessionCookieValue==null) {
    		cookieValue = "LWSSO_COOKIE_KEY=" + ssoCookieValue;
    	} else {
    		cookieValue = "LWSSO_COOKIE_KEY=" + ssoCookieValue + "; " + "QCSession=" + qcSessionCookieValue;
    	}    	
        conn.setRequestProperty("Cookie", cookieValue);       
    }
    
    protected void buildBody(QCRestRequest request, HttpURLConnection conn) throws IOException, QCRestWSException {
        // to the POST/PUT method, we need set the output
    	if (request.isOutputRequest()) {
            conn.setDoOutput(true);
    		request.writeBody(conn.getOutputStream());
    	}
    }

    protected boolean isSuccessful(QCRestRequest request, int responseCode) {
        if (request.isOutputRequest()) {
            return (responseCode == HttpURLConnection.HTTP_CREATED) || (responseCode == HttpURLConnection.HTTP_OK);
        } else {
            return responseCode == HttpURLConnection.HTTP_OK;
        }
    }
    
    protected String getCookieValue(URLConnection conn, String cookieName) {
        if (conn == null) {
            return null;
        }
        final Map<String, List<String>> headers = conn.getHeaderFields();
        final List<String> values = headers.get("Set-Cookie");

        if (values != null) {
            for (final String line : values) {
                if (line != null) {
                    final String[] cookies = line.split(";");
                    if ((cookies != null) && (cookies.length > 0)) {
                        for (final String cookie2 : cookies) {
                            final String cookie = cookie2.trim();
                            final int pos = cookie.indexOf("=");
                            if (pos > 0) {
                                if (cookieName.equals(cookie.substring(0, pos).trim())) {
                                    return cookie.substring(pos + 1).trim();
                                }
                            }
                        }
                    }
                }
            }
        }

        return null;
    }

    /**
     * Logout from the QC session.
     * @param QCServerURL
     */
    public void logoutSession(String QCServerURL) throws IOException
    {
    	if (!QCServerURL.endsWith("/")) QCServerURL +="/"; 
        final URL url = new URL(QCServerURL + "authentication-point/logout");        
        final HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        // add the timeout to the connection and read
        this.setTimeout(conn);
        conn.setRequestMethod(QCRestRequest.GET_METHOD);

        // set cookie
        setCookie(conn);        
        
        // do get
        conn.connect();
        
        log.debug("Logout return code is:" + conn.getResponseCode());

        // disconnect
        conn.disconnect();
    }

}
