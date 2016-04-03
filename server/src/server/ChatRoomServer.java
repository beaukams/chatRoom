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

import javax.swing.JTextArea;

public class ChatRoomServer extends Thread{
	private boolean running;
	private ServerSocket serverSocket;
	private Vector<ChatRoom> rooms;
	private Vector<ChatUser> users;
	private Vector<ThreadRoom> thRooms;
	private Vector<ThreadUser> thUsers;
	private String userBdd = "user_bdd_file.txt";
	
	private int countRoom = 0;
	
	private JTextArea textOutPut = null;
	
	public ChatRoomServer(int port) throws IOException{
		super();
		this.serverSocket = new ServerSocket(port);
		this.running = false;
		this.thUsers = new Vector<ThreadUser>();
		this.users = new Vector<ChatUser> ();
		this.rooms = new Vector<ChatRoom>();
		this.addRoom(); //room de base contenant tous les utilisateurs
		
	}
	
	public ChatRoomServer(int port, JTextArea outPut) throws IOException{
		this(port);
		this.textOutPut = outPut;
		this.start();
	}
	
	public void run(){
		this.startServer();
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
		if(this.textOutPut != null)
			this.textOutPut.setText(this.textOutPut.getText()+msg+"\n");
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
		//l'ajouter au room de base
		this.getBaseRoom().addUser(user);
	}
	
	public void delUser(ChatUser user){
		Iterator<ChatUser> iter = this.users.iterator();
		while(iter.hasNext()){
			ChatUser cl = (ChatUser) iter.next();
			if(cl == user){
				this.users.remove(user);
				
			}
		}
	}
	
	public void delUser(ThreadUser user){
		Iterator<ThreadUser> iter = this.thUsers.iterator();
		while(iter.hasNext()){
			ThreadUser cl = (ThreadUser) iter.next();
			if(cl == user){
				this.thUsers.remove(user);
				this.getBaseRoom().removeUser(user);
			}
		}
	}
	
	public void alterUser(){
		
	}

	public ChatRoom getBaseRoom(){
		ChatRoom base = this.rooms.firstElement();
		return base;
	}
	
	public ChatRoom getLastRoom(){
		return this.rooms.lastElement();
	}
	
	public void addRoom(){
		this.notifie("Creation du room "+this.countRoom);
		this.rooms.add(new ChatRoom(this.countRoom));
		this.countRoom++;
	}
	
	/**
	 * Recupere le room corespondant au numero donné
	 * @param id
	 * @return
	 */
	public ChatRoom getRoom(int id){
		Iterator<ChatRoom> iter = this.rooms.iterator();
		ChatRoom res = null;
		while(iter.hasNext()){
			ChatRoom temp = (ChatRoom) iter.next();
			if(temp.getIdRoom() == id){
				res = temp; break;
			}
		}
		
		return res;
	}
	
	/**
	 * Envoyer un message à tous les clients du room
	 * @param msg
	 * @param idRoom
	 * @param source
	 */
	public void diffuseMsg(String msg, int idRoom, ThreadUser source){
		this.getRoom(idRoom).diffuseMsg(msg, source);
	}
	
	/**
	 * Envoyer un fichier à tous clients du room
	 * @param msg
	 * @param idRoom
	 * @param source
	 */
	public void diffuseFile(String msg, int idRoom, ThreadUser source){
		this.getRoom(idRoom).diffuseFile(msg, source);
	}
	
	/* getters et setters */
	public ServerSocket getServerSocket(){
		return this.serverSocket;
	}
	
}


