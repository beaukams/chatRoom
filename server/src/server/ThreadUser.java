package server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.StringTokenizer;

public class ThreadUser extends Thread{
	private Socket socket;
	private ChatRoomServer server;
	private boolean running;
	private ChatUser user;
	
	BufferedInputStream in;
	BufferedOutputStream out;
	
	public ThreadUser(Socket socket, ChatRoomServer server){
		super();
		this.socket = socket;
		this.server = server;
		this.start();
	}
	
	public void run(){
		
		try {
			this.server.notifie("Connexion deu client "+this.socket.getInetAddress().getHostAddress()+":"+this.socket.getPort());
			this.running = true;
			this.communication();
		} catch (IOException e) {
			e.printStackTrace();
			this.running = false;
		}
	}
	
	public void communication() throws IOException{
		
		
		this.in = new BufferedInputStream(this.socket.getInputStream());
		this.out = new BufferedOutputStream(this.socket.getOutputStream());
		
		
		
		
		while(this.running){
			int b;
			byte buffer [] = new byte [4096*2];
			if((b=this.in.read(buffer)) > 0){
				this.server.notifie(new String(buffer).trim());
				
				
				this.interprete(buffer);
				buffer = null;
			}
		}
	}
	
	public void toStop(){
		try {
			this.running = false;
			this.in.close();
			this.out.flush();
			this.out.close();
			this.socket.close();
			this.server.delUser(this);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	
		
	}
	
	public synchronized void  interprete(byte [] recu){
		
		String tout = new String(recu).trim();
		int idRoom;
		String temp = "";
		String msg = "";
		
		while(this.containsKeyWord(tout)){
			System.out.println("tout2 "+tout+"\n");
			String commande = tout.substring(0, tout.indexOf(":"));
			tout = tout.substring(tout.indexOf(":")+1, tout.length());
			
			if(this.indexFirstKey(tout) != -1){
				msg = tout.substring(0, this.indexFirstKey(tout));
				tout = tout.substring(this.indexFirstKey(tout), tout.length());
			}else
				msg = tout;
			
			System.out.println("tout1 "+tout+"\n");
			
			//msg = msg.substring(msg.indexOf(":")+1, msg.length());
			this.server.notifie("commande "+commande+" msg "+msg);
			
			switch(commande){
			case "MSG":
				System.out.println("cc "+msg);
				idRoom = Integer.parseInt(msg.substring(0, msg.indexOf(":")));
				msg = msg.substring(msg.indexOf(":")+1, msg.length());
				this.server.diffuseMsg(msg, idRoom, this);
				break;
			case "AUTH":
				String pseudo = msg;
				//out.println("Bonjour!!!"+pseudo);
				System.out.println("NOUVEAU UTILISATEUR CONNECTE: "+msg);
				this.user = new ChatUser(pseudo);
				this.server.addUser(this);
				break;
				
			case "TFS":
				
				idRoom = Integer.parseInt(msg.substring(0, msg.indexOf(":")));
				
				msg = msg.substring(msg.indexOf(":")+1, msg.length());
				this.server.diffuseFile(msg.getBytes(), idRoom, this);
				//this.sendMsgFile(msg.getBytes());
				break;
				
			case "EXIT":
				this.toStop();
				break;
				
			case "NEWROOM":
				this.server.addRoom();
				break;
				
			case "JOINROOM":
				this.server.addUser(this);
				this.server.getRoom(Integer.parseInt(msg)).addUser(this);
				break;
				
			case "QUITROOM":
				this.server.getRoom(Integer.parseInt(msg)).removeUser(this);
				break;
				
			default:	
			
			}
		}
	}
	
	public int indexNextKey(String msg, String key){
		if(this.containsKeyWord(msg)){
			return msg.indexOf(key);
		}else
			return -1;
	}
	
	public int indexFirstKey(String msg){
		int min = 20;
		String keys [] = {"MSG:", "AUTH:", "TFS:", "EXIT:", "NEWROOM:", "JOINROOM:", "QUITROOM:"};
		for(int i=0; i<keys.length; i++){
			if(min > indexNextKey(msg, keys[i]) && indexNextKey(msg, keys[i]) != -1){
				min = indexNextKey(msg, keys[i]);
			}
		}
		
		if(min == 20 )
			min = -1;
		return min;
	}
	
	public boolean containsKeyWord(String msg){
		return msg.contains("MSG:") || msg.contains("AUTH:") || msg.contains("TFS:") || msg.contains("EXIT:") || msg.contains("NEWROOM:") || msg.contains("JOINROOM:") || msg.contains("QUITROOM:");
	}
	
	
	
	public void sendMsg(String msg){
		this.server.notifie("envoie-----------------------------MSG:"+msg);
		this.send("MSG:"+msg);
	}
	
	public void sendMsgFile(byte [] msg){
		this.server.notifie("envoie-----------------------------fichier----------------");
		this.send(msg);
	}
	
	public void send(String msg){
		this.send(msg.getBytes());
		
	}
	
	public void send(byte [] msg){
		try {
			this.out.write(msg, 0, msg.length);
			this.out.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void sendFile(){
		
	}
	
	public ChatUser getUser(){
		return this.user;
	}
}
