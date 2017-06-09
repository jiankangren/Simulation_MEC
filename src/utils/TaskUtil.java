package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.sun.jmx.snmp.Timestamp;

import simulation.MobileDevice;

public class TaskUtil {
	
	private String filePath;
	private static List<Integer> list;
	private final int content = 5000;
	private static TaskUtil taskUtil;
	
	public static TaskUtil getInstance() {
		if( taskUtil == null) {
			taskUtil = new TaskUtil();
		}
		return taskUtil;
	}
	
	public TaskUtil() {
		filePath = System.getProperty("user.dir") + "/task/" ;
		list = Collections.synchronizedList(new ArrayList<Integer>());
		list.add(0);
	}
	

	public void runTaskOnServer(float weight) {
		Thread thread = new Thread() {
			@Override
			public void run() {
				try {
					int curTask = (int) (content * weight);
					int nxtFile = list.get(list.size()-1) + 1;
					list.add(nxtFile);
					File file = new File(filePath + nxtFile);
					FileWriter writer = new FileWriter(file);
					for( int i = 0; i < curTask; ++i) {
						writer.write(i);
					}
					writer.close();
				} catch( IOException e) {
					e.printStackTrace();
				}
 			}
		};
		thread.start();
	}
	
	public void runTaskOnMobileDevice(float weight, MobileDevice device) {
		Thread thread = new Thread() {
			@Override
			public void run() {
				try {
					long startTime = System.currentTimeMillis();
					int curTask = (int) (content * weight);
					int nxtFile = list.get(list.size()-1) + 1;
					list.add(nxtFile);
					File file = new File(filePath+nxtFile);
					FileWriter writer = new FileWriter(file);
					for( int i = 0; i < curTask; ++i) {
						this.sleep(5);
						writer.write(i);
					}
					writer.close();
					long endTime = System.currentTimeMillis();
					device.updateBattery(endTime-startTime);
				} catch( IOException | InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
		thread.start();
	}
	
	
}
