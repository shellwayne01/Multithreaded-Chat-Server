package project3_v4;

/*
 * Chat Client Thread version 4
 * 
 */


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;


public class ChatClientThread extends Thread{

	private Socket socket = null;
	private ChatClient client = null;
	private DataInputStream strIn = null; //read from
	private DataOutputStream strOut = null; //write to 
	private boolean done = true;
	
	public ChatClientThread(ChatClient _client, Socket _socket){
		client = _client;
		socket = _socket;
		open();
		start();//start this thread so it can call run
	}
	
	public void open(){
		try {
			strOut = new DataOutputStream(new BufferedOutputStream (socket.getOutputStream()) );
			strIn = new DataInputStream(new BufferedInputStream (socket.getInputStream()) ); 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Inside ChatClientThread Problem opening Stream In "+e.getMessage());
		}
	}
	
	public void run(){
		done = false; //when is done going to be true?
			while(!done){
			try {
//				System.out.println("ClientThread checking for valid client input to handle...");
				client.handle(strIn.readUTF());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void close(){
			try {
				done = true; //just gonna put this here for now
				if(strOut !=null){
					strOut.close();
				}
				if(socket !=null){
					socket.close();
				}
			}catch (IOException e) {
				System.out.println("In ChatClientThread closing streams problem " );
			}
		}
	
	
	
	
}
