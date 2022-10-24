// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com


import java.io.IOException;
import common.ChatIF;
import ocsf.server.*;

/**
 * This class overrides some of the methods in the abstract 
 * superclass in order to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 * @version July 2000
 */
public class EchoServer extends AbstractServer
{
  //Class variables *************************************************
  
  /**
   * The default port to listen on.
   */
  final public static int DEFAULT_PORT = 5555;
  private ChatIF server;
  private boolean serverstatus;
  private String loginUserName;
  private boolean beenBefore = false;
  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the echo server.
   *
   * @param port The port number to connect on.
   */
  public EchoServer(int port) 
  {
    super(port);
  }

  public EchoServer(int port, ChatIF serverConsole) {
    super(port);
    this.server = serverConsole;
}

// public void clientConnected(ConnectionToClient client){
    
//       System.out.println("Client has connected! ");
//   }

  // synchronized protected void clientException( ConnectionToClient client, Throwable exception) {
  //   System.out.println("Client has disconnected! ");
  // }

  // public void clientDisconnected(ConnectionToClient client){
  //   System.out.println("Client has disconnected! ");
  // }

   public void handleMessageFromServerUI(String message){

    message = message.trim();
    String command = "";
    if(message.charAt(0) == '#'){
    for(int i =1; i <message.length();i++){
      command += message.charAt(i);
    }
   }

    if(message.charAt(0) == ' '){
      command=message;
    }

	  if(command != ""){
			try{
        handleCommand(command);
			}
			catch(IOException e){
				System.out.println(e);
			}
		}
	  else{
      sendToAllClients("SERVER MESSAGE>" + message);
		  server.display("SERVER MESSAGE>" + message);
		  
	  }
  }
  
  private void handleCommand(String command) throws IOException{
	  //create string array to handle setHost and setPort

    if(command.contains("setport")){
      if(serverstatus== false){
        String numberOnly= command.replaceAll("[^0-9]", "");
        setPort(Integer.valueOf(numberOnly));
      }else{
        throw  new IOException("Client already connected");
      }
    }
	  
	  switch (command){ 
	  	case "quit" : 
          System.exit(0);
	  	  	break;	
      case "close": 
        close();
		  	break;
		  case "stop" : 
        stopListening();
		  	break;
		  
		  case "start":
        if(isListening() == false) {
          listen();
          }
        else{
          throw new IOException("Currently listening");
        }
        break;
		  case "getport":
			  server.display(""+getPort());
			  break;
		  default:
			  throw new IOException("Invalid Command"); 
		  	
	  }
  }
  
  synchronized protected void clientException(ConnectionToClient client, Throwable exception) {
	  clientDisconnected(client);
  }

  synchronized protected void clientDisconnected(ConnectionToClient client) {
    beenBefore = true;
	  System.out.println(client.getInfo("userName") + " has disconnected");
  }


  protected void clientConnected(ConnectionToClient client) {

    System.out.println("A new client is attempting to connect to the server.");
    client.setInfo("fmsg", true);

    if(beenBefore){
      handleMessageFromClient("#login "+ loginUserName , client);
    }
	
  }
  

  //Instance methods ************************************************
  
  /**
   * This method handles any messages received from the client.
   *
   * @param msg The message received from the client.
   * @param client The connection from which the message originated.
   */
  public void handleMessageFromClient(Object msg, ConnectionToClient client)
  {
    // System.out.println("Message received: " + msg + " from " + client);
    // this.sendToAllClients(msg);
    String[] messages = ((String)msg).split(" ");
    boolean fmsg = (boolean) (client.getInfo("fmsg"));

   

    System.out.println("Message received: " + msg + " from " + client.getInfo("userName"));
    if(fmsg){	
			client.setInfo("fmsg", false);
      String login = messages[0];
      String loginID = messages[1];
      loginUserName = loginID;
      if(login.contains("#login")){
				client.setInfo("userName", loginID);
        System.out.println(client.getInfo("userName")+ " has logged on.");
			}
			else{
				try{
					client.sendToClient(" Not logged in!");
					client.close();
				}
				catch(IOException e){
          System.out.println(e);
				}
			}
		}
		else{
			if(((String)msg).contains("#login")){
				try{
					client.sendToClient("Already logged in");
				}
				catch(IOException e){
          System.out.println(e);
				}
			}
       
		    sendToAllClients(client.getInfo("userName") + "> " + msg);
		}
    
  }
    
  
  /**
   * This method overrides the one in the superclass.  Called
   * when the server starts listening for connections.
   */
  protected void serverStarted(){
    serverstatus = true;
    System.out.println("Server listening for connections on port " + getPort());
     
  }
  
  /**
   * This method overrides the one in the superclass.  Called
   * when the server stops listening for connections.
   */
  protected void serverStopped(){
    serverstatus = false;
  
  }
  
  //Class methods ***************************************************
  
  /**
   * This method is responsible for the creation of 
   * the server instance (there is no UI in this phase).
   *
   * @param args[0] The port number to listen on.  Defaults to 5555 
   *          if no argument is entered.
   */
  public static void main(String[] args) 
  {
    int port = 0; //Port to listen on

    try
    {
      port = Integer.parseInt(args[0]); //Get port from command line
    }
    catch(Throwable t)
    {
      port = DEFAULT_PORT; //Set port to 5555
    }
	
    EchoServer sv = new EchoServer(port);
    
    try 
    {
      sv.listen(); //Start listening for connections
    } 
    catch (Exception ex) 
    {
      System.out.println("ERROR - Could not listen for clients!");
    }
  }


}
//End of EchoServer class
