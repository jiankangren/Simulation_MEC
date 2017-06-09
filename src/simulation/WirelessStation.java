package simulation;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashMap;

import utils.MsgUtil;

public class WirelessStation{

	private static final int channelNum = 5;
	private static final int bandWidth = 5;
	private static final int lossFactor = 1;
	
	private static ServerSocket[] channels;
	private static int[] ports;
	private static HashMap<Integer,Integer> connectionInfo;
	
	/**
	 * Initialize channels 
	 */
	public WirelessStation() {
		channels = new ServerSocket[channelNum];
		ports = new int[channels.length];
		connectionInfo = new HashMap<>();
		try {
			for( int i = 0; i < channelNum; ++i)  {
				channels[i] = new ServerSocket(0);
				ports[i] = channels[i].getLocalPort();
				connectionInfo.put(ports[i], 0);
			}
		}
		catch ( IOException e) {
			e.printStackTrace();
		}
		System.out.print("Wireless Station starts to run with "+channelNum+ " channels ");
		for( int i : ports) 
			System.out.print(i+" ");
		System.out.println();
	}
	
	/**
	 * get ports for connection
	 * @return a copy of available ports to prevent modification on original array
	 */
	public static int[] getPorts() {
		return Arrays.copyOfRange(ports, 0, ports.length);
	}
	
	public static HashMap<Integer,Integer> getConnectionInfo() {
		return connectionInfo;
	}
	
	public void start() {
		for( ServerSocket curChannel : channels) {
			Thread thread = new Thread() {
				
				@Override
				public void run() {
					try {
						while(true) {
							Socket socket = curChannel.accept();
//							MsgUtil.sendMessage(socket, content);
							String msg = MsgUtil.readMessage(socket);
							if( msg.equals("getInfo")) {
								WirelessInfoThread thread = new WirelessInfoThread(socket, curChannel.getLocalPort());
								thread.start();
							}
							else if( msg.startsWith("offloading ") ) {
								System.out.println("Wireless station recieves request...");
								float weight = Float.parseFloat(msg.substring("offloading ".length()));
								OffloadThread thread = new OffloadThread(weight,socket,curChannel.getLocalPort());
								thread.start();
							}
						}
					}
					catch( IOException e ) {
						e.printStackTrace();
					}
				}
				
			};
			thread.start();
		}
	}

	public static String getWirelessStationInfo() {
		String res = "channelNum:"+channelNum+" bandWidth:"+bandWidth + 
				     " lossFactor:"+lossFactor;
		return res;
	}
	
	public static void updateConnectionInfo(boolean increase, int port,int num) {
		int value = connectionInfo.get(port);
		if( increase )
			value += num;
		else
			value -= num;
		connectionInfo.put(port, value);
	}
	
	public static int getLossFactor() {
		return lossFactor;
	}
	
	public static int getBandWidth() {
		return bandWidth;
	}
}

class WirelessInfoThread extends Thread{
	
	private Socket socket;
	private int port;
	
	public WirelessInfoThread(Socket socket, int port) {
		this.socket = socket;
		this.port = port;
	}
	
	@Override
	public void run() {
		WirelessStation.updateConnectionInfo(true, port, 1);
		try {
			MsgUtil.sendMessage(socket, WirelessStation.getWirelessStationInfo());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

class OffloadThread extends Thread {
	
	private Socket stationToCloud;
	private Socket userToStation;
	private float weight;
	private int port;
	
	public OffloadThread(float weight, Socket userToStation, int port) throws UnknownHostException, IOException {
		this.weight = weight;
		stationToCloud = new Socket("127.0.0.1",CloudServer.getPort());
		this.userToStation = userToStation;
		this.port = port;
	}
	
	@Override
	public void run() {
		try {
			
			MsgUtil.sendMessage(stationToCloud, "offloading "+weight);
			System.out.println("WirelessStation offload task to cloud");
			String msg = MsgUtil.readMessage(stationToCloud);
			if( msg.startsWith("FINISH ")) {
				MsgUtil.sendMessage(userToStation, msg);
			}
			stationToCloud.close();
			WirelessStation.updateConnectionInfo(false, port, 1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
