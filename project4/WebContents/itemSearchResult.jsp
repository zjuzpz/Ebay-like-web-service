<!DOCTYPE html>
<!--Get data from request-->
<%@page import="edu.ucla.cs.cs144.ItemBid" %>
<%@page import="java.util.ArrayList" %>

<%
String id = "";
if(request.getAttribute("id") != null && !request.getAttribute("id").equals("")) {
	id = request.getAttribute("id").toString();
}
String name = "N/A";
if(request.getAttribute("name") != null && !request.getAttribute("name").equals("")) {
	name = request.getAttribute("name").toString();
}
String currently = "N/A";
if(request.getAttribute("currently") != null && !request.getAttribute("currently").equals("")) {
	currently = request.getAttribute("currently").toString();
}
String buy_price = "N/A";
if(request.getAttribute("buy_price") != null && !request.getAttribute("buy_price").equals("")) {
	buy_price = request.getAttribute("buy_price").toString();
}
String first_bid = "N/A";
if(request.getAttribute("first_bid") != null && !request.getAttribute("first_bid").equals("")) {
	first_bid = request.getAttribute("first_bid").toString();
}
String number_of_bids = "0";
if(request.getAttribute("number_of_bids") != null && !request.getAttribute("number_of_bids").equals("")) {
	number_of_bids = request.getAttribute("number_of_bids").toString();
}
String item_country = "N/A";
if(request.getAttribute("item_country") != null && !request.getAttribute("item_country").equals("")) {
	item_country = request.getAttribute("item_country").toString();
}
String item_location = "N/A";
if(request.getAttribute("item_location") != null && !request.getAttribute("item_location").equals("")) {
	item_location = request.getAttribute("item_location").toString();
}
String latitude = "N/A";
if(request.getAttribute("latitude") != null && !request.getAttribute("latitude").equals("")) {
	latitude = request.getAttribute("latitude").toString();
}
String longitude = "N/A";
if(request.getAttribute("longitude") != null && !request.getAttribute("longitude").equals("")) {
	longitude = request.getAttribute("longitude").toString();
}
String description = "N/A";
if(request.getAttribute("description") != null && !request.getAttribute("description").equals("")) {
	description = request.getAttribute("description").toString();
}
String started = "N/A";
if(request.getAttribute("started") != null && !request.getAttribute("started").equals("")) {
	started = request.getAttribute("started").toString();
}
String ends = "N/A";
if(request.getAttribute("ends") != null && !request.getAttribute("ends").equals("")) {
	ends = request.getAttribute("ends").toString();
}
String seller = "N/A";
if(request.getAttribute("sellerID") != null && !request.getAttribute("sellerID").equals("")) {
	seller = request.getAttribute("sellerID").toString();
}
String sellerRating = "N/A";
if(request.getAttribute("sellerRating") != null && !request.getAttribute("sellerRating").equals("")) {
	sellerRating = request.getAttribute("sellerRating").toString();
}
String category = "N/A";
if(request.getAttribute("category") != null && !request.getAttribute("category").equals("")) {
	category = request.getAttribute("category").toString();
}
ArrayList<ItemBid> bids = new ArrayList<ItemBid>();
if(request.getAttribute("bids") != null) {
	bids = (ArrayList<ItemBid>)(request.getAttribute("bids"));
}

Boolean map = false;
%>
<html>
  <head>
    <title>ItemID Search</title>
		<meta name="viewport" content="initial-scale=1.0, user-scalable=no" />  
		<script type="text/javascript" 
			src="http://maps.google.com/maps/api/js?sensor=false"> 
		</script> 
		
		<script type="text/javascript"> 
		  function initialize() { 
			var latlng = "";
			<%if(latitude != "N/A" && longitude != "N/A") { 
				map = true;%>
				latlng = new google.maps.LatLng(<%=latitude%>,<%=longitude%>);
			<% } else {%>	
				  geocoder = new google.maps.Geocoder();
				  var address = "<%= item_location%>";
				  geocoder.geocode({'address': address}, function(results, status) {
					  if(status == google.maps.GeocoderStatus.OK) {
						  latlng = results[0].geometry.location;
						  <% map = true; 
						  %>
					  } else {
						  <% map = false; 
						  %>
					  }
				  }
		   <% }%>
			var myOptions = { 
			  zoom: 14, // default is 8  
			  center: latlng, 
			  mapTypeId: google.maps.MapTypeId.ROADMAP 
			}; 
			var map = new google.maps.Map(document.getElementById("map_canvas"), 
				myOptions); 
		  } 
		</script>
		
  </head>
  <body onload="initialize()">
    <h1>ItemID Search</h1>
    <form action="/eBay/item" method="GET">
	  ItemID <input type="text" name="id">
	  <input type="submit" value="Search">
	</form>
	<h3>Your search results:</h3>
	<% if(id.equals("")) { 
	  response.sendRedirect("/eBay/getItem.html");
	}%>
	<% if(name.equals("N/A")) { %>
	  <p>No result matched!</p>
	<%} else { %>
	  <p>
	  Item ID: <%=id%> </br>
	  Item Name: <%=name%> </br>
	  Started: <%=started%> </br>
	  Ends: <%=ends%></br>
	  Item Category: <%=category%></br>
	  Seller: <%=seller%></br>
	  Seller Rating: <%=sellerRating%></br>
	  Currently: <%=currently%></br>
	  Buy Price: <%=buy_price%></br>
	  First Bid: <%=first_bid%></br>
	  Country: <%=item_country%></br>
	  Location: <%=item_location + " " + latitude + "/" + longitude %> (Latitude/Longitude)</br> 
	  Number of Bids: <%=number_of_bids%></p>
 <!--Add google map-->	
     <p>Google Map for the item location:</p>
      <%if(map) { %>
        <div id="map_canvas" style="width:400px; height:400px"></div>
	  <%} else {%>
	    <p>No location map information available!<p/>
	  <%} %>
 
	  
	  <%if(!number_of_bids.equals("0")) {%>
		  <p>Bids:</br></br> 
			<%  for(int i=0; i<bids.size(); i++) {
				ItemBid bid = bids.get(i);
				%>
				Bidder: <%=bid.getUserID()%> </br>
				Bidder Rating: <%=bid.getRating()%> </br>
				Amount: $<%=bid.getAmount()%> </br>
				Time: <%=bid.getTime()%> </br>
				Country: <%=bid.getCountry()%> </br>
				Location: <%=bid.getLocation()%> </p>
		   <%} %>	  
		  </p>
	  <%}%>
  <%}%>  
  </body>
</html>