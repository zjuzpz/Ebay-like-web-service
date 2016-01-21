package edu.ucla.cs.cs144;

import java.io.IOException;
import java.io.StringReader;
import java.io.File;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class Indexer {
    
    /** Creates a new instance of Indexer */
    public Indexer() {
    }
	
    public void rebuildIndexes() {
		
		//Erase existing index
		try {
			getIndexWriter(true);
		} catch (IOException e) {
			System.out.println(e);
		}

        Connection conn = null;

        // create a connection to the database to retrieve Items from MySQL
		try {
			conn = DbManager.getConnection(true);
		} catch (SQLException ex) {
			System.out.println(ex);
		}


		/*
		 * Add your code here to retrieve Items using the connection
		 * and add corresponding entries to your Lucene inverted indexes.
			 *
			 * You will have to use JDBC API to retrieve MySQL data from Java.
			 * Read our tutorial on JDBC if you do not know how to use JDBC.
			 *
			 * You will also have to use Lucene IndexWriter and Document
			 * classes to create an index and populate it with Items data.
			 * Read our tutorial on Lucene as well if you don't know how.
			 *
			 * As part of this development, you may want to add 
			 * new methods and create additional Java classes. 
			 * If you create new classes, make sure that
			 * the classes become part of "edu.ucla.cs.cs144" package
			 * and place your class source files at src/edu/ucla/cs/cs144/.
		 * 
		 */
		 
		
		String item_sql = "select ItemID, Name, Description from Item";
		String category_sql = "select Category from ItemCategory where ItemID = ?";
		Statement item_stmt = DbManager.createStmt(conn);
		PreparedStatement category_prepared_stmt = DbManager.prepareStmt(conn, category_sql);
		ResultSet item_rs = DbManager.executeQuery(item_stmt, item_sql);
		
		String itemID, name, description, category;
		try {
			while(item_rs.next()) {
				//Get attributes from Item table
				itemID = item_rs.getString("ItemID");
				name = item_rs.getString("Name");
				description = item_rs.getString("Description");
				
				//Get attributes from ItemCategory table
				category_prepared_stmt.setString(1, itemID);
				ResultSet category_rs = DbManager.executeQuery(category_prepared_stmt);
				category = "";
				
				while(category_rs.next()) {
					category += category_rs.getString("Category") + " ";
				}

				//Create indexes for all the things
				try {
					createIndex(itemID, name, category, description);
				} catch (IOException e) {
					System.out.println(e);
				}
			
				//Close rs
				category_rs.close();
			}
			item_rs.close();
			category_prepared_stmt.close();
			item_stmt.close();
		} catch (SQLException e) {
			System.out.println(e);
		}
		//close index writer
		try {
			closeIndexWriter();
		} catch (IOException ex) {
			System.out.println(ex);
		}
			// close the database connection
		try {
			conn.close();
		} catch (SQLException ex) {
			System.out.println(ex);
		}
    }

	public void createIndex	(String itemID, String name, String category, String description) throws IOException {
		IndexWriter writer = getIndexWriter(false);
		String content = name + " " + category + " " + description;
		Document doc = new Document();
		doc.add(new StringField("ItemID", itemID, Field.Store.YES));
		doc.add(new StringField("Name", name, Field.Store.YES));
		doc.add(new StringField("Category", category, Field.Store.YES));
		doc.add(new TextField("content", content, Field.Store.NO));
		writer.addDocument(doc);
	}
	
	//Copy from the example with minute modification
	private IndexWriter indexWriter = null;

    public IndexWriter getIndexWriter(boolean create) throws IOException {
        if (indexWriter == null) {
            Directory indexDir = FSDirectory.open(new File("/var/lib/lucene/index1"));
            IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_10_2, new StandardAnalyzer());
			indexWriter = new IndexWriter(indexDir, config);
        }
        return indexWriter;
    }


    public void closeIndexWriter() throws IOException {
        if (indexWriter != null) {
            indexWriter.close();
        }
    }	

    public static void main(String args[]) {
        Indexer idx = new Indexer();
        idx.rebuildIndexes();
    }   
}
