package com.hp.jmx.qc.rest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QCRestConfig {

	private static final Logger log = LoggerFactory.getLogger(QCRestConfig.class);
	
	private static final String QC_CONFIG_FILE = "conf/qc_rest.properties";

	public static final String QC_REST_URL_KEY = "qc.rest.url";

	public static final String QC_REST_USERNAME_KEY = "qc.rest.username";

	public static final String QC_REST_PASSWORD_KEY = "qc.rest.password";

    public static final String QC_REST_DOMAIN_KEY = "qc.rest.domain";

    public static final String QC_REST_PROJECT_KEY = "qc.rest.project";
    
    public static final String QC_TEST_LOCATION="qc.test.location";
    public static final String QC_TESTSET_LOCATION="qc.testset.location";
    
    public static final String LOG_MODE="log.mode";
    public static final String LOG_PREFIX="log.prefix";    
    public static final String EXCLUDE="exclude";
	public static final String INCLUDE="include";

	private Properties properties;
	
	private static class Holder {
		private static final QCRestConfig instance = new QCRestConfig();
	}

	private QCRestConfig() {
		properties = new Properties();
		try {
			InputStream in = new FileInputStream(QC_CONFIG_FILE);
			properties.load(in);
		} catch (FileNotFoundException e) {
			log.error("QC REST properties file is not found." + QC_CONFIG_FILE, e);
		} catch (IOException e) {
			log.error("Error when reading QC REST properties file.", e);
		}
	}

	public String getQCProperty(String key) {
		return this.properties.getProperty(key);
	}
	
	public static QCRestConfig getInstance() {
		return Holder.instance;
	}
	
	public static String getQCRestURL() {
		return QCRestConfig.getInstance().getQCProperty(QC_REST_URL_KEY);
	}
	
	public static String getQCRestUsername() {
		return QCRestConfig.getInstance().getQCProperty(QC_REST_USERNAME_KEY);
	}
	
	public static String getQCRestPassword() {
		return QCRestConfig.getInstance().getQCProperty(QC_REST_PASSWORD_KEY);
	}
	
    public static String getQCRestDomain() {
        return QCRestConfig.getInstance().getQCProperty(QC_REST_DOMAIN_KEY);
    }

    public static String getQCRestProject() {
        return QCRestConfig.getInstance().getQCProperty(QC_REST_PROJECT_KEY);
    }
    
    public static String getQCTestLocation() {
        return QCRestConfig.getInstance().getQCProperty(QC_TEST_LOCATION);
    }
    
    public static String getQCTestsetLocation() {
        return QCRestConfig.getInstance().getQCProperty(QC_TESTSET_LOCATION);
    }
    
    public static String getLogMode() {
        return QCRestConfig.getInstance().getQCProperty(LOG_MODE);
    }
    
    public static String getLogPrefix() {
        return QCRestConfig.getInstance().getQCProperty(LOG_PREFIX);
    }
    
}
