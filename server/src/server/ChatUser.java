package server;

import java.io.Serializable;

public class ChatUser implements Serializable{
	private String pseudo;
	private String nom;
	private String prenom;
	private String addr;
	private int id;
	private static long lastId;
	private String lastConnectDate;
	private String firstConnectDate;
	private boolean inline; //true(connecte, false(non connecte)
	
	public ChatUser(String pseudo){
		this.pseudo = pseudo;
		this.nom = "KAMA";
		this.prenom = "Abdoulaye";
		this.inline = false;
	}
	
	public boolean getStatus(){
		return this.inline;
	}
	
	public void setStatus(boolean val){
		this.inline = val;
	}
}
