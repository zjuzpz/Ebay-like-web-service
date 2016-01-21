/*1*/
select count(*) from User;
/*2*/
select count(*) from Item where binary Location = 'New York';
/*3*/
select count(*) from 
(select ItemID from 
ItemCategory group by ItemID having count(*) = 4) as S;
/*4*/
select ItemID from Item where Currently = (select max(Currently) from Item where
Started < "2001-12-20 00:00:00" and Ends > "2001-12-20 00:00:00" and Number_of_Bids > 0)
and Number_of_Bids > 0; 
/*5*/
select count(*) from User where Seller_Rating > 1000;
/*6*/
select count(*) from User where Seller_Rating != "N/A" and Bidder_Rating != "N/A";
/*7*/
create temporary table if not exists temp_itemID as (
select ItemID from Bid where Amount > 100.00
);
select count(distinct Category) from ItemCategory where
ItemID in (select * from temp_itemID);

