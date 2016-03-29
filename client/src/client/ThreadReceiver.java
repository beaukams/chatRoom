package client;

public class ThreadReceiver extends Thread{
	ChatRoomClient client;
	public ThreadReceiver(ChatRoomClient client){
		super();
		this.client = client;
		
		this.start();
	}
	
	public void run(){
		while(this.client.getStatus()){
			
		}
	}
}
