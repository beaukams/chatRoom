package server;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 *
 * @author mansour
 */
public class Connexion {

    private static final String hote="jdbc:mysql://localhost:3306/";
    private static final String bdd="chat";
    private static final String user="abdoulaye";
    private static final String password="kamstelecom";

    public static Connection connection;
	public static Connection getConnexion(){	
            try {
                    Class.forName("com.mysql.jdbc.Driver");
                    connection = (Connection) DriverManager.getConnection(hote+bdd,user,password);
                }catch (Exception e) {
                    e.printStackTrace();
                }
                    return connection ;

	}

}

