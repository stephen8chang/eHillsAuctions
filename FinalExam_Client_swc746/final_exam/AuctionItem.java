package final_exam;

public class AuctionItem{
	String itemName;
	String itemDes;
	double minPrice;
	double currBid;
	double highestBidPossible;
	int boughtOrNot;
	
	String image;

	AuctionItem(){
		itemName = "no item name";
		itemDes = "no item description";
		currBid = 1.0;
		minPrice = 1.0;
		highestBidPossible = 1.0;
		boughtOrNot = 0;
		image = "no image";
	}
	
	AuctionItem(String itemName, String itemDes, double minPrice, double currBid, double highestBidPossible, int boughtOrNot, String img){
		this.itemName = itemName;
		this.itemDes = itemDes;
		this.minPrice = minPrice;
		this.currBid = currBid;
		this.highestBidPossible = highestBidPossible;
		this.boughtOrNot = boughtOrNot;
		this.image = img;
	}
	
	public String getItemName() {
		return itemName;
	}
	
	public void setItemName(String s) {
		itemName = s;
	}
	
	public String getItemDes() {
		return itemDes;
	}
	
	public void setItemDes(String s) {
		itemDes = s;
	}
	
	
	public double getMinPrice() {
		return minPrice;
	}
	
	public void setMinPrice(double p) {
		minPrice = p;
	}
	
	public double getCurrBid() {
		return currBid;
	}
	
	public void setCurrBid(double d) {
		currBid = d;
	}
	
	public double getHighestBidPossible() {
		return highestBidPossible;
	}
	
	public void setHighestBidPossible(double d) {
		highestBidPossible = d;
	}
	
	public int getBoughtOrNot() {
		return boughtOrNot;
	}
	
	public void setBoughtOrNot(int d) {
		boughtOrNot= d;
	}
	
	public String getImageString() {
		return image;
	}
	
	public void setImageString(String d) {
		image = d;
	}
	public String toString() {
		return itemName + " " + Double.toString(currBid) + " "+ Double.toString(highestBidPossible);
	}
}