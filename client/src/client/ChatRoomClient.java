package client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
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
import java.nio.charset.Charset;


import javax.swing.JOptionPane;



public class ChatRoomClient {
	private Socket socket;
	private boolean running = false;
	private BufferedOutputStream out;
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
			
			this.out = new BufferedOutputStream(this.socket.getOutputStream(), 4096*2);
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
		try {
			this.out.flush();
			this.out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
	/**
	 * Envoyer une chaine de caractere 
	 * @param msg
	 */
	private void send(String msg){

		this.send(msg.getBytes());
	}
	
	private void send(byte [] msg){
		try{
			this.notifie(new String(msg));
			this.out.write(msg, 0, msg.length);
			this.out.flush();
			
		}catch(Exception e){
			JOptionPane.showMessageDialog(null, "Imposssible d'envoyer ce message");
			System.exit(-1);
		}
	}
	
	private void send(byte [][] msg){
		for(int i=0; i<msg.length; i++)
			this.send(msg[i]);
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
	/*public void sendFile(String fichier, int idRoom){
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
		
		
	}*/
	
	public void sendFile(String fichier, int idRoom){
		File file = new File(fichier);
		int max = 4096*2;
		
		try {
			
			BufferedInputStream rdf = new BufferedInputStream(new FileInputStream(file));
			byte data [] = this.readStream(rdf);
			rdf.close();
			
			byte msg [][] = makeBytes("TFS:"+idRoom+":TFSC:", data, max);
			
			this.send("TFS:"+idRoom+":TFSI:"+msg.length);
			
			this.send(msg);

			this.send("TFS:"+idRoom+":TFSF:"+file.getName());
			
			
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null, "Fichier introuvable");
			//System.exit(-1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public static byte [] makeBytes(String msg, byte [] b){
		byte ms [] = msg.getBytes();
		byte res [] = new byte [ms.length+b.length];
		
		for(int i=0; i<ms.length; i++){
			res [i] = ms[i];
		}
		
		for(int i=0; i<b.length; i++){
			res[ms.length+i] = b[i];
		}
		
		return res;
	}
	
	public static byte [] makeBytes(byte [] ms, byte [] b){
		
		byte res [] = new byte [ms.length+b.length];
		
		for(int i=0; i<ms.length; i++){
			res [i] = ms[i];
		}
		
		for(int i=0; i<b.length; i++){
			res[ms.length+i] = b[i];
		}
		
		return res;
	}
	
	public static byte [][] makeBytes(byte [] ms, byte [] b, int taille){
		
		byte [][] temp = byteToBytes(b, taille-ms.length);
		byte [][] res = new byte [temp.length][taille];
		
		for(int i=0; i<res.length; i++){
			res[i] = makeBytes(ms, temp[i]);
		}
		
		return res;
	}
	
	public static byte [][] makeBytes(String ms, byte [] b, int taille){
		
		return makeBytes(ms.getBytes(), b, taille);
	}

	/**
	 * Mettre un tableau de byte en plusieurs byte de taille taille
	 * @param b
	 * @param taille
	 * @return
	 */
	public static byte [][] byteToBytes(byte [] b, int taille){
		
		byte temp [] = b;
		int n = temp.length%taille==0 ? temp.length/taille : temp.length/taille+1; 
		
		byte res [][] = new byte [n][taille];
		
		for(int i=0; i<temp.length; i++){
			res[i/taille][i%taille] = temp[i];
		}
		
		return res;
	}
	
	public  byte [] readStream(InputStream is) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int b;
 

		while ( (b=is.read()) >= 0) {
			baos.write(b);
		}
		
		return baos.toByteArray();
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
	
	public void recvFile(String name, long length, byte [][] contents){
		
		String extension = name.substring(name.lastIndexOf("."), name.length());
		String nameFile = name.substring(0, name.lastIndexOf("."));
		
		File file = new File(name);
		
		if(file.exists()){
			file = new File(nameFile+"_new"+Math.random()*1000+""+extension);
		}
		
		try {
			if(file.createNewFile()){
				BufferedOutputStream pw = new BufferedOutputStream(new FileOutputStream(file));
				for(int i=0; i<contents.length; i++){
					pw.write(contents[i], 0, contents[i].length);
					pw.flush();
				}
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
