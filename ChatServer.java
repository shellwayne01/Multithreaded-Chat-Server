package project3_v4;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/*ChatServer Version 4
 * Accept a client just like in server version 0,1
 * and print what the client said
 * new in version 1
 * repeat as long as the client does not say "bye"
 * new in version 2
 * prepare to accept another client ... use a new thread
 * new in version 3
 * handle multiple clients simultaneously
 * new in version 4
 * sent output from each client to ALL connected clients
 */

/*
 * Chat Server version 4 
 * - Waits and listens for clients. Able to take up to 5 clients.
 * - Handles Private Messages with One Time Pad
 * - Private messages must be sent with format private: encrypted <recipient> msg
 */

public class ChatServer implements Runnable{
	private ServerSocket server = null;
	private Thread thread = null;
	private ChatServerThread [] clients = new ChatServerThread[5];
	private static int clientCount = 0; 

	 
	public ChatServer(int port){//same as previous version
		try {
			System.out.println("Will start server on port "+port);
			server = new ServerSocket(port);//step 1 create a Server Socket remember to pass in the port
			start();
		} catch (IOException e) {
			System.err.println("My Server does not work "+ e.getMessage());
		}
	}
	
	public void start(){//same as previous version
		if(thread == null){
			thread = new Thread(this);
			thread.start();
		}
	}
	
	@Override
	public void run() {//same as previous version
		while(thread!=null){
			try{
				System.out.println("Will wait for a client to connect");
				//add a thread and accept a client on it... ChatServerThread
				addThread(server.accept());
				clientCount++;
				System.out.print(clientCount + " clients online.\n");
				
				
			}catch (IOException e) {
				System.err.println("YIKES!!! "+ e.getMessage());
			}
		}
	}

	//NOT BEING HANDLED
	//Handles any messages
	public void handle(int ID, String msg){//introduced in v4
//		System.out.print("existing msg read as ... "+msg);
		if (msg.startsWith("private:")) {
			String[] info;
			info = preparePrivate(msg);
			handlePrivate(ID, info);
		}
		else {
			for(int i=0; i<clientCount; i++){
				System.out.println("will send to "+clients[i].getSocketID() + " with msg= "+msg);
				
				if(clients[i].getSocketID() == ID) {  //had to create a publicly accessible method to see Socket ID variable in ChatClientThread
					clients[i].send("You said: "+ msg);
				}else {
				clients[i].send("User: "+ID+" said: "+ msg);
				}
			}
		}
	}
	
	
	//Retrieves info about private
	public String[] preparePrivate(String msg){
		//msg should be in the format private: (un)encrypted recipient <message>
		String[] msgArr;
		String recipient; 
		String message;
		String security; 
		int msgStartIndex; 
		String[] info;
		
		msgArr = msg.split(" "); 
		security = msgArr[1];
		recipient  = msgArr[2]; 
		msgStartIndex =  msg.lastIndexOf(recipient) + recipient.length() + 1 ; //return the index of first char in recipient + 
		message = msg.substring(msgStartIndex); //new msg
		info = new String[] {security,recipient, message};
		System.out.println("Will send private message intended for "+ recipient);
		return(info); 
	}
	
	
	//Handles private messages
	public void handlePrivate(int ID, String[] info){ //ID is the senders id
		int recipient;
		String msg; 
		String security; 
		security = info[0];
		recipient = Integer.parseInt(info[1]); //recipient as int
		msg = info[2];
		
		if (security.equalsIgnoreCase("Encrypted")){
			//Use one-time pad
			OneTimePad otp = new OneTimePad(msg);
			msg = otp.getEncrypted();
		}
	
		for(int i=0; i<clientCount; i++){
			if (clients[i].getSocketID() == recipient){ //intended recipient
				System.out.println("will send to "+clients[i].getSocketID() + " with msg= "+msg);	
				clients[i].send("<Private Message> User: "+ID+" said: "+ msg);
				//have an option for recipient to decrypt
			}else if (clients[i].getSocketID() == ID) {
				clients[i].send("<Private Message> You: "+ID+" said: "+ msg);
			}else {
				//do nothing
			}
		}
	}
	
	public void remove(int ID){//v4
		int loc = findClient(ID);//0
		if(loc >= 0 && loc < clientCount){
			ChatServerThread tempToClose = clients[loc];//clients[loc].close();
			for(int i = loc+1; (i <= clientCount && i < clients.length); i++){
				clients[i-1] = clients[i];//shift all from right to left
			}
			if(loc == clients.length-1){//special circumstance last index loc
				clients[loc] = null;
			}
			clientCount--;
			
			System.out.println("removed "+ID+" from index location "+loc);
			try {
				tempToClose.close();//end connections ... io streams and ChatServerThread
				System.out.println("closed streams on "+tempToClose.getId() );
			} catch (IOException e) {
				System.out.println("Problem removing client "+e.getMessage());
			}
		}
	}
	
	private int findClient(int ID){
		for(int i=0; i<clientCount; i++){
			if(clients[i].getSocketID() == ID){
				return i;//location of the client 
			}
		}
		return -1; //client not in the array
	}
	
	public void addThread(Socket socket){//similar but using the array in v4
		if( clientCount < clients.length ){
			clients[clientCount] = new ChatServerThread(this, socket);
			
			try{
				clients[clientCount].open();//open stream for ChatServerThread to handle input
				clients[clientCount].start();//start running ChatServerThread to handle the client
				
			}catch (IOException e) {
				System.err.println("Exception in addThread of ChatServer"+ e.getMessage());
			}
		}
		else{
			System.out.println("Client refused max num of clients is "+clients.length);
		}
	}

	public static void main(String [] args){
		//ChatServer myServer = new ChatServer(8080);
		if(args.length != 1){
			System.out.println("You need a port number to run your server");
		}
		else{
			int port = Integer.parseInt(args[0]);
			ChatServer myServer = new ChatServer(port);
		}
	}
	
	//Just In Case
	public static int getClientCount() {
		return clientCount;
		
	}
	
}
