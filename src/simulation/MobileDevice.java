package simulation;

import java.io.IOException;
import java.net.Socket;
import java.util.Random;

import offloadingAlgos.OffloadingAlgos;
import utils.LogUtil;
import utils.MsgUtil;
import utils.TaskUtil;

public class MobileDevice {
	
	private int id;
	private float offloadWeight; // percentage of tasks that offloaded to cloud
	private long battery;
	private long originalBattery;
	private long cloudTimeSpan;
	private long localTimeSpan;
	private int transferTimeSpan;
	private long completelyLocalySpan = 30;
	private int targetPort;
	private int dataSize;
	private boolean hasCloudFinished = false;
	private boolean hasLocalFinished = false;
	
	public MobileDevice(int id, int battery, int dataSize) {
		this.id = id;
		this.battery = battery;
		this.originalBattery = battery;
		this.dataSize = dataSize * 10 + new Random().nextInt(10);
		transferTimeSpan = new Random().nextInt(10) + 1 ;
		System.out.println("Mobile Device created:"+id +" with "+ this.dataSize + "GB data and "+battery+"% battery life" );
	}
	
	public void computeByMEC() throws IOException{
		MobileDevice curDevice = this;
		Thread thread = new Thread() {
			@SuppressWarnings("static-access")
			@Override
			public void run() {
				try{
					System.out.println("Mobile device " +id+ " starts to run by MEC offloading algorithm...");
					int port = getRandomPort();
					Socket socket = new Socket("127.0.0.1", port);
					WirelessStation.updateConnectionInfo(true, port, 1);
					System.out.println("Mobile Device "+id+" communicates with wireless station on random selected channel "+port);
					MsgUtil.sendMessage(socket, "getInfo");
//					String wirelessStationInfo = MsgUtil.readMessage(socket);
					OffloadingAlgos decisionMaker = new OffloadingAlgos();
					decisionMaker.offloadingDecisionByMEC(curDevice, port);
					if( port != curDevice.targetPort) {
						System.out.println("**********************Accoding to MEC offloading algorithm, best channel changes from "+port +" to "+targetPort +" on device"+ id +"***");
						WirelessStation.updateConnectionInfo(false, port, 1);
						WirelessStation.updateConnectionInfo(true, curDevice.targetPort, 1);
					}
					// start cloud computing and locally computing at the same time
					socket.close();
					socket = new Socket("127.0.0.1", curDevice.targetPort);
					System.out.println("Device "+id+" decides to connect to channel "+curDevice.targetPort+" which provides best performance");
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
					WirelessStation.updateConnectionInfo(false, curDevice.targetPort, 1);
					String content = updateBattery("MEC");
					LogUtil.log(curDevice, content);
					curDevice.reset();
					curDevice.computeByDynamic();
				} catch( IOException | InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
		thread.start();
		// communicate with wireless station use offloa ding algorithm
	}

	public void computeByDynamic() {
		MobileDevice curDevice = this;
		Thread thread = new Thread() {
			@SuppressWarnings("static-access")
			@Override
			public void run() {
				try{
					System.out.println("Mobile device " +id+ " starts to run by Dynamic offloading algorithm...");
					int port = getRandomPort();
					Socket socket = new Socket("127.0.0.1", port);
					WirelessStation.updateConnectionInfo(true, port, 1);
					System.out.println("Mobile Device "+id+" communicates with wireless station on random selected channel "+port);
					MsgUtil.sendMessage(socket, "getInfo");
//					String wirelessStationInfo = MsgUtil.readMessage(socket);
					OffloadingAlgos decisionMaker = new OffloadingAlgos();
					decisionMaker.offloadingDecisionByDynamic(curDevice);
					if( port != curDevice.targetPort && curDevice.targetPort != 0) {
						System.out.println("**********************Accoding to Dynamic offloading algorithm, best channel changes from "+port +" to "+targetPort +" on device"+ id +"***");
						WirelessStation.updateConnectionInfo(false, port, 1);
						WirelessStation.updateConnectionInfo(true, curDevice.targetPort, 1);
					}
					// start cloud computing and locally computing at the same time
					socket.close();
					String msg = "";
					if( curDevice.getOffloadWeight() == 1) {
						socket = new Socket("127.0.0.1", curDevice.targetPort);
						System.out.println("Device "+id+" decides to connect to channel "+curDevice.targetPort+" which provides best performance");
						MsgUtil.sendMessage(socket, "offloading "+offloadWeight);
						System.out.println("Device "+id+" offloads "+offloadWeight +" of its total task to cloud");
						// get the finish time and compute
						msg = MsgUtil.readMessage(socket);
						if( msg.startsWith("FINISH") ) {
							curDevice.cloudTimeSpan = Long.parseLong(msg.substring("Finish".length() + 1));
							curDevice.hasCloudFinished = true;
						}
						System.out.println("Device "+id+"'s cloud task completed");
						WirelessStation.updateConnectionInfo(false, curDevice.targetPort, 1);
						
					}
					else{
						TaskUtil.getInstance().runTaskOnMobileDevice(1, curDevice);
						while( !curDevice.hasCloudFinished  ) {
							this.sleep(3000);
						}
					}
					String content = updateBattery("Dynamic");
					LogUtil.log(curDevice, content);
				} catch( IOException | InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
		thread.start();
	}
	
	
	public String updateBattery(String offloadingType) {
		String res = "";
		long usedBattery = localTimeSpan / 2;
		usedBattery += (transferTimeSpan/3) == 0 ? 1 : (transferTimeSpan/3);
		battery = battery - usedBattery;
		System.out.println("------------------------"+offloadingType+"----------------------------------");
		res += "-----------------------------"+offloadingType+"---------------------------------\n";
		res += "At the beginning, Device "+id+" has "+ originalBattery +"% of battery life and "+dataSize+"GB data to be transferred.\n";
		System.out.println("Device "+id+" has finished its task.");
		res += "Device "+id+" has finished its task.\n";
		System.out.println(offloadWeight + " percentage of task has completed on cloud");
		res += offloadWeight + " percentage of task has completed on cloud\n";
		System.out.println((10 - offloadWeight*10)/10+ " percentage of task has completed locally");
		res += (10 - offloadWeight*10) / 10+ " percentage of task has completed locally\n";
		System.out.println("The transfer of data costs " + transferTimeSpan  + " seconds" );
		res += "The transfer of data costs " + transferTimeSpan  + " seconds\n";
		System.out.println("The transfer of data uses " + (usedBattery - transferTimeSpan/3)  +"% of total battery life");
		res += "The transfer of data uses " + (usedBattery - localTimeSpan/2)  +"% of total battery life\n";
		System.out.println("The transfer and local computation consumes "+  usedBattery + "% of total battery life");
		res += "The transfer and local computation consumes "+  usedBattery + "% of total battery life\n";
		System.out.println("The task computes on the cloud uses "+cloudTimeSpan + " seconds");
		res += "The task computes on the cloud uses "+cloudTimeSpan + " seconds\n";
		System.out.println("The task computes locally uses "+localTimeSpan + " seconds");
		res += "The task computes locally uses "+localTimeSpan + " seconds\n";
		System.out.println("Cloud computing helps reduce " + 
				(completelyLocalySpan - Math.max(localTimeSpan, cloudTimeSpan+transferTimeSpan)) +" seconds and " + (completelyLocalySpan/2 - usedBattery) +"% of total battery life" );
		res += "Cloud computing helps reduce " + 
				(completelyLocalySpan - Math.max(localTimeSpan, cloudTimeSpan+transferTimeSpan)) +" seconds and " + (completelyLocalySpan/2 - usedBattery) +"% of total battery life\n";
		res += "At the end, Device "+id +" has "+battery +"% of battery life remained\n";
		res += "--------------------------------------------------------------------------------\n";
		return res;
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

	public int getDataSize() {
		return dataSize;
	}

	public void setDataSize(int dataSize) {
		this.dataSize = dataSize;
	}

	public int getTransferTimeSpan() {
		return transferTimeSpan;
	}

	public void setTransferTimeSpan(int transferTimeSpan) {
		this.transferTimeSpan = transferTimeSpan;
	}

	public void reset() {
		this.battery = originalBattery;
		this.cloudTimeSpan = 0;
		this.hasCloudFinished = false;
		this.hasLocalFinished = false;
		this.localTimeSpan = 0;
		this.offloadWeight = 0;
		this.targetPort = 0;
		this.transferTimeSpan = 0;
	}
	
}
