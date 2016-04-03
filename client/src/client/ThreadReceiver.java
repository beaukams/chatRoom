package client;

import java.awt.TextArea;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ThreadReceiver extends Thread{
	private ChatRoomClient client;
	private TextArea output = null;
	private BufferedInputStream in;
	
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
			this.in = new BufferedInputStream(this.client.getSocket().getInputStream());
			long fileLength = 0;
			int count = 0;
			byte [][] fileContents = null;
			String fileName = "";
			String msg = "";
			byte buffer [] = new byte [4096*2];
			int b;
			while(this.client.getStatus()){
				
				while((b=this.in.read(buffer, 0, buffer.length)) > 0){
					msg = new String(buffer).trim();
					this.notifie(msg.trim());
					
					if(msg.startsWith("TFSI:")){ //longuuer
						
						String n = msg.substring(msg.indexOf(":")+1, msg.length());
						
						fileLength = Long.parseLong(n);
						fileContents = new byte [(int)fileLength][4096*2];
						
					}else if(msg.startsWith("TFSC:")){ //contenu
						
						fileContents[count] = msg.substring(msg.indexOf(":")+1, msg.length()).getBytes();
						count++;
						
					}else if(msg.startsWith("TFSF:")){ //nom et fin
						fileName = msg.substring(msg.indexOf(":")+1, msg.length());
						System.out.println("messs ");
						
						//cree le 
						this.client.recvFile(fileName, fileLength, fileContents);
						
						fileContents = null;
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
	
	public String getKeyWord(byte [] b, int index){
		String res = "";
		
		for(int i=0; i<b.length; i++){
			if(b[i] == ':'){
				index--;
				continue;
			}
			
			if(index == 0) break;
			
			if(index == 1)
				res += b[i];
		}
		
		return res;
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
