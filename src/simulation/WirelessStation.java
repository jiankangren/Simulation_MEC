package simulation;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;

import utils.MsgUtil;

public class WirelessStation{

	private static final int channelNum = 5;
	private static final int bandWidth = 5;
	private static final float lossFactor = 0.4f;
	
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
				channels[i] = new ServerSocket();
				ports[i] = channels[i].getLocalPort();
				connectionInfo.put(ports[i], 0);
			}
		}
		catch ( IOException e) {
			e.printStackTrace();
		}
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

	public static int getChannelnum() {
		return channelNum;
	}

	public static int getBandwidth() {
		return bandWidth;
	}

	public static float getLossfactor() {
		return lossFactor;
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
		int key = WirelessStation.getConnectionInfo().get(port);
		int value = WirelessStation.getConnectionInfo().get(key);
		WirelessStation.getConnectionInfo().put(key, value + 1);
		MsgUtil.sendMessage(socket, "ChannelNum:"+WirelessStation.getChannelnum());
	}
}
