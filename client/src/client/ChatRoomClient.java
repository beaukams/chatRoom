package client;

import java.net.Socket;

public class ChatRoomClient {
	private Socket socket;
	private boolean status;
	
	public ChatRoomClient(String addr, int port){
		
	}

	public boolean getStatus(){
		return this.status;
	}
	
	public void setStatus(boolean status){
		this.status = status;
	}
	
}
