create table Item (
ItemID varchar(40) not null,
Name varchar(500) not null comment 'item name',
Currently decimal(8,2) not null comment 'current highest bid',
Buy_Price decimal(8,2) comment 'the price get the item immediately',  
First_Bid decimal(8,2) not null comment 'min first bit price',
Number_of_Bids int,
Country varchar(200),
Location varchar(200),
Latitude varchar(200),
Longitude varchar(200),
Description varchar(4000) comment 'item description',
UserID varchar(40) not null,
Started timestamp not null comment 'auction start time',
Ends timestamp not null comment 'auction end time',
primary key(ItemID)
);

create table ItemCategory (
ItemID varchar(40) not null,
Category varchar(300) not null,
primary key(ItemID, Category)
);

create table Bid (
ItemID varchar(40) not null,
UserID varchar(40) not null,
Time timestamp not null,
Amount decimal(8,2),
primary key(ItemID, UserID, Time)
);

create table User (
UserID varchar(40) not null,
Country varchar(200),
Location varchar(400),
Bidder_Rating varchar(40) comment 'the rating for user as a bidder',
Seller_Rating varchar(40) comment 'the rating for user as a seller',
primary key(UserID)
);
