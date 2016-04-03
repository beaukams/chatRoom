package client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JOptionPane;



public class ChatRoomClient {
	private Socket socket;
	private boolean running = false;
	private PrintWriter out;
	private ThreadReceiver recepteur;
	private ClientGui gui = null;
	
	public ChatRoomClient(String host, int port) throws UnknownHostException, IOException{
		this.socket = new Socket(host, port);
		this.notifie("Connexion au serveur "+this.socket.getInetAddress().getHostAddress()+":"+this.socket.getPort());
	}
	
	public ChatRoomClient(String host, int port, ClientGui gui) throws UnknownHostException, IOException{
		this(host, port);
		this.gui = gui;
	}
	
	/**
	 * Demarrer et ouvrir le flux
	 */
	public void startClient(){
		try{
			
			this.out = new PrintWriter(this.socket.getOutputStream(), true);
			this.running = true;
			this.recepteur = new ThreadReceiver(this);
			
		} 
		catch (UnknownHostException e) {
			JOptionPane.showMessageDialog(null, "Vous n'est pas connecte à un serveur! Veuillez demarrer le serveur!");
			System.exit(-1);}
		catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Vous n'est pas connecte à un serveur! Veuillez demarrer le serveur!");
			System.exit(-1);}
		
	}
	
	public void notifie(String msg){
		System.out.println(msg + "\n");
	}
	
	/**
	 * arreter le flux
	 */
	public void stopClient(){
		this.out.close();
	}
	
	
	
	/**
	 * Envoyer une chaine de caractere 
	 * @param msg
	 */
	private void send(String msg){
		try{
			this.notifie(msg);
			this.out.println(msg);

			this.out.flush();
			
		}catch(Exception e){
			JOptionPane.showMessageDialog(null, "Imposssible d'envoyer ce message");
			System.exit(-1);
		}
	}
	
	/**
	 * Envoyer un message
	 * @param msg
	 * @param idRoom
	 */
	public void sendMsg(String msg, int idRoom){
		this.send("MSG:"+idRoom+":"+msg);
	}
	
	 /**
	  * Envoyer un fichier 
	  * @param fichier
	  * @param idRoom
	  */
	public void sendFile(String fichier, int idRoom){
		File file = new File(fichier);
		try {
			BufferedReader rdf = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			String msg = "TFS:"+idRoom+":TFSI:"+1000;
			this.send(msg);
			
			while(rdf.ready()){
				msg = "TFS:"+idRoom+":TFSC:"+rdf.readLine();
				this.send(msg);
			}
			
			msg = "TFS:"+idRoom+":TFSF:"+file.getName();
			this.send(msg);
			
			rdf.close();
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null, "Fichier introuvable");
			//System.exit(-1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	
	public void quit(){
		this.send("EXIT");
	}
	
	public void creeRoom(){
		this.send("NEWROOM:");
	}
	
	public void joinRoom(int idRoom){
		this.send("JOINROOM:"+idRoom);
	}
	
	public void quitRoom(int idRoom){
		this.send("QUITROOM:"+idRoom);
	}
	
	public void addUserToRoom(){
		
	}
	
	public void recvFile(String name, long length, String contents){
		
		File file = new File(name);
		if(file.exists()){
			file = new File(name+"_new"+Math.random()*1000);
		}
		
		try {
			if(file.createNewFile()){
				PrintWriter pw = new PrintWriter(new FileOutputStream(file), true);
				pw.println(contents);
				pw.close();
				this.notifie("reception fichier"+file.getName()+" contents:"+contents);
			}
			
		
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
	public boolean getStatus(){
		return this.running;
	}
	
	public Socket getSocket(){
		return this.socket;
	}
	
	public void setStatus(boolean status){
		this.running = status;
	}
	
}
