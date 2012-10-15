package test;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hp.jmx.cmd.LogUtil;


public class TestRegMatch {
	public static void main(String[] args) throws Exception {
    	String a = "\"test\":\"<test name>\" , \"run , time\":\"<run time of this test>\" ,  \"result\":\"<run , status>\"";
    	a = "\"test\":\"\"";
    	//String[] as = a.split(",");
    	String[] as = a.split("\"\\s*,\\s*\"");
    	for (String b:as) {
    		System.out.println("=="+b+"==");
    	}
    	String pattern = "^\"([^\"]+)\"\\s*:\\s*\"([^\"]+)\"$";
    	Pattern p=Pattern.compile(pattern);	
    	Matcher matcher = p.matcher(a);    	
		while (matcher.find()) {
			for (int i = 0; i<=matcher.groupCount();i++){
				System.out.println("matcher.group("+i+") = "+matcher.group(i));
			}
			
		}
		
		LogUtil logUtil = new LogUtil();
		Map<String,String> map = logUtil.getDetails("C:\\Users\\jiamingx\\Downloads\\details.txt");
		if (map !=null)
		printMap(map);	    
        
    }
	
	private static void printMap(Map<String,String> map) {
		for (String key: map.keySet()){
			System.out.println(key+":"+map.get(key));
		}
	    
	}
}
