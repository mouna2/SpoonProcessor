import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

import com.mysql.jdbc.FailoverConnectionProxy;

import Tables.TableTraces;
import Tables.tracesmethodscallees;
import spoon.Launcher;
import spoon.SpoonAPI;
import spoon.reflect.CtModel;
import spoon.reflect.factory.ClassFactory;
import spoon.reflect.factory.Factory;

public class Predictions {
	
	/** The name of the MySQL account to use (or empty for anonymous) */
	private final String userName = "root";

	/** The password for the MySQL account (or empty for anonymous) */
	private final String password = "root";

	/** The name of the computer running MySQL */
	private final String serverName = "localhost";

	/** The port of the MySQL server (default is 3306) */
	private final int portNumber = 3306;

	/** The name of the database we are testing with (this default is installed with MySQL) */
	private final String dbName = "databasechess";
	
	/** The name of the table we are testing with */
	private final String tableName = "classes";
	DBDemo2 dbdemo = new DBDemo2(); 
	static String methodid=null; 
	static String classname=null; 
	Factory factory; 
	static ClassFactory classFactory; 
	/**
	 * Get a new database connection
	 * 
	 * @return
	 * @throws SQLException
	 */
	public Connection getConnection() throws SQLException {
		Connection conn = null;
		Properties connectionProps = new Properties();
		connectionProps.put("root", this.userName);
		connectionProps.put("123", this.password);
		conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/databasechess","root","123");
		SpoonAPI spoon = new Launcher();
		spoon.addInputResource("C:\\Users\\mouna\\Downloads\\chessgantcode\\workspace_codeBase\\Chess");
		spoon.getEnvironment().setAutoImports(true);
		spoon.getEnvironment().setNoClasspath(true);
		CtModel model = spoon.buildModel();
		//List<String> classnames= new ArrayList<String>(); 

		// Interact with model
		 factory= spoon.getFactory();
		 classFactory = factory.Class();
		return conn;
	}

	/**
	 * Run a SQL command which does not return a recordset:
	 * CREATE/INSERT/UPDATE/DELETE/DROP/etc.
	 * 
	 * @throws SQLException If something goes wrong
	 */
	public boolean executeUpdate(Connection conn, String command) throws SQLException {
	    Statement stmt = null;
	    try {
	        stmt = conn.createStatement();
	        stmt.executeUpdate(command); // This will throw a SQLException if it fails
	        return true;
	    } finally {

	    	// This will run whether we throw an exception or not
	        if (stmt != null) { stmt.close(); }
	    }
	}
	
	/**
	 * Connect to MySQL and do some stuff.
	 * @param tracesCalleesList 
	 * @throws SQLException 
	 * @throws FileNotFoundException 
	 */
	public static void run() throws SQLException, FileNotFoundException {
		ResultSet rs = null; 
		// Connect to MySQL
		Connection conn = null;
		Predictions PRED= new Predictions(); 
		conn = PRED.getConnection();
		Statement st= conn.createStatement();
		System.out.println("Connected to database");
		ResultSet methodids = st.executeQuery("SELECT methods.id from methods"); 
		while(methodids.next()){
			 methodid = methodids.getString("id"); 
			   }
	
	
	
	ResultSet classnames = st.executeQuery("SELECT methods.classname from methods"); 
	while(classnames.next()){
		 classname = classnames.getString("classname"); 
		   }
	
	
	System.out.println("heyyyyyyyyyyyyyy   method id ===============================================================>"+methodid); 
	System.out.println("heyyyyyyyyyyyyyyyy  classname ===============================================================>"+classname); 
	TableTraces tracestable = new TableTraces(); 
	List<tracesmethodscallees> tracesCalleesList = tracestable.traces(st, classFactory); 
	for(tracesmethodscallees tc: tracesCalleesList) {
		

		System.out.println("tc.goldmmmmmmmmmm===============================================================>"+tc.gold); 
		System.out.println("tc.calleemmmmmmmmmmm===============================================================>"+tc.callee); 
		 String query = "update traces set goldprediction = ? where methodid = ? and requirementid = ?";
	     PreparedStatement pstmt = conn.prepareStatement(query); // create a statement
	     pstmt.setString(1, tc.gold); // set input parameter 1
	     pstmt.setString(2, tc.callee); // set input parameter 2
	     pstmt.setString(3, tc.requirementid); // set input parameter 3
	     pstmt.executeUpdate(); // execute update statement
		
		//PreparedStatement preparedstatement = conn.prepareStatement("update table `databasechess`.`traces` SET `traces`.`goldprediction`='"+tc.gold+"' where `traces`.`methodid`='"+tc.callee+"'"); 
		// int goldpredictions = preparedstatement.executeUpdate();
		// conn.commit();
		// preparedstatement.close();
		
		
		
	}
	
	
}
	public static void main (String [] args) throws SQLException, ClassNotFoundException, IOException {

		//DBDemo2 dbdemo = new DBDemo2(); 
		
		run(); 
		
	}
	
	
}