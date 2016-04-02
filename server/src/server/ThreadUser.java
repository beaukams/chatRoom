package server;

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
	
	BufferedReader in;
	PrintWriter out;
	
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
		
		
		this.in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
		this.out = new PrintWriter(this.socket.getOutputStream(), true);
		
		String msgToSend = "", msgRecv = "";
		
		
		while(this.running){
			
			if(this.in.ready()){
				msgRecv = this.in.readLine();
				this.server.notifie(msgRecv);
				
				
				this.interprete(msgRecv);
				msgRecv = "";
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
	
	public void interprete(String msg){
		
		String commande = msg.substring(0, msg.indexOf(":"));
		msg = msg.substring(msg.indexOf(":")+1, msg.length());
		this.server.notifie("commande "+commande+" msg "+msg);
		
		if(commande.equals("MSG")){//message
			this.server.diffuseMsg(msg);
			
		}else if(commande.equals("AUTH")){ //enregistrement
			String pseudo = msg;
			out.println("Bonjour!!!"+pseudo);
			System.out.println("NOUVEAU UTILISATEUR CONNECTE: "+msg);
			this.user = new ChatUser(pseudo);
			this.server.addUser(this);
			
		}else if(commande.equals("INSCR")){ //inscription
			
		}else if(commande.equals("DESINS")){ //desinscription
			
		}else if(commande.equals("EXIT")){ //deconnexion
			this.toStop();
			
		}else if(commande.equals("EXITR")){ //quitter un room
			
		}else if(commande.equals("TFS")){ //transfert de fichiers
			this.server.diffuseFile(msg);
		}else{
			
		}
		
	}
	
	public void sendMsg(String msg){
		this.out.println("MSG:"+msg);
	}
	
	public void sendMsgFile(String msg){
		this.out.println(msg);
	}
	
	public void sendFile(){
		
	}
	
	public ChatUser getUser(){
		return this.user;
	}
}
