import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import spoon.Launcher;
import spoon.SpoonAPI;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.ClassFactory;
import spoon.reflect.factory.Factory;
import spoon.reflect.factory.MethodFactory;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.FieldAccessFilter;
import spoon.reflect.visitor.filter.TypeFilter;

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
public class DBDemo3 {

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
		conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/databasechess","root","123");

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
	 * @throws IOException 
	 */
	public void run() throws IOException {
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
		
			st.executeUpdate("DROP TABLE `databasechess`.`methodcallsexecuted`");
		   st.executeUpdate("CREATE TABLE `databasechess`.`methodcallsexecuted` (\r\n" + 
		   		"  `id` INT NOT NULL AUTO_INCREMENT,\r\n" + 
		   		"  `methodcalledid` LONGTEXT NULL,\r\n" + 
		   		"  `methodcalledname` LONGTEXT NULL,\r\n" + 
		   		"  `methodcalledclass` LONGTEXT NULL,\r\n" + 
		   		"  `callingmethodid` LONGTEXT NULL,\r\n" + 
		   		"  `callingmethodname` LONGTEXT NULL,\r\n" + 
		   		"  `callingmethodclass` LONGTEXT NULL,\r\n" + 
		   		"  PRIMARY KEY (`id`),\r\n" + 
		   		"  UNIQUE INDEX `id_UNIQUE` (`id` ASC)); " ); 
		   Spoon(); 
		  
		   
		   
		
	    } catch (SQLException e) {
			System.out.println("ERROR: Could not create the table");
			e.printStackTrace();
			return;
		}
		
		
	}
	
	/**
	 * Connect to the DB and do some stuff
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		DBDemo3 app = new DBDemo3();
		app.run();
	}
	
	public void Spoon() throws SQLException, IOException {
	DBDemo3 dao = new DBDemo3();
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
    	MethodFactory methodFactory = factory.Method(); 
    	int i=1; 
   	  	
    
/*********************************************************************************************************************************************************************************/	
/*********************************************************************************************************************************************************************************/	
/*********************************************************************************************************************************************************************************/   	
//BUILD METHODSCALLED TABLE

		File file = new File("C:\\Users\\mouna\\git\\ParseFile\\ParseFile\\src\\data");
		FileReader fileReader = new FileReader(file);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		StringBuffer stringBuffer = new StringBuffer();
		StringBuffer stringBuffer2 = new StringBuffer();
		String line;
		while ((line = bufferedReader.readLine()) != null) {
			String methodsCalling= line.substring(1, line.indexOf("---")); 	
			String ClassFROM=methodsCalling.substring(0, methodsCalling.lastIndexOf("."));
			String MethodFROM=methodsCalling.substring(methodsCalling.lastIndexOf(".")+1, methodsCalling.indexOf(")")+1);
			MethodFROM=MethodFROM.replace("/", "."); 
			MethodFROM=MethodFROM.replace(";", ""); 
			MethodFROM=MethodFROM.replace("Lde", "de"); 
			MethodFROM=MethodFROM.replace("-", ""); 
			String methodsCalled=line.substring(line.lastIndexOf("---")+5, line.length()-1); 			
			String ClassTO=methodsCalled.substring(0, methodsCalled.lastIndexOf("."));
			String MethodTO=methodsCalled.substring(methodsCalled.lastIndexOf(".")+1, methodsCalled.indexOf(")")+1); 
			MethodTO=MethodTO.replace("/", "."); 
			MethodTO=MethodTO.replace(";", ""); 
			MethodTO=MethodTO.replace("Lde", "de"); 
			MethodTO=MethodTO.replace("-", "");
			stringBuffer.append("\n");
			/*stringBuffer2.append("(SELECT MethodsID from Methods \r\n" + 
					"INNER JOIN Classes \r\n" + 
					"ON Classes.ClassID=Methods.ClassID\r\n" + 
					"where Methods.MethodName='"+MethodTO+"'  AND Classes.ClassName='"+ClassTO+"')),"); 
			stringBuffer2.append("\n");*/
			//
			
			System.out.println("CLASS FROM: "+ClassFROM+"        METHOD FROM       "+ MethodFROM+ "       CLASS TO       "+ ClassTO+"       Method To       "+MethodTO); 
			String callingmethodid=null; 
			String callingmethodsrefinedid=null; 
			String callingmethodsrefinedname=null; 
			String callingmethodclass=null; 
			String calledmethodid=null; 
			String calledmethodname=null; 
			String calledmethodclass=null; 
			//CALLING METHOD ID 
			ResultSet callingmethodsrefined = st.executeQuery("SELECT methods.id from methods INNER JOIN classes on methods.classname=classes.classname where methods.methodname='"+MethodFROM+"' "); 
			while(callingmethodsrefined.next()){
				callingmethodsrefinedid = callingmethodsrefined.getString("id"); 
	   		   }
			 
			//CALLING METHOD NAME 
			ResultSet callingmethodsrefinednames = st.executeQuery("SELECT methods.methodname from methods INNER JOIN classes on methods.classname=classes.classname where methods.methodname='"+MethodFROM+"'"); 
			while(callingmethodsrefinednames.next()){
				callingmethodsrefinedname = callingmethodsrefinednames.getString("methodname"); 
	   		   }
			
			
			//CALLING METHOD CLASS 
			ResultSet callingmethodsclasses = st.executeQuery("SELECT classes.classname from classes where classes.classname ='"+ClassFROM+"'"); 
			while(callingmethodsclasses.next()){
				callingmethodclass = callingmethodsclasses.getString("classname"); 
	   		   }
			
			
			//CALLED METHOD ID 
			ResultSet calledmethodsids= st.executeQuery("SELECT methods.id from methods INNER JOIN classes on methods.classname=classes.classname where methods.methodname='"+MethodTO+"'and classes.classname='"+ClassTO+"'"); 
			while(calledmethodsids.next()){
				calledmethodid = calledmethodsids.getString("id"); 
	   		   }
			 
			//CALLED METHOD NAME 
			ResultSet callemethodnames = st.executeQuery("SELECT methods.methodname from methods INNER JOIN classes on methods.classname=classes.classname where methods.methodname='"+MethodTO+"'"); 
			while(callemethodnames.next()){
				calledmethodname = callemethodnames.getString("methodname"); 
	   		   }
			
			
			//CALLED METHOD CLASS 
			ResultSet calledmethodclasses = st.executeQuery("SELECT classes.classname from classes where classes.classname ='"+ClassTO+"'"); 
			while(calledmethodclasses.next()){
				calledmethodclass = calledmethodclasses.getString("classname"); 
	   		   }
			
			

		//	System.out.println("CLASS FROM: "+ClassFROM+"        METHOD FROM       "+ MethodFROM+ "       CLASS TO       "+ ClassTO+"       Method To       "+MethodTO+"calling merthod refined id    "+ callingmethodsrefinedid+ "called method id    "+ calledmethodid); 

			if(callingmethodsrefinedid!=null && callingmethodclass!=null && calledmethodclass!=null && calledmethodname!=null && calledmethodid!=null) {
				String statement = "INSERT INTO `methodcallsexecuted`(`methodcalledid`,  `methodcalledname`,  `methodcalledclass`,`callingmethodid`,  `callingmethodname`, `callingmethodclass`) VALUES ('"+calledmethodid +"','" +MethodFROM+"','" +ClassFROM+"','" +callingmethodsrefinedid+"','" +MethodTO+"','" +ClassTO+"')";
				
				st.executeUpdate(statement);
			}
			
		}
		
	
	
		
	
		System.out.println("Contents of file:");
		System.out.println(stringBuffer.toString());
		
	



		
		
    	
	}
}