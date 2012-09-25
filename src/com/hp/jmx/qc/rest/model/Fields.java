//
// ���ļ����� JavaTM Architecture for XML Binding (JAXB) ����ʵ�� v2.2.6 ���ɵ�
// ����� <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// �����±���Դģʽʱ, �Դ��ļ��������޸Ķ�����ʧ��
// ����ʱ��: 2012.09.19 ʱ�� 08:52:02 PM CST 
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
 * <p>anonymous complex type�� Java �ࡣ
 * 
 * <p>����ģʽƬ��ָ�������ڴ����е�Ԥ�����ݡ�
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
     * <p>anonymous complex type�� Java �ࡣ
     * 
     * <p>����ģʽƬ��ָ�������ڴ����е�Ԥ�����ݡ�
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
         * ��ȡsize���Ե�ֵ��
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
         * ����size���Ե�ֵ��
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
         * ��ȡhistory���Ե�ֵ��
         * 
         */
        public boolean isHistory() {
            return history;
        }

        /**
         * ����history���Ե�ֵ��
         * 
         */
        public void setHistory(boolean value) {
            this.history = value;
        }

        /**
         * ��ȡlistId���Ե�ֵ��
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
         * ����listId���Ե�ֵ��
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
         * ��ȡrequired���Ե�ֵ��
         * 
         */
        public boolean isRequired() {
            return required;
        }

        /**
         * ����required���Ե�ֵ��
         * 
         */
        public void setRequired(boolean value) {
            this.required = value;
        }

        /**
         * ��ȡsystem���Ե�ֵ��
         * 
         */
        public boolean isSystem() {
            return system;
        }

        /**
         * ����system���Ե�ֵ��
         * 
         */
        public void setSystem(boolean value) {
            this.system = value;
        }

        /**
         * ��ȡtype���Ե�ֵ��
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
         * ����type���Ե�ֵ��
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
         * ��ȡverify���Ե�ֵ��
         * 
         */
        public boolean isVerify() {
            return verify;
        }

        /**
         * ����verify���Ե�ֵ��
         * 
         */
        public void setVerify(boolean value) {
            this.verify = value;
        }

        /**
         * ��ȡvirtual���Ե�ֵ��
         * 
         */
        public boolean isVirtual() {
            return virtual;
        }

        /**
         * ����virtual���Ե�ֵ��
         * 
         */
        public void setVirtual(boolean value) {
            this.virtual = value;
        }

        /**
         * ��ȡactive���Ե�ֵ��
         * 
         */
        public boolean isActive() {
            return active;
        }

        /**
         * ����active���Ե�ֵ��
         * 
         */
        public void setActive(boolean value) {
            this.active = value;
        }

        /**
         * ��ȡeditable���Ե�ֵ��
         * 
         */
        public boolean isEditable() {
            return editable;
        }

        /**
         * ����editable���Ե�ֵ��
         * 
         */
        public void setEditable(boolean value) {
            this.editable = value;
        }

        /**
         * ��ȡfilterable���Ե�ֵ��
         * 
         */
        public boolean isFilterable() {
            return filterable;
        }

        /**
         * ����filterable���Ե�ֵ��
         * 
         */
        public void setFilterable(boolean value) {
            this.filterable = value;
        }

        /**
         * ��ȡgroupable���Ե�ֵ��
         * 
         */
        public boolean isGroupable() {
            return groupable;
        }

        /**
         * ����groupable���Ե�ֵ��
         * 
         */
        public void setGroupable(boolean value) {
            this.groupable = value;
        }

        /**
         * ��ȡsupportsMultivalue���Ե�ֵ��
         * 
         */
        public boolean isSupportsMultivalue() {
            return supportsMultivalue;
        }

        /**
         * ����supportsMultivalue���Ե�ֵ��
         * 
         */
        public void setSupportsMultivalue(boolean value) {
            this.supportsMultivalue = value;
        }

        /**
         * ��ȡlabel���Ե�ֵ��
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
         * ����label���Ե�ֵ��
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
         * ��ȡname���Ե�ֵ��
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
         * ����name���Ե�ֵ��
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
         * ��ȡphysicalName���Ե�ֵ��
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
         * ����physicalName���Ե�ֵ��
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
