package simulation;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Arrays;

public class WirelessStation{

	private static final int channelNum = 5;
	private static final int bandWidth = 5;
	private static final float lossFactor = 0.4f;
	
	private ServerSocket[] channels;
	private int[] ports;
	
	/**
	 * Initialize channels 
	 */
	public WirelessStation() {
		channels = new ServerSocket[channelNum];
		ports = new int[channels.length];
		try {
			for( int i = 0; i < channelNum; ++i)  {
				channels[i] = new ServerSocket();
				ports[i] = channels[i].getLocalPort();
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
	public int[] getPorts() {
		return Arrays.copyOfRange(ports, 0, ports.length);
	}
	
	public void start() {
		
	}
	
}
