package test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class TestReflection {

    private long id;
    
    public long getID() {
        return this.id;
    }
    
    public void setID(long id) {
        this.id = id;
    }
    
    /**
     * @param args
     * @throws NoSuchMethodException 
     * @throws SecurityException 
     * @throws InvocationTargetException 
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     */
    public static void main(String[] args) throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        TestReflection test = new TestReflection();
        
        Method method = TestReflection.class.getDeclaredMethod("getID");
        System.out.println(method.getReturnType().getCanonicalName());
        
        Method[] setters = TestReflection.class.getMethods();
        for(Method setter : setters) {
            if (setter.getName().equals("setID")) {
                System.out.println(setter.getParameterTypes()[0].getCanonicalName());
                setter.invoke(test, 1111);
                System.out.println(test.getID());
            }
        }
    }

}
