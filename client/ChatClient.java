// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com

package client;

import ocsf.client.*;
import common.*;
import java.io.*;



/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 * @version July 2000
 */
public class ChatClient extends AbstractClient
{
  //Instance variables **********************************************
  
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  ChatIF clientUI; 
  String login;

  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the chat client.
   *
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   */
  
  public ChatClient(String login, String host, int port, ChatIF clientUI) 
    throws IOException 
  {
    super(host, port); //Call the superclass constructor
    this.login=login;
    this.clientUI = clientUI;
    openConnection();
    String msg = "#login "+login;
    sendToServer(msg);
  }

  protected void connectionException(Exception exception) {
    clientUI.display("The server has shut down");
    connectionClosed(false);
	}

  public  void connectionClosed(boolean x) {
    if(!x){
      System.exit(0);
    }
    
	}

  
  //Instance methods ************************************************
    
  /**
   * This method handles all data that comes in from the server.
   *
   * @param msg The message from the server.
   */
  public void handleMessageFromServer(Object msg) 
  {
    clientUI.display(msg.toString());
  }

  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
   * @throws IOException
   */

  private void handleCommand(String command) throws IOException{

    if(command.contains("sethost")){
      if(isConnected() == false){
        String host = "";
        for(int i = 7; i < command.length(); i ++){
          host += command.charAt(i);
        }
        setHost(host);
      }else{
        throw  new IOException("Client already connected");
      }
    }
    if(command.contains("setport")){
      if(isConnected() == false){
        String numberOnly= command.replaceAll("[^0-9]", "");
        setPort(Integer.valueOf(numberOnly));
      }else{
        throw  new IOException("Client already connected");
      }
    }

      switch(command){
        case "quit":
            closeConnection();
            quit();
            break;
    
        case "logoff": 
          if(isConnected()){
            closeConnection();
      
        }else{
          throw new IOException("Client not connected already");
        }
        break;
            
        case "login":
            if(isConnected() == false){
              openConnection();
          
            }else{
              throw new IOException("Client already connected");
            }
            break;
        case "gethost":
          clientUI.display("Host: "+ getHost());
          break;
        case "getport":
           clientUI.display("Port: "+ getPort());
           break;

        default:
           throw new IOException("Invalid Command"); 
           
      }

    }
  public void handleMessageFromClientUI(String message) throws IOException
  {
    message = message.trim();
    String command = "";
    for(int i =1; i <message.length();i++){
      command += message.charAt(i);
    }

    if(message.charAt(0) == '#'){
      try{ handleCommand(command);}
      catch(Exception e){
        System.out.println(e);
      }
    }else{
      try
      {
        sendToServer(message);
      }
      catch(IOException e)
      {
        clientUI.display
          ("Could not send message to server.  Terminating client.");
        quit();
      }
  }
}
  
  /**
   * This method terminates the client.
   */
  public void quit()
  {
    try
    {
      closeConnection();
    }
    catch(IOException e) {}
    System.exit(0);
  }
}
//End of ChatClient class
