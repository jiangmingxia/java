package com.hp.jmx.cmd;


import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import com.hp.jmx.cmd.LogUtil.TestInfo;
import com.hp.jmx.qc.dao.QCEntityDAO;
import com.hp.jmx.qc.dao.impl.DAOFactory;
import com.hp.jmx.qc.model.EntityObject;
import com.hp.jmx.qc.model.KeyType;
import com.hp.jmx.qc.model.QCEntity;
import com.hp.jmx.qc.rest.QCRestClient;
import com.hp.jmx.qc.rest.QCRestConfig;
import com.hp.jmx.qc.rest.entity.EntityFieldHelper;
import com.hp.jmx.qc.util.FolderUtil;
import com.hp.jmx.qc.util.RunUtil;
import com.hp.jmx.qc.util.TestSetUtil;
import com.hp.jmx.qc.util.TestUtil;


public class LogCommand implements Command {
	private static final String commandSample = "log -file <log file> -details <details file>";	 
	public static final String command_name = "log";	
	private static final String FileOption = "file";
	private static final String DetailsOption = "details";	
	private static boolean SaveToSourceTestSetWhenNotFound = true; //can be changed later by command option
	private static final QCEntityDAO entityDAO = DAOFactory.getQCEntityDAO();
	
	//since we may have several same name tests with different test set name
	//use this to avoid query test id multiple times
	private static final Map<String,String> TestNameIdMapCatch=new HashMap<String,String>() ;


	@Override
	public boolean execute(Hashtable<String,String> options) {
		if (!validateInput(options)) return false;		
		String logFileName = options.get(FileOption);
		String detailsFileName = options.get(DetailsOption);
		String runOwner = QCRestConfig.getQCRestUsername();
		Date logDate = new Date(new File(logFileName).lastModified()); //log file last modified time as default test case run time					
		LogUtil logUtil=LogUtilFactory.getLogUtil();
		
		//read run details, match all pattern like xxxx=xxx
        Map<String,String> details = logUtil.getDetails(detailsFileName);
        if (details==null) return false;
        Map<String,String> testSetDetails = convertLabelToFieldName(EntityObject.TEST_SET_TYPE,details);
        /***
         * This is done for ALM Center: map description --> comment field for run entity
         * need to remove it if not using center or there is other fields we can use for run details.
         */
        /////////////////////////BEGIN///////////////////////////////
        map2CommentsFieldforCenter(details);
        ///////////////////////// END ///////////////////////////////
        Map<String,String> runDetails = convertLabelToFieldName(EntityObject.RUN_TYPE,details);
        if (testSetDetails==null||runDetails==null) return false;
        
		//get test run result information, save it to Map: testId->test name, run result, run date, test set name
		ArrayList<TestInfo> testRuns = logUtil.getTestRunInfo(logFileName, logDate);
		if (testRuns==null) return false;		
		testRuns = covertTestNameToId(testRuns); //convert test name to test id in QC
		if (testRuns==null) return false;		
		Date maxDate=getMaxRunDate(testRuns);
		Date minDate=getMinRunDate(testRuns);
		
		//insert run result to QC
		//1.select testset match details and max/min test run time		
		//1) foreach details either = value or empty
		//2) testset start date earlier than max run date, end date either empty or later than min run date
		//3) testset is not under source folder/subfolder
		QCEntity testSetEntity = new QCEntity(EntityObject.TEST_SET_TYPE, KeyType.FIELD_NAME);
		for (String key:testSetDetails.keySet()){   //details search condition
		    String value = testSetDetails.get(key);		    
		    testSetEntity.put(key,getDetailsQuery(value));
		}
		
		String maxDateString=logUtil.QCDateFormat.format(maxDate);
		String minDateString=logUtil.QCDateFormat.format(minDate);
		testSetEntity.put(TestSetUtil.OPEN_DATE_FIELD,"<="+maxDateString);
		testSetEntity.put(TestSetUtil.CLOSE_DATE_FIELD,">="+minDateString+" or "+getRestEmptyQuery());		
		
		//Not = sourceFolderId1 AND Not = sourceFolderId2 AND Not = .....
		List<String> sourceFolders = FolderUtil.getSourceTestSetFolders();
		StringBuffer sf = new StringBuffer();
		boolean first = true;
		for (String sourceFolderId:sourceFolders){
		    if(!first) {
		        sf.append(" AND ");
		    }
		    sf.append("Not = "+sourceFolderId);
		    first=false;
		}
		testSetEntity.put("parent-id",sf.toString());        
        List<QCEntity> nonSourceTestSetEntities = entityDAO.query(testSetEntity,true);        
		
		//2.get all instances of these test set, save to testsetId->testset entity, instanceId->testsetId
		//3.get all instances' test entity, save to testId->instanceIds
        TestSetsInfo testSetsInfo = new TestSetsInfo(nonSourceTestSetEntities);
        Map<String,QCEntity> testSetMap=testSetsInfo.getTestSetMap(); //testsetId->testset entity
        Map<String,String> instanceTestSetMap=testSetsInfo.getInstanceTestSetMap(); //instanceId->testsetId
        Map<String,List<String>> testInstancesMap=testSetsInfo.getTestInstancesMap(); //testId->InstanceIds		
		
		//foreach test run: testname:status[:test set name]
		//1) according to testname, find corresponding test entity, test instanceIds, testset Ids: List<String[3]>
		//2) according to testset Id get test set entities, check if its close date, start date meet the test run date, CHECK if its name meets test set name in test run info
		//3) if find one result: insert this run		
        boolean needToCheckDate=true;
        if (maxDateString.equals(minDateString)) needToCheckDate=false;
        TestSetsInfo sourceTestSetsInfo=null;
        Map<String,QCEntity> sourceTestSetMap=null;
        Map<String,String> sourceInstanceTestSetMap=null;
        Map<String,List<String>> sourceTestInstancesMap=null;
        
		for (TestInfo testRunInfo: testRuns) {
		    String testId = testRunInfo.getTestId();
		    String runStatus=testRunInfo.getRunResult();
		    Date runDate=testRunInfo.getDate();
		    String testSetNameOfRun=testRunInfo.getTestSetName();		    
		    boolean find = false;		    
		    
		    List<String> instances = testInstancesMap.get(testId);
		    if (instances!=null) {
		        for (String instanceId:instances) {
		            String testSetId=instanceTestSetMap.get(instanceId);
		            if (!testSetNameOfRun.isEmpty()) {
		            	QCEntity testSet=testSetMap.get(testSetId);
		            	//when test set name not match, skip this instance
		            	if (!testSet.getEntityName().equals(testSetNameOfRun)){
		            		continue;
		            	}
		            	//test set name matches-->go to next check
		            }
		            if(needToCheckDate){
		                QCEntity testSet=testSetMap.get(testSetId);
		                try {
	                        if (TestSetUtil.isInTestSetPeriod(testRunInfo.getDate(),testSet)) {	                           
	                            find =true;
	                            //insert run	                            
	                            if (RunUtil.createRun(testId, instanceId, testSetId, runStatus, runOwner, runDate,runDetails) == null) {
	                                return false;
	                            }
	                        }	                        
	                    } catch (ParseException e) {
	                        CommandOutput.errorOutput("Error when parse data.");
	                        e.printStackTrace();
	                        return false;
	                    }
		            } else {
		                find = true;
		               //insert run
		                if (RunUtil.createRun(testId, instanceId, testSetId, runStatus, runOwner, runDate,runDetails) == null) {
                            return false;
                        }
		            }
		        }
		    }
		    
		    if(find==false) {
		        //when not found try to insert it into source instances
		        if (LogCommand.SaveToSourceTestSetWhenNotFound) {
		            //4.select all source test sets (Y/N)
		            //5.get all instances of these test set, save to instanceId->testsetId
		            //6.get all instances' test id: save to testId->InstanceIds
		            if (sourceTestSetsInfo==null) {		                
		                sourceTestSetsInfo = new TestSetsInfo(TestSetUtil.getAllSourceTestSets());
		                sourceTestSetMap = sourceTestSetsInfo.getTestSetMap();//testsetId->test set entity
		                sourceInstanceTestSetMap=sourceTestSetsInfo.getInstanceTestSetMap(); //instanceId->testsetId
	                    sourceTestInstancesMap=sourceTestSetsInfo.getTestInstancesMap(); //instanceId->testId
		            }
		            List<String> sourceInstances = sourceTestInstancesMap.get(testId);
		            if (sourceInstances==null) {
		                CommandOutput.errorOutput("Cannot find instance for test with id "+testId);
		                return false;
		            }
		            //just insert it into the first matched source instances
		            for (String instanceId: sourceInstances) {
		            	String testSetId=sourceInstanceTestSetMap.get(instanceId);
		            	if (!testSetNameOfRun.isEmpty()) {
		            		QCEntity testSet=sourceTestSetMap.get(testSetId);
		            		if (!testSet.getEntityName().equals(testSetNameOfRun)) {
		            			continue;
		            		}
		            	}
		            	if (RunUtil.createRun(testId, instanceId, testSetId, runStatus, runOwner, runDate,runDetails) == null) {
	                        return false;
	                    } else {
	                    	break;
	                    }		            	
		            }		            
		        }
		    }		    
		}
		
		//logout
		 try {	        	
			QCRestClient.getInstance().logoutSession(QCRestConfig.getQCRestURL());
		} catch (IOException e) {}
		return true;
	}
	
    private Map<String, String> convertLabelToFieldName(String entityType, Map<String, String> details) {
        Map<String,String> fieldNameMap = new HashMap<String,String>();
        for (String key:details.keySet()){   //details search condition
            String fieldName = EntityFieldHelper.getFieldNameByLabel(entityType,key);
            if (fieldName==null) {
                CommandOutput.errorOutput("Cannot find field "+key+" for entity "+entityType);
                return null;
            }            
            fieldNameMap.put(fieldName, "\""+details.get(key)+"\"");
        }
        return fieldNameMap;
    }
    
    /**
	 * convert Map key from test name to test ID
	 * @param nameRunInfo
	 * @return if test name not found in ALM, return null
	 */
	private ArrayList<TestInfo> covertTestNameToId(ArrayList<TestInfo> runInfo) {		
	    for (TestInfo ti:runInfo) { 
	    	String testName = ti.getName();
	    	if (TestNameIdMapCatch.containsKey(testName)) {
	    		ti.setTestId(TestNameIdMapCatch.get(testName));	    		
	    	} else {
	    		QCEntity test = TestUtil.getTestByName(testName);
	            if (test==null) {
	                CommandOutput.errorOutput("Test "+testName+" is not found in ALM. Please check.");
	                return null;
	            }
	            ti.setTestId(test.getEntityId());
	            TestNameIdMapCatch.put(testName, test.getEntityId());
	    	}            
	    }
        return runInfo;
    }
	
    private boolean validateInput(Hashtable<String,String> options)	{
		if (options.get(DetailsOption)!=null&&options.get(FileOption)!=null) {
			if (checkFileExists(options.get(FileOption))&&checkFileExists(options.get(DetailsOption))) return true;
			return false;			
		}
		
		CommandOutput.errorOutput("The command should be like:");
		CommandOutput.errorOutput("		"+commandSample);
		return false;
	}
	
	private boolean checkFileExists(String path){		
		File file = new File(path);
		if (!file.exists()){
			CommandOutput.errorOutput("File does not exists:"+path);
			return false;
		}
		if(!file.isFile()){
			CommandOutput.errorOutput("It is not a file: "+path);
			return false;
		}
		return true;
	}
	
	//either empty or same as detail's valule
	private String getDetailsQuery(String detail) {
	    return detail+" or "+getRestEmptyQuery();
	}
	
	private String getRestEmptyQuery(){
	    return "\"\"";
	}
   
    /**
     * get the latest run date
     * @param testInfoCollection
     * @return
     */
    private Date getMaxRunDate(Collection<TestInfo> testInfoCollection){
        Date maxDate=null;
        for (TestInfo ti: testInfoCollection) {
            Date currentDate=ti.getDate();
            if (maxDate == null) {
                maxDate=currentDate;
            } else {
                if (currentDate.after(maxDate)) {
                    maxDate=currentDate;
                }
            }
        }
        return maxDate;
    }
    
    /**
     * get the earliest date of run date
     * @param testInfoSet
     * @return
     */
    private Date getMinRunDate(Collection<TestInfo> testInfoCollection){
        Date minDate=null;
        for (TestInfo ti: testInfoCollection) {
            Date currentDate=ti.getDate();
            if (minDate == null) {
                minDate=currentDate;
            } else {
                if (currentDate.before(minDate)) {
                    minDate=currentDate;
                }
            }
        }
        return minDate;        
    }
    
    /***
     * This is done for ALM Center: map Security Environment --> comment field for run entity
     * need to remove it if not using center or there is other fields we can use for run details.
     */
    private Map<String,String> map2CommentsFieldforCenter(Map<String,String> details) {
    	for (String key: details.keySet()) {
    		if (key.equals("Security Environment")) {
    			String value = details.get(key);
    			details.remove(key);
    			details.put("Comments",value);
    		}
    	}
    	return details;
    }
}
