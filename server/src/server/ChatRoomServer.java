package server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.util.Vector;

public class ChatRoomServer {
	private boolean status;
	private ServerSocket room;
	private Vector <ChatUser> users;
	private String userBdd = "user_bdd_file.txt";
	
	
	public ChatRoomServer(int port) throws IOException{
		this.room = new ServerSocket(port);
	}
	
	/**
	 * Demarrer le server
	 * @return
	 */
	public boolean startServer(){
		boolean res = false || this.status;

		if(!res){
			
			this.notifie("Demarrage du serveur sur le port "+this.room.getLocalPort());
			
			try {
				this.status = true;
				
				while(this.status){
					new ThreadUser(this.room.accept(), this);
				}
				
				
			} catch (IOException e) {
				this.status = false;
				this.notifie("Demarrage impossible");
			}
		}
			
		
		return res;
	}
	
	/**
	 * Arreter le serveur
	 * @return
	 */
	public boolean stopServer(){
		boolean res = true && this.status;
	
		if(res){
			
			
		}
		
		return res;
	}
	
	public void saveUsersList(){
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File(this.userBdd)));
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File(this.userBdd)));
			
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void restorUsersList(){
		
	}
	
	/**
	 * Ajouter un utilisateur
	 * @param user
	 */
	public void addUser(ChatUser user){
		this.users.add(user);
		this.saveUsersList();
	}
	
	public void delUser(){
		
	}
	
	public void alterUser(){
		
	}
	
	public void notifie(String msg){
		System.out.println("msg");
	}
	
	
}


