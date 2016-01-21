create table ItemLocation (
ItemID varchar(40) not null,
Location point not null,
primary key(ItemID)
) ENGINE = MyISAM;

Insert into ItemLocation
select ItemID, point(Latitude, Longitude) from Item
where Latitude != "" and Longitude != "";

create spatial index LocationIndex on ItemLocation(Location);