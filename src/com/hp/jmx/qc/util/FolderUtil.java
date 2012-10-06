package com.hp.jmx.qc.util;

import java.util.ArrayList;
import java.util.List;

import com.hp.jmx.qc.dao.QCEntityDAO;
import com.hp.jmx.qc.dao.impl.DAOFactory;
import com.hp.jmx.qc.model.EntityObject;
import com.hp.jmx.qc.model.KeyType;
import com.hp.jmx.qc.model.QCEntity;
import com.hp.jmx.qc.rest.QCRestConfig;

public class FolderUtil {
	private static final QCEntityDAO entityDAO = DAOFactory.getQCEntityDAO();
	
	private static final String TestRootFolder = "Subject";
	private static final String TestRootParentId = "0";
	private static String TestRootFolderId;
	private static String TestSourceFolderId; //automation test folder id
		
	private static final String TestSetRootFolderId = "0";
	private static String TestSetSourceFolderId; //automation source test set folder id
	
	private static List<String> sourceTestSetFolders;
	private static List<String> sourceTestFolders;
	
	
	/**
	 * return folder entity according to its path. 
	 * eg., path=a/b/c, will return the folder c of which path is Subject/a/b/c in ALM
	 * @param path
	 * @return null if not exists, folder entity
	 */
	public static QCEntity getTestFolderByPath(String path){
		String[] folders = path.split("/");
		String parentId = getTestRootFolderId();
		if (parentId == null) return null;
		QCEntity currentFolder=null;
		for (String folder:folders){			
			currentFolder = getTestFolder(folder,parentId);
			if (currentFolder==null) return null;
			parentId = currentFolder.getEntityId();
		}			
		return currentFolder;
	}
	
	public static QCEntity getTestSetFolderByPath(String path){
		String[] folders = path.split("/");
		String parentId = getTestSetRootFolderId();
		if (parentId == null) return null;
		QCEntity currentFolder=null;
		for (String folder:folders){			
			currentFolder = getTestSetFolder(folder,parentId);
			if (currentFolder==null) return null;
			parentId = currentFolder.getEntityId();
		}			
		return currentFolder;
	}
	
	public static QCEntity createTestFolderByPath(String path){
		String[] folders = path.split("/");
		String parentId = getTestRootFolderId();
		if (parentId == null) return null;
		boolean created = false;
		QCEntity currentFolder=null;
		for (String folder:folders){
			if (created == false){
				currentFolder = getTestFolder(folder,parentId);
				if (currentFolder==null) {
					currentFolder = createTestFolder(folder,parentId);
					created = true;
				}
			} else {
				//Ancestor folder was created so this folder must be created
				currentFolder = createTestFolder(folder,parentId);
				created = true;
			}
				
			if (currentFolder==null) return null;
			parentId=currentFolder.getEntityId();
		}			
		return currentFolder;
	}
	
	public static QCEntity createTestSetFolderByPath(String path){
		String[] folders = path.split("/");
		String parentId = getTestSetRootFolderId();
		if (parentId == null) return null;
		boolean created = false;
		QCEntity currentFolder=null;
		for (String folder:folders){
			if (created == false){
				currentFolder = getTestSetFolder(folder,parentId);
				if (currentFolder==null) {
					currentFolder = createTestSetFolder(folder,parentId);
					created = true;
				}
			} else {
				//Ancestor folder was created so this folder must be created
				currentFolder = createTestSetFolder(folder,parentId);
				created = true;
			}
				
			if (currentFolder==null) return null;
			parentId=currentFolder.getEntityId();
		}			
		return currentFolder;
	}
	
	//return true if folder is ancestor of the test set
	public static boolean isTestSetFolderAncestor(String folderId, String testSetId){
		QCEntity testSetEntity = TestSetUtil.getTestSetById(testSetId);
		String parentId= testSetEntity.getEntityParentId();
		QCEntity testSetFolderEntity;
		while (!parentId.equals(folderId)&&!parentId.equals(getTestSetRootFolderId())) {
			testSetFolderEntity = FolderUtil.getTestSetFolderById(parentId);
			parentId = testSetFolderEntity.getEntityParentId();
		}
		
		if (parentId.equals(folderId)) return true;		
		return false;
	}	
	
	//return true if folder is ancestor of the test 
    public static boolean isTestFolderAncestor(String folderId, String testId){
        QCEntity testEntity = TestUtil.getTestById(testId);
        String parentId= testEntity.getEntityParentId();
        QCEntity testFolderEntity;
        while (!parentId.equals(folderId)&&!parentId.equals(getTestRootFolderId())) {
            testFolderEntity = FolderUtil.getTestFolderById(parentId);
            parentId = testFolderEntity.getEntityParentId();
        }
        
        if (parentId.equals(folderId)) return true;     
        return false;
    }   
	
	public static QCEntity getTestFolderById(String id){
		return EntityUtil.getEntityById(EntityObject.TEST_FOLDER_TYPE, id);
	}
	
	public static QCEntity getTestSetFolderById(String id){
		return EntityUtil.getEntityById(EntityObject.TEST_SET_FOLDER_TYPE, id);
	}
	
	//return root folder id
	private static String getTestRootFolderId(){
		if (TestRootFolderId==null){			
			TestRootFolderId = getTestFolder(TestRootFolder,TestRootParentId).getEntityId();
		}
		return TestRootFolderId;			
	}
	
	private static String getTestSetRootFolderId(){
		return 	TestSetRootFolderId;	
	}
	
	//return Folder entity according to folder name and its parent Id.
	private static QCEntity getFolder(String name, String parentId,String entityType){
		QCEntity entity = new QCEntity(entityType, KeyType.FIELD_NAME);
		entity.put("name", name);   
		entity.put("parent-id", parentId);
        List<QCEntity> entities = entityDAO.query(entity);
        if (entities.size()<1){        	
        	return null;
        }
        return entities.get(0);
	}
	
	private static QCEntity getTestFolder(String name, String parentId){
		return getFolder(name,parentId,EntityObject.TEST_FOLDER_TYPE);		
	}
	
	private static QCEntity getTestSetFolder(String name, String parentId){
		return getFolder(name,parentId,EntityObject.TEST_SET_FOLDER_TYPE);		
	}
	
	private static QCEntity createFolder(String name, String parentId, String entityType){
		QCEntity entity = new QCEntity(entityType, KeyType.FIELD_NAME);
		entity.put("name", name);   
		entity.put("parent-id", parentId);
		entityDAO.save(entity);
        return entity;
	}
	
	private static QCEntity createTestFolder(String name, String parentId){
		return createFolder(name,parentId,EntityObject.TEST_FOLDER_TYPE);
	}
	
	private static QCEntity createTestSetFolder(String name, String parentId){
		return createFolder(name,parentId,EntityObject.TEST_SET_FOLDER_TYPE);
	}
	
	public static String getTestSourceFolderId(){
		if (TestSourceFolderId == null){
			String path = QCRestConfig.getQCTestLocation();
			QCEntity entity = createTestFolderByPath(path);
			if (entity==null) {
				System.out.println("Fail to get automation test folder: "+path);
				return null;
			}
			TestSourceFolderId = entity.getEntityId();
		}
		return TestSourceFolderId;
	}
	
	public static String getTestSetSourceFolderId(){
		if (TestSetSourceFolderId == null){
			String path = QCRestConfig.getQCTestsetLocation();
			QCEntity entity = createTestSetFolderByPath(path);
			if (entity==null) {
				System.out.println("Fail to get automation test set folder: "+path);
				return null;
			}
			TestSetSourceFolderId = entity.getEntityId();
		}
		return TestSetSourceFolderId;
	}
	
	//return all folders Id under given folder, the first element is the given folder id
	private static List<String> getAllSubFolderIds(String entityType, String root){
	    List<String> folderIds = new ArrayList<String>();
	    folderIds.add(root);
	    
	    //sub folder Ids
	    QCEntity entity = new QCEntity(entityType, KeyType.FIELD_NAME);          
        entity.put("parent-id", root);
        List<QCEntity> entities = entityDAO.query(entity);
        if (entities.size()==0){
            return folderIds;
        }
        for (QCEntity subFolder: entities) {
            List<String> subFolderIds = getAllSubFolderIds(entityType,subFolder.getEntityId());
            for (String id: subFolderIds){
                folderIds.add(id);
            }
        }
        
        return folderIds;
	}
	
	//get all source test set folder Ids
	public static List<String> getSourceTestSetFolders() {
	    if (sourceTestSetFolders ==null){
	        sourceTestSetFolders = getAllSubFolderIds(EntityObject.TEST_SET_FOLDER_TYPE,getTestSetSourceFolderId());
	    } 
	    
	    return sourceTestSetFolders;
	}
	
	//get all source test folder Ids
    public static List<String> getSourceTestFolders() {
        if (sourceTestFolders ==null){
            sourceTestFolders = getAllSubFolderIds(EntityObject.TEST_FOLDER_TYPE,getTestSourceFolderId());
        } 
        
        return sourceTestFolders;
    }

}
