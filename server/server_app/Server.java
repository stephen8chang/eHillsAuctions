package final_exam;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;


public class Server extends Observable{
	
	public static ArrayList<String> itemNames = new ArrayList<String>();
	public static ArrayList<String> itemDescriptions = new ArrayList<String>();
	public static ArrayList<Double> minimumPrice = new ArrayList<Double>();
	public static ArrayList<Double> currentBid = new ArrayList<Double>();
	//public static ArrayList<String> userWithCurrentBid = new ArrayList<String>();
	public static ArrayList<Integer> boughtOrNot = new ArrayList<Integer>();
	public static ArrayList<Double> highestBid = new ArrayList<Double>();
	public static ArrayList<String> images = new ArrayList<String>();
	
	ArrayList<String> currentClients;		//for handling multiple clients
		
	public static void main(String[] args) throws IOException{
		JsonReader reader = new JsonReader(new FileReader("./FinalExam_Server_swc746/final_exam/AuctionItemJsonFile.json"));
		AuctionItem[] ai = new Gson().fromJson(reader, AuctionItem[].class);
		try {
			Server a = new Server();
			a.setUpNetworking();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private void setUpNetworking() throws Exception {
		@SuppressWarnings("resource")
		ServerSocket serverSock = new ServerSocket(4242);
		while (true) {
			Socket clientSocket = serverSock.accept();
			//ClientObserver writer = new ClientObserver(clientSocket.getOutputStream());
			
//			Thread t = new Thread(new ClientHandler(this, clientSocket));
//			t.start();
//			this.addObserver(writer);
//			System.out.println("connected to client");
			
			ClientHandler handler = new ClientHandler(this, clientSocket);
		    this.addObserver(handler);

		    Thread t = new Thread(handler);
		    t.start();
		}
	}
	
	protected void processRequest(String input) {
		String[] parse = input.split(",");
		if(parse[3].equals("0")) {
            int itemIndex= Integer.parseInt(parse[1]); 
            double updatedBid = Double.parseDouble(parse[2]);
            currentBid.set(itemIndex, updatedBid);
            System.out.println("inside process request");
            this.setChanged();
            this.notifyObservers("updateBid,"+ itemIndex + "," + updatedBid);
		}
		if(parse[3].equals("1")) {
			int itemIndex= Integer.parseInt(parse[1]); 
            double updatedBid = Double.parseDouble(parse[2]); 
//			itemNames.remove(itemIndex);
//			itemDescriptions.remove(itemIndex);
//			minimumPrice.remove(itemIndex);
//			currentBid.remove(itemIndex);
//			highestBid.remove(itemIndex);
//			boughtOrNot.remove(itemIndex);
			boughtOrNot.set(itemIndex, 1);
            currentBid.set(itemIndex, updatedBid);
			this.setChanged();
			this.notifyObservers("removeItem,"+ itemIndex + "," + updatedBid);	
		}
		
		if(parse[0].equals("sendClientUpdatedData")) {
			//for clients after the first one, want to send updated current bids and item auction status
			System.out.println(currentBid.toString());
			for(int i = 0; i < 5; i++) {
				this.setChanged();
				this.notifyObservers("updatedCurrentBidForLaterClients," + i + "," + currentBid.get(i));
			}
			for(int i = 0; i < 5; i++) {
				this.setChanged();
				this.notifyObservers("updatedItemStatusForLaterClients," + i + "," + boughtOrNot.get(i));
			}
		}
		
		
	 }
	class ClientHandler implements Runnable, Observer {

		  private Server server;
		  private Socket clientSocket;
		  private BufferedReader fromClient;
		  private PrintWriter toClient;

		  protected ClientHandler(Server server, Socket clientSocket) {
		    this.server = server;
		    this.clientSocket = clientSocket;
		    try {
		      fromClient = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
		      toClient = new PrintWriter(this.clientSocket.getOutputStream());
		      
		      //send json object as string here ONLY when sendAuctionItemsFlag is true
		      JsonReader reader = new JsonReader(new FileReader("./FinalExam_Server_swc746/final_exam/AuctionItemJsonFile.json"));
			AuctionItem[] ai = new Gson().fromJson(reader, AuctionItem[].class);
		      for(int i = 0; i < ai.length; i++) {
					itemNames.add(ai[i].getItemName());
					itemDescriptions.add(ai[i].getItemDes());
					minimumPrice.add(ai[i].getMinPrice());
					currentBid.add(ai[i].getCurrBid());
					highestBid.add(ai[i].getHighestBidPossible());
					boughtOrNot.add(ai[i].getBoughtOrNot());		//represents not bought
					images.add(ai[i].getImageString());
				}
		      
		      for(int i = 0; i < ai.length; i++) {
					this.sendToClient("itemName," + ai[i].getItemName());
					this.sendToClient("itemDesc," + ai[i].getItemDes());
					this.sendToClient("minPrice," + ai[i].getMinPrice());
					this.sendToClient("currBid," + ai[i].getCurrBid());
			    	this.sendToClient("highestBidPossible," + ai[i].getHighestBidPossible());
			    	this.sendToClient("boughtOrNot," + ai[i].getBoughtOrNot());
			    	this.sendToClient("image," + ai[i].getImageString());
				}
		     // processRequest(ai);
		      
		    } catch (IOException e) {
		      e.printStackTrace();
		    }
		  }

		  protected void sendToClient(String string) {
		    System.out.println("Sending to client: " + string);
		    toClient.println(string);
		    toClient.flush();
		  }

		  @Override
		  public void run() {
		    String input;
		    try {
		      while ((input = fromClient.readLine()) != null) {
		        System.out.println("From client: " + input);
		        if(input.equals("logout")) {
		        	toClient.println("log,");
		        	toClient.flush();
		        	toClient.close();
		        	fromClient.close();
		        	clientSocket.close();
		        	return;
		        }
		        else
		        	server.processRequest(input);
		      }
		    } catch (IOException e) {
		      e.printStackTrace();
		    }
		  }
		  
		  
		  @Override
		  public void update(Observable o, Object arg) {
		    this.sendToClient((String) arg);
		  }
	}
}
