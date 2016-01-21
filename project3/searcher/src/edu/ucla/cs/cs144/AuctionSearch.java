package edu.ucla.cs.cs144;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;
import java.util.Date;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.text.SimpleDateFormat;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.lucene.document.Document;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import edu.ucla.cs.cs144.DbManager;
import edu.ucla.cs.cs144.SearchRegion;
import edu.ucla.cs.cs144.SearchResult;

public class AuctionSearch implements IAuctionSearch {

	/* 
         * You will probably have to use JDBC to access MySQL data
         * Lucene IndexSearcher class to lookup Lucene index.
         * Read the corresponding tutorial to learn about how to use these.
         *
	 * You may create helper functions or classes to simplify writing these
	 * methods. Make sure that your helper functions are not public,
         * so that they are not exposed to outside of this class.
         *
         * Any new classes that you create should be part of
         * edu.ucla.cs.cs144 package and their source files should be
         * placed at src/edu/ucla/cs/cs144.
         *
         */
	
	public SearchResult[] basicSearch(String query, int numResultsToSkip, 
			int numResultsToReturn) {
		// TODO: Your code here!
		try {
			SearchEngine se = new SearchEngine();
			int total = numResultsToSkip + numResultsToReturn;
			TopDocs topDocs = se.performSearch(query, total);
			ScoreDoc[] hits = topDocs.scoreDocs;
			SearchResult[] result = new SearchResult[Math.max(0, hits.length - numResultsToSkip)];
			for(int i=numResultsToSkip;i<hits.length;i++) {
				Document doc = se.getDocument(hits[i].doc);
				result[i - numResultsToSkip] = new SearchResult(doc.get("ItemID"), doc.get("Name"));
			}
			//System.out.println(result.length);
			return result;
			
		} catch(IOException e) {
			e.printStackTrace();
		} catch(ParseException pe) {
			pe.printStackTrace();
		}
		return new SearchResult[0];
	}

	public SearchResult[] spatialSearch(String query, SearchRegion region,
			int numResultsToSkip, int numResultsToReturn) {
		// TODO: Your code here!
		
		//Get all basicSearch results
		SearchResult[] basicSearchResult = basicSearch(query, 0, Integer.MAX_VALUE);
        // create a connection to the database to retrieve Items from MySQL
		Connection conn = null;
		try {
			conn = DbManager.getConnection(true);
		} catch (SQLException ex) {
			System.out.println(ex);
		}

		//Get the corner points
		double lx = region.getLx();
		double ly = region.getLy();
		double rx = region.getRx();
		double ry = region.getRy();
		String pointLU = lx + " " + ry;
		String pointLD = lx + " " + ly;
		String pointRU = rx + " " + ry;
		String pointRD = rx + " " + ly;
		
		String getLocation_sql = "select ItemID from ItemLocation where MBRContains" +
		" (GeomFromText('Polygon((" + pointLD + "," + pointLU + "," + pointRU + "," + 
		pointRD + "," + pointLD + "))'), Location)";
		
		Statement stmt = DbManager.createStmt(conn);
		ResultSet rs = DbManager.executeQuery(stmt, getLocation_sql);
		HashSet<String> locationItemID = new HashSet<String>();
		try {
			while(rs.next()) {
				locationItemID.add(rs.getString("ItemID"));
			}
			//Get all itemID in the region.
			ArrayList<SearchResult> allResult = new ArrayList<SearchResult>();		
			for(int i=0; i<basicSearchResult.length; i++) {
				if(locationItemID.contains(basicSearchResult[i].getItemId())) {
					allResult.add(basicSearchResult[i]);
				}
			} 
			int total = Math.min(numResultsToSkip + numResultsToReturn, allResult.size());
			SearchResult[] result = new SearchResult[Math.max(0, total - numResultsToSkip)];
			for(int i=numResultsToSkip;i<total;i++) {
				result[i - numResultsToSkip] = allResult.get(i);
			}
			rs.close();
			stmt.close();
			conn.close();
			return result;
		} catch(SQLException e) {
			System.out.println(e);
		}
		return new SearchResult[0];
	}

	public String getXMLDataForItemId(String itemId) {
		// TODO: Your code here!
	
		Connection conn = null;
		try {
			conn = DbManager.getConnection(true);
		} catch (SQLException ex) {
			System.out.println(ex);
		}
		String getItem_sql = "select * from Item where ItemID = " + itemId;
		String getItemCategory_sql = "select Category from ItemCategory where ItemID = " + itemId;
		String getBid_sql = "select UserID, Time, Amount from Bid where ItemID = " + itemId;
		
		Statement stmt = DbManager.createStmt(conn);
		ResultSet rs = DbManager.executeQuery(stmt, getItem_sql);
		String result = "";
		
		try {
			if(!rs.next()) {
				return "";
			}
			String name, currently, buy_price, first_bid;
			String number_of_bids, item_country, item_location, latitude;
			String longitude, description, sellerID, started, ends;
			name = rs.getString("Name");
			currently = rs.getString("Currently");
			buy_price = rs.getString("Buy_Price");
			first_bid = rs.getString("First_Bid");
			number_of_bids = rs.getString("Number_of_Bids");
			item_country = rs.getString("Country");
			item_location = rs.getString("Location");
			latitude = rs.getString("Latitude");
			longitude = rs.getString("Longitude");
			description = rs.getString("Description");
			sellerID = rs.getString("UserID");
			started = rs.getString("Started");
			ends = rs.getString("Ends");
			
			//Get seller rating from User table
			String seller_Rating = "";
			String getSellerRating_sql = "select Seller_Rating from User where UserID = " + "\"" + sellerID + "\"";
			rs = DbManager.executeQuery(stmt, getSellerRating_sql);
			if(rs.next()) {
				seller_Rating = rs.getString("Seller_Rating");
			}
			
			//Get category infomation
			ArrayList<String> categories = new ArrayList<String>();
			rs = DbManager.executeQuery(stmt, getItemCategory_sql);
			while(rs.next()) {
				categories.add(rs.getString("Category"));
			}
			
			//Get bid information
			ArrayList<BidXML> bids = new ArrayList<BidXML>();
			rs = DbManager.executeQuery(stmt, getBid_sql);
			while(rs.next()) {
				String bidderID = rs.getString("UserID");
				String time = rs.getString("Time");
				String amount = rs.getString("Amount");
				BidXML newBid = new BidXML(bidderID, time, amount);
				//	Get bid information from User table
				String getUser_sql = "select Country, Location, Bidder_Rating from User where UserID = " + "\"" + bidderID + "\"";
				Statement stmt_user = DbManager.createStmt(conn);
				ResultSet rs_user = DbManager.executeQuery(stmt_user, getUser_sql);
				if(rs_user.next()) {
					newBid.setBidderRating(rs_user.getString("Bidder_Rating"));
					newBid.setCountry(rs_user.getString("Country"));
					newBid.setLocation(rs_user.getString("Location"));
				}
				bids.add(newBid);
			}
			
			conn.close();
			
			result = "<Item ItemID=\"" + itemId + "\">\n";
			result += " <Name>" + convertEscapeCharacter(name) + "</Name>\n";
			for(int i=0; i<categories.size(); i++) {
				result += " <Category>" + convertEscapeCharacter(categories.get(i)) + "</Category>\n";
			}
			result += " <Currently>$" + currently + "</Currently>\n";
			result += " <First_Bid>$" + first_bid + "</First_Bid>\n";
			result += " <Number_of_Bids>" + number_of_bids + "</Number_of_Bids>\n";
			
			result += " <Bids>\n";
			for(int i=0; i<bids.size(); i++) {
				result += "  <Bid>\n";
				BidXML bid = bids.get(i);
				if(!bid.getBidderRating().equals("")) {
					result += "   <Bidder Rating=\"" + bid.getBidderRating() + "\" ";
				} else {
					result += "   <Bidder ";
				}
				result += "UserID=\"" + convertEscapeCharacter(bid.getBidderID()) + "\">\n";
				if(!bid.getLocation().equals("")) {
					result += "    <Location>" + convertEscapeCharacter(bid.getLocation()) + "</Location>\n";
				}
				if(!bid.getCountry().equals("")) {
					result += "    <Country>" + convertEscapeCharacter(bid.getCountry()) + "</Country>\n";
				}
				result += "   </Bidder>\n";
				result += "   <Time>" + convertToXMLDateType(bid.getTime()) + "</Time>\n";
				result += "   <Amount>$" + bid.getAmount() + "</Amount>\n";
				result += "  </Bid>\n";
			}
			result += " </Bids>\n";
			if(!latitude.equals("")) {
				result += " <Location Latitude=\"" + latitude + "\"" + " Longitude=\"" + longitude + "\">";
			} else {
				result += "<Location>";
			}
			result += convertEscapeCharacter(item_location) + "</Location>\n";
			result += " <Country>" + convertEscapeCharacter(item_country) + "</Country>\n";
			result += " <Started>" + convertToXMLDateType(started) + "</Started>\n";
			result += " <Ends>" + convertToXMLDateType(ends) + "</Ends>\n";
			if(seller_Rating != "" && seller_Rating != "N/A") {
				result += " <Seller Rating=" + seller_Rating + "\" UserID=\"" + sellerID + "\" />\n";
			} else {
				result += " <Seller UserID=\"" + sellerID + "\" />\n";
			}
			result += " <Description>" + convertEscapeCharacter(description) + "</Description>\n";
			result += "</Item>";
			return result;
		} catch(SQLException e) {
			System.out.println(e);
		}
			
		return "";
		
	}
	
	//Why here catch ParseException will cause error?
	static String convertToXMLDateType(String time) {
		SimpleDateFormat outputFormat = new SimpleDateFormat("MMM-dd-yy HH:mm:ss");
		SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String res = "";
		try {
			Date parsed = inputFormat.parse(time);
			res = outputFormat.format(parsed);
		} catch(Exception pe) {
			System.out.println("ERROR: Cannot parse \"" + time + "\"");
		}
		return res;
	}
	
	
	static String convertEscapeCharacter(String s) {
		String res = s.replaceAll("<", "&lt;");
		res = res.replaceAll(">", "&gt;");
		res = res.replaceAll("'", "&apos;");
		res = res.replaceAll("&", "&amp;");
		res = res.replaceAll("\"", "&quot;");
		return res;
	}
	
	public String echo(String message) {
		return message;
	}

}

class BidXML {
	private String bidderID = "", bidderRating = "", location = "", country = "";
	private String time = "", amount = "";
	BidXML(String id, String time, String amount) {
		this.bidderID = id;
		this.time = time;
		this.amount = amount;
	}
	public void setBidderID(String bidderID) {
		this.bidderID = bidderID;
	}
	public void setBidderRating(String bidderRating) {
		this.bidderRating = bidderRating;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	
	public String getBidderID() {
		return bidderID;
	}
	public String getBidderRating() {
		return bidderRating;
	}
	public String getLocation() {
		return location;
	}
	public String getCountry() {
		return country;
	}
	public String getTime() {
		return time;
	}
	public String getAmount() {
		return amount;
	}

}
