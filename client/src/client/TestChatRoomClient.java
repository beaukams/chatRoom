package client;

import java.io.IOException;
import java.net.UnknownHostException;

public class TestChatRoomClient {
	public static void main(String args []){
		try {
			ChatRoomClient client = new ChatRoomClient("127.0.0.1", 20019);
			client.startClient();
			
			//envoyer un message
			client.sendMsg("AUTH:laye");
			
			client.sendMsg("MSG:Bonjour 1");
			client.sendMsg("MSG:Bonjour 2");
			
			client.sendFile("kams");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
