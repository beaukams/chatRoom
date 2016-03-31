package serie2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

/**
 * 
 * @author abdoulaye
 *
 */

public class ChatRoom{
	ServerSocket sockServeur;
	public int nbClient=0;
	private String fichierDeSauvegarde = "session_chat.txt";
	ObjectInputStream ois;
	ObjectOutputStream oos;
	private boolean arret = true;
	NouveauClient clients [];
	
	public ChatRoom(int port, int maxClients) throws IOException{
		this.sockServeur = new ServerSocket(port);
		this.clients = new NouveauClient [maxClients];
	}
	
	public void demarrer(){
		try {
			this.arret = false;
			this.oos = new ObjectOutputStream(new FileOutputStream(new File(this.fichierDeSauvegarde)));
			this.ois = new ObjectInputStream(new FileInputStream(new File(this.fichierDeSauvegarde)));

			System.out.println("Serveur demarré sur le port "+this.sockServeur.getLocalPort());
			
			while(true){
				Socket sockClient = this.sockServeur.accept();
				new NouveauClient(sockClient,this).start();
			}
			
		} catch (IOException e) {
			this.arret = true;
			System.out.println("Demarrage impossible");
		}
		
	}
	
	public void arreter(){
		try {
			this.oos.flush();
			this.oos.close();
			this.ois.close();
		} catch (IOException e) {

		}	
	}
	
	public void refresh(){
		
	}
	
	/**
	 * diffuser le message à tous les utilisateurs connectés
	 * @param msg
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public synchronized void diffuser(String msg) throws FileNotFoundException, IOException, ClassNotFoundException{
		
		this.diffuser(null,msg);
		
	}
	
	/**
	 * diffuser le message à tous les utilisateurs connectés
	 * @param source
	 * @param msg
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public synchronized void diffuser(NouveauClient source, String msg) throws FileNotFoundException, IOException, ClassNotFoundException{

		
		for(int i=0; i<this.clients.length; i++){
			
			if(this.clients[i] != null){
				if(this.clients[i] != source){
					
					PrintWriter pw = new PrintWriter(this.clients[i].getSocClient().getOutputStream(), true);
					pw.println(msg);
					pw.flush();

					
				}
			}
			
		}
		
		
	}
	
	/**
	 * supprimer un utilisateur du fichier
	 * @param i
	 * @throws ClassNotFoundException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public synchronized void deleteUser(NouveauClient user) throws FileNotFoundException, IOException, ClassNotFoundException{
		
		UserConnecte [] users = this.restaureDeFichier(this.ois);

		
		for(int i=0; i<users.length; i++){
			if(users[i] == user.getDonnees())
				this.nbClient--;
			else
				this.sauvegardeDansFichier(this.oos, users[i]);
			
		}
		

		for(int i=0; i<this.clients.length; i++){
			if(this.clients[i] != null){
				if(this.clients[i].getDonnees().getPseudo().equals(user.getDonnees().getPseudo())  && this.clients[i].getDonnees().getDateConnexion().equals(user.getDonnees().getDateConnexion())){
					this.clients[i] = null;break;
				}
			}
		}

		
	}
	
	/**
	 * Un nouveau utilisateur connecté
	 * @param nvo
	 * @throws IOException
	 * @throws ClassNotFoundException 
	 */
	public synchronized void addUser(NouveauClient nvo) throws IOException, ClassNotFoundException{
		
		if(nvo != null && this.nbClient<this.clients.length){
			
			UserConnecte [] users = this.restaureDeFichier(this.ois);

			for(int i=0; i<users.length; i++){
				this.sauvegardeDansFichier(this.oos, users[i]);
			}
			
			
			this.sauvegardeDansFichier(this.oos, nvo.getDonnees());
			this.nbClient++;
			System.out.println(this.nbClient+" utilisateurs connectes");
			

				for(int i=0; i<this.clients.length; i++){
					if(this.clients[i] == null){
						this.clients[i] = nvo;break;
					}
				}

		}
	}
	
	/**
	 * 
	 * @param n
	 * @param nvo
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void sauvegardeDansFichier(ObjectOutputStream oos, UserConnecte nvo) throws FileNotFoundException, IOException{

		oos.writeObject(nvo);
		oos.flush();

	}
	
	public UserConnecte [] restaureDeFichier(ObjectInputStream ois) throws FileNotFoundException, IOException, ClassNotFoundException{

		UserConnecte res [] = new UserConnecte [this.nbClient];
		
		for(int i=0; i<this.nbClient; i++){
			res [i] = (UserConnecte) ois.readObject();
		}
		
		return res;
	}

	
	
}

class NouveauClient extends Thread{
	
	private Socket sockClient;
	private ChatRoom serveur;
	private int numeroClient;
	private String pseudo;
	private UserConnecte donnees;
	
	public NouveauClient(Socket sock, ChatRoom serveur){
		super();
		this.sockClient = sock;
		this.serveur = serveur;
		
		this.numeroClient = this.serveur.nbClient;
		
	}
	
	public void run(){
		
		try {
			
			this.serveur.addUser(this);
			System.out.println("Connexion avec le client "+ this.serveur.nbClient +" réussie: "+this.sockClient.getInetAddress().getHostAddress());
			
			this.traitement();
			
		} catch (IOException e) {
			System.out.println("Ajout impossible"); e.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.out.println("Ajout impossible"); e.printStackTrace();
		}
		
		
		
	}
	
	
	
	public void traitement(){
		
		BufferedReader in;
		PrintWriter out;
		String msg ="";
		
		try {
			
			//stocker la session
			out = new PrintWriter(this.sockClient.getOutputStream(), true);
			
			
			in = new BufferedReader(new InputStreamReader(this.sockClient.getInputStream()));
		

			boolean enregistre = false;
			while(true){
				if(in.ready()){
					msg = in.readLine();
					
					if(enregistre == false){ //le premier message du client <pseudo>
						this.pseudo = msg;
						out.println("Bienvenue!!!"+this.pseudo+". Il y'a deja "+(this.serveur.nbClient-1)+" utilisateur(s) connecté(es)");
						System.out.println("NOUVEAU UTILISATEUR CONNECTE: "+msg);
						this.donnees = new UserConnecte(this.sockClient, this.pseudo);
						enregistre = true;
						this.serveur.diffuser(this,"NOUVEAU UTILISATEUR CONNECTE: "+msg);
					}else{
						System.out.println("MESSAGE: "+msg);
						this.serveur.diffuser(this,msg);
					}
				}
			
				
			}
			
			
		} catch (IOException e) {
			
			
		} catch (ClassNotFoundException e) {
			
		}finally{
			//deconnexion
			try {
				
				this.serveur.deleteUser(this);
				
			} catch (FileNotFoundException e1) {
				
			} catch (IOException e1) {
				
			} catch (ClassNotFoundException e1) {
				
			}
		}
		
		
	}
	public Socket getSocClient(){
		return this.sockClient;
	}
	
	public UserConnecte getDonnees(){
		return this.donnees;
	}
	
	
	
}

class UserConnecte implements Serializable{
	private String pseudo;
	private String host;
	private String dateConnexion;
	private int port;
	
	public UserConnecte(Socket socket, String pseudo){
		this.host = socket.getInetAddress().getHostAddress();
		this.port = socket.getPort();
		this.pseudo = pseudo;
		this.dateConnexion = new Date().toString();
	}
	public String getDateConnexion(){
		return this.dateConnexion;
	}
	public String getPseudo(){
		return this.pseudo;
	}
}

