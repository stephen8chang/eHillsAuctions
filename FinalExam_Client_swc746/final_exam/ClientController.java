package final_exam;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Observable;
import java.util.Scanner;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ClientController{

	@FXML
	public Label welcomeName;
	@FXML
	public TextField txtSend;
	
	@FXML
	private ComboBox<String> dropDown;
	@FXML
	private TextField promptToChooseItem;
	
	@FXML
	private TextField itemName;
	@FXML
	private TextArea itemDesc;
	@FXML
	private TextField minPrice;
	@FXML
	private TextField highestPossible;
	@FXML
	public TextField currentBid;
	
	
	@FXML
	private TextField bidAmount;
	@FXML
	private TextField yourCurrentBid;
	@FXML
	private TextArea bidHistory;
	
	@FXML
	private TextField validBidFeedback;
	
	@FXML
	public TextArea itemStatusFlag;
	
	public BufferedReader reader;
	public PrintWriter writer;
	
	@FXML
	private ImageView image;
	
	public static int currentItemIndex = 0;
	
	public static int isInitialized = 0;
	
	
	public void logout(ActionEvent event) {
		//System.exit(0);
		writer.println("logout");
		writer.flush();
	}
	
	public void sendOver(ActionEvent event) {
		writer.println(txtSend.getText());			//client to server
		writer.flush();
	}	
	
	public void setComboBoxData() {
		dropDown.getItems().clear();
		for(int i = 0; i < LoginController.itemNames.size(); i++) {
			dropDown.getItems().addAll(LoginController.itemNames.get(i));
		}
		
//		itemName.setText("Item Name: " +LoginController.itemNames.get(0));
//		itemDesc.setText("Item Description: \n" + LoginController.itemDescriptions.get(0));
//		minPrice.setText("Minimum Price: " + (LoginController.minimumPrice.get(0)).toString());
//		highestPossible.setText("Highest Possible: " + (LoginController.highestBid.get(0)).toString());
//		currentBid.setText("CURRENT BID -> "+(LoginController.currentBid.get(0)).toString());
//		itemStatusFlag.setText("ITEM STATUS -> OPEN");
			
	}
	
	public void buyNow(ActionEvent event) {
		if(LoginController.boughtOrNot.get(currentItemIndex)==0) {
			Double temp = LoginController.highestBid.get(currentItemIndex);
			LoginController.currentBid.set(currentItemIndex,temp);
		
			validBidFeedback.setText("This " + LoginController.itemNames.get(currentItemIndex) +" is yours now! Congrats!");
			bidHistory.setText(bidHistory.getText() + "For $" + temp +", you bought a " + LoginController.itemNames.get(currentItemIndex) + "!\n");
			yourCurrentBid.setText("Congrats!");
		
			sendCurrentBidBackToServer(currentItemIndex, temp, 1);
		}
		else
			validBidFeedback.setText("This has already been bought, bro.");
	}
	
	public void sendBid(ActionEvent event) {
		int doNotDisplayFlag = 0;
		  try 
	        { 
	            Double temp = Double.parseDouble(bidAmount.getText()); 
	            if(LoginController.boughtOrNot.get(currentItemIndex) == 1) {
	            	validBidFeedback.setText("Bid's over. What are you doing?");
	            }
	            else if(temp >= LoginController.minimumPrice.get(currentItemIndex) && temp > LoginController.currentBid.get(currentItemIndex) && temp < LoginController.highestBid.get(currentItemIndex)) {
	            	validBidFeedback.setText("Valid bid!");
	            	LoginController.currentBid.set(currentItemIndex, temp);
	            	currentBid.setText("CURRENT BID -> " +bidAmount.getText());
	    			yourCurrentBid.setText(bidAmount.getText());
	    			bidHistory.setText(bidHistory.getText() + "You bid $" + temp + " dollars on a "+LoginController.itemNames.get(currentItemIndex)+"!\n");
	    			
	    			//send bid update to server
	    			sendCurrentBidBackToServer(currentItemIndex,temp, 0);
	            }
	            else if(temp >= LoginController.highestBid.get(currentItemIndex)) {
	            	validBidFeedback.setText("This " + LoginController.itemNames.get(currentItemIndex) +" is yours now! Congrats!");
	            	yourCurrentBid.setText(bidAmount.getText());
	    			bidHistory.setText(bidHistory.getText() + "For $" + temp +", you won a " + LoginController.itemNames.get(currentItemIndex) + "!\n");
	            	//finish bid for item: remove from all static arraylists, remove from combobox
//	    			LoginController.itemNames.remove(currentItemIndex);
//	    			LoginController.itemDescriptions.remove(currentItemIndex);
//	    			LoginController.minimumPrice.remove(currentItemIndex);
//	    			LoginController.currentBid.remove(currentItemIndex);
//	    			//add userwithcurrentbid here?
//	    			LoginController.highestBid.remove(currentItemIndex);
//	    			LoginController.boughtOrNot.remove(currentItemIndex);
	    			//dropDown.getItems().remove(currentItemIndex);
	    			yourCurrentBid.setText("Congrats!");
	    			
	    			//send update that item has been bought to server
	    			//DO THIS TODAY!!!!!
	    			sendCurrentBidBackToServer(currentItemIndex, temp, 1);
	            }
	            else
	            	doNotDisplayFlag = 1;
	        }  
	        catch (NumberFormatException e)  
	        { 
            	validBidFeedback.setText("Not a valid bid!");
	        } 
		  
		  if(doNotDisplayFlag == 1) {
          	validBidFeedback.setText("You aren't bidding enough.");
		  }
	}
	
	public void sendCurrentBidBackToServer(int itemIndex, double updatedBid, int itemStatus) {
		writer.println("updateBid,"+ itemIndex + "," + updatedBid + ","+ itemStatus);
		writer.flush();
	}
	
//	@FXML 
//	public void update() {
//		dropDown.setOnAction(e ->{
//			
//		});
//	}
	@FXML
	public void initialize() {
		dropDown.setOnAction(e ->{
			String selectedItem = (String) dropDown.getValue();
			promptToChooseItem.setText(" BID! BID! BID! BID! BID! BID! BID! BID! BID BID! BID! BID! BID! BID! BID! BID! BID! BID! BID! BID BID! BID! BID!");
			yourCurrentBid.setText("");
			isInitialized = 1;
			
			if(selectedItem != "" || selectedItem != null) {
				currentItemIndex = LoginController.itemNames.indexOf(selectedItem);
			}
			//System.out.println(currentItemIndex);
			//yourCurrentBid.setText(Integer.toString(LoginController.currentBid.get(currentItemIndex)));
			if(currentItemIndex >= 0) {
				Image i = new Image(LoginController.images.get(currentItemIndex));
				image.setImage(i);
				itemName.setText("Item Name: " +LoginController.itemNames.get(currentItemIndex));
				itemDesc.setText("Item Description: \n" + LoginController.itemDescriptions.get(currentItemIndex));
				minPrice.setText("Minimum Price: " + (LoginController.minimumPrice.get(currentItemIndex)).toString());
				highestPossible.setText("Highest Possible: " + (LoginController.highestBid.get(currentItemIndex)).toString());
				if(LoginController.boughtOrNot.get(currentItemIndex) == 1) {
					itemStatusFlag.setText("ITEM STATUS -> CLOSED \n(do not bid pls)");
					currentBid.setText("FINAL BID -> "+(LoginController.currentBid.get(currentItemIndex)).toString());
				}
				else {
					currentBid.setText("CURRENT BID -> "+(LoginController.currentBid.get(currentItemIndex)).toString());
					itemStatusFlag.setText("ITEM STATUS -> OPEN");
				}
			}
		});
	}
}
