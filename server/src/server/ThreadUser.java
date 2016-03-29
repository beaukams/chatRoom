package server;

import java.net.Socket;

public class ThreadUser extends Thread{
	private Socket socket;
	private ChatRoomServer server;
	
	public ThreadUser(Socket socket, ChatRoomServer server){
		super();
		this.socket = socket;
		this.server = server;
		this.start();
	}
	
	public void run(){
		
	}
}
