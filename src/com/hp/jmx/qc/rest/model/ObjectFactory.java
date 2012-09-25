//
// ���ļ����� JavaTM Architecture for XML Binding (JAXB) ����ʵ�� v2.2.6 ���ɵ�
// ����� <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// �����±���Դģʽʱ, �Դ��ļ��������޸Ķ�����ʧ��
// ����ʱ��: 2012.09.19 ʱ�� 08:52:02 PM CST 
//


package com.hp.jmx.qc.rest.model;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.hp.jmx.qc.rest.model package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.hp.jmx.qc.rest.model
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Entities }
     * 
     */
    public Entities createEntities() {
        return new Entities();
    }

    /**
     * Create an instance of {@link com.hp.jmx.qc.rest.model.Fields }
     * 
     */
    public com.hp.jmx.qc.rest.model.Fields createFields() {
        return new com.hp.jmx.qc.rest.model.Fields();
    }

    /**
     * Create an instance of {@link QCRestException }
     * 
     */
    public QCRestException createQCRestException() {
        return new QCRestException();
    }

    /**
     * Create an instance of {@link Types }
     * 
     */
    public Types createTypes() {
        return new Types();
    }

    /**
     * Create an instance of {@link com.hp.jmx.qc.rest.model.Entity }
     * 
     */
    public com.hp.jmx.qc.rest.model.Entity createEntity() {
        return new com.hp.jmx.qc.rest.model.Entity();
    }

    /**
     * Create an instance of {@link com.hp.jmx.qc.rest.model.Entity.Fields }
     * 
     */
    public com.hp.jmx.qc.rest.model.Entity.Fields createEntityFields() {
        return new com.hp.jmx.qc.rest.model.Entity.Fields();
    }

    /**
     * Create an instance of {@link com.hp.jmx.qc.rest.model.Entity.Fields.Field }
     * 
     */
    public com.hp.jmx.qc.rest.model.Entity.Fields.Field createEntityFieldsField() {
        return new com.hp.jmx.qc.rest.model.Entity.Fields.Field();
    }

    /**
     * Create an instance of {@link QCRestException.ExceptionProperties }
     * 
     */
    public QCRestException.ExceptionProperties createQCRestExceptionExceptionProperties() {
        return new QCRestException.ExceptionProperties();
    }

    /**
     * Create an instance of {@link Entities.Entity }
     * 
     */
    public Entities.Entity createEntitiesEntity() {
        return new Entities.Entity();
    }

    /**
     * Create an instance of {@link Entities.Entity.Fields }
     * 
     */
    public Entities.Entity.Fields createEntitiesEntityFields() {
        return new Entities.Entity.Fields();
    }

    /**
     * Create an instance of {@link Entities.Entity.Fields.Field }
     * 
     */
    public Entities.Entity.Fields.Field createEntitiesEntityFieldsField() {
        return new Entities.Entity.Fields.Field();
    }

    /**
     * Create an instance of {@link com.hp.jmx.qc.rest.model.Fields.Field }
     * 
     */
    public com.hp.jmx.qc.rest.model.Fields.Field createFieldsField() {
        return new com.hp.jmx.qc.rest.model.Fields.Field();
    }

    /**
     * Create an instance of {@link Types.Type }
     * 
     */
    public Types.Type createTypesType() {
        return new Types.Type();
    }

    /**
     * Create an instance of {@link com.hp.jmx.qc.rest.model.Entity.Fields.Field.Value }
     * 
     */
    public com.hp.jmx.qc.rest.model.Entity.Fields.Field.Value createEntityFieldsFieldValue() {
        return new com.hp.jmx.qc.rest.model.Entity.Fields.Field.Value();
    }

    /**
     * Create an instance of {@link QCRestException.ExceptionProperties.ExceptionProperty }
     * 
     */
    public QCRestException.ExceptionProperties.ExceptionProperty createQCRestExceptionExceptionPropertiesExceptionProperty() {
        return new QCRestException.ExceptionProperties.ExceptionProperty();
    }

    /**
     * Create an instance of {@link Entities.Entity.Fields.Field.Value }
     * 
     */
    public Entities.Entity.Fields.Field.Value createEntitiesEntityFieldsFieldValue() {
        return new Entities.Entity.Fields.Field.Value();
    }

}
