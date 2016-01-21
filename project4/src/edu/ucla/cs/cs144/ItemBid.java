package edu.ucla.cs.cs144;

public class ItemBid {
	private String userID, rating;
    private String country, location, time, amount = "0";	
	ItemBid(String userID, String rating) {
		this.userID = userID;
		this.rating = rating;
	}
	
	public String getUserID() {
		return userID;
	}
	public String getRating() {
		return rating;
	}
	public String getCountry() {
		return country;
	}
	public String getLocation() {
		return location;
	}
	public String getTime() {
		return time;
	}
	public String getAmount() {
		return amount;
	}
	public void setUserID(String userID) {
		this.userID = userID;
	}
	public void setRating(String rating) {
		this.rating = rating;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
}