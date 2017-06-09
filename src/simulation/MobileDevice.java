package simulation;

import java.net.Socket;
import java.util.Random;

import utils.MsgUtil;

public class MobileDevice {
	
	private int id;
	private float offloadWeight; // percentage of tasks that offloaded to cloud
	private long battery = 100;
	private long cloudTimeSpan;
	private long localTimeSpan;
	private long completelyLocalySpan = 30;
	
	public MobileDevice() {
		
	}
	
	public void computeByEMU() throws Exception{
		// communicate with wireless station use offloading algorithms
		int port = getTargetPort();
		Socket socket = new Socket("127.0.0.1", port);
		
		// start cloud computing and locally computing at the same time
		
		
		// get the finish time and compute
		MsgUtil.readMessage(socket);
		
	}
	
	
	public void updateBattery(long usage) {
		long usedBattery = usage / 2000;
		this.localTimeSpan = usage * 2;
		battery = battery - usedBattery;
		System.out.println("------------------------------------");
		System.out.println("Device "+id+" has finished its task.");
		System.out.println(offloadWeight + " percentage of task has completed on cloud");
		System.out.println((1 - offloadWeight) + " percentage of task has completed locally");
		System.out.println("The task consumes "+ usedBattery + "% of total battery life");
		System.out.println("The task computes on the cloud uses "+cloudTimeSpan + " seconds");
		System.out.println("The task computes locally uses "+localTimeSpan + " seconds");
		System.out.println("Cloud computing helps reduce" + 
				(completelyLocalySpan - Math.max(localTimeSpan, cloudTimeSpan)) +" seconds");
	}

	public int getTargetPort() {
		int[] ports = WirelessStation.getPorts();
		int targetPort = ports[new Random().nextInt(ports.length)];
		return targetPort;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public float getOffloadWeight() {
		return offloadWeight;
	}

	public void setOffloadWeight(float offloadWeight) {
		this.offloadWeight = offloadWeight;
	}

	public long getBattery() {
		return battery;
	}

	public void setBattery(long battery) {
		this.battery = battery;
	}

	public long getCloudTimeSpan() {
		return cloudTimeSpan;
	}

	public void setCloudTimeSpan(long cloudTimeSpan) {
		this.cloudTimeSpan = cloudTimeSpan;
	}

	public long getLocalTimeSpan() {
		return localTimeSpan;
	}

	public void setLocalTimeSpan(long localTimeSpan) {
		this.localTimeSpan = localTimeSpan;
	}

	public long getCompletelyLocalySpan() {
		return completelyLocalySpan;
	}

	public void setCompletelyLocalySpan(long completelyLocalySpan) {
		this.completelyLocalySpan = completelyLocalySpan;
	}
	

}
