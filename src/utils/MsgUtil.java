package utils;

import java.io.*;
import java.net.Socket;

public class MsgUtil {

	public static String readMessage(Socket socket){
		String[] res = new String[1];
		Thread thread = new Thread() {
			@Override
			public void run() {
				try {
					DataInputStream reader = new DataInputStream(socket.getInputStream());
					res[0] = reader.readUTF();
				}
				catch (IOException e ) {
					e.printStackTrace();
				}
			}
		};
		thread.start();
		return res[0];
	}

	public static void sendMessage(Socket socket, String content) throws IOException {
		Thread thread = new Thread() {
			@Override
			public void run() {
				try{
					DataOutputStream writer = new DataOutputStream(socket.getOutputStream());
					writer.writeUTF(content);
				}
				catch( IOException e) {
					e.printStackTrace();
				}
			}
		};
		thread.start();
	}
}
