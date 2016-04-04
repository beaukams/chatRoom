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
	private int idRoom;
	
	public ChatRoom(int id){
		this.idRoom = id;
		this.users = new Vector<ThreadUser>();
	}
	
	/**
	 * Pour savoir si un utilisateur appartient un room
	 * @return
	 */
	public boolean isInRoom(ThreadUser user){
		return this.users.contains(user) && (!this.users.isEmpty());
	}
	
	/**
	 * Ajouter un utilisateur au room
	 * @param user
	 */
	public void addUser(ThreadUser user){
		
		if( !this.isInRoom(user) ){
			this.users.add(user);
			System.out.println(this.isInRoom(user)+"\n");
		}
	}
	
	/**
	 * supprimer un utilisateur du room
	 * @param user
	 */
	public void removeUser(ThreadUser user){
		if(this.isInRoom(user)){
			Iterator<ThreadUser> iter = this.users.iterator();
			while(iter.hasNext()){
				ThreadUser cl = (ThreadUser) iter.next();
				if(cl == user)
					this.users.remove(user);
			}
		}
	}
	
	/**
	 * Diffuser un message aux membres du room
	 * @param msg
	 * @param source
	 */
	public void diffuseMsg(String msg, ThreadUser source){
		if(this.isInRoom(source)){
			Iterator<ThreadUser> iter = this.users.iterator();
			while(iter.hasNext()){
				ThreadUser user = (ThreadUser) iter.next();
				if(user != source)
					user.sendMsg(msg);
			}
		}
	}
	
	public void diffuse(String msg, ThreadUser source){
		if(this.isInRoom(source)){
			Iterator<ThreadUser> iter = this.users.iterator();
			while(iter.hasNext()){
				ThreadUser user = (ThreadUser) iter.next();
				if(user != source)
					user.send(msg);
			}
		}
	}
	
	/**
	 * Diffuser un fichier au membre du room
	 * @param msg
	 * @param source
	 */
	public void diffuseFile(byte [] msg, ThreadUser source){
		if(this.isInRoom(source)){
			Iterator<ThreadUser> iter = this.users.iterator();
			while(iter.hasNext()){
				ThreadUser user = (ThreadUser) iter.next();
				if(user != source)
					user.sendMsgFile(msg);
			}
		}
		
	}
	
	public int getIdRoom(){
		return this.idRoom;
	}
}
