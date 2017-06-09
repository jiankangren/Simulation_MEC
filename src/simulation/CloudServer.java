package simulation;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import utils.MsgUtil;
import utils.TaskUtil;

public class CloudServer extends Thread{

	private static int port;
	private static ServerSocket server;
	
	public CloudServer() {
		try {
			this.server = new ServerSocket(0);
			this.port = server.getLocalPort();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Cloud Server starts to run....");
	}
	
	public static int getPort() {
		if( port != 0)
			return port;
		else
			return -1;
	}
	
	@Override
	public void run() {
		while( true ) {
			try {
				Socket socket = server.accept();
				handleRequest(socket);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	// content format: cloud 0.7
	public void handleRequest(Socket socket) {
		Thread thread = new Thread() {
			@Override
			public void run() {
				try{
					System.out.println("Cloud server starts computing");
					long startTime = System.currentTimeMillis();
					String msg = MsgUtil.readMessage(socket);
					float weight = Float.parseFloat(msg.split(" ")[1]);
					long timeSpan = TaskUtil.getInstance().runTaskOnServer(weight);
					MsgUtil.sendMessage(socket, "FINISH "+timeSpan);
				} catch( IOException e) {
					e.printStackTrace();
				}
			}
		};
		thread.start();
	}
	

	
}
