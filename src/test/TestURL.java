package test;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

public class TestURL {
    
    private static final String URL_STRING = "http://abc+def/1 2/";
    
    public static void main(String[] args) throws UnsupportedEncodingException, URISyntaxException {
        System.out.println(URLEncoder.encode((URL_STRING), "UTF-8"));
        
        URI uri = new URI("http", "abc+def", "/1 2/3+4/", "a=b+c&d=e f", null);
        System.out.println(uri.toString());
    }
}
