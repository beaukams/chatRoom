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
	private boolean status; //true(connecte, false(non connecte)
	
	public ChatUser(String pseudo, String nom, String prenom){
		this.pseudo = pseudo;
		this.nom = nom;
		this.prenom = prenom;
		this.status = false;
	}
	
	public boolean getStatus(){
		return this.status;
	}
	
	public void setStatus(boolean val){
		this.status = val;
	}
}
