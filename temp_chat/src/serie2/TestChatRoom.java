package serie2;

import java.io.IOException;

/**
 * 
 * @author abdoulaye
 *
 */
public class TestChatRoom {
	public static void main(String args []){
		try {
			ChatRoom serv = new ChatRoom(29999, 100);
			serv.demarrer();
		} catch (IOException e) {}
		
	}
}
