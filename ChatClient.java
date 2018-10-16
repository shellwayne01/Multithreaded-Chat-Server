package project3_v4;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

/*
 * Chat Client version 4
 * Essentially starts a new client, starts a new corresponding client thread, and reads in the client message from the console
 * 
 */

//Interacts with the server thread
public class ChatClient implements Runnable{
	private Socket socket = null;
	private DataOutputStream  strOut= null; //writes to . flush used to write everything out
//	private DataInputStream  strIn= null;  //reads from
//	private ByteArrayInputStream strIn=null;
	private Thread thread = null;
	private String line = "";
	private ChatClientThread client;
	private BufferedReader console= null;
	private GUI_Frame gui;
	private boolean done = false;
	
	public ChatClient(String serverName, int serverPort){
		
	javax.swing.SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				gui = new GUI_Frame();
			}
		});
		
		try {
			socket = new Socket(serverName, serverPort);//step 1 connect to server using Socket
			start();//step 2 open streams
			communicate();//step 3 communicate

			//stop();//step 4 close streams and sockets
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void start() throws IOException{//step 2 open streams
//		strIn =  new DataInputStream(new BufferedInputStream (socket.getInputStream()) );
		System.out.println("starting new chatclient with socket ID ..."); //socketID is currently determined in chatserverthread so inaccessible
		strOut =  new DataOutputStream(new BufferedOutputStream (socket.getOutputStream()) );
		console = new BufferedReader(new InputStreamReader(System.in)); //sets up environment for client msgs
	
		//Note: Later, Rather than reading in the console, data must be retrieved consistently from the GUI
		//Try again
		
		if(thread == null){
			client = new ChatClientThread(this, socket);
			thread = new Thread(this);
			thread.start();
			//Where should this go?
			//Is there another way to start a gui for each new client?
			//Does the run method in here interfere with the run method below?
		}
		
	}
	
	public void run(){
		while( (thread !=null) ){
			try{
				communicate(); //handle datainputstreams and outputstreams
			}catch(IOException e){
				System.out.println("Chat Client IO problem "
						+ "running thread to"
						+ " read line and send it");
			}
		}
	}
	
	//Still working on this. So many different methods that failed
	public void communicate() throws IOException{
		do{
			//Messages recieved by the client
			
			
			//Messages being sent by the client	
			//Chat client is reading in data from its own user and from the chat client thread
//			 line = console.readLine(); //client msg is read from console environment
			
			//nope 
//			Scanner scanner = new Scanner(System.in);
//	        String line = scanner.nextLine();
//	        System.out.println(line);
//			scanner.close();
			
			//nope
//	        line = strIn.readUTF();
//			System.out.println("User said: "+ line);
			

//			nope needs to be a constant stream cant just get client message from client gui
//			line = gui.getLatestMsg();
			
//			nope initial stream is null. read from gui stream
//			ByteArrayInputStream bInput = new ByteArrayInputStream(gui.b);
//			line = bInput.toString();
//			
			strOut.writeUTF(line);  // Writes a string to the underlying output stream using modified UTF-8 encoding
			strOut.flush(); //Forces any buffered output bytes to be written out to the stream.
			
		}while(!line.equalsIgnoreCase("bye")); //not working. Still listens
	}
	
	public void handle(String msg){
		if(msg.equalsIgnoreCase("bye")){
			System.out.println("Will stop client...Goodbye");
			line="bye";
			stop();
		}else if(msg.isEmpty()){
			//do nothing
		}
		else{
			System.out.println(msg); 
		
		}
		
	}
	
	
	public void stop() {
		try{
			if(console !=null){
				console.close();
			}
			
			if(strOut !=null){
				strOut.close();
			}
			if(socket !=null){
				socket.close();
			}
		}
		catch(IOException e){
			System.out.println("problem inside stop of "
					+ "ChatClient " + e.getMessage());
		}
	}
	
	public static void main(String [] args){
		//ChatClient myClient = new ChatClient("localhost", 8080);
		
		if(args.length != 2){
			System.out.println("You need a hostname and a port number to connect your client to a server");
		}
		else{
			String serverName = args[0];
			int port = Integer.parseInt(args[1]);
			ChatClient client = new ChatClient(serverName, port);
		}
	}
	
}
