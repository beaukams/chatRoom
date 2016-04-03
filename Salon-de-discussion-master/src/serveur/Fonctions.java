package serveur;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.crypto.dsig.DigestMethod;

/**
 *
 * @author mansour
 */
public class Fonctions {
    
    public static Connection connect;	
    
    
    
    /**
     * verifie si le nombre de users est different de 0
     * @param pseudo
     * @return 
     */
    public static boolean verifIdNotNull(){
        try{
            connect = ConnexionBD.getConnexion();
            String req = "select count(id_user) from utilisateur";
            Statement s=connect.createStatement();
            ResultSet resultats=s.executeQuery(req);
            resultats.next();
            if(resultats.getInt(1)!=0){
                return true;
            }
            connect.close();
        }catch(SQLException e)
        {
            System.out.println(e.getMessage());
        }
            return false;
    }
    
    /**
     * recupere l'id de l'utilisateur pseudo
     * @param pseudo
     * @return 
     */
    public static int getIdClient(String pseudo){
            int id =-1;
            try{
                    connect = ConnexionBD.getConnexion();
                    String req = "select id_user from utilisateur where pseudo='"+pseudo+"'";
                    if(verifIdNotNull()){
                        Statement s=connect.createStatement();
                        ResultSet resultats = s.executeQuery(req);
                        resultats.next();
                        id = resultats.getInt(1);
                    }
                    connect.close();

            }catch(SQLException e)
            {
                System.out.println(e.getMessage());
            }
        return id;
    }
    
    /**
     * 
     * @param pseudo
     * @return identifiant contact
     */
    public static int getIdContact(String pseudo,int id_user){
        int id =-1;
            try{
                    connect = ConnexionBD.getConnexion();
                    String req = "select id_contact from contact,utilisateur where utilisateur.pseudo='"+pseudo+"' and contact.id_user='"+id_user+"'";
                   
                    Statement s=connect.createStatement();
                    ResultSet resultats = s.executeQuery(req);
                    resultats.next();
                    if(resultats.getInt(1)!=0)
                        id = resultats.getInt(1);
                    
                    connect.close();

            }catch(SQLException e)
            {
                System.out.println(e.getMessage());
            }
        return id;
    }
    
    /**
     * 
     * @param room
     * @return idenetifiant du room
     */
    public static int getIdRoom(String room, int id_user){
        int id =-1;
            try{
                    connect = ConnexionBD.getConnexion();
                    String req = "select id_room from room,utilisateur where room.nom_room='"+room+"' and room.id_user='"+id_user+"'";
                   
                    Statement s=connect.createStatement();
                    ResultSet resultats = s.executeQuery(req);
                    resultats.next();
                    if(resultats.getInt(1)!=0)
                        id = resultats.getInt(1);
                    
                    connect.close();

            }catch(SQLException e)
            {
                System.out.println(e.getMessage());
            }
        return id;
    }
    /**
     * crypter un mot de passe en sha1
     * @param input
     * @return
     *  
     */
    public static String sha1(String input) {
        MessageDigest mDigest;
        StringBuffer sb = new StringBuffer();   
        try {
            mDigest = MessageDigest.getInstance("SHA1");
            byte[] result = mDigest.digest(input.getBytes());
            for (int i = 0; i < result.length; i++) {
                sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
            }
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Fonctions.class.getName()).log(Level.SEVERE, null, ex);
        }
        
         
        return sb.toString();
    }
    /**
     * ajouter un user dans la base
     * @param nom
     * @param prenom
     * @param pseudo
     * @param mdp
     * @param ip
     * @param port
     * 
     */
    public static void ajouterUser(String nom, String prenom, String pseudo, String mdp, String ip, int port){
        try{    
                String pwd= sha1(mdp);
                connect = ConnexionBD.getConnexion();
                String req = "insert into utilisateur(pseudo,nom,prenom,password, adresse_ip,port,date_connexion) values ('"+pseudo+"','"+nom+"','"+prenom+"','"+pwd+"','"+ip+"','"+port+"','"+LocalDate.now()+"')" ;
                PreparedStatement ps = connect.prepareStatement(req);
                ps.execute();
                connect.close();
            }catch(SQLException e)
            {
                System.out.println(e.getMessage());
            }
    }
    
    /**
     * ajouter contact
     * @param id_user
     * 
     */
    public static void ajouterContact(int id_contact, int id_user){
        try{    
                connect = ConnexionBD.getConnexion();
                String req = "insert into contact(id_contact, id_user) values ('"+id_contact+"','"+id_user+"')" ;
                PreparedStatement ps = connect.prepareStatement(req);
                ps.execute();
                connect.close();
            }catch(SQLException e)
            {
                System.out.println(e.getMessage());
            }
    }

    /**
     * ajouter room
     * @param nom_room
     * @param id_user
     * @param id_contact 
     */
    public static void ajouterRoom(String nom_room, int id_user, int id_contact){
        try{    
                connect = ConnexionBD.getConnexion();
                String req = "insert into room(nom_room,id_user,id_contact) values ('"+nom_room+"','"+id_user+"','"+id_contact+"')" ;
                PreparedStatement ps = connect.prepareStatement(req);
                ps.execute();
                connect.close();
            }catch(SQLException e)
            {
                System.out.println(e.getMessage());
            }
    }
    /**
     * connexion de user avec pseudo et mdp
     * 
     * @param pseudo
     * @param mdp
     * @return un tableau des infos de user 
     */
    public static String[] connexionUser(String pseudo, String mdp){
        String tab[] = new String[8];
        try{
            connect = ConnexionBD.getConnexion();
            if(verifIdNotNull()){
                String req = "select * from utilisateur where pseudo='"+pseudo+"' and password='"+sha1(mdp)+"'";
                Statement s=connect.createStatement();
                ResultSet resultats=s.executeQuery(req);
                while(resultats.next()){
                    for (int i = 1; i <= 8; i++)
                        tab[i-1] = resultats.getString(i);
                   
                }
            }else{
                tab=null;
                System.out.println("database is empty");
            }
            connect.close();
        }catch(SQLException e)
        {
            System.out.println(e.getMessage());
        }
        
        return tab;
    }
    
    /**
     * supprimer contact
     * @param id_contact 
     */
    public static void SupprimerContact(int id_contact){
        try{    
              connect = ConnexionBD.getConnexion();
              String req = "delete from contact where id_contact='"+id_contact+"'" ;
              PreparedStatement ps = connect.prepareStatement(req);
              ps.execute();
              connect.close();
          }catch(SQLException e)
          {
              System.out.println(e.getMessage());
          }
    }
    
    /**
     * supprimer room
     * @param id_room 
     */
    public static void SupprimerRoom(int id_room){
        try{    
              connect = ConnexionBD.getConnexion();
              String req = "delete from room where id_room = '"+id_room+"'" ;
              PreparedStatement ps = connect.prepareStatement(req);
              ps.execute();
              connect.close();
          }catch(SQLException e)
          {
              System.out.println(e.getMessage());
          }
    }
   
    /**
     * liste des users 
     * @return 
     */
    public Object []listUsers(){
        ArrayList<String> list = new ArrayList<String>();
        try{
                connect = ConnexionBD.getConnexion();
                String req = "select pseudo,nom,prenom from utilisateur";
                Statement s=connect.createStatement();
                ResultSet resultats=s.executeQuery(req);

                while(resultats.next()){
                    String user;
                    user = resultats.getString(1)+"("+resultats.getString(2)+" "+resultats.getString(3)+")";
                    list.add(user);
                }
                connect.close();

        }catch(SQLException e)
        {
                System.out.println(e.getMessage());
        }
        return list.toArray();
    }
    
    /**
     * liste des rooms de user
     * @param id_user
     * @return 
     */
    public Object []listRooms(int id_user){
        ArrayList<String> list = new ArrayList<String>();
        try{
                connect = ConnexionBD.getConnexion();
                String req = "select nom_room from room where id_user='"+id_user+"'";
                Statement s=connect.createStatement();
                ResultSet resultats=s.executeQuery(req);

                while(resultats.next()){
                    String room;
                    room = resultats.getString(1);
                    list.add(room);
                }
                connect.close();

        }catch(SQLException e)
        {
                System.out.println(e.getMessage());
        }
        return list.toArray();
    }
    
    /**
     * 
     * @param id
     * @return un utilisateur 
     */
    public String getUserById(int id){
        String user="";
        try{
                connect = ConnexionBD.getConnexion();
                String req = "select pseudo,nom,prenom from utilisateur where id_user='"+id+"'";
                Statement s=connect.createStatement();
                ResultSet resultats = s.executeQuery(req);
                resultats.next();
                user = resultats.getString(1)+"("+resultats.getString(2)+" "+resultats.getString(2)+")";
                connect.close();

        }catch(SQLException e)
        {
            System.out.println(e.getMessage());
        }
        return user;

    }
    /**
     * 
     * @param id_user
     * @return liste des contacts de user 
     */
    public Object []listContacts(int id_user){
        ArrayList<String> list = new ArrayList<String>();
        try{
                connect = ConnexionBD.getConnexion();
                String req = "select id_contact from contact where id_user='"+id_user+"'";
                Statement s=connect.createStatement();
                ResultSet resultats=s.executeQuery(req);
                
                while(resultats.next()){
                    int id;
                    id = resultats.getInt(1);
                    list.add(getUserById(id));
                }
                connect.close();

        }catch(SQLException e)
        {
                System.out.println(e.getMessage());
        }
        return list.toArray();
    }
}
