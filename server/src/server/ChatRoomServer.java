package server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.util.Iterator;
import java.util.Vector;

public class ChatRoomServer {
	private boolean running;
	private ServerSocket serverSocket;
	private Vector<ChatRoom> rooms;
	private Vector<ChatUser> users;
	private Vector<ThreadRoom> thRooms;
	private Vector<ThreadUser> thUsers;
	private String userBdd = "user_bdd_file.txt";
	
	
	public ChatRoomServer(int port) throws IOException{
		this.serverSocket = new ServerSocket(port);
		this.running = false;
		this.thUsers = new Vector<ThreadUser>();
		this.users = new Vector<ChatUser> ();
	}
	
	/**
	 * Demarrer le server
	 * @return
	 */
	public boolean startServer(){
		boolean res = false || this.running;

		if(!res){
			
			this.notifie("Demarrage du serveur sur le port "+this.serverSocket.getLocalPort());
			
			try {
				this.running = true;
				
				while(this.running){
					new ThreadUser(this.serverSocket.accept(), this);
				}
				
			} catch (IOException e) {
				this.running = false;
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
		boolean res = true && this.running;
	
		if(res){
			
			try {
				this.serverSocket.close();
			} catch (IOException e) {
				this.notifie("Fermeture impossible");
			}
		}
		
		return res;
	}
	
	
	public void notifie(String msg){
		System.out.println(msg);
	}
	

	/**
	 * Sauvegarder la liste des utilisateurs dans un fichier
	 * @throws IOException
	 */
	public void saveUsersList() throws IOException{
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File(this.userBdd)));
			oos.writeObject(this.users);
	}
	
	/**
	 * restaurer la liste des utilisateurs à partir d'un fichier
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public void restorUsersList() throws ClassNotFoundException, IOException{
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File(this.userBdd)));
		this.users = (Vector<ChatUser>) ois.readObject();
	}
	
	/**
	 * Ajouter un utilisateur
	 * @param user
	 */
	public void addUser(ChatUser user){
		
		this.users.add(user);
		try {
			this.saveUsersList();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void addUser(ThreadUser user){
		this.notifie("ajout d'utilisateur");
		this.thUsers.add(user);
	}
	
	public void delUser(ChatUser user){
		Iterator<ChatUser> iter = this.users.iterator();
		while(iter.hasNext()){
			ChatUser cl = (ChatUser) iter.next();
			if(cl == user)
				this.users.remove(user);
		}
	}
	
	public void delUser(ThreadUser user){
		Iterator<ThreadUser> iter = this.thUsers.iterator();
		while(iter.hasNext()){
			ThreadUser cl = (ThreadUser) iter.next();
			if(cl == user)
				this.thUsers.remove(user);
		}
	}
	
	public void alterUser(){
		
	}

	
	
	public void createRoom(){
		
	}
	
	/**
	 * Diffuser un message a tous les clients connectes
	 */
	public void diffuseMsg(String msg){
		Iterator<ThreadUser> iter = this.thUsers.iterator();
		while(iter.hasNext()){
			((ThreadUser) iter.next()).sendMsg(msg);
		}
	}
	
	public void diffuseFile(String msg){
		Iterator<ThreadUser> iter = this.thUsers.iterator();
		while(iter.hasNext()){
			((ThreadUser) iter.next()).sendMsgFile(msg);
		}
	}
	
	/* getters et setters */
	public ServerSocket getServerSocket(){
		return this.serverSocket;
	}
	
}


