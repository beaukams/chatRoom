package server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Vector;

public class ChatRoom extends Thread{
	private Vector <ChatUser> users;
	
	private ChatRoomServer server;
	
	public ChatRoom(ChatRoomServer server){
		this.server = server;
	}
	
	public void run(){
		while(true){
			
		}
	}
	
	public void diffuseMsg(String msg, ChatUser source){
		
	}
	
	public void diffuseMsg(String msg){
		
	}
}
