/*Create table*/
drop table if exists Actors cascade;
create table Actors (
Name varchar(40),
Movie varchar(80),
Year integer,
Role Varchar(40)
);

/*Load data*/
load data local infile '~/data/actors.csv' into table Actors fields terminated by ',' optionally enclosed by '"';

/*Query*/
select Name from Actors where Movie = "Die Another Day";

drop table if exists Actors cascade;
