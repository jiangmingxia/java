package com.hp.jmx;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileAccessor {
	
	
	public static String getNoDuplicateFileName(String fileNamePrefix, String parentDir) {
		int count = 0;
		int maxCount = 100;
		String fileName = fileNamePrefix;
		String fullPath = parentDir+File.separator+fileName;
		while (count<maxCount && new File(fullPath).exists()) {
			fileName = fileNamePrefix+count;
			fullPath = parentDir+File.separator+fileName;
			count++;			
		}
		
		if (count == maxCount ) {
			System.out.println("Please change another preffred file name or delete some existing files. This prefix '"+fileNamePrefix+"' already has too much files.");
			return null;
		}
		return fullPath;
	}
	
	// return true if given file has keywords
	// else return false
	public static boolean isFileMatch(String fileName, String pattern) {

		try {
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			String s = null;
			Pattern p = Pattern.compile(pattern);
			Matcher matcher;
			while ((s = br.readLine()) != null) {
				matcher = p.matcher(s);
				if (!matcher.matches()) {
					continue;
				} else {
					br.close();
					return true;
				}
			}
			br.close();
			return false;

		} catch (FileNotFoundException e1) {
			System.out.println("File not exists: " + fileName);
			e1.printStackTrace();
			return false;
		} catch (IOException e2) {
			e2.printStackTrace();
			return false;
		}
	}

	// return matched results (regexp match)
	// if no match return null
	public static String[] fileMatch(String fileName, String pattern) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			String s = null;
			Pattern p = Pattern.compile(pattern);
			Matcher matcher;
			while ((s = br.readLine()) != null) {
				matcher = p.matcher(s);
				if (!matcher.matches()) {
					continue;
				} else {
					br.close();
					int count = matcher.groupCount();
					String[] results = new String[count];
					for (int i = 1; i <= count; i++) {
						results[i - 1] = matcher.group(i);
					}
					return results;
				}
			}
			br.close();
			return null;

		} catch (FileNotFoundException e1) {
			System.out.println("File not exists: " + fileName);
			e1.printStackTrace();
			return null;
		} catch (IOException e2) {
			e2.printStackTrace();
			return null;
		}
	}
	
	public static boolean isFileMatchFromEnd(String fileName, String pattern) {
		Pattern p=Pattern.compile(pattern);
		String line = "";
		RandomAccessFile rf = null;	
		Matcher matcher;
		try {
			rf = new RandomAccessFile(fileName, "r");
			long len = rf.length();
			long start = 0;
			long nextend = start + len - 1;			
			int readChar = -1;
			
			while (nextend > start) {
				rf.seek(nextend);
				readChar = rf.read();
				if (readChar == '\n' || readChar == '\r') {
					line = rf.readLine();
					if (line != null && line.trim()!="") {						
						matcher = p.matcher(line);
						if (matcher.matches()) {
							return true; //return when find match
						} 
					}					
				}
				nextend--;
			}
			if (nextend == 0) { //first line
				rf.seek(nextend);
				line = rf.readLine();				
				matcher = p.matcher(line);
				if (matcher.matches()) {
					return true;
				}
			}
			return false;
		} catch (FileNotFoundException e) {
					e.printStackTrace();
					return false;
		} catch (IOException e) {
					e.printStackTrace();
					return false;
		} finally {			
			try {
				if (rf != null)
					rf.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
}
