package server;

import java.io.IOException;

public class TestChatRoomServer {
	public static void main(String args []){
		try {
			ChatRoomServer server = new ChatRoomServer(20019);
			server.addRoom();
			server.addRoom();
			server.startServer();
			
			
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
