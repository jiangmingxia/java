package com.hp.jmx.qc.rest;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QCValueHelper {

    private static final Logger log = LoggerFactory.getLogger(QCValueHelper.class);
    
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");

    private static final String INT_TYPE = "int";

    private static final String LONG_TYPE = "long";
    
    public static String invokeGetterMethod(Method method, Object o) {
        try {
            Object value = method.invoke(o);
            if (value == null)
                return null;
            
            if (value.getClass() == Date.class) {
                Date date = (Date) value;
                return dateFormat.format(date);
            } else if (value.getClass() == Integer.class || value.getClass() == Long.class) {
                if ("0".equals(value.toString()))
                    return null;
            }
            
            return value.toString();
        } catch (Exception e) {
            logAndException("Error when invoking the getter method." + method.getName(), e);
        }
        return null;
    }
    
    @SuppressWarnings("unchecked")
    public static void invokeSetterMethod(Method method, Object o, String valueString) {
        if (valueString == null || valueString.length() == 0)
            return;
        
        Class[] parameterTypes = method.getParameterTypes();
        if (parameterTypes == null || parameterTypes.length != 1)
            logAndException("The setter method should only have one parameter. " + method.getName());
        
        Class parameterType = parameterTypes[0];
        
        try {
            if (INT_TYPE.equals(parameterType.getName())) {
                int value = Integer.parseInt(valueString);
                method.invoke(o, value);
            } else if (LONG_TYPE.equals(parameterType.getName())) {
                long value = Long.parseLong(valueString);
                method.invoke(o, value);
            } else if (Date.class == parameterType) {
                Date value = dateFormat.parse(valueString);
                method.invoke(o, value);
            } else if (String.class == parameterType)
                method.invoke(o, valueString);
            else
                logAndException("Unsupported parameter type: " + parameterType + " for method: " + method.getName());
        } catch (Exception e) {
            logAndException("Error happedn when calling the setter method: " + method.getName(), e);
        }
    }

    private static void logAndException(String message) {
        log.error(message);
        throw new RuntimeException(message);
    }

    private static void logAndException(String message, Exception e) {
        log.error(message, e);
        throw new RuntimeException(message, e);
    }

}
