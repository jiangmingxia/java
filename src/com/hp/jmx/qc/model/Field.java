package com.hp.jmx.qc.model;

import java.math.BigInteger;

public class Field {
    
    private int size;
    private boolean history;
    private BigInteger listId;
    private boolean required;
    private boolean system;
    private String type;
    private boolean verify;
    private boolean virtual;
    private boolean active;
    private boolean editable;
    private boolean filterable;
    private boolean groupable;
    private boolean supportsMultivalue;
    private String label;
    private String name;
    private String physicalName;
    
    public int getSize() {
        return size;
    }
    public void setSize(int size) {
        this.size = size;
    }
    public boolean isHistory() {
        return history;
    }
    public void setHistory(boolean history) {
        this.history = history;
    }
    public BigInteger getListId() {
        return listId;
    }
    public void setListId(BigInteger listId) {
        this.listId = listId;
    }
    public boolean isRequired() {
        return required;
    }
    public void setRequired(boolean required) {
        this.required = required;
    }
    public boolean isSystem() {
        return system;
    }
    public void setSystem(boolean system) {
        this.system = system;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public boolean isVerify() {
        return verify;
    }
    public void setVerify(boolean verify) {
        this.verify = verify;
    }
    public boolean isVirtual() {
        return virtual;
    }
    public void setVirtual(boolean virtual) {
        this.virtual = virtual;
    }
    public boolean isActive() {
        return active;
    }
    public void setActive(boolean active) {
        this.active = active;
    }
    public boolean isEditable() {
        return editable;
    }
    public void setEditable(boolean editable) {
        this.editable = editable;
    }
    public boolean isFilterable() {
        return filterable;
    }
    public void setFilterable(boolean filterable) {
        this.filterable = filterable;
    }
    public boolean isGroupable() {
        return groupable;
    }
    public void setGroupable(boolean groupable) {
        this.groupable = groupable;
    }
    public boolean isSupportsMultivalue() {
        return supportsMultivalue;
    }
    public void setSupportsMultivalue(boolean supportsMultivalue) {
        this.supportsMultivalue = supportsMultivalue;
    }
    public String getLabel() {
        return label;
    }
    public void setLabel(String label) {
        this.label = label;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getPhysicalName() {
        return physicalName;
    }
    public void setPhysicalName(String physicalName) {
        this.physicalName = physicalName;
    }

}
