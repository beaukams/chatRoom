package client;

import java.io.IOException;
import java.net.UnknownHostException;

public class TestChatRoomClient {
	public static void main(String args []){
		try {
			ChatRoomClient cl1 = new ChatRoomClient("127.0.0.1", 20019);
			cl1.startClient();
			ChatRoomClient cl2 = new ChatRoomClient("127.0.0.1", 20019);
			cl2.startClient();
			ChatRoomClient cl3 = new ChatRoomClient("127.0.0.1", 20019);
			cl3.startClient();
			
			cl1.joinRoom(1);
			cl1.joinRoom(2);
			
			cl3.joinRoom(2);
			cl2.joinRoom(1);
			cl2.joinRoom(2);
			
			try {
				Thread.sleep(3000);
			/*	cl1.sendMsg("client 1: message g 0",  0);
				cl1.sendMsg("client 1: message g 1",  1);
				cl1.sendMsg("client 1: message g 2",  2);
				
				cl2.sendMsg("client 2: message g 1",  1); */
				
				cl1.sendFile("senegal.png", 1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
	
}
