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
			this.server = new ServerSocket();
			this.port = server.getLocalPort();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
				this.handleRequest(socket);
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
					String msg = MsgUtil.readMessage(socket);
					float weight = Float.parseFloat(msg.split(" ")[1]);
					TaskUtil.getInstance().runTaskOnServer(weight);
					MsgUtil.sendMessage(socket, "FINISH");
				} catch( IOException e) {
					e.printStackTrace();
				}
			}
		};
	}
	

	
}
