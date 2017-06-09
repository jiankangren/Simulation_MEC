package simulation;

import java.io.IOException;
import java.net.Socket;
import java.util.Random;

import offloadingAlgos.OffloadingAlgos;
import utils.MsgUtil;
import utils.TaskUtil;

public class MobileDevice {
	
	private int id;
	private float offloadWeight; // percentage of tasks that offloaded to cloud
	private long battery ;
	private long cloudTimeSpan;
	private long localTimeSpan;
	private long completelyLocalySpan = 30;
	private int targetPort;
	private boolean hasCloudFinished = false;
	private boolean hasLocalFinished = false;
	
	public MobileDevice(int id, int battery) {
		this.id = id;
		System.out.println("Mobile Device created:"+id);
		this.battery = battery;
	}
	
	public void compute() throws IOException{
		MobileDevice curDevice = this;
		Thread thread = new Thread() {
			@SuppressWarnings("static-access")
			@Override
			public void run() {
				try{
					System.out.println("Mobile device " +id+ " starts to run...");
					int port = getRandomPort();
					Socket socket = new Socket("127.0.0.1", port);
					System.out.println("Mobile Device "+id+" communicates with wireless station on random selected channel "+port);
					MsgUtil.sendMessage(socket, "getInfo");
					String wirelessStationInfo = MsgUtil.readMessage(socket);
					OffloadingAlgos decisionMaker = new OffloadingAlgos();
					decisionMaker.offloadingDecision(curDevice, port);
					if( port != curDevice.targetPort)
						System.out.println("***Best port changed***");
					// start cloud computing and locally computing at the same time
					socket.close();
					socket = new Socket("127.0.0.1", targetPort);
					System.out.println("Device "+id+" decides to connect to channel "+targetPort+" which provides best performance");
					MsgUtil.sendMessage(socket, "offloading "+offloadWeight);
					System.out.println("Device "+id+" offloads "+offloadWeight +" of its total task to cloud");
					TaskUtil.getInstance().runTaskOnMobileDevice( (10 - offloadWeight*10)/10, curDevice);
					// get the finish time and compute
					String msg = MsgUtil.readMessage(socket);
					System.out.println("Device "+id+"'s cloud task completed");
					if( msg.startsWith("FINISH") ) {
						curDevice.cloudTimeSpan = Long.parseLong(msg.substring("Finish".length() + 1));
						curDevice.hasCloudFinished = true;
					}

					while( !curDevice.hasCloudFinished || !curDevice.hasLocalFinished ) {
						this.sleep(3000);
//						System.out.println("ew111");
					}
					updateBattery();
				} catch( IOException | InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
		thread.start();
		// communicate with wireless station use offloa ding algorithm
		
	}
	
	
	
	public void updateBattery() {
		long usedBattery = localTimeSpan / 2;
		battery = battery - usedBattery;
		System.out.println("------------------------------------");
		System.out.println("Device "+id+" has finished its task.");
		System.out.println(offloadWeight + " percentage of task has completed on cloud");
		System.out.println((1 - offloadWeight) + " percentage of task has completed locally");
		System.out.println("The task consumes "+ usedBattery + "% of total battery life");
		System.out.println("The task computes on the cloud uses "+cloudTimeSpan + " seconds");
		System.out.println("The task computes locally uses "+localTimeSpan + " seconds");
		System.out.println("Cloud computing helps reduce " + 
				(completelyLocalySpan - Math.max(localTimeSpan, cloudTimeSpan)) +" seconds");
	}

	public int getRandomPort() {
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
	
	public void setTargetPort(int targetPort) {
		this.targetPort = targetPort;
	}
	
	public void setHasLocalFinished() {
		this.hasLocalFinished = true;
	}

}
