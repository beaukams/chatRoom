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
import java.util.Vector;


import javax.swing.JOptionPane;



public class ChatRoomClient {
	private Socket socket;
	private boolean running = false;
	private BufferedOutputStream out;
	private ThreadReceiver recepteur;
	private Gui gui = null;
	private Vector<String> users;
	private Vector<String> rooms;
	private Vector<String> contacts;
	
	
	public ChatRoomClient(String host, int port) throws UnknownHostException, IOException{
		this.socket = new Socket(host, port);
		this.notifie("Connexion au serveur "+this.socket.getInetAddress().getHostAddress()+":"+this.socket.getPort());
		
		this.users = new Vector<String>();
		this.rooms = new Vector<String>();
		this.contacts = new Vector<String>();
	}
	
	public ChatRoomClient(String host, int port, Gui gui) throws UnknownHostException, IOException{
		this(host, port);
		this.setGui(gui);
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
	
	public void initConnexion(String pseudo, String passwd){
		this.startClient();
		this.sauthentifie(pseudo, passwd);
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
	 * S'authentifier au pres du serveur
	 * @param pseudo
	 * @param password
	 */
	private void sauthentifie(String pseudo, String password){
		String msg = "AUTH:"+pseudo+":"+password;
		this.send(msg);
		
	}
	
	/**
	 * Envoyer une chaine de caractere 
	 * @param msg
	 */
	private void send(String msg){
		this.notifie(new String(msg));
		this.send(msg.getBytes());
	}
	
	
	
	private void send(byte [] msg){
		try{
			
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
		System.out.println("taille en byte "+file.length());
		int max = (int) file.length();
		
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
		System.out.println("taille en byte ald "+is.available());
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int b;
 

		while ( (b=is.read()) >= 0) {
			baos.write(b);
		}
		
		byte bb [] = baos.toByteArray();
		System.out.println("taille en byte ddd   "+bb.length);
		return bb;
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
		
		
		String extension = "";
		String nameFile = name;
		
		if(name.endsWith(".py") || name.endsWith(".png") || name.endsWith(".JPEG") || name.endsWith(".txt")){
			extension = name.substring(name.lastIndexOf("."), name.length())+"";
			name.substring(0, name.lastIndexOf("."));
		}
		
		File fichier;
		
		
		if(new File(name).exists()){
			name = nameFile+"_new"+Math.random()*1000+""+extension;
		} 
		
		try {
			fichier = new File(name);
			fichier.createNewFile();
			try {
				System.out.println("name: "+fichier.getName()+"\n");
				BufferedOutputStream pw = new BufferedOutputStream(new FileOutputStream(fichier));
				for(int i=0; i<contents.length; i++){
					pw.write(contents[i], 0, contents[i].length);
					pw.flush();
				}
				pw.close();
				this.notifie("reception fichier"+fichier.getName()+" contents:"+contents);
			
		
		} catch (IOException e) {
			e.printStackTrace();
		}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
	}
	
	
	public void addContact(String contact){
		this.contacts.add(contact);
		this.gui.refreshListContact((String []) this.contacts.toArray());
	}
	
	public void addUser(String user){
		this.users.add(user);
		this.gui.refreshListUsers((String []) this.users.toArray());
	}
	
	public void addRoom(String room){
		this.rooms.add(room);
		this.gui.refreshListRooms((String []) this.rooms.toArray());
	}
	
	public void delContact(String contact){
		this.contacts.remove(contact);
		this.gui.refreshListContact((String []) this.contacts.toArray());
	}
	
	public void delUser(String user){
		this.users.remove(user);
		this.gui.refreshListUsers((String []) this.users.toArray());
	}
	
	public void delRoom(String room){
		this.rooms.remove(room);
		this.gui.refreshListRooms((String []) this.rooms.toArray());
	}
	
	/**
	 * Inscription
	 * @param pseudo
	 * @param nom
	 * @param prenom
	 * @param mdp
	 * @param ip
	 * @param port
	 */
	public void sinscrire(String pseudo, String nom,String prenom, String mdp,  String ip, String port){
		
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

	public Gui getGui() {
		return gui;
	}

	public void setGui(Gui gui) {
		this.gui = gui;
	}
	
}
