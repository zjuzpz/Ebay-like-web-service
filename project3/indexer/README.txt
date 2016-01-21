Indexes created: 
ItemID: It is the primary key of Item table, 
        and we need ItemID to relate Item and ItemCategory table

Name
Category
User may use these three attributes to do search

Content: Name + Category + Description
User may use them together to do search



##################################################################################################
This example contains a simple utility class to simplify opening database
connections in Java applications, such as the one you will write to build
your Lucene index. 

To build and run the sample code, use the "run" ant target inside
the directory with build.xml by typing "ant run".
