//
// 此文件是由 JavaTM Architecture for XML Binding (JAXB) 引用实现 v2.2.6 生成的
// 请访问 <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// 在重新编译源模式时, 对此文件的所有修改都将丢失。
// 生成时间: 2012.09.17 时间 02:30:55 PM CST 
//


package com.hp.jmx.qc.model;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.hp.jmx.qc.model package. 
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
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.hp.jmx.qc.model
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Users }
     * 
     */
    public Users createUsers() {
        return new Users();
    }

    /**
     * Create an instance of {@link Users.User }
     * 
     */
    public Users.User createUsersUser() {
        return new Users.User();
    }

    /**
     * Create an instance of {@link Users.User.Email }
     * 
     */
    public Users.User.Email createUsersUserEmail() {
        return new Users.User.Email();
    }

    /**
     * Create an instance of {@link Users.User.Phone }
     * 
     */
    public Users.User.Phone createUsersUserPhone() {
        return new Users.User.Phone();
    }

}
