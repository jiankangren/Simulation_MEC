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
	
	public static TaskUtil getInstance() {
		
		return new TaskUtil();
	}
	
	public TaskUtil() {
		filePath = System.getProperty("user.dir") + "/task/" ;
		list = Collections.synchronizedList(new ArrayList<Integer>());
		list.add(0);
	}
	

	public long runTaskOnServer(float weight) {
		long timeSpan = 0, startingTime = 0, endTime = 0;
		try {
			startingTime = System.currentTimeMillis();
			int curTask = (int) (content * weight);
			int nxtFile = list.get(list.size()-1) + 1;
			list.add(nxtFile);
			File file = new File(filePath + nxtFile);
			FileWriter writer = new FileWriter(file);
			for( int i = 0; i < curTask; ++i) {
				writer.write(i);
			}
			writer.close();
			endTime = System.currentTimeMillis();
		} catch( IOException e) {
			e.printStackTrace();
		}
		return ( endTime - startingTime ) / 1000;
	}

	public void runTaskOnMobileDevice(float weight, MobileDevice device) {
		Thread thread = new Thread() {
			@Override
			public void run() {
				try {
					System.out.println("Device "+ device.getId()+" computes +"+weight+" of total task locally");
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
					device.setLocalTimeSpan( (endTime - startTime) / 1000);
					device.setHasLocalFinished();
				} catch( IOException  e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		thread.start();
	}
	
	
}
