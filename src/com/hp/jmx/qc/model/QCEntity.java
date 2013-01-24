package com.hp.jmx.qc.model;

import java.util.HashMap;
import java.util.Map;

import com.hp.jmx.qc.rest.entity.EntityFieldHelper;

public class QCEntity extends HashMap<String, String>{
    
    public static final String ID_NAME = "id";
    
    public static final String NAME_NAME="name";
    
    public static final String PARENTID_NAME="parent-id";

    private static final long serialVersionUID = 945435541967440914L;

    private String entityType;
    
    private KeyType keyType;

    public QCEntity(String entityType, KeyType keyType) {
        this.entityType = entityType;
        this.keyType = keyType;
    }
    
    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public KeyType getKeyType() {
        return keyType;
    }

    public void setKeyType(KeyType keyType) {
        this.keyType = keyType;
    }
    
    public String getEntityId() {
        if (this.getKeyType() == KeyType.FIELD_NAME)
            return this.get(QCEntity.ID_NAME);
        Map<String, Field> nameFieldMap = EntityFieldHelper.getNameFieldMap(this.getEntityType());
        Field idField = nameFieldMap.get(QCEntity.ID_NAME);
        if (idField != null) {
            String key = null;
            if (this.getKeyType() == KeyType.FIELD_LABEL)
                key = idField.getLabel();
            else if (this.getKeyType() == KeyType.FIELD_PHISICAL_NAME)
                key = idField.getPhysicalName();
            return this.get(key);
        }
        return null;
    }
    
    public String getEntityParentId() {
        if (this.getKeyType() == KeyType.FIELD_NAME)
            return this.get(QCEntity.PARENTID_NAME);
        Map<String, Field> nameFieldMap = EntityFieldHelper.getNameFieldMap(this.getEntityType());
        Field parentIdField = nameFieldMap.get(QCEntity.PARENTID_NAME);
        if (parentIdField != null) {
            String key = null;
            if (this.getKeyType() == KeyType.FIELD_LABEL)
                key = parentIdField.getLabel();
            else if (this.getKeyType() == KeyType.FIELD_PHISICAL_NAME)
                key = parentIdField.getPhysicalName();
            return this.get(key);
        }
        return null;
    }
    
    public String getEntityName() {
        if (this.getKeyType() == KeyType.FIELD_NAME)
            return this.get(QCEntity.NAME_NAME);
        Map<String, Field> nameFieldMap = EntityFieldHelper.getNameFieldMap(this.getEntityType());
        Field nameField = nameFieldMap.get(QCEntity.NAME_NAME);
        if (nameField != null) {
            String key = null;
            if (this.getKeyType() == KeyType.FIELD_LABEL)
                key = nameField.getLabel();
            else if (this.getKeyType() == KeyType.FIELD_PHISICAL_NAME)
                key = nameField.getPhysicalName();
            return this.get(key);
        }
        return null;
    }
        
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Entity (" + this.entityType + ") Key Type : " + this.keyType);
        
        for (String key : this.keySet()) {
            sb.append("\n\t" + key + ": " + this.get(key));
        }
        
        return sb.toString();
    }
}
