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
	 * Envoyer un message
	 * @param msg
	 */
	public void sendMsg(String msg){
		try{
			this.out.println(msg);

			this.out.flush();
			this.notifie(msg);
		}catch(Exception e){
			JOptionPane.showMessageDialog(null, "Vous n'est pas connecte à un serveur! Veuillez demarrer le serveur!");
			System.exit(-1);
		}
	}
	
	public void sendFile(String fichier){
		File file = new File(fichier);
		try {
			BufferedReader rdf = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			String msg = "TFS:TFSI:"+1000;
			this.sendMsg(msg);
			
			while(rdf.ready()){
				msg = "TFS:TFSC:"+rdf.readLine();
				this.sendMsg(msg);
			}
			
			msg = "TFS:TFSF:"+file.getName();
			this.sendMsg(msg);
			
			rdf.close();
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null, "Fichier introuvable");
			//System.exit(-1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
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
