package edu.ucla.cs.cs144;

import java.io.IOException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import java.io.*;
import java.util.*;


public class ItemServlet extends HttpServlet implements Servlet {
       
    public ItemServlet() {}

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        // your codes here
		String id = "";		
		if(request.getParameter("id") != null) {
			id = request.getParameter("id");
		}
		request.setAttribute("id", id);
		String xml = AuctionSearchClient.getXMLDataForItemId(id);
		if(xml == null || xml.equals("")) {
			request.getRequestDispatcher("/itemSearchResult.jsp").forward(request, response);
			return;
		}		
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		    DocumentBuilder builder = factory.newDocumentBuilder();
            InputSource is = new InputSource();
			StringReader reader = new StringReader(xml);
            is.setCharacterStream(reader);
			Document doc = builder.parse(is);
			
			//Get the root item			
			Element item = doc.getDocumentElement();
			
			//Get item information		
			String name = getElementTextByTagNameNR(item, "Name");
			request.setAttribute("name", name);
			
			String currently = getElementTextByTagNameNR(item, "Currently");
			request.setAttribute("currently", currently);
			
			String buy_price = getElementTextByTagNameNR(item, "Buy_Price");
			request.setAttribute("buy_price", buy_price);
			
			String first_bid = getElementTextByTagNameNR(item, "First_Bid");
			request.setAttribute("first_bid", first_bid);
			
			String number_of_bids = getElementTextByTagNameNR(item, "Number_of_Bids");
			request.setAttribute("number_of_bids", number_of_bids);
			
			String item_country = getElementTextByTagNameNR(item, "Country");
			request.setAttribute("item_country", item_country);
			
			String item_location = getElementTextByTagNameNR(item, "Location");
			request.setAttribute("item_location", item_location);
						
			Element eleItemLocation = getElementByTagNameNR(item, "Location");
			String latitude = eleItemLocation.getAttribute("Latitude");
			request.setAttribute("latitude", latitude);
			
			String longitude = eleItemLocation.getAttribute("Longitude");
			request.setAttribute("longitude", longitude);
			
			String description = getElementTextByTagNameNR(item, "Description");
			request.setAttribute("description", description);
			
			String started = getElementTextByTagNameNR(item, "Started");
			request.setAttribute("started", started);
			
			String ends = getElementTextByTagNameNR(item, "Ends");
			request.setAttribute("ends", ends);
			
			Element seller = getElementByTagNameNR(item, "Seller");
			String sellerID = seller.getAttribute("UserID");
			request.setAttribute("sellerID", sellerID);
			
			String sellerRating = seller.getAttribute("Rating");
			request.setAttribute("sellerRating", sellerRating);
			
			//Get categories
			String category = "";
			Element[] categories = getElementsByTagNameNR(item, "Category");
			for(int j = 0; j < categories.length; j++) {
				category += categories[j].getTextContent();
				if(j != categories.length - 1) {
					category += ", ";
				}
			}
			request.setAttribute("category", category);
			
			//Get all bids for the item
			ArrayList<ItemBid> bids = new ArrayList<ItemBid>();
			Element bidRoot = getElementByTagNameNR(item, "Bids");
			Element[] allBids = getElementsByTagNameNR(bidRoot, "Bid");
			for (int j = 0; j < allBids.length; j ++) {
				Element bidder = getElementByTagNameNR(allBids[j], "Bidder");
				String userID = bidder.getAttribute("UserID");
				String bidder_rating = bidder.getAttribute("Rating");
				ItemBid new_bid = new ItemBid(userID, bidder_rating);
				
				String user_country = getElementTextByTagNameNR(bidder, "Country");
				if(user_country == null || user_country.equals("")) {
					user_country = "N/A";
				}
				String user_location = getElementTextByTagNameNR(bidder, "Location");
				if(user_location == null || user_location.equals("")) {
					user_country = "N/A";
				}
				String time = getElementTextByTagNameNR(allBids[j], "Time");
				String amount = getElementTextByTagNameNR(allBids[j], "Amount");
				new_bid.setCountry(user_country);
				new_bid.setLocation(user_location);
				new_bid.setTime(time);
				if(amount != null && !amount.equals("")) {
					//Remove the $ sign!
				  new_bid.setAmount(amount.substring(1));
				}
				bids.add(new_bid);
			}
			
			//Sort bids by amount(and actually, the most recently amount should have largest amount)
			Collections.sort(bids, new PriceComparator());
			
			request.setAttribute("bids", bids);
			request.getRequestDispatcher("/itemSearchResult.jsp").forward(request, response);
		} catch(Exception e) {
			System.out.println("An error happens");
			e.printStackTrace();
		}
    }
	
	static class PriceComparator implements Comparator {
		public int compare(Object object1, Object object2) {
			ItemBid b1 = (ItemBid) object1;
			ItemBid b2 = (ItemBid) object2;
			return new Double(b2.getAmount()).compareTo(new Double(b1.getAmount()));
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
}
