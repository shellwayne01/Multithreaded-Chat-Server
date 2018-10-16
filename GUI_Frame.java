package project3_v4;

import java.awt.BorderLayout;
import java.awt.Color;
//import java.awt.Component;
import java.awt.Dimension;
//import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
//import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Console;
import java.io.IOException;
import java.util.Scanner;

//import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
//import javax.swing.JTextField;
import javax.swing.JToggleButton;

public class GUI_Frame extends JFrame{
	private MainChatPanel mainChatJP; 
	private PrivateChatPanel privateChatJP; //can have multiple
	private UsersPanel usersJP; 
	private Font font0 = new Font("Serif", Font.BOLD, 32);
	private int clientCount; //get count from chatserver
	private String msg = "defaultmsg";
	ByteArrayOutputStream bOutput;
	byte[] b; //null on start
	
	Color customColor1 = Color.decode("#DEC8FA");
	Color customColor2 = Color.decode("#E0E0E0");
	
	public GUI_Frame(){
	 	setSize(1200,800);
		setResizable(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
		
		JPanel mainJP = new JPanel();  //Main chat server panel
		mainJP.setLayout(new BorderLayout());
		JPanel northJP = new JPanel(); //Sections of main chat server panel
		JPanel eastJP = new JPanel();
		JPanel westJP = new JPanel();
		JPanel southJP = new JPanel();
		JPanel centerJP = new JPanel();
		
		
		JLabel title = new JLabel("Chat Server");
		title.setFont(font0);
		mainChatJP = new MainChatPanel();
		privateChatJP = new PrivateChatPanel(); //dont really need private chatJP available yet
		usersJP = new UsersPanel(); 
		
		JLabel labelEast = new JLabel("Placeholder for private chat");
		JLabel labelWest = new JLabel("Placeholder for extra settings");
		JLabel labelSouth = new JLabel("Placeholder for users online now");
		JLabel labelCenter = new JLabel("Placeholder for main chat");

		northJP.setBackground(Color.CYAN);
		eastJP.setBackground(Color.YELLOW);
		westJP.setBackground(Color.YELLOW);
		southJP.setBackground(Color.GREEN);
		centerJP.setBackground(Color.PINK);
		
		northJP.add(title);
		eastJP.add(labelEast); 
//		eastJP.add(privateChatJP); // Needs Updating
		westJP.add(labelWest); //Create a settings and extras panel to add to west section
		southJP.add(labelSouth);
//		southJP.add(usersJP); //Needs Updating
//		centerJP.add(labelCenter);
		centerJP.add(mainChatJP);
		
		mainJP.add(northJP, BorderLayout.NORTH);
		mainJP.add(eastJP, BorderLayout.EAST);
		mainJP.add(westJP, BorderLayout.WEST);
		mainJP.add(southJP, BorderLayout.SOUTH);
		mainJP.add(centerJP, BorderLayout.CENTER);
		add(mainJP); 
	}
	
	public void updateGUI() {
		usersJP.generateUserIcons();
	}
	
	private class MainChatPanel extends JPanel implements ActionListener, ItemListener{
		private JPanel userChatOptions;
		private JTextArea userInput;
		private JButton sendBtn;
		private JButton sendPrivateBtn;
		private JToggleButton encryptBtn; 
		
		//create an output stream for chat client to later read from
		
		private JScrollPane mainChatScroll;
		private JTextArea mainChat;
		private Font font1 = new Font("Serif", Font.PLAIN, 18);
		
		public MainChatPanel() {
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			
			userChatOptions = new JPanel(); //modify width
			userChatOptions.setBackground(Color.PINK);
			userInput = new JTextArea(3,39);  //fix
			userInput.setLineWrap(true);
			userInput.setFont(font1);
			sendBtn = new JButton("Send");
			sendPrivateBtn = new JButton("PM");
			encryptBtn = new JToggleButton("Encrypt");
			
			
			mainChat = new JTextArea(30,50); //fix. the columns must match or will be forced to match
			mainChat.setBackground(Color.WHITE);
			mainChat.setFont(font1);
			mainChat.setLineWrap(true);
			mainChat.setEditable(false);
			mainChatScroll = new JScrollPane(mainChat);
			
			sendBtn.addActionListener(this);
			sendPrivateBtn.addActionListener(this);
			encryptBtn.addItemListener(this);
			
			userChatOptions.add(userInput);
			userChatOptions.add(sendBtn);
			userChatOptions.add(sendPrivateBtn);
			userChatOptions.add(encryptBtn);
			
			add(userChatOptions);
			add(mainChatScroll);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			JButton chosenbtn = (JButton) e.getSource();
			msg = userInput.getText().trim();
			
			
			if (chosenbtn == sendBtn) {
				//send message to the mainChat and to the chat server
				mainChat.append("You said: " + msg + "\n" );
				ByteArrayOutputStream bOutput = new ByteArrayOutputStream();
				try {
					bOutput.write(msg.getBytes());
					b = bOutput.toByteArray();
				} catch (IOException e1) {
					System.out.println("Problem getting user input from gui as byte stream.");
				}
				
//				ByteArrayInputStream input = new ByteArrayInputStream(msg.getBytes());
//				System.setIn(input);
				
//				System.out.println(msg);
//				Console.printf(msg);
				
			}else if (chosenbtn == sendPrivateBtn) {
				//create a new private chat and send to intended recipient
				System.out.println("Send a private message. Be sure recipient is selected.");
			}
			userInput.setText("");	
		}
		
		//Only one toggle button so this is fine
		public void itemStateChanged(ItemEvent e) {
		    if (e.getStateChange() == ItemEvent.SELECTED) {
		        System.out.println("Encryption is selected.");
		    } else {
		    	 System.out.println("No Encryption. Send regular message.");   
		    }
		}

	}
	
	
	
	//Addition
	private class PrivateChatPanel extends JPanel{
		private JTextArea subChat;
		private JScrollPane subChatScroll;
		
		private JPanel userOptions;
		private JTextField userInput;
		private JButton sendPrivateBtn;
		
		public PrivateChatPanel() {
//			setBackground(Color.PINK);
//			setLayout(new BorderLayout(this, BoxLayout.Y_AXIS));
			setSize(300,500);
			
			subChat = new JTextArea(300,100);
			subChat.setBackground(Color.GRAY);
			subChatScroll = new JScrollPane(subChat);
		
			
			userInput = new JTextField();
			userInput.setPreferredSize( new Dimension( 300, 24 ) );
			sendPrivateBtn = new JButton("PM");
			
			userOptions = new JPanel();
			userOptions.setSize(300, 100);
			userOptions.add(userInput);
			userOptions.add(sendPrivateBtn);
			
			add(subChatScroll);
			add(userOptions);
			
		}
	}
	
	
	
	
	
	
	
	private class UsersPanel extends JPanel{
		private JLabel[] iconImgs; //for icon
		private JLabel[] usernames;
//		private JLabel[] nicknames;
		private Font font1 = new Font("Helvetica", Font.BOLD, 18);
				
		public UsersPanel() {
			setBackground(Color.GREEN);
//			setSize(1200,150);
			setLayout(new GridLayout(2,clientCount));
//			populateUserNames();
			generateUserIcons();
//			nicknames = populateNickNames(); 
		}
		
		//Ignore - See next method
		public JLabel[] populateUserNames() {
			JLabel[] usernames = new JLabel[clientCount];
			//for each client in the chat create a new label with user ID
			for (int i=0; i<clientCount; i++) {
				JLabel username = new JLabel("User "+ i);
				username.setPreferredSize(new Dimension(100, 100));
				username.setFont(font1);
				usernames[i] = username;
				add(username);
			}
			return usernames;
		}
		
		public JLabel[] generateUserIcons() {
			clientCount = ChatServer.getClientCount();
			JLabel[] usernames = new JLabel[clientCount];
			JLabel[] iconImgs = new JLabel[clientCount];
			JLabel icon = new JLabel();
//			icon.setPreferredSize(new Dimension(100, 100));
			
			
			//WOW THIS PART IS SOOOOOOO DIFFICULT :/
			//In order to resize images this was the method most people seemed to use
//			BufferedImage img = null;
//			try {
//				img = ImageIO.read(new File("randomuser_1_10.png"));
//				
//				//Resize the original image to proportional size
//				Image imgModified = img.getScaledInstance(icon.getWidth(), icon.getHeight(),
//				        Image.SCALE_SMOOTH);
//				ImageIcon iconImg = new ImageIcon(imgModified);
//				
//				for (int i=0; i<clientCount; i++) { 
//					icon = new JLabel(iconImg);
//					iconImgs[i] = icon;
//					add(icon);
//					int w = iconImg.getIconWidth();
//			        int h = iconImg.getIconHeight();
//			        System.out.println("Icon Width= "+w+"\nIcon Height= "+h);
//				}
//			} catch (IOException e) {
//			    e.printStackTrace();
//			    System.out.println("still cant read the image");
//			}
			
			
			ImageIcon iconImg = new ImageIcon(getClass().getResource("randomuserSmall.png")); 
			for (int i=0; i<clientCount; i++) { 
				//User name label
				JLabel username = new JLabel("user "+ i, JLabel.CENTER);
//				username.setPreferredSize(new Dimension(100, 100));
				username.setFont(font1);
				usernames[i] = username;
				add(username);
//				
//				int w = iconImg.getIconWidth();
//		        int h = iconImg.getIconHeight();
//		        System.out.println("Icon Width= "+w+"\nIcon Height= "+h);
			}
			
			for (int i=0; i<clientCount; i++) { //User icon img
				icon = new JLabel(iconImg);
				icon.setPreferredSize(new Dimension(100, 100)); //works for labels not icons
				iconImgs[i] = icon;
				
				add(icon);
			}
			return iconImgs;
		}
	}
	
	
	public String getLatestMsg(){
		return msg;
	}
}
	
