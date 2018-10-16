package project3_v4;

/*
 * Chat Server Thread version 4 - For project
 * 
 */

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
//Representative of each clients interaction with the server socket

public class ChatServerThread extends Thread{
	private ChatServer server = null;
	private Socket socket = null;
	private DataInputStream strIn = null;
	private DataOutputStream strOut = null;
	private int ID = -1;
	public String msgIn = "";
	
	public ChatServerThread(ChatServer _server, Socket _socket){
		super();
		server = _server;
		socket = _socket;
		ID = socket.getPort(); //SocketID
		System.out.println("New ChatServerThread \n INFO: server= "+server+ " socket= "+socket+ " ID="+ID);
//		Gets opened and started automatically by chatserver
//		open(); 
//		start();
	}

	//Open the I/O streams
	public void open() throws IOException{
		//Reading in data
		strIn = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
		
		//Writing out data to send see send() method
		strOut = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
		System.out.println("Opened Input & Output Streams Successfully for thread.");
		
//		catch(IOException e) {
//			System.out.print("Error while opening I/O streams");
//		}
	}

	//Sends messages handled by the server to the appropriate clients
	public void send(String msg){//introduced in v4
		try {
			strOut.writeUTF(msg); //writes to output stream
			strOut.flush();
		} catch (IOException e) {
			server.remove(ID); //on server call remove pass in my ID
			ID = -1; //set my ID to -1
		}
	}
	
	@Override
	public void run(){ //is reading the input just fine
		try{
			while(ID != -1){  //while there is a thread continuously handle the datainputstream
				server.handle(ID, strIn.readUTF());//v4 get input and send out to intended clients using the server's handle
			}
		}catch(IOException e){
			System.out.println("Exception running ChatServerThread "+e.getMessage());
		}
	}
	
	public void close() throws IOException{//step 5 close streams and sockets
		if(strIn!= null){
			strIn.close();
		}
		if(strOut != null){
			strOut.close();//introduced in v4
		}
		if(socket!= null){
				socket.close();
		}
	}
	
	public int getSocketID(){
		return ID; //Socket ID
	}
	

}
