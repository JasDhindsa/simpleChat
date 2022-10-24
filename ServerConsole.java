
import java.io.*;
import common.*;

public class ServerConsole implements ChatIF{

	
	final public static int DEFAULT_PORT = 5555;
	EchoServer server = null;
	BufferedReader fromConsole = new BufferedReader(new InputStreamReader(System.in));

	public ServerConsole(int port){
		server = new EchoServer(port,this);
	}

	public void display(String msg){
		System.out.println(">" + msg);
    }
 
	public void accept(){
		try{
			String message;
			
			while (true){
				message = fromConsole.readLine();
				server.handleMessageFromServerUI(message);
			}
		} 
		catch (Exception ex) 
		{
			System.out.println
			("Unexpected error while reading from console!");
		}
	}
	

	 public static void main(String[] args){
		 int port; 
	
		 try{
			 port = Integer.valueOf(args[0]); 
		 }
		 catch(Exception e){
			 port = DEFAULT_PORT;
		 }
		
		 ServerConsole server = new ServerConsole(port);
	    
		 try{
			 server.server.listen();
		 } 
		 catch (Exception e){
			 System.out.println("Could not listen for clients");
		 }
		 server.accept();
	  }
	}

