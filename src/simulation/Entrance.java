package simulation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class Entrance extends Thread{
	
	public void test() throws IOException, InterruptedException {
		long startingTime = System.currentTimeMillis();
		int curTask = 5000;
		File file = new File(System.getProperty("user.dir") + "/task/1");
		FileWriter writer = new FileWriter(file);
		for( int i = 0; i < curTask; ++i) {
			this.sleep(5);
			writer.write(i);
		}
		writer.close();
		long endTime = System.currentTimeMillis();
		System.out.println( (endTime - startingTime) / 1000  );
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		WirelessStation wirelessStation = new WirelessStation();
		wirelessStation.start();
		CloudServer cloudServer = new CloudServer();
		cloudServer.start();
		for( int i = 1; i <= 25; ++i) {
			int battery = 75,
				dataSize = 30 ;
			MobileDevice device = new MobileDevice(i,battery,dataSize);
			device.computeByMEC();
		}
	}

}
