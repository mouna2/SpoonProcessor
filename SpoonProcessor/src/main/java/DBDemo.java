import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import spoon.Launcher;
import spoon.SpoonAPI;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.ClassFactory;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.FieldAccessFilter;

/**
 * This class demonstrates how to connect to MySQL and run some basic commands.
 * 
 * In order to use this, you have to download the Connector/J driver and add
 * its .jar file to your build path.  You can find it here:
 * 
 * http://dev.mysql.com/downloads/connector/j/
 * 
 * You will see the following exception if it's not in your class path:
 * 
 * java.sql.SQLException: No suitable driver found for jdbc:mysql://localhost:3306/
 * 
 * To add it to your class path:
 * 1. Right click on your project
 * 2. Go to Build Path -> Add External Archives...
 * 3. Select the file mysql-connector-java-5.1.24-bin.jar
 *    NOTE: If you have a different version of the .jar file, the name may be
 *    a little different.
 *    
 * The user name and password are both "root", which should be correct if you followed
 * the advice in the MySQL tutorial. If you want to use different credentials, you can
 * change them below. 
 * 
 * You will get the following exception if the credentials are wrong:
 * 
 * java.sql.SQLException: Access denied for user 'userName'@'localhost' (using password: YES)
 * 
 * You will instead get the following exception if MySQL isn't installed, isn't
 * running, or if your serverName or portNumber are wrong:
 * 
 * java.net.ConnectException: Connection refused
 */
public class DBDemo {

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

		conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/databasechess2","root","123");

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
	 */
	public void run() {
		ResultSet rs = null; 
		// Connect to MySQL
		Connection conn = null;
		try {
			conn = this.getConnection();
			System.out.println("Connected to database");
		} catch (SQLException e) {
			System.out.println("ERROR: Could not connect to the database");
			e.printStackTrace();
			return;
		}

		// Create a table
		try {
			
			Statement st= conn.createStatement();
			st.executeUpdate("TRUNCATE TABLE `classes`"); 
			st.executeUpdate("TRUNCATE TABLE `superclasses`"); 
			st.executeUpdate("TRUNCATE TABLE `interfaces`"); 
		   Spoon(); 
		  
		   
		   
		   rs= st.executeQuery("SELECT * FROM CLASSES");
		   while(rs.next()){
			   //System.out.println(rs.getString("classname"));
		   }
	    } catch (SQLException e) {
			System.out.println("ERROR: Could not create the table");
			e.printStackTrace();
			return;
		}
		
		
	}
	
	/**
	 * Connect to the DB and do some stuff
	 */
	public static void main(String[] args) {
		DBDemo app = new DBDemo();
		app.run();
	}
	
	public void Spoon() throws SQLException {
	DBDemo dao = new DBDemo();
	Connection conn=getConnection();
	Statement st= conn.createStatement();
	
	    
		SpoonAPI spoon = new Launcher();
    	spoon.addInputResource("C:\\Users\\mouna\\Downloads\\chessgantcode\\workspace_codeBase\\Chess");
    	spoon.getEnvironment().setAutoImports(true);
    	spoon.getEnvironment().setNoClasspath(true);
    	CtModel model = spoon.buildModel();
    	//List<String> classnames= new ArrayList<String>(); 
  
    	// Interact with model
    	Factory factory = spoon.getFactory();
    	ClassFactory classFactory = factory.Class();
    	int i=1; 
        /*********************************************************************************************************************************************************************************/	
        /*********************************************************************************************************************************************************************************/	
        /*********************************************************************************************************************************************************************************/	  	
    	for(CtType<?> clazz : classFactory.getAll()) {
    		
    	//BUILD CLASSES TABLE 
    		
			
			String FullClassName= clazz.getPackage()+"."+clazz.getSimpleName(); 
			st.executeUpdate("INSERT INTO `classes`(`classname`) VALUES ('"+FullClassName+"');");
		
			 ResultSet rs = st.executeQuery("SELECT * FROM classes"); 
   		   while(rs.next()){
   			   //System.out.println(rs.getString("classname"));
   		   }			
   		
    		
    				
    	
   
    		
  		
    		 for(CtField<?> field : clazz.getFields()) {
    				for(CtMethod<?> method :clazz.getMethods()) {
    	    			// method.getParameters()
    	    			method.<CtFieldAccess<?>>getElements(new FieldAccessFilter(field.getReference()));
    	    		}
    		 }
    	}
    	
    /*********************************************************************************************************************************************************************************/	
    /*********************************************************************************************************************************************************************************/	
    /*********************************************************************************************************************************************************************************/	

    	//BUILD SUPERCLASSES TABLE 
    	for(CtType<?> clazz : classFactory.getAll()) {
    		String childclassQuery = null; 
    		String superclassQuery = null;
    		
			String FullClassName= clazz.getPackage()+"."+clazz.getSimpleName(); 
if(clazz.getSuperclass()!=null && clazz.getSuperclass().toString().contains("de.java_chess") && clazz.getSuperclass().toString().contains("TestCase")==false) {
    			
    			String superclass= clazz.getSuperclass().toString();
    			System.out.println(i+"    HERE IS MY SUPERCLASS"+superclass+"AND HERE IS MY SUBCLASS  "+FullClassName);
    		i++; 
    
    					ResultSet sClass = st.executeQuery("SELECT id from classes where classname='"+superclass+"'"); 
    					while(sClass.next()){
    						 superclassQuery= sClass.getString("id"); 
    						System.out.println("superclass: "+superclassQuery);	
    			   		   }
    					
    					
    					ResultSet cClass = st.executeQuery("SELECT id from classes where classname='"+FullClassName+"'"); 
    					while(cClass.next()){
    						 childclassQuery= cClass.getString("id"); 
    						System.out.println("subclass: "+childclassQuery);	
    			   		   }
    					
    					
    			String result= "SELECT classname from classes where classname='"+FullClassName+"'"; 
    			st.executeUpdate("INSERT INTO `superclasses`(`superclass`, `childclass`) VALUES ('"+superclassQuery +"','" +childclassQuery+"')");
    			
    		
    		
    		/*	st.executeUpdate("INSERT INTO `superclasses`(`superclass`, `childclass`) VALUES( "
    					+"(("+ superclassQuery+")"
    					+ ", ("+childclassQuery+")));" ); */
        		clazz.getSuperInterfaces();
        		
    		}
    	}
    	
        /*********************************************************************************************************************************************************************************/	
        /*********************************************************************************************************************************************************************************/	
        /*********************************************************************************************************************************************************************************/	  	
     	//BUILD INTERFACES TABLE 
    	for(CtType<?> clazz : classFactory.getAll()) {
    		
    		
    		String myinterface = null;
    		String myclass = null;
    		
			String FullClassName= clazz.getPackage()+"."+clazz.getSimpleName(); 
			Set<CtTypeReference<?>> interfaces = clazz.getSuperInterfaces(); 
			
			for(CtTypeReference<?> inter: interfaces) {
				System.out.println("my interface   "+inter);
				if(inter.toString().contains("java_chess")) {
					
					
					ResultSet interfacesclasses = st.executeQuery("SELECT id from classes where classname='"+inter+"'"); 

					while(interfacesclasses.next()){
						myinterface= interfacesclasses.getString("id"); 
						System.out.println("interface: "+myinterface);	
			   		   }
					
					ResultSet classesreferenced = st.executeQuery("SELECT id from classes where classname='"+FullClassName+"'"); 
					while(classesreferenced.next()){
						myclass= classesreferenced.getString("id"); 
						System.out.println("class referenced: "+myclass);	
			   		   }
					
					
					
	    			st.executeUpdate("INSERT INTO `interfaces`(`classid`, `interfaceid`) VALUES ('"+myclass +"','" +myinterface+"')");
				}
				
			}
			

    	}
	}
}