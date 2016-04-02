package server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.Vector;

public class ChatRoom extends Thread{
	private Vector <ThreadUser> users;
	private String id;
	
	public ChatRoom(String id){
		this.id = id;
	}
	
	public void addUser(ThreadUser user){
		this.users.add(user);
	}
	
	public void removeUser(ThreadUser user){
		Iterator<ThreadUser> iter = this.users.iterator();
		while(iter.hasNext()){
			ThreadUser cl = (ThreadUser) iter.next();
			if(cl == user)
				this.users.remove(user);
		}
	}
	
	/**
	 * 
	 * @param msg
	 * @param source
	 */
	public void diffuseMsg(String msg, ThreadUser source){
		Iterator<ThreadUser> iter = this.users.iterator();
		while(iter.hasNext()){
			ThreadUser user = (ThreadUser) iter.next();
			if(user != source)
				user.sendMsg(msg);
		}
	}
	
	public void diffuseFile(String msg, ThreadUser source){
		Iterator<ThreadUser> iter = this.users.iterator();
		while(iter.hasNext()){
			ThreadUser user = (ThreadUser) iter.next();
			if(user != source)
				user.sendMsgFile(msg);
		}
	}
}
