/* CS144
 *
 * Parser skeleton for processing item-???.xml files. Must be compiled in
 * JDK 1.5 or above.
 *
 * Instructions:
 *
 * This program processes all files passed on the command line (to parse
 * an entire diectory, type "java MyParser myFiles/*.xml" at the shell).
 *
 * At the point noted below, an individual XML file has been parsed into a
 * DOM Document node. You should fill in code to process the node. Java's
 * interface for the Document Object Model (DOM) is in package
 * org.w3c.dom. The documentation is available online at
 *
 * http://java.sun.com/j2se/1.5.0/docs/api/index.html
 *
 * A tutorial of Java's XML Parsing can be found at:
 *
 * http://java.sun.com/webservices/jaxp/
 *
 * Some auxiliary methods have been written for you. You may find them
 * useful.
 */

package edu.ucla.cs.cs144;

import java.io.*;
import java.text.*;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ErrorHandler;


//All keys are required constructor parameters, other attributes are set by using setters.
class Item {

	private String itemID = "", name = "", currently = "", buy_price = "", first_bid = "";
	private String number_of_bids = "", item_location = "", item_country = "";
	private String latitude = "", longitude = "";
	private String description = "", userID = "", started = "", ends = "";
	
	private String separator = "|*|";
	
	Item(String itemID) {
		this.itemID = itemID;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	public void setCurrently(String currently) {
		this.currently = currently;
	}
	public void setBuy_price(String buy_price) {
		this.buy_price = buy_price;
	}
	public void setFirst_bid(String first_bid) {
		this.first_bid = first_bid;
	}
	public void setNumber_of_bids(String number_of_bids) {
		this.number_of_bids = number_of_bids;
	}
	public void setItem_location(String item_location) {
		this.item_location = item_location;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public void setItem_country(String item_country) {
		this.item_country = item_country;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public void setUserID(String userID) {
		this.userID = userID;
	}
	public void setStarted(String started) {
		this.started = started;
	}
	public void setEnds(String ends) {
		this.ends = ends;
	}
	
	public String toString() {
		return itemID + separator + name + separator + currently + separator +
				buy_price + separator + first_bid + separator + number_of_bids +
				separator + item_country + separator + item_location + separator +
				latitude + separator + longitude + separator + description + separator + 
				userID + separator + started + separator + ends;
	}
}

class Category {
	private String itemID = "", category = "";	
	private String separator = "|*|";
	
	Category(String itemID, String category) {
		this.itemID = itemID;
		this.category = category;
	}
	
	public String toString() {
		return itemID + separator + category;
	}
	
}

class Bid {
	private String itemID = "", userID = "", time = "", amount = "";
	private String separator = "|*|";
	
	Bid(String itemID, String userID, String time) {
		this.itemID = itemID;
		this.userID = userID;
		this.time = time;
	}
	
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String toString() {
		return itemID + separator + userID + separator + time + separator + amount;
	}
	
}

class User {
	private String userID = "", country = "", location = "";
	private String bidder_rating = "", seller_rating ="";
	private String separator = "|*|";
	
	User(String userID) {
		this.userID = userID;
	}
	
	public void setCountry(String country) {
		this.country = country;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public void setBidder_rating(String bidder_rating) {
		this.bidder_rating = bidder_rating;
	}

	public void setSeller_rating(String seller_rating) {
		this.seller_rating = seller_rating;
	}
	
	public String toString() {
		if (bidder_rating == "") {
			bidder_rating = "N/A";
		}
		if (seller_rating == "") {
			seller_rating = "N/A";
		}
		return userID + separator + country + separator + location + 
				separator + bidder_rating + separator + seller_rating;
	}
}

class MyParser {
    
    static final String columnSeparator = "|*|";
    static DocumentBuilder builder;
    
    static final String[] typeName = {
	"none",
	"Element",
	"Attr",
	"Text",
	"CDATA",
	"EntityRef",
	"Entity",
	"ProcInstr",
	"Comment",
	"Document",
	"DocType",
	"DocFragment",
	"Notation",
    };
    
    static class MyErrorHandler implements ErrorHandler {
        
        public void warning(SAXParseException exception)
        throws SAXException {
            fatalError(exception);
        }
        
        public void error(SAXParseException exception)
        throws SAXException {
            fatalError(exception);
        }
        
        public void fatalError(SAXParseException exception)
        throws SAXException {
            exception.printStackTrace();
            System.out.println("There should be no errors " +
                               "in the supplied XML files.");
            System.exit(3);
        }
        
    }
    
    /* Non-recursive (NR) version of Node.getElementsByTagName(...)
     */
    static Element[] getElementsByTagNameNR(Element e, String tagName) {
        Vector< Element > elements = new Vector< Element >();
        Node child = e.getFirstChild();
        while (child != null) {
            if (child instanceof Element && child.getNodeName().equals(tagName))
            {
                elements.add( (Element)child );
            }
            child = child.getNextSibling();
        }
        Element[] result = new Element[elements.size()];
        elements.copyInto(result);
        return result;
    }
    
    /* Returns the first subelement of e matching the given tagName, or
     * null if one does not exist. NR means Non-Recursive.
     */
    static Element getElementByTagNameNR(Element e, String tagName) {
        Node child = e.getFirstChild();
        while (child != null) {
            if (child instanceof Element && child.getNodeName().equals(tagName))
                return (Element) child;
            child = child.getNextSibling();
        }
        return null;
    }
    
    /* Returns the text associated with the given element (which must have
     * type #PCDATA) as child, or "" if it contains no text.
     */
    static String getElementText(Element e) {
        if (e.getChildNodes().getLength() == 1) {
            Text elementText = (Text) e.getFirstChild();
            return elementText.getNodeValue();
        }
        else
            return "";
    }
    
    /* Returns the text (#PCDATA) associated with the first subelement X
     * of e with the given tagName. If no such X exists or X contains no
     * text, "" is returned. NR means Non-Recursive.
     */
    static String getElementTextByTagNameNR(Element e, String tagName) {
        Element elem = getElementByTagNameNR(e, tagName);
        if (elem != null)
            return getElementText(elem);
        else
            return "";
    }
    
    /* Returns the amount (in XXXXX.xx format) denoted by a money-string
     * like $3,453.23. Returns the input if the input is an empty string.
     */
    static String strip(String money) {
        if (money.equals(""))
            return money;
        else {
            double am = 0.0;
            NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.US);
            try { am = nf.parse(money).doubleValue(); }
            catch (ParseException e) {
                System.out.println("This method should work for all " +
                                   "money values you find in our data.");
                System.exit(20);
            }
            nf.setGroupingUsed(false);
            return nf.format(am).substring(1);
        }
    }
    
	
    /* Process one items-???.xml file.
     */
	
/******************************/	
	//Store item, category, bid and user 
	static Map<String, Item> lookupItem = new HashMap<String, Item>();
	static ArrayList<Category> lookupCategory = new ArrayList<Category>();
	static ArrayList<Bid> lookupBid = new ArrayList<Bid>();
	static Map<String, User> lookupUser = new HashMap<String, User>();
/*****************************/
		
    static void processFile(File xmlFile) {
        Document doc = null;
        try {
            doc = builder.parse(xmlFile);
        }
        catch (IOException e) {
            e.printStackTrace();
            System.exit(3);
        }
        catch (SAXException e) {
            System.out.println("Parsing error on file " + xmlFile);
            System.out.println("  (not supposed to happen with supplied XML files)");
            e.printStackTrace();
            System.exit(3);
        }
        
        /* At this point 'doc' contains a DOM representation of an 'Items' XML
         * file. Use doc.getDocumentElement() to get the root Element. */
        System.out.println("Successfully parsed - " + xmlFile);
        
        /* Fill in code here (you will probably need to write auxiliary
            methods). */
			
        Element root = doc.getDocumentElement();
		//Get all items
		Element[] items = getElementsByTagNameNR(root, "Item");		
		
		
		// Get all attributes		
		for (int i = 0; i < items.length; i++) {
			String itemID = items[i].getAttribute("ItemID");
			Item new_item = new Item(itemID);
			
			String name = getElementTextByTagNameNR(items[i], "Name");
			String currently = strip(getElementTextByTagNameNR(items[i], "Currently"));
			String buy_price = strip(getElementTextByTagNameNR(items[i], "Buy_Price"));
			String first_bid = strip(getElementTextByTagNameNR(items[i], "First_Bid"));
			String number_of_bids = getElementTextByTagNameNR(items[i], "Number_of_Bids");	
			
			if(name != null) {
				new_item.setName(name);
			}
			if(currently != null) {
				new_item.setCurrently(currently);
			}
			if(buy_price != null) {
				new_item.setBuy_price(buy_price);
			}
			if(first_bid != null) {
				new_item.setFirst_bid(first_bid);
			}
			if(number_of_bids != null) {
				new_item.setNumber_of_bids(number_of_bids);
			}
			
			//Items location may have latitude and longitude
			String item_location = getElementTextByTagNameNR(items[i], "Location");
		
			if(item_location != null) {
				new_item.setItem_location(item_location);
			}
			
			Element item_locations = getElementByTagNameNR(items[i], "Location");
			String latitude = item_locations.getAttribute("Latitude");
			if(latitude != null) {
				new_item.setLatitude(latitude);
			}	
			String longitude = item_locations.getAttribute("Longitude");
			if(longitude != null) {
				new_item.setLongitude(longitude);
			}
			
			String item_country = getElementTextByTagNameNR(items[i], "Country");
			if(item_country != null) {
				new_item.setItem_country(item_country);
			}
				
			//Description is no longer than 4000 characters
			String description = getElementTextByTagNameNR(items[i], "Description");
			if (description.length() > 4000) {
				description = description.substring(0, 4000);
			}
			if(description != null) {
				new_item.setDescription(description);
			}
			
			//Get bids
			Element bidRoot = getElementByTagNameNR(items[i], "Bids");
			Element[] bids = getElementsByTagNameNR(bidRoot, "Bid");
			for (int j = 0; j < bids.length; j ++) {
				Element bidder = getElementByTagNameNR(bids[j], "Bidder");
				String userID = bidder.getAttribute("UserID");
				String bidder_rating = bidder.getAttribute("Rating");
				String user_country = getElementTextByTagNameNR(bidder, "Country");
				String user_location = getElementTextByTagNameNR(bidder, "Location");
				String time = convertToDateType(getElementTextByTagNameNR(bids[j], "Time"));
				String amount = strip(getElementTextByTagNameNR(bids[j], "Amount"));
				
				User new_user;
				if(!lookupUser.containsKey(userID)) {
					new_user = new User(userID);
				} else {
					new_user = lookupUser.get(userID);
				}
				
				if(bidder_rating != null) {
					new_user.setBidder_rating(bidder_rating);
				}
				if(user_country != null) {
					new_user.setCountry(user_country);
				}
				if(user_location != null) {
					new_user.setLocation(user_location);
				}
				if(!lookupUser.containsKey(userID)) {
					lookupUser.put(userID, new_user);
				}
				
				Bid new_bid = new Bid(itemID, userID, time);
				new_bid.setAmount(amount);
				//Get all attributes of bids, thus put it into lookupBid
				lookupBid.add(new_bid);
			}
					
			String started = convertToDateType(getElementTextByTagNameNR(items[i], "Started"));
			if(started != null) {
				new_item.setStarted(started);
			}
			String ends = convertToDateType(getElementTextByTagNameNR(items[i], "Ends"));
			if(ends != null) {
				new_item.setEnds(ends);
			}			

			
			Element seller = getElementByTagNameNR(items[i], "Seller");
			String userID = seller.getAttribute("UserID");
			if(userID != null) {
				new_item.setUserID(userID);
			}
			
			//Get all attributes of items, thus put it into lookupItem
			lookupItem.put(itemID, new_item);
			
			String seller_rating = seller.getAttribute("Rating");	
			
			User new_user;			
			if(!lookupUser.containsKey(userID)) {
				new_user = new User(userID);
			} else {
				new_user = lookupUser.get(userID);
			}			
			if(seller_rating != null) {
				new_user.setSeller_rating(seller_rating);
			}
			//Get all attributes of users
			if(!lookupUser.containsKey(userID)) {
				lookupUser.put(userID, new_user);
			}			
			
			//Attributes of category
			Element[] categories = getElementsByTagNameNR(items[i], "Category");
			for(int j = 0; j < categories.length; j ++) {
				String category_name = categories[j].getTextContent();
				Category new_category = new Category(itemID, category_name);
				//Get all attributes of category, thus put it into lookupCategory
				lookupCategory.add(new_category);
			}
		}		
		
        /**************************************************************/
        
    }
    
    static void writeToFile(String filename, HashMap<String, Object> map) {
    	FileWriter fw = null;
    	PrintWriter p = null;
    	try {
			fw = new FileWriter(filename, true);
			p = new PrintWriter(fw);
			for (String key : map.keySet()) {
				p.println(map.get(key));
			}
			p.close();
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Error in writing data to file!");
			e.printStackTrace();
		}
    }
    
    static void writeToFile(String filename, ArrayList<Object> list) {
    	FileWriter fw = null;
    	PrintWriter p = null;
    	try {
			fw = new FileWriter(filename, true);
			p = new PrintWriter(fw);
			for (int i = 0; i < list.size(); i++) {
				p.println(list.get(i));
			}
			p.close();
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Error in writing data to file!");
			e.printStackTrace();
		}
    }

	static String convertToDateType(String time) {
		SimpleDateFormat inputFormat = new SimpleDateFormat("MMM-dd-yy HH:mm:ss");
		SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String res = "";
		try {
			Date parsed = inputFormat.parse(time);
			res = outputFormat.format(parsed);
		} catch (ParseException pe) {
			System.out.println("ERROR: Cannot parse \"" + time + "\"");
		}
		return res;
	}
    
    /**************************************************************/
    
    public static void main (String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java MyParser [file] [file] ...");
            System.exit(1);
        }
        
        /* Initialize parser. */
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(false);
            factory.setIgnoringElementContentWhitespace(true);      
            builder = factory.newDocumentBuilder();
            builder.setErrorHandler(new MyErrorHandler());
        }
        catch (FactoryConfigurationError e) {
            System.out.println("unable to get a document builder factory");
            System.exit(2);
        } 
        catch (ParserConfigurationException e) {
            System.out.println("parser was unable to be configured");
            System.exit(2);
        }
        
        /* Process all files listed on command line. */
        for (int i = 0; i < args.length; i++) {
            File currentFile = new File(args[i]);
            processFile(currentFile);
        }
		
		writeToFile("item.dat", new HashMap<String, Object>(lookupItem));
		writeToFile("bid.dat", new ArrayList<Object>(lookupBid));
		writeToFile("user.dat", new HashMap<String, Object>(lookupUser));
		writeToFile("category.dat", new ArrayList<Object>(lookupCategory));
    }
}
