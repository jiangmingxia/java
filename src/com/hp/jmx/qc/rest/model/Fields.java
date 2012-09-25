//
// 此文件是由 JavaTM Architecture for XML Binding (JAXB) 引用实现 v2.2.6 生成的
// 请访问 <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// 在重新编译源模式时, 对此文件的所有修改都将丢失。
// 生成时间: 2012.09.19 时间 08:52:02 PM CST 
//


package com.hp.jmx.qc.rest.model;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>anonymous complex type的 Java 类。
 * 
 * <p>以下模式片段指定包含在此类中的预期内容。
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Field" maxOccurs="unbounded">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="Size" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *                   &lt;element name="History" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *                   &lt;element name="List-Id" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *                   &lt;element name="Required" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *                   &lt;element name="System" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *                   &lt;element name="Type" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="Verify" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *                   &lt;element name="Virtual" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *                   &lt;element name="Active" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *                   &lt;element name="Editable" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *                   &lt;element name="Filterable" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *                   &lt;element name="Groupable" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *                   &lt;element name="SupportsMultivalue" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *                 &lt;/sequence>
 *                 &lt;attribute name="Label" use="required" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *                 &lt;attribute name="Name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="PhysicalName" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "field"
})
@XmlRootElement(name = "Fields")
public class Fields {

    @XmlElement(name = "Field", required = true)
    protected List<Fields.Field> field;

    /**
     * Gets the value of the field property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the field property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getField().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Fields.Field }
     * 
     * 
     */
    public List<Fields.Field> getField() {
        if (field == null) {
            field = new ArrayList<Fields.Field>();
        }
        return this.field;
    }


    /**
     * <p>anonymous complex type的 Java 类。
     * 
     * <p>以下模式片段指定包含在此类中的预期内容。
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="Size" type="{http://www.w3.org/2001/XMLSchema}integer"/>
     *         &lt;element name="History" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
     *         &lt;element name="List-Id" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
     *         &lt;element name="Required" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
     *         &lt;element name="System" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
     *         &lt;element name="Type" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="Verify" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
     *         &lt;element name="Virtual" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
     *         &lt;element name="Active" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
     *         &lt;element name="Editable" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
     *         &lt;element name="Filterable" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
     *         &lt;element name="Groupable" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
     *         &lt;element name="SupportsMultivalue" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
     *       &lt;/sequence>
     *       &lt;attribute name="Label" use="required" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
     *       &lt;attribute name="Name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="PhysicalName" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "size",
        "history",
        "listId",
        "required",
        "system",
        "type",
        "verify",
        "virtual",
        "active",
        "editable",
        "filterable",
        "groupable",
        "supportsMultivalue"
    })
    public static class Field {

        @XmlElement(name = "Size", required = true)
        protected BigInteger size;
        @XmlElement(name = "History")
        protected boolean history;
        @XmlElement(name = "List-Id")
        protected BigInteger listId;
        @XmlElement(name = "Required")
        protected boolean required;
        @XmlElement(name = "System")
        protected boolean system;
        @XmlElement(name = "Type", required = true)
        protected String type;
        @XmlElement(name = "Verify")
        protected boolean verify;
        @XmlElement(name = "Virtual")
        protected boolean virtual;
        @XmlElement(name = "Active")
        protected boolean active;
        @XmlElement(name = "Editable")
        protected boolean editable;
        @XmlElement(name = "Filterable")
        protected boolean filterable;
        @XmlElement(name = "Groupable")
        protected boolean groupable;
        @XmlElement(name = "SupportsMultivalue")
        protected boolean supportsMultivalue;
        @XmlAttribute(name = "Label", required = true)
        @XmlSchemaType(name = "anySimpleType")
        protected String label;
        @XmlAttribute(name = "Name", required = true)
        protected String name;
        @XmlAttribute(name = "PhysicalName", required = true)
        protected String physicalName;

        /**
         * 获取size属性的值。
         * 
         * @return
         *     possible object is
         *     {@link BigInteger }
         *     
         */
        public BigInteger getSize() {
            return size;
        }

        /**
         * 设置size属性的值。
         * 
         * @param value
         *     allowed object is
         *     {@link BigInteger }
         *     
         */
        public void setSize(BigInteger value) {
            this.size = value;
        }

        /**
         * 获取history属性的值。
         * 
         */
        public boolean isHistory() {
            return history;
        }

        /**
         * 设置history属性的值。
         * 
         */
        public void setHistory(boolean value) {
            this.history = value;
        }

        /**
         * 获取listId属性的值。
         * 
         * @return
         *     possible object is
         *     {@link BigInteger }
         *     
         */
        public BigInteger getListId() {
            return listId;
        }

        /**
         * 设置listId属性的值。
         * 
         * @param value
         *     allowed object is
         *     {@link BigInteger }
         *     
         */
        public void setListId(BigInteger value) {
            this.listId = value;
        }

        /**
         * 获取required属性的值。
         * 
         */
        public boolean isRequired() {
            return required;
        }

        /**
         * 设置required属性的值。
         * 
         */
        public void setRequired(boolean value) {
            this.required = value;
        }

        /**
         * 获取system属性的值。
         * 
         */
        public boolean isSystem() {
            return system;
        }

        /**
         * 设置system属性的值。
         * 
         */
        public void setSystem(boolean value) {
            this.system = value;
        }

        /**
         * 获取type属性的值。
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getType() {
            return type;
        }

        /**
         * 设置type属性的值。
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setType(String value) {
            this.type = value;
        }

        /**
         * 获取verify属性的值。
         * 
         */
        public boolean isVerify() {
            return verify;
        }

        /**
         * 设置verify属性的值。
         * 
         */
        public void setVerify(boolean value) {
            this.verify = value;
        }

        /**
         * 获取virtual属性的值。
         * 
         */
        public boolean isVirtual() {
            return virtual;
        }

        /**
         * 设置virtual属性的值。
         * 
         */
        public void setVirtual(boolean value) {
            this.virtual = value;
        }

        /**
         * 获取active属性的值。
         * 
         */
        public boolean isActive() {
            return active;
        }

        /**
         * 设置active属性的值。
         * 
         */
        public void setActive(boolean value) {
            this.active = value;
        }

        /**
         * 获取editable属性的值。
         * 
         */
        public boolean isEditable() {
            return editable;
        }

        /**
         * 设置editable属性的值。
         * 
         */
        public void setEditable(boolean value) {
            this.editable = value;
        }

        /**
         * 获取filterable属性的值。
         * 
         */
        public boolean isFilterable() {
            return filterable;
        }

        /**
         * 设置filterable属性的值。
         * 
         */
        public void setFilterable(boolean value) {
            this.filterable = value;
        }

        /**
         * 获取groupable属性的值。
         * 
         */
        public boolean isGroupable() {
            return groupable;
        }

        /**
         * 设置groupable属性的值。
         * 
         */
        public void setGroupable(boolean value) {
            this.groupable = value;
        }

        /**
         * 获取supportsMultivalue属性的值。
         * 
         */
        public boolean isSupportsMultivalue() {
            return supportsMultivalue;
        }

        /**
         * 设置supportsMultivalue属性的值。
         * 
         */
        public void setSupportsMultivalue(boolean value) {
            this.supportsMultivalue = value;
        }

        /**
         * 获取label属性的值。
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getLabel() {
            return label;
        }

        /**
         * 设置label属性的值。
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setLabel(String value) {
            this.label = value;
        }

        /**
         * 获取name属性的值。
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getName() {
            return name;
        }

        /**
         * 设置name属性的值。
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setName(String value) {
            this.name = value;
        }

        /**
         * 获取physicalName属性的值。
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getPhysicalName() {
            return physicalName;
        }

        /**
         * 设置physicalName属性的值。
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setPhysicalName(String value) {
            this.physicalName = value;
        }

    }

}
