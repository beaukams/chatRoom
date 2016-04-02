package client;

import java.awt.TextArea;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ThreadReceiver extends Thread{
	private ChatRoomClient client;
	private TextArea output = null;
	private BufferedReader in;
	
	public ThreadReceiver(ChatRoomClient client){
		super();
		this.client = client;
		this.start();
	}
	
	public ThreadReceiver(ChatRoomClient client, TextArea output){
		this(client);
		this.output = output;
	}
	
	public void run(){
		try {
			this.in = new BufferedReader(new InputStreamReader(this.client.getSocket().getInputStream()));
			long fileLength = 0;
			String fileContents = "";
			String fileName = "";
			String msg = "";
			
			while(this.client.getStatus()){
				if(this.in.ready()){
					msg = this.in.readLine();
					this.notifie(msg);
					
					if(msg.startsWith("TFSI:")){ //longuuer
						String n = msg.substring(msg.indexOf(":")+1, msg.length());
						System.out.println(n);
						fileLength = Long.parseLong(n);
						
						
					}else if(msg.startsWith("TFSC:")){ //contenu
						
						fileContents += msg.substring(msg.indexOf(":")+1, msg.length());
						
					}else if(msg.startsWith("TFSF:")){ //nom et fin
						fileName = msg.substring(msg.indexOf(":")+1, msg.length());
						
						//cree le 
						this.client.recvFile(fileName, fileLength, fileContents);
						
						fileContents = "";
						fileLength = 0;
						fileName = "";
						
					}else{
								
						
					}
					
					msg = "";
				}
				
			}
		} catch (IOException e) {
			this.toStop();
		}
		
	}
	
	public void toStop(){
		try {
			this.in.close();
		} catch (IOException e) {
		}
	}
	
	public void interpretor(String msg){
		String commande = msg.substring(0, msg.indexOf(":"));
		msg = msg.substring(msg.indexOf(":")+1, msg.length());
		if(commande.equals("MSG")){
			
		}else if(commande.equals("TFS")){
			
		}else{
			
		}
	}
	
	/**
	 * Notifier du message.
	 * Ce message va s'afficher dans un output (textArea) ou afficher
	 * @param msg
	 */
	public void notifie(String msg){
		if(this.output != null)
			this.output.append(msg+" \n");
		else
			System.out.println(msg+" \n");
	}
}
