package com.hp.jmx;

public class DBDumpFactory {

	public static DBDump getDBDump(String dumpFile, String url, String oracleSpace, OracleDBAccessor dbAccessor, String newSchemaName) {
		
		try {
			NormalDBDump nDump = new NormalDBDump(dumpFile,url,oracleSpace,dbAccessor,newSchemaName);
			if (nDump.getInfo()) {
				System.out.println("This is normal dump file");
				return nDump;
			}
			DPump dPump = new DPump(dumpFile, url, oracleSpace,dbAccessor,newSchemaName);
			if (dPump.getInfo()) {
				System.out.println("This is dpump file");
				return dPump;
			}
			
			System.out.println("Both of two dump formats check fail. Please check log of them.");
			return null;
			
		} catch (Exception e) {
			System.out.println("Fail to init DBDump.");
			e.printStackTrace();
			return null;
		}
	}
}
