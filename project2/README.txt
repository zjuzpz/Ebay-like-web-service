Item: 1) ItemID (Primary key)
	  2) Name
	  3) Currently
	  4) Buy_Price
	  5) First_Bid
	  6) Number_of_Bids
	  7) Country
	  8) Location
	  9) Latitude
	  10) Longitude
	  11) Description
      12) UserID
	  13) Started
	  14) Ends

ItemCategory: 1) ItemID
		      2) Category (ID and Category are primary key)
	   
Bid: 1) ItemID
     2) UserID
	 3) Time (IDs and Time are primary key)
	 4) Amount

User: 1) UserID (Primary key)
      2) Country
	  3) Location
	  4) Bidder_Rating
	  5) Seller_Rating
	  
All of relations in 4NF.
