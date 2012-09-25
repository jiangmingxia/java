package com.hp.jmx.qc.model;

import com.hp.jmx.qc.annotation.EntityField;
import com.hp.jmx.qc.annotation.EntityType;

@EntityType("test")
public class Test implements EntityObject{

	@EntityField("id")
	private long id;
	
	@EntityField("name")
	private String name;

	@EntityField("parent-id")
	private long parentId;
	
	@EntityField("subtype-id")
	private String subTypeId;
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public String getSubTypeId() {
        return subTypeId;
    }

    public void setSubTypeId(String subTypeId) {
        this.subTypeId = subTypeId;
    }
	
	
}
