package com.hp.jmx.util;

public class MyEncode {
	
	public static String encode(String s) {		 		
        byte[] encodedS= Base64.encodeToByte(s.getBytes(), true);
        byte[] encodedS2 = new byte[encodedS.length*2];
        for (int i = 0; i< encodedS.length;i++) {
        	encodedS2[i*2] = '%';
        	encodedS2[i*2+1]=encodedS[i];
        }
        String result = new String(encodedS2);
        System.out.println("Encoded string is "+result);
        return result;			
	}
	
	public static String decode(String s) {
		byte[] source = s.getBytes();
		byte[] trimSource = new byte[source.length/2];
		for (int i=1;i<source.length;){
			trimSource[(i-1)/2] = source[i];
			i = i+2;
		}
		byte[] decodedB = Base64.decode(trimSource);
		String result = new String(decodedB);		
		return result;
	}
}
