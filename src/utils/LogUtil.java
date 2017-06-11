package utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import simulation.MobileDevice;

public class LogUtil {

	private static String filePath;
	
//	public LogUtil() {
//		filePath = System.getProperty("user.dir")+"/logs/";
//	}
	
	static {
		filePath = System.getProperty("user.dir")+"/logs/";
	}
	
	public static void log(MobileDevice device, String content) {
		Thread thread = new Thread() {
			@Override
			public void run() {
				try {
					File logFile = new File(filePath+device.getId()+".txt");
					logFile.createNewFile();
					FileWriter writer = new FileWriter(logFile);
					writer.write(content);
					writer.close();
				} catch (IOException e) {
				}
				
			}
		};
		thread.start();
		
	}
	
}
