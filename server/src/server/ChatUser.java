package server;

import java.io.Serializable;

public class ChatUser implements Serializable{
	private String pseudo;
	private String nom;
	private String prenom;
	private String addr;
	private String ip;
	private int port;
	private String passwd;
	
	
	private int id;
	private static long lastId;
	private String lastConnectDate;
	private String firstConnectDate;
	private boolean inline; //true(connecte, false(non connecte)
	
	public ChatUser(String nom, String prenom, String pseudo, String mdp, String ip, int port){
		this.pseudo = pseudo;
		this.nom = nom;
		this.prenom = prenom;
		this.passwd = mdp;
		this.ip = ip;
		this.port = port;
		this.inline = false;
	}
	
	public ChatUser(String user []){
		this.setId(Integer.parseInt(user[0]));
		this.pseudo = user[1];
		this.nom = user[2];
		this.prenom = user[3];
		this.passwd = user[4];
		this.ip = user[5];
		this.port = Integer.parseInt(user[6]);
		this.firstConnectDate = user[7];
		this.inline = false;
	}
	
	public String getPseudo(){
		return this.pseudo;
	}
	public boolean getStatus(){
		return this.inline;
	}
	
	public void setStatus(boolean val){
		this.inline = val;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
