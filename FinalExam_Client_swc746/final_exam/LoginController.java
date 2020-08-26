package final_exam;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class LoginController extends Observable{
	@FXML
	private Label lblStatus;
	
	@FXML
	private TextField txtUsername;
	
	private static String host = "127.0.0.1";
	private BufferedReader fromServer;
	private PrintWriter toServer;
	private Scanner consoleInput = new Scanner(System.in);
	  
	
	public static ArrayList<String> itemNames = new ArrayList<String>();
	public static ArrayList<String> itemDescriptions = new ArrayList<String>();
	public static ArrayList<Double> minimumPrice = new ArrayList<Double>();
	public static ArrayList<Double> currentBid = new ArrayList<Double>();
	//public static ArrayList<String> userWithCurrentBid = new ArrayList<String>();
	public static ArrayList<Integer> boughtOrNot = new ArrayList<Integer>();
	public static ArrayList<Double> highestBid = new ArrayList<Double>();
	public static ArrayList<String> images= new ArrayList<String>();
	
	
	public void login(ActionEvent event) throws Exception {
		if(!txtUsername.getText().equals("")) {
			lblStatus.setText("Login: Success");
			Stage primaryStage = new Stage();																//client page
//			BorderPane root = (BorderPane)FXMLLoader.load(getClass().getResource("Client.fxml"));
			FXMLLoader loader = new FXMLLoader(getClass().getResource("Client.fxml"));
			Parent root = loader.load();
			ClientController controller = (ClientController) loader.getController();
			controller.welcomeName.setText("Welcome " + txtUsername.getText() + "!");
			
			setUpNetworking(controller);
			
			Scene scene = new Scene(root,600,400);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.setTitle(txtUsername.getText() + "'s Auction Page!");
			primaryStage.show();
						
			controller.setComboBoxData();
			
			toServer.println("sendClientUpdatedData,null,null,null");		//SOURCE OF PROBLEM
			toServer.flush();
		}
		else
			lblStatus.setText("Login: Failed");
	}
	
	private void setUpNetworking(ClientController controller) throws Exception {
		@SuppressWarnings("resource")
	    Socket socket = new Socket(host, 4242);
	    System.out.println("Connecting to... " + socket);
	    fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	    toServer = new PrintWriter(socket.getOutputStream());
	    
	    controller.writer = toServer;
	    controller.reader = fromServer;

	    Thread readerThread = new Thread(new Runnable() {
	      @Override
	      public void run() {
	        String input;
	        try {
	          while ((input = fromServer.readLine()) != null) {
	            System.out.println("From server: " + input);
	            //receive new auctionItemData here
	            String[] temp = input.split(",");
	            if(temp[0].equals("updateBid")) {
	            	int itemIndex= Integer.parseInt(temp[1]); 
	                Double updatedBid =Double.parseDouble(temp[2]);
	            	currentBid.set(itemIndex, updatedBid);
	            	if(itemIndex == ClientController.currentItemIndex && ClientController.isInitialized == 1)
	            		controller.currentBid.setText("CURRENT BID -> " + updatedBid);		//CHANGE THIS LINE
	            }
	            if(temp[0].equals("removeItem")) {
	            	int itemIndex= Integer.parseInt(temp[1]);
	                Double updatedBid =Double.parseDouble(temp[2]);
	            	boughtOrNot.set(itemIndex, 1);
	            	currentBid.set(itemIndex, updatedBid);
	            	System.out.println(boughtOrNot.toString());
	            	if(itemIndex == ClientController.currentItemIndex && ClientController.isInitialized == 1) {
	            		controller.currentBid.setText("FINAL BID -> " + updatedBid);		//CHANGE THIS LINE
	            		controller.itemStatusFlag.setText("ITEM STATUS -> CLOSED \n(do not bid pls)");		//CHANGE THIS LINE
	            	}
	            }
	            
	            if(temp[0].equals("updatedCurrentBidForLaterClients")) {
	            	int itemIndex = Integer.parseInt(temp[1]);
	            	Double updatedCurrBid = Double.parseDouble(temp[2]);
	            	currentBid.set(itemIndex, updatedCurrBid);
	            }
	            if(temp[0].equals("updatedItemStatusForLaterClients")) {
	            	int itemIndex = Integer.parseInt(temp[1]);
	            	int updatedItemStatus = Integer.parseInt(temp[2]);
	            	boughtOrNot.set(itemIndex, updatedItemStatus);
	            }
	            if(temp[0].equals("log")) {
	            	fromServer.close();
	            	toServer.close();
	            	System.exit(0);
	            }
	            
	            else
	            	processRequest(input); //for receiving whole auction items from server
	          }
	        } catch (Exception e) {
	          e.printStackTrace();
	        }
	      }
	    });

	    Thread writerThread = new Thread(new Runnable() {
	      @Override
	      public void run() {
	        while (true) {
	          String input = consoleInput.nextLine();
//	          String[] variables = input.split(",");
//	          Message request = new Message(variables[0], variables[1], Integer.valueOf(variables[2]));
//	          GsonBuilder builder = new GsonBuilder();
//	          Gson gson = builder.create();
	          sendToServer(input);
	        }
	      }
	    });

	    readerThread.start();
	    writerThread.start();
	  }

	  protected void processRequest(String input) {
		  String[] arr = input.split(",");
		  if(arr[0].equals("itemName")) {
			  itemNames.add(arr[1]);
		  }
		  if(arr[0].equals("itemDesc")) {
			  itemDescriptions.add(arr[1]);
		  }
		  if(arr[0].equals("minPrice")) {
			  Double n = Double.parseDouble(arr[1]);
			  minimumPrice.add(n);
		  }
		  if(arr[0].equals("currBid")) {
			  Double n = Double.parseDouble(arr[1]);
			  currentBid.add(n);
		  }
		  if(arr[0].equals("highestBidPossible")) {
			  Double n = Double.parseDouble(arr[1]);
			  highestBid.add(n);
		  }
		  if(arr[0].equals("boughtOrNot")) {
			  Integer n = Integer.parseInt(arr[1]);
			  boughtOrNot.add(n);
		  }
		  if(arr[0].equals("image")) {
			  images.add(arr[1]);
		  }
	  }

	  protected void sendToServer(String string) {
	    System.out.println("Sending to server: " + string);
	    toServer.println(string);
	    toServer.flush();
	  }

}
