package simulation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

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
		Scanner scanner = new Scanner(System.in);
		
		while( true ) {
			System.out.print("Input device number:");
			int deviceNumber = scanner.nextInt();
			MobileDevice[] devices = new MobileDevice[deviceNumber];
			for( int i = 1; i <= deviceNumber; ++i) {
				System.out.print("Input battery level of device "+i+":(1~100 or -1 to randomize one)");
				int battery = scanner.nextInt();
				battery = battery == -1 ? new Random().nextInt(101) : battery;
//				int battery = new Random().nextInt(101),
				System.out.print("Input data size of device "+i+":( 1~5 or -1 to randomize one)");
				int dataSize = scanner.nextInt();
				dataSize = dataSize == -1 ? new Random().nextInt(5) + 1 : dataSize;
				devices[i-1] = new MobileDevice(i,battery,dataSize);
			}
			
			for( MobileDevice device : devices)
				device.computeByMEC();
			
			System.out.println("-------------------------New Test------------------------------");
		}
		
	}

}
