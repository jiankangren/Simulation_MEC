package utils;

import java.io.*;
import java.net.Socket;

public class MsgUtil {

	public static String readMessage(Socket socket){
		String res = null;
		try {
			DataInputStream reader = new DataInputStream(socket.getInputStream());
			res = reader.readUTF();
		}
		catch (IOException e ) {
			e.printStackTrace();
		}
		return res;
	}

	public static void sendMessage(Socket socket, String content) throws IOException {
		try{
			DataOutputStream writer = new DataOutputStream(socket.getOutputStream());
			writer.writeUTF(content);
		}
		catch( IOException e) {
			e.printStackTrace();
		}
	}
}
