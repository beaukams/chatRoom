package server;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ServerGui extends JFrame{
	
	private JMenuBar menuBar;

	private JMenu menuFichier;
	private JMenuItem menuItemQuitter;
	
	
	private JMenu menuServeur;
	private JMenuItem menuAddServeur;
	private JMenuItem menuRmServeur;
	private JMenuItem menuListeServeur;
	
	private JMenu menuAnnuaire;
	private JMenuItem menuAddContact;
	private JMenuItem menuRmContact;
	private JMenuItem menuSearchContact;
	private JMenuItem menuListeContact;
	
	private JMenu menuAide;
	private JMenuItem menuItemAide;
	private JMenuItem menuItemApropos;
	
	private JTextArea txtOutput = new JTextArea();
	private ChatRoomServer server;
	private boolean marche = false;
	
	public ServerGui(){
		super();
		this.setTitle("Serveur Chat");
		
		this.setSize(500, 400);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		JPanel panel = (JPanel)this.getContentPane();
		panel.setPreferredSize(new Dimension(500,400));
	    JScrollPane sclPane = new JScrollPane(this.txtOutput);
	    sclPane.setSize(500, 350);
	    
	    JPanel panb = new JPanel();
	    panb.setSize(500, 50);
	    panb.setLayout(new FlowLayout(FlowLayout.LEFT));
	    JLabel lab = new JLabel("Port "); lab.setSize(new Dimension(100,30));
	    final JTextField text = new JTextField(20); text.setPreferredSize(new Dimension(50,30));
	    final JButton demarre = new JButton("Demarrer"); demarre.setSize(new Dimension(200,30));
	    final JButton arrete = new JButton("Arreter"); arrete.setSize(new Dimension(200,30));
	    
	    
	    demarre.addActionListener(new ActionListener() {
			
            public void actionPerformed(ActionEvent e) {
            		if(marche==true){
            			JOptionPane.showMessageDialog(null,"Le serveur est deja demarré");
            		}else if(text.getText().equals("") || text==null){
                		JOptionPane.showMessageDialog(null,"Veuiller donner le numéro de port!");
                	}else{
                		
                		try {
							server = new ChatRoomServer(Integer.parseInt(text.getText()), txtOutput);
							//server.startServer();
	                		marche = true;
						} catch (NumberFormatException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
                		
                		
                	}
            }
        });
	    
	    arrete.addActionListener(new ActionListener() {
			
            public void actionPerformed(ActionEvent e) {
                	if(marche == true){
                		marche = false;
                		server.stopServer();
                	}else{
                		JOptionPane.showMessageDialog(null,"Le serveur est deja arrété");
                	}
            }
        });
		
	    
	    panb.add(lab);
	    panb.add(text);
	    panb.add(demarre);
	    panb.add(arrete);
	    
	    
	    panel.add(sclPane, BorderLayout.CENTER);
	    panel.add(panb, BorderLayout.SOUTH);

	    this.txtOutput.setBackground(new Color(220,220,220));
	    this.txtOutput.setEditable(false);
	    
	    this.pack();
	    this.setDefaultCloseOperation(EXIT_ON_CLOSE);
	    this.setLocation(600, 10);
		this.setVisible(true);
	}
	
	public static void main(String args []){
		new ServerGui();
	}
}
