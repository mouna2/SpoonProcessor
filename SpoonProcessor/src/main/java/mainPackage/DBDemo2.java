package mainPackage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import Tables.RequirementClassKey;
import Tables.fieldmethod;
import Tables.methodcalls;
import Tables.methodcallsexecuted;
import Tables.methods;
import Tables.tracesmethods;
import Tables.tracesmethodscallees;
import spoon.Launcher;
import spoon.SpoonAPI;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtConstructor;
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
public class DBDemo2 {

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
	public List<tracesmethodscallees> TracesCalleesList= new ArrayList<tracesmethodscallees>();
	public List<tracesmethodscallees> TracesCallersList= new ArrayList<tracesmethodscallees>();

	
	public DBDemo2(List<tracesmethodscallees> tracesCalleesList) {
		 TracesCalleesList= new ArrayList<tracesmethodscallees>();

	}

	public DBDemo2() {
		// TODO Auto-generated constructor stub
	}

	public List<tracesmethodscallees> getTracesCalleesList() {
		return TracesCalleesList;
	}

	public void setTracesCalleesList(List<tracesmethodscallees> tracesCalleesList) {
		TracesCalleesList = tracesCalleesList;
	}

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
			st.executeUpdate("DROP SCHEMA `databasechess`"); 
			
			st.executeUpdate("CREATE DATABASE `databasechess`"); 
			st.executeUpdate("CREATE TABLE `databasechess`.`classes` (\r\n" + 
					"  `id` INT NOT NULL AUTO_INCREMENT,\r\n" + 
					"  `classname` LONGTEXT NULL,\r\n" + 
					"  PRIMARY KEY (`id`),\r\n" + 
					"  UNIQUE INDEX `id_UNIQUE` (`id` ASC));"); 
			
			

		    
		   st.executeUpdate("CREATE TABLE `databasechess`.`superclasses` (\r\n" + 
		   		"  `id` INT NOT NULL AUTO_INCREMENT,\r\n" + 
		   		"  `superclassid` INT NULL,\r\n" + 
		   		"  `superclassname` LONGTEXT NULL,\r\n" + 
		   		"  `ownerclassid` INT NULL,\r\n" + 
		   		"  `childclassname` LONGTEXT NULL,\r\n" + 
		   		"  PRIMARY KEY (`id`),\r\n" + 
		   		"  INDEX `superclassid_idx` (`superclassid` ASC),\r\n" + 
		   		"  INDEX `ownerclassid_idx` (`ownerclassid` ASC),\r\n" + 
		   		"  CONSTRAINT `superclassid`\r\n" + 
		   		"    FOREIGN KEY (`superclassid`)\r\n" + 
		   		"    REFERENCES `databasechess`.`classes` (`id`)\r\n" + 
		   		"    ON DELETE NO ACTION\r\n" + 
		   		"    ON UPDATE NO ACTION,\r\n" + 
		   		"  CONSTRAINT `ownerclassid`\r\n" + 
		   		"    FOREIGN KEY (`ownerclassid`)\r\n" + 
		   		"    REFERENCES `databasechess`.`classes` (`id`)\r\n" + 
		   		"    ON DELETE NO ACTION\r\n" + 
		   		"    ON UPDATE NO ACTION);"); 
		   
		   st.executeUpdate("CREATE TABLE `databasechess`.`interfaces` (\r\n" + 
		   		"  `id` INT NOT NULL AUTO_INCREMENT,\r\n" + 	   	
		   		"  `interfaceclassid` INT NULL,\r\n" + 
		   		"  `interfacename` LONGTEXT NULL,\r\n" + 
		   		"  `ownerclassid` INT NULL,\r\n" + 
		   		"  `classname` LONGTEXT NULL,\r\n" +	   		
		   		"  PRIMARY KEY (`id`),\r\n" + 
		   		"  UNIQUE INDEX `id_UNIQUE` (`id` ASC),\r\n" + 
		   		"  INDEX `interfaceclassid_idx` (`interfaceclassid` ASC),\r\n" + 
		   		"  INDEX `classid_idx` (`ownerclassid` ASC),\r\n" + 
		   		"  CONSTRAINT `interfaceclassid`\r\n" + 
		   		"    FOREIGN KEY (`interfaceclassid`)\r\n" + 
		   		"    REFERENCES `databasechess`.`classes` (`id`)\r\n" + 
		   		"    ON DELETE NO ACTION\r\n" + 
		   		"    ON UPDATE NO ACTION,\r\n" + 
		   		"  CONSTRAINT `ownerclassid2`\r\n" + 
		   		"    FOREIGN KEY (`ownerclassid`)\r\n" + 
		   		"    REFERENCES `databasechess`.`classes` (`id`)\r\n" + 
		   		"    ON DELETE NO ACTION\r\n" + 
		   		"    ON UPDATE NO ACTION);"); 
		   
		   st.executeUpdate("CREATE TABLE `databasechess`.`methods` (\r\n" + 
		   		"  `id` INT NOT NULL AUTO_INCREMENT,\r\n" + 
		   		"  `methodname` LONGTEXT NULL,\r\n" + 
		   		"  `methodnamerefined` LONGTEXT NULL,\r\n" + 
		   		"  `methodabbreviation` LONGTEXT NULL,\r\n" + 
		   		"  `classid` INT NULL,\r\n" + 
		   		"  `classname` LONGTEXT NULL,\r\n" + 
		   		"  PRIMARY KEY (`id`),\r\n" + 
		   		"  UNIQUE INDEX `id_UNIQUE` (`id` ASC),\r\n" + 
		   		"  INDEX `classid_idx` (`classid` ASC),\r\n" + 
		   		"  CONSTRAINT `classid2`\r\n" + 
		   		"    FOREIGN KEY (`classid`)\r\n" + 
		   		"    REFERENCES `databasechess`.`classes` (`id`)\r\n" + 
		   		"    ON DELETE NO ACTION\r\n" + 
		   		"    ON UPDATE NO ACTION);"); 
		   st.executeUpdate("CREATE TABLE `databasechess`.`parameters` (\r\n" + 
		   		"  `id` INT NOT NULL AUTO_INCREMENT,\r\n" + 
		   		"  `parametername` VARCHAR(200) NULL,\r\n" + 
		   		"  `parametertype` VARCHAR(200) NULL,\r\n" + 
		   		"  `parameterclass` INT NULL,\r\n" + 
		   		"  `classid` INT NULL,\r\n" + 
		   		"  `classname` VARCHAR(200) NULL,\r\n" + 
		   		"  `methodid` INT NULL,\r\n" + 
		   		"  `methodname` VARCHAR(200) NULL,\r\n" + 
		   		"  `isreturn` TINYINT NOT NULL,\r\n"+
		   		"  PRIMARY KEY (`id`),\r\n" + 
		   		"  UNIQUE INDEX `id_UNIQUE` (`id` ASC),\r\n" + 
		   		"  INDEX `classid_idx` (`classid` ASC),\r\n" + 
		   		"  INDEX `methodid_idx` (`methodid` ASC),\r\n" + 
		   		"  CONSTRAINT cons UNIQUE (id, parametername, classid, classname, methodname), \r\n"+
		   		"  CONSTRAINT `classid8`\r\n" + 
		   		"    FOREIGN KEY (`classid`)\r\n" + 
		   		"    REFERENCES `databasechess`.`classes` (`id`)\r\n" + 
		   		"    ON DELETE NO ACTION\r\n" + 
		   		"    ON UPDATE NO ACTION,\r\n" + 
		   		"  CONSTRAINT `classid3`\r\n" + 
		   		"    FOREIGN KEY (`classid`)\r\n" + 
		   		"    REFERENCES `databasechess`.`classes` (`id`)\r\n" + 
		   		"    ON DELETE NO ACTION\r\n" + 
		   		"    ON UPDATE NO ACTION,\r\n" + 
		   		"  CONSTRAINT `methodid`\r\n" + 
		   		"    FOREIGN KEY (`methodid`)\r\n" + 
		   		"    REFERENCES `databasechess`.`methods` (`id`)\r\n" + 
		   		"    ON DELETE NO ACTION\r\n" + 
		   		"    ON UPDATE NO ACTION"+   	
		   		 ")"); 
		   st.executeUpdate("CREATE TABLE `databasechess`.`fieldclasses` (\r\n" + 
		   		"  `id` INT NOT NULL AUTO_INCREMENT,\r\n" + 
		   		"  `fieldname` LONGTEXT NULL,\r\n" + 
		   		"  `fieldtypeclassid` INT NULL,\r\n" + 
		   		"  `fieldtype` LONGTEXT NULL,\r\n" + 
		   		"  `ownerclassid` INT NULL,\r\n" + 
		   		"  `classname` LONGTEXT NULL,\r\n" + 
		   		"  PRIMARY KEY (`id`),\r\n" + 
		   		"  INDEX `classid_idx` (`ownerclassid` ASC),\r\n" + 
		   		"  INDEX `classid_idx2` (`ownerclassid` ASC),\r\n" + 	
		   		"  CONSTRAINT `classid4`\r\n" + 
		   		"    FOREIGN KEY (`ownerclassid`)\r\n" + 
		   		"    REFERENCES `databasechess`.`classes` (`id`)\r\n" + 
		   		"    ON DELETE NO ACTION\r\n" + 
		   		"    ON UPDATE NO ACTION,"+ 
		   		"  CONSTRAINT `classid6`\r\n" + 
		   		"    FOREIGN KEY (`fieldtypeclassid`)\r\n" + 
		   		"    REFERENCES `databasechess`.`classes` (`id`)\r\n" + 
		   		"    ON DELETE NO ACTION\r\n" + 
		   		"    ON UPDATE NO ACTION);"); 
		   

		   
		   st.executeUpdate("CREATE TABLE `databasechess`.`fieldmethods` (\r\n" + 
		   		"  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,\r\n" + 
		   		"  `fieldaccess` VARCHAR(200) NULL,\r\n" + 
		   		"  `fieldtypeclassid` INT NULL,\r\n" + 
		   		"  `fieldtype` LONGTEXT NULL,\r\n" + 
		   		"  `classname` VARCHAR(200) NULL,\r\n" + 
		   		"  `ownerclassid` INT NULL,\r\n" + 
		   		"  `methodname` VARCHAR(200) NULL,\r\n" + 
		   		"  `ownermethodid` INT NULL,\r\n" + 
		   		"  PRIMARY KEY (`id`),\r\n" + 
		   		"  UNIQUE INDEX `id_UNIQUE` (`id` ASC),\r\n" + 
		   		"  INDEX `classid_idx` (`fieldtypeclassid` ASC),\r\n" + 
		   		"  INDEX `methodid_idx` (`ownermethodid` ASC),\r\n" + 		
		   		"  CONSTRAINT `classid5`\r\n" + 
		   		"    FOREIGN KEY (`fieldtypeclassid`)\r\n" + 
		   		"    REFERENCES `databasechess`.`classes` (`id`)\r\n" + 
		   		"    ON DELETE NO ACTION\r\n" + 
		   		"    ON UPDATE NO ACTION,\r\n" + 
		   		"  CONSTRAINT `classid7`\r\n" + 
		   		"    FOREIGN KEY (`fieldtypeclassid`)\r\n" + 
		   		"    REFERENCES `databasechess`.`classes` (`id`)\r\n" + 
		   		"    ON DELETE NO ACTION\r\n" + 
		   		"    ON UPDATE NO ACTION,\r\n" + 
		   		"  CONSTRAINT `methodid2`\r\n" + 
		   		"    FOREIGN KEY (`ownermethodid`)\r\n" + 
		   		"    REFERENCES `databasechess`.`methods` (`id`)\r\n" + 
		   		"    ON DELETE NO ACTION\r\n" + 
		   		"    ON UPDATE NO ACTION);"); 
		   

		   st.executeUpdate("CREATE TABLE `databasechess`.`methodcalls` (\r\n" + 
		   		"  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,\r\n" + 
		   		"  `callermethodid` INT NULL,\r\n" + 
		   		"  `callername` LONGTEXT NULL,\r\n" + 
		   		"  `callerclass` LONGTEXT NULL,\r\n" + 
		   		"  `calleemethodid` INT NULL,\r\n" + 
		   		"  `calleename` LONGTEXT NULL,\r\n" + 
		   		"  `calleeclass` LONGTEXT NULL,\r\n" + 
		   		"  PRIMARY KEY (`id`),\r\n" + 
		   		"  UNIQUE INDEX `id_UNIQUE` (`id` ASC),\r\n" + 
		   		"  INDEX `caller_idx` (`callermethodid` ASC),\r\n" + 
		   		"  INDEX `callee_idx` (`calleemethodid` ASC),\r\n" + 
		   		"  CONSTRAINT `methodcalledid`\r\n" + 
		   		"    FOREIGN KEY (`callermethodid`)\r\n" + 
		   		"    REFERENCES `databasechess`.`methods` (`id`)\r\n" + 
		   		"    ON DELETE NO ACTION\r\n" + 
		   		"    ON UPDATE NO ACTION,\r\n" + 
		   		"  CONSTRAINT `callingmethodid`\r\n" + 
		   		"    FOREIGN KEY (`calleemethodid`)\r\n" + 
		   		"    REFERENCES `databasechess`.`methods` (`id`)\r\n" + 
		   		"    ON DELETE NO ACTION\r\n" + 
		   		"    ON UPDATE NO ACTION);"); 
		   st.executeUpdate("CREATE TABLE `databasechess`.`methodcallsexecuted` (\r\n" + 
			   		"  `id` INT NOT NULL AUTO_INCREMENT,\r\n" + 
			   		"  `callermethodid` LONGTEXT NULL,\r\n" + 
			   		"  `callername` LONGTEXT NULL,\r\n" + 
			   		"  `callerclass` LONGTEXT NULL,\r\n" + 
			   		"  `calleemethodid` LONGTEXT NULL,\r\n" + 
			   		"  `calleename` LONGTEXT NULL,\r\n" + 
			   		"  `calleeclass` LONGTEXT NULL,\r\n" + 
			   		"  PRIMARY KEY (`id`),\r\n" + 
			   		"  UNIQUE INDEX `id_UNIQUE` (`id` ASC)); " ); 
		   st.executeUpdate("CREATE TABLE `databasechess`.`traces` (\r\n" + 
		   		"  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,\r\n" + 
		   		"  `requirement` LONGTEXT NULL,\r\n" + 
		   		"  `requirementid` INT,\r\n" + 
		   		"  `method` LONGTEXT NULL,\r\n" + 
		   		"  `fullmethod` LONGTEXT NULL,\r\n" +
		   		"  `methodid` INT NULL,\r\n" + 
		   		"  `classname` LONGTEXT NULL,\r\n" + 
		   		"  `classid` LONGTEXT NULL,\r\n" + 
		   		"  `gold` LONGTEXT NULL,\r\n" + 
		   		"  `subject` LONGTEXT NULL,\r\n" + 
		   		"  `goldpredictioncallee` LONGTEXT NULL,\r\n" + 
		   		"  `goldpredictioncaller` LONGTEXT NULL,\r\n" + 
		   		"  PRIMARY KEY (`id`),\r\n" + 
		   		"  INDEX `methodid_idx8` (`methodid` ASC),\r\n" + 
		   		"  CONSTRAINT `methodid8`\r\n" + 
		   		"    FOREIGN KEY (`methodid`)\r\n" + 
		   		"    REFERENCES `databasechess`.`methods` (`id`)\r\n" + 
		   		"    ON DELETE NO ACTION\r\n" + 
		   		"    ON UPDATE NO ACTION);\r\n" + 	
		   		""); 
		 
		   
		   st.executeUpdate("CREATE TABLE `databasechess`.`requirements` (\r\n" + 
		   		"  `id` INT NOT NULL AUTO_INCREMENT,\r\n" + 
		   		"  `requirementname` LONGTEXT NULL,\r\n" + 
		   		"  PRIMARY KEY (`id`),\r\n" + 
		   		"  UNIQUE INDEX `id_UNIQUE` (`id` ASC));"); 
			 st.executeUpdate("CREATE TABLE `databasechess`.`tracesclasses` (\r\n" + 
			 		"  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,\r\n" + 
			 		"  `requirement` LONGTEXT NULL,\r\n" + 
			 		"  `requirementid` INT NULL,\r\n" + 
			 		"  `classname` LONGTEXT NULL,\r\n" + 
			 		"  `classid` INT NULL,\r\n" + 
			 		"  `gold` LONGTEXT NULL,\r\n" + 
			 		"  `subject` LONGTEXT NULL,\r\n" + 
			 		"  PRIMARY KEY (`id`),\r\n" + 
			 		"  UNIQUE INDEX `idtracesclasses_UNIQUE` (`id` ASC));\r\n" + 
			 		""); 
		   
		   try {
			Spoon();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		DBDemo2 app = new DBDemo2();
		app.run();
	}
	
	public void Spoon() throws SQLException, FileNotFoundException {
	DBDemo2 dao = new DBDemo2();
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
    
    	
    	
    	
    	
    	//BUILD CLASSES TABLE 
    	for(CtType<?> clazz : classFactory.getAll()) {
    		
    	
    		
			
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
    		String superclassQueryName=null; 
    		String childclassQueryName=null; 
    		
    		String FullClassName= clazz.getPackage()+"."+clazz.getSimpleName(); 
    		//String superclass= clazz.getSuperclass().toString();
    		
			
			//System.out.println("SUPERCLASS"+superclass +"SUBCLASS "+FullClassName);
if(clazz.getSuperclass()!=null && clazz.getSuperclass().toString().contains(clazz.getPackage().toString()) ) {
    			
    			String superclass= clazz.getSuperclass().toString();
    		//	System.out.println(i+"    HERE IS MY SUPERCLASS"+superclass+"AND HERE IS MY SUBCLASS  "+FullClassName);
    		i++; 
    
    					ResultSet sClass = st.executeQuery("SELECT id from classes where classname='"+superclass+"'"); 
    					while(sClass.next()){
    						 superclassQuery= sClass.getString("id"); 
    			//			System.out.println("superclass: "+superclassQuery);	
    			   		   }

    					ResultSet sClassName = st.executeQuery("SELECT classname from classes where classname='"+superclass+"'"); 
    					while(sClassName.next()){
    						 superclassQueryName= sClassName.getString("classname"); 
    			//			System.out.println("superclass: "+superclassQuery);	
    			   		   }		
    					
    					ResultSet cClass = st.executeQuery("SELECT id from classes where classname='"+FullClassName+"'"); 
    					while(cClass.next()){
    						 childclassQuery= cClass.getString("id"); 
    			//			System.out.println("subclass: "+childclassQuery);	
    			   		   }
    					ResultSet cClassName = st.executeQuery("SELECT classname from classes where classname='"+FullClassName+"'"); 
    					while(cClassName.next()){
    						 childclassQueryName= cClassName.getString("classname"); 
    			//			System.out.println("subclass: "+childclassQuery);	
    			   		   }
    					
    			String result= "SELECT classname from classes where classname='"+FullClassName+"'"; 
    			if(superclassQuery!=null)
    			st.executeUpdate("INSERT INTO `superclasses`(`superclassid`, `superclassname`, `ownerclassid`, `childclassname`) VALUES ('"+superclassQuery +"','" +superclassQueryName+"','" +childclassQuery+"','" +childclassQueryName+"')");
    			
    		
    		
    		/*	st.executeUpdate("INSERT INTO `superclasses`(`superclass`, `childclass`) VALUES( "
    					+"(("+ superclassQuery+")"
    					+ ", ("+childclassQuery+")));" ); */
        		//clazz.getSuperInterfaces();
        		
    		}
    	}
    	/*********************************************************************************************************************************************************************************/	
        /*********************************************************************************************************************************************************************************/	
        /*********************************************************************************************************************************************************************************/	
    	  	
     	//BUILD INTERFACES TABLE 
    	for(CtType<?> clazz : classFactory.getAll()) {
    		
    		
    		String myinterfaceclassid = null;
    		String myinterfacename = null;
    		String myclassid = null;
    		String myclassname = null;
    		
			String FullClassName= clazz.getPackage()+"."+clazz.getSimpleName(); 
			Set<CtTypeReference<?>> interfaces = clazz.getSuperInterfaces(); 
			
			for(CtTypeReference<?> inter: interfaces) {
			//	System.out.println("my interface   "+inter);
				if(inter.toString().contains(clazz.getPackage().toString())) {
					ResultSet interfacesnames = st.executeQuery("SELECT classname from classes where classname='"+inter+"'"); 
					while(interfacesnames.next()){
						myinterfacename= interfacesnames.getString("classname"); 
				//		System.out.println("interface: "+myinterface);	
			   		   }
					
					ResultSet interfacesclasses = st.executeQuery("SELECT id from classes where classname='"+inter+"'"); 
					while(interfacesclasses.next()){
						myinterfaceclassid= interfacesclasses.getString("id"); 
				//		System.out.println("interface: "+myinterface);	
			   		   }
					
					ResultSet classesnames= st.executeQuery("SELECT classname from classes where classname='"+FullClassName+"'"); 
					while(classesnames.next()){
						myclassname= classesnames.getString("classname"); 
				//		System.out.println("class referenced: "+myclass);	
			   		   }
					
					ResultSet interfacesname = st.executeQuery("SELECT id from classes where classname='"+FullClassName+"'"); 
					while(interfacesname.next()){
						myclassid= interfacesname.getString("id"); 
				//		System.out.println("class referenced: "+myclass);	
			   		   }
					
	    			st.executeUpdate("INSERT INTO `interfaces`(`interfaceclassid`,`interfacename`,`ownerclassid`, `classname`) VALUES ('"+myinterfaceclassid +"','" +myinterfacename+"','" +myclassid+"','" +myclassname+"')");
				}
				
			}
			

    	}
    	
    
    	
    	/*********************************************************************************************************************************************************************************/	
        /*********************************************************************************************************************************************************************************/	
        /*********************************************************************************************************************************************************************************/	  	
    	//BUILD METHODS TABLE 
    	List<methods> mymethodlist = new ArrayList<methods>(); 
    	for(CtType<?> clazz : classFactory.getAll()) {
    		
    	
    		String myclassid = null;
    		String myclassname = null;
    		
    		//ALTERNATIVE: Collection<CtMethod<?>> methods = clazz.getAllMethods(); 
			Collection<CtMethod<?>> methods = clazz.getMethods(); 
			String FullClassName= clazz.getPackage()+"."+clazz.getSimpleName(); 
			
			int count = StringUtils.countMatches(clazz.getPackage().toString(), ".");
			//System.out.println("count:   "+count);
			//NEEDS TO BE CHANGED 
		//	if(count==2) {
			 List<CtConstructor> MyContructorlist = clazz.getElements(new TypeFilter<>(CtConstructor.class)); 
			 for(CtConstructor<?> constructor: MyContructorlist) {
				 
				 	
					String FullConstructorName=constructor.getSignature().toString(); 
					
					String methodabbreviation=FullConstructorName.substring(0, FullConstructorName.indexOf("(")); 
					 methodabbreviation=FullClassName+".-init-"; 

					System.out.println("FULL CONSTRUCTOR NAME BEFORE METHOD ABBREVIATION:"+methodabbreviation);

					//st.executeUpdate("INSERT INTO `fields`(`fieldname`) VALUES ('"+field+"');");
					//24 is the size of the string "de.java_chess.javaChess."
					int packagesize= "de.java_chess.javaChess.".length(); 
						FullConstructorName=FullConstructorName.substring(packagesize, FullConstructorName.length()); 
						FullConstructorName="-init-"+FullConstructorName.substring(FullConstructorName.lastIndexOf('('));  
						
							System.out.println("FULL CONSTRUCTOR NAME AFTER:"+FullConstructorName);

						ResultSet classesreferenced = st.executeQuery("SELECT id from classes where classname='"+FullClassName+"'"); 
						while(classesreferenced.next()){
							myclassid= classesreferenced.getString("id"); 
					//		System.out.println("class referenced: "+myclass);	
				   		   }
						ResultSet classnames = st.executeQuery("SELECT classname from classes where classname='"+FullClassName+"'"); 
						while(classnames.next()){
							myclassname= classnames.getString("classname"); 
					//		System.out.println("class referenced: "+myclass);	
				   		   }
						
							System.out.println("FullClassName====="+ FullConstructorName);
					
							String FullMethodNameRefined=FullConstructorName.substring(0, FullConstructorName.indexOf("(")); 
						
							System.out.println(FullClassName);
							methods meth= new methods(FullConstructorName, myclassid, myclassname); 
							if(meth.contains(mymethodlist, meth)==false ) {
				    			st.executeUpdate("INSERT INTO `methods`(`methodname`, `methodnamerefined`, `methodabbreviation`,`classid`, `classname`) VALUES ('"+FullConstructorName+"','" +FullMethodNameRefined +"','" +methodabbreviation+"','" +myclassid+"','" +myclassname+"')");

								
				    			mymethodlist.add(meth); 
							}
						

						}
			 
			 
			 
			for(CtMethod<?> method: methods) {
				 
				 
				String FullMethodName=method.getSignature().toString(); 
				//st.executeUpdate("INSERT INTO `fields`(`fieldname`) VALUES ('"+field+"');");
			//	System.out.println(FullClassName);
				String FullMethodNameRefined=FullMethodName.substring(0, FullMethodName.indexOf("(")); 
				String methodabbreviation= clazz.getQualifiedName()+"."+FullMethodName; 
				methodabbreviation=methodabbreviation.substring(0, methodabbreviation.indexOf("(")); 
					ResultSet classesreferenced = st.executeQuery("SELECT id from classes where classname='"+FullClassName+"'"); 
					while(classesreferenced.next()){
						myclassid= classesreferenced.getString("id"); 
				//		System.out.println("class referenced: "+myclass);	
			   		   }
					ResultSet classnames = st.executeQuery("SELECT classname from classes where classname='"+FullClassName+"'"); 
					while(classnames.next()){
						myclassname= classnames.getString("classname"); 
				//		System.out.println("class referenced: "+myclass);	
			   		   }
					
				
				
					
						System.out.println(FullClassName);
						methods meth= new methods(methodabbreviation, myclassid, myclassname); 
						if(meth.contains(mymethodlist, meth)==false ) {
			    			st.executeUpdate("INSERT INTO `methods`(`methodname`,  `methodnamerefined`,`methodabbreviation`, `classid`, `classname`) VALUES ('"+FullMethodName +"','" +FullMethodNameRefined+"','" +methodabbreviation+"','" +myclassid+"','" +myclassname+"')");

							
			    			mymethodlist.add(meth); 
						}
						
						

					}

					
				
				
			//}
			
			
		
			
		
    	}
      	/*********************************************************************************************************************************************************************************/	
        /*********************************************************************************************************************************************************************************/	
        /*********************************************************************************************************************************************************************************/
    	
    	//BUILD PARAMETERS TABLE 
for(CtType<?> clazz : classFactory.getAll()) {
    		
    		System.out.println(clazz.getSimpleName());
    		System.out.println(clazz.getPackage());
    		String fullname= clazz.getPackage()+""+clazz.getQualifiedName(); 
    		String MethodReferenced=null; 
    		String MethodName=null; 
    		String parameter=null; 
    	    String ClassName=null; 
    	    String classid=null; 
    		String parameterclass=null; 
    		String paramclassid=null; 
    				
    		 //for(CtField<?> field : clazz.getFields()) {
    				for(CtMethod<?> method :clazz.getMethods()) {
    	    			List<CtParameter<?>> params = method.getParameters(); 
    				
    	    			
    	    			
    	    		
    	    	
    	    			for( CtParameter<?> myparam :params) {
    	    				boolean flag2=false; 
    	    				
    	    				ResultSet classnames = st.executeQuery("SELECT classes.classname from classes INNER JOIN methods ON classes.id=methods.classid where methods.methodname='"+method.getSignature().toString()+"' "); 
    	    				
	    					while(classnames.next()){
	    						 ClassName =classnames.getString("classname"); 
	    					
	    			   		   }
	    					
	    					ResultSet classids = st.executeQuery("SELECT classes.id from classes INNER JOIN methods ON classes.id=methods.classid where methods.methodname='"+method.getSignature().toString()+"' "); 
    	    				
	    					while(classids.next()){
	    						 classid =classids.getString("id"); 
	    					
	    			   		   }
	    					
    	    					ResultSet methods = st.executeQuery("SELECT methods.id from methods INNER JOIN classes ON classes.id=methods.classid where methods.methodname='"+method.getSignature().toString()+"' and classes.id='"+classid+"'"); 
    	    				
    	    					while(methods.next()){
    	    						MethodReferenced =methods.getString("id"); 
    	    					
    	    			   		   }
    	    				
    	    					ResultSet paramclassids = st.executeQuery("SELECT classes.id from classes where classes.classname='"+myparam.getType()+"'"); 
        	    				
    	    					while(paramclassids.next()){
    	    						flag2=true; 
    	    						paramclassid =paramclassids.getString("id"); 
    	    					
    	    			   		   }
    	    			
    	    				
    	    					
    	    					
    	    				
    	    				//	if(field.toString().contains("java.awt")==false && field.toString().contains("javax")==false) {
    	    						System.out.println("HERE IS A PARAMETER: "+ myparam);
    	    						if(MethodReferenced==null) {
    	    							System.out.println("HERE IS NULL PARAMETER: "+myparam+"method referenced======>"+MethodReferenced);
    	    						}
    	    						if(MethodReferenced!=null && flag2==true)
    	    		    			st.executeUpdate("INSERT INTO `parameters`(`parametername`, `parametertype`, `parameterclass`,`classid`, `classname`, `methodid`, `methodname`, `isreturn`) VALUES ('"+myparam +"','" +myparam.getType() +"','"+paramclassid+"','"+classid +"','"+ClassName+"','" +MethodReferenced+"','" +method.getSignature().toString()+"','" +0+"')");

    	    				//	}
    	    				
    	    				
    	    			}
    	    			
    	    		
    	    			/*List<CtStatement> bodystatements = methodbody.getStatements(); 
    	    			//List<CtReturn> returnstatement = methodbody.getElements(new TypeFilter<>(CtReturn.class)); 
    	    		
    	    				List<CtReturn> returnstatement = methodbody.getElements(new TypeFilter<>(CtReturn.class)); 
    	    				for(CtReturn ret: returnstatement) {
    	    					System.out.println("HERE IS RETURN: "+ret.getReturnedExpression().getType());
    	    					ret.getReturnedExpression().getType(); 
    	    				
    	    			}*/
    	    			boolean flag=false; 
    	    			CtTypeReference<?> MethodType = method.getType();  
     	    			System.out.println("METHOD TYPE  "+ MethodType);
     	    			ResultSet classnames = st.executeQuery("SELECT classes.classname from classes INNER JOIN methods ON classes.id=methods.classid where methods.methodname='"+method.getSignature().toString()+"' "); 
	    				
    					while(classnames.next()){
    						 ClassName =classnames.getString("classname"); 
    					
    			   		   }
    					
    					ResultSet classids = st.executeQuery("SELECT classes.id from classes INNER JOIN methods ON classes.id=methods.classid where methods.methodname='"+method.getSignature().toString()+"' "); 
	    				
    					while(classids.next()){
    						 classid =classids.getString("id"); 
    					
    			   		   }
    					
	    					ResultSet methods = st.executeQuery("SELECT methods.id from methods INNER JOIN classes ON classes.id=methods.classid where methods.methodname='"+method.getSignature().toString()+"' and classes.id='"+classid+"'"); 
	    				
	    					while(methods.next()){
	    						MethodReferenced =methods.getString("id"); 
	    					
	    			   		   }
	    				
	    					
	    					
	    					ResultSet parameterclasses = st.executeQuery("SELECT classes.id from classes where classes.classname='"+MethodType+"'"); 
		    				
	    					while(parameterclasses.next()){
	    						parameterclass =parameterclasses.getString("id"); 
	    						flag=true; 
	    					
	    			   		   }
     	    			
    	    			if(MethodReferenced!=null && flag==true)
    		    			st.executeUpdate("INSERT INTO `parameters`(`parametername`, `parametertype`, `parameterclass`,`classid`, `classname`, `methodid`, `methodname`, `isreturn`) VALUES ('"+MethodType +"','" +MethodType+"','" +parameterclass +"','" +classid +"','"+ClassName+"','" +MethodReferenced+"','" +method.getSignature().toString()+"','" +1+"')");

    	    		
    	    		}
    		 //}
    	}
/*********************************************************************************************************************************************************************************/	
/*********************************************************************************************************************************************************************************/	
/*********************************************************************************************************************************************************************************/
	
//BUILD FIELDS TABLE -- CLASSES
for(CtType<?> clazz : classFactory.getAll()) {
	
	
	
	String myclass = null;
	String myclassname=null; 
	String fieldid=null; 
//ALTERNATIVE: Collection<CtFieldReference<?>> fields = clazz.getAllFields(); 
	Collection<CtField<?>> fields = clazz.getFields(); 
	String FullClassName= clazz.getPackage()+"."+clazz.getSimpleName(); 
	
//ALTERNATIVE: 	for(CtFieldReference<?> field: fields) {	
	for(CtField<?> field: fields) {
		boolean flag=false; 
		//st.executeUpdate("INSERT INTO `fields`(`fieldname`) VALUES ('"+field+"');");
	//	System.out.println("my field   "+field);
		
			
			ResultSet classesreferenced = st.executeQuery("SELECT id from classes where classname='"+FullClassName+"'"); 
			while(classesreferenced.next()){
				myclass= classesreferenced.getString("id"); 
	//			System.out.println("class referenced: "+myclass);	
	   		   }
			ResultSet classnames = st.executeQuery("SELECT classname from classes where classname='"+FullClassName+"'"); 
			while(classnames.next()){
				myclassname= classnames.getString("classname"); 
	//			System.out.println("class referenced: "+myclass);	
	   		   }
			
			ResultSet fieldids = st.executeQuery("SELECT id from classes where classname='"+field.getType()+"'"); 
			while(fieldids.next()){
				flag=true; 
				fieldid= fieldids.getString("id"); 
	//			System.out.println("class referenced: "+myclass);	
	   		   }
			
		//	if(field.toString().contains("java.awt")==false && field.toString().contains("javax")==false) {
			if(fieldid!=null && flag==true) {
    			st.executeUpdate("INSERT INTO `fieldclasses`(`fieldname`, `fieldtypeclassid`, `fieldtype`, `ownerclassid`,  `classname`) VALUES ('"+field.getSimpleName() +"','"+fieldid +"','"+field.getType() +"','" +myclass+"','" +myclassname+"')");

			}

		//	}
		
		
	}
	

}
/*********************************************************************************************************************************************************************************/	
/*********************************************************************************************************************************************************************************/	
/*********************************************************************************************************************************************************************************/   	
//BUILD FIELDS TABLE -- METHODS

for(CtType<?> clazz : classFactory.getAll()) {
	String fieldname=null; 
	String Fieldid=null; 
	String Methodid=null; 
	String myclassname=null; 
	String MethodName=null; 
	String FieldName=null; 
	String myclass=null; 
	String fieldid=null; 
	String FullClassName= clazz.getPackage()+"."+clazz.getSimpleName();
	List<fieldmethod> FieldMethodsList= new ArrayList<fieldmethod>(); 
	
	
	for(CtMethod<?> method :clazz.getMethods()) {
		List<CtFieldAccess> list = method.getElements(new TypeFilter<>(CtFieldAccess.class)); 
		for(CtFieldAccess fieldaccess: list) {
			boolean flag=false; 
			ResultSet classesreferenced = st.executeQuery("SELECT id from classes where classname='"+FullClassName+"'"); 
			while(classesreferenced.next()){
				 myclass = classesreferenced.getString("id"); 
	//			System.out.println("class referenced: "+myclass);	
	   		   }
			
			
			ResultSet fieldnames = st.executeQuery("SELECT fieldname from fieldclasses where fieldclasses.fieldname='"+fieldaccess.toString()+"'"); 
			while(fieldnames.next()){
				 FieldName = fieldnames.getString("fieldname"); 
	//			System.out.println("class referenced: "+myclass);	
	   		   }
			ResultSet classnames = st.executeQuery("SELECT classname from classes where classname='"+FullClassName+"'"); 
			while(classnames.next()){
				 myclassname = classnames.getString("classname"); 
	//			System.out.println("class referenced: "+myclass);	
	   		   }
			
			ResultSet methodids = st.executeQuery("SELECT id from methods where methodname='"+method.getSignature().toString()+"'"); 
			
			while(methodids.next()){
				  Methodid = methodids.getString("id"); 
			
	   		   }
ResultSet methodnames = st.executeQuery("SELECT methodname from methods where methodname='"+method.getSignature().toString()+"'"); 
			
			while(methodnames.next()){
				  MethodName = methodnames.getString("methodname"); 
			
	   		   }
			
			ResultSet fieldids = st.executeQuery("SELECT id from classes where classname='"+fieldaccess.getType()+"'"); 
			while(fieldids.next()){
				flag=true; 
				fieldid= fieldids.getString("id"); 
	//			System.out.println("class referenced: "+myclass);	
	   		   }
			
			
			
			
			
			fieldmethod myfield= new fieldmethod(FieldName, myclassname, myclass, MethodName, Methodid); 
		
			
				if(myfield.contains(FieldMethodsList, myfield)==false && FieldName!=null && flag==true) {
					st.executeUpdate("INSERT INTO `fieldmethods`(`fieldaccess`, `fieldtypeclassid`, `fieldtype`,  `classname`,  `ownerclassid`,  `methodname`, `ownermethodid`) VALUES ('"+FieldName +"','" +fieldid+"','" +fieldaccess.getType()+"','" +myclassname+"','" +myclass+"','" +MethodName+"','" +Methodid+"')");
					FieldMethodsList.add(myfield); 
				}
			
			
			
			//ALTERNATIVE: 
			//st.executeUpdate("INSERT INTO `fieldmethods`(`fieldaccess`,  `classname`,  `classid`,  `methodname`, `methodid`) VALUES ('"+fieldaccess.toString() +"','" +myclassname+"','" +myclass+"','" +MethodName+"','" +Methodid+"')");
		}
	}


	

}   	
/*********************************************************************************************************************************************************************************/	
/*********************************************************************************************************************************************************************************/	
/*********************************************************************************************************************************************************************************/   	
//BUILD METHODSCALLED TABLE
List<methodcalls> methodcallsList = new ArrayList<methodcalls>(); 
for(CtType<?> clazz : classFactory.getAll()) {
	
	for(CtMethod<?> method :clazz.getMethods()) {
		 List<CtInvocation> methodcalls = method.getElements(new TypeFilter<>(CtInvocation.class)); 

		for( CtInvocation calledmethod: methodcalls) {
			String callingmethodid=null; 
			String callingmethodsrefinedid=null; 
			String callingmethodsrefinedname=null; 
			String callingmethodclass=null; 
			String calledmethodid=null; 
			String calledmethodname=null; 
			String calledmethodclass=null; 
			String paramclassid=null; 
			//CALLING METHOD ID 
			ResultSet callingmethodsrefined = st.executeQuery("SELECT methods.id from methods INNER JOIN classes on methods.classname=classes.classname where methods.methodname='"+calledmethod.getExecutable().getSignature().toString()+"' and classes.classname='"+  clazz.getQualifiedName() +"'"); 
			while(callingmethodsrefined.next()){
				callingmethodsrefinedid = callingmethodsrefined.getString("id"); 
	   		   }
			 
			//CALLING METHOD NAME 
			ResultSet callingmethodsrefinednames = st.executeQuery("SELECT methods.methodname from methods INNER JOIN classes on methods.classname=classes.classname where methods.methodname='"+calledmethod.getExecutable().getSignature().toString()+"' and classes.classname='"+  clazz.getQualifiedName() +"'"); 
			while(callingmethodsrefinednames.next()){
				callingmethodsrefinedname = callingmethodsrefinednames.getString("methodname"); 
	   		   }
			
			
			//CALLING METHOD CLASS 
			ResultSet callingmethodsclasses = st.executeQuery("SELECT classes.classname from methods INNER JOIN classes on methods.classname=classes.classname where methods.methodname='"+calledmethod.getExecutable().getSignature().toString()+"' and classes.classname='"+  clazz.getQualifiedName() +"'"); 
			while(callingmethodsclasses.next()){
				callingmethodclass = callingmethodsclasses.getString("classname"); 
	   		   }
			
			
			//CALLED METHOD ID 
			ResultSet calledmethodsids= st.executeQuery("SELECT methods.id from methods INNER JOIN classes on methods.classname=classes.classname where methods.methodname='"+method.getSignature().toString()+"' and classes.classname='"+  clazz.getQualifiedName() +"'"); 
			while(calledmethodsids.next()){
				calledmethodid = calledmethodsids.getString("id"); 
	   		   }
			 
			//CALLED METHOD NAME 
			ResultSet callemethodnames = st.executeQuery("SELECT methods.methodname from methods INNER JOIN classes on methods.classname=classes.classname where methods.methodname='"+method.getSignature().toString()+"' and classes.classname='"+  clazz.getQualifiedName() +"'"); 
			while(callemethodnames.next()){
				calledmethodname = callemethodnames.getString("methodname"); 
	   		   }
			
			
			//CALLED METHOD CLASS 
			ResultSet calledmethodclasses = st.executeQuery("SELECT classes.classname from methods INNER JOIN classes on methods.classname=classes.classname where methods.methodname='"+method.getSignature().toString()+"' and classes.classname='"+  clazz.getQualifiedName() +"'"); 
			while(calledmethodclasses.next()){
				calledmethodclass = calledmethodclasses.getString("classname"); 
	   		   }
			
			
			//System.out.println("CALLED METHOD "+calledmethodname+ "\tCLASS2: "+calledmethodclass+"\tCALLINGMETHOD: "+callingmethodsrefinedname+"CALLING MENTHOD CLASS"+callingmethodclass);

		    
			
			
			methodcalls methodcall= new methodcalls(calledmethodid, calledmethodname, calledmethodclass, callingmethodsrefinedid, callingmethodsrefinedname, callingmethodclass); 
			
			
			if(methodcall.contains(methodcallsList, methodcall)==false && callingmethodsrefinedname!=null && callingmethodsrefinedid!=null && callingmethodclass!=null && calledmethodclass!=null && calledmethodname!=null && calledmethodid!=null) {
				String statement = "INSERT INTO `methodcalls`(`callermethodid`,  `callername`,  `callerclass`,`calleemethodid`,  `calleename`, `calleeclass`) VALUES ('"+calledmethodid +"','" +calledmethodname+"','" +calledmethodclass+"','" +callingmethodsrefinedid+"','" +callingmethodsrefinedname+"','" +callingmethodclass+"')";
				
				st.executeUpdate(statement);
				methodcallsList.add(methodcall); 
			}
		
			
			
			
			
			
			
			
			

		
		}
	}


	

}       	
/*********************************************************************************************************************************************************************************/	
/*********************************************************************************************************************************************************************************/	
/*********************************************************************************************************************************************************************************/   	
//BUILD METHODSCALLED EXECUTED TABLE
File file = new File("C:\\Users\\mouna\\git\\SpoonProcessor\\data.txt");
FileReader fileReader = new FileReader(file);
BufferedReader bufferedReader = new BufferedReader(fileReader);
StringBuffer stringBuffer = new StringBuffer();
String line;
try {
	
	List<methodcallsexecuted> methodcallsexecutedlist= new ArrayList<methodcallsexecuted>(); 

	while ((line = bufferedReader.readLine()) != null) {
		String methodsCalling= line.substring(1, line.indexOf("---")); 	
		String ClassFROM=methodsCalling.substring(0, methodsCalling.lastIndexOf("."));
		String MethodFROM=methodsCalling.substring(methodsCalling.lastIndexOf(".")+1, methodsCalling.indexOf(")")+1);
		String returnFROM= methodsCalling.substring(methodsCalling.lastIndexOf(")")+1, methodsCalling.length());
		MethodFROM=MethodFROM.replace("/", "."); 
		MethodFROM=MethodFROM.replace(";", ","); 
		  int endIndex = MethodFROM.lastIndexOf(",");
		    if (endIndex != -1)  
		    {
		    	MethodFROM = MethodFROM.substring(0, endIndex)+")"; // not forgot to put check if(endIndex != -1)
		    }
		MethodFROM=MethodFROM.replace("Lde", "de"); 
		MethodFROM=MethodFROM.replace("Ljava", "java"); 
		//MethodFROM=MethodFROM.replace("-", ""); 
		String methodsCalled=line.substring(line.lastIndexOf("---")+5, line.length()-1); 			
		String ClassTO=methodsCalled.substring(0, methodsCalled.lastIndexOf("."));
		String MethodTO=methodsCalled.substring(methodsCalled.lastIndexOf(".")+1, methodsCalled.indexOf(")")+1); 
		String returnTO= methodsCalled.substring(methodsCalled.lastIndexOf(")")+1, methodsCalled.length());
		MethodTO=MethodTO.replace("/", "."); 
		MethodTO=MethodTO.replace(";", ","); 
		
		   endIndex = MethodTO.lastIndexOf(",");
		    if (endIndex != -1)  
		    {
		    	MethodTO = MethodTO.substring(0, endIndex)+")"; // not forgot to put check if(endIndex != -1)
		    }
		//MethodTO=MethodTO.substring(0, MethodTO.lastIndexOf(",")-2)+")"; 
		MethodTO=MethodTO.replace("Lde", "de"); 
		MethodTO=MethodTO.replace("Ljava", "java"); 
		//MethodTO=MethodTO.replace("-", "");
		stringBuffer.append("\n");
		/*stringBuffer2.append("(SELECT MethodsID from Methods \r\n" + 
				"INNER JOIN Classes \r\n" + 
				"ON Classes.ClassID=Methods.ClassID\r\n" + 
				"where Methods.MethodName='"+MethodTO+"'  AND Classes.ClassName='"+ClassTO+"')),"); 
		stringBuffer2.append("\n");*/
		// 
		//
		
		//System.out.println("CLASS FROM: "+ClassFROM+"        METHOD FROM       "+ MethodFROM+ "       CLASS TO       "+ ClassTO+"       Method To       "+MethodTO); 
		String callingmethodid=null; 
		String callingmethodsrefinedid=null; 
		String callingmethodsrefinedname=null; 
		String callingmethodclass=null; 
		String calledmethodid=null; 
		String calledmethodname=null; 
		String calledmethodclass=null; 
		String classFROMid=null; 
		String classTOid=null; 
		String ClassFROMName=null; 
		 String ClassTOName=null; 
		 String ParameterClassID=null; 
		//get rid of everything that comes after the $ sign 
		
				
				
		String MethodFROMTransformed= MethodFROM.substring(0, MethodFROM.indexOf("(")); 
		String MethodTOTransformed= MethodTO.substring(0, MethodTO.indexOf("(")); 
		//CALLING METHOD ID 
		
		if(ClassFROM.contains("$")) {
			ClassFROM=ClassFROM.substring(0, ClassFROM.indexOf("$")); 

		}
		if(ClassTO.contains("$")) {
			ClassTO=ClassTO.substring(0, ClassTO.indexOf("$")); 
		}
		if(MethodTOTransformed.equals("-clinit-")) {
			MethodTOTransformed="-init-"; 
		}
		if(MethodFROMTransformed.equals("-clinit-")) {
			MethodFROMTransformed="-init-"; 
		}
		
		//CALLING METHOD ID 
		ResultSet callingmethodsrefined = st.executeQuery("SELECT methods.id from methods INNER JOIN classes on methods.classname=classes.classname where methods.methodnamerefined='"+MethodFROMTransformed+"' and classes.classname='"+ClassFROM+"'"); 
		while(callingmethodsrefined.next()){
			callingmethodsrefinedid = callingmethodsrefined.getString("id"); 
	
		}
		 
		//CALLING METHOD NAME 
		ResultSet callingmethodsrefinednames = st.executeQuery("SELECT methods.methodname from methods INNER JOIN classes on methods.classname=classes.classname where methods.methodname='"+MethodFROMTransformed+"'"); 
		while(callingmethodsrefinednames.next()){
			callingmethodsrefinedname = callingmethodsrefinednames.getString("methodname"); 
			   }
		
		
		//CALLING METHOD CLASS 
		ResultSet callingmethodsclasses = st.executeQuery("SELECT classes.classname from classes where classes.classname ='"+ClassFROM+"'"); 
		while(callingmethodsclasses.next()){
			callingmethodclass = callingmethodsclasses.getString("classname"); 
			   }
		
		
		//CALLED METHOD ID 
		ResultSet calledmethodsids= st.executeQuery("SELECT methods.id from methods INNER JOIN classes on methods.classname=classes.classname where methods.methodnamerefined='"+MethodTOTransformed+"'and classes.classname='"+ClassTO+"'"); 
		while(calledmethodsids.next()){
			calledmethodid = calledmethodsids.getString("id"); 
			   }
		 
		//CALLED METHOD NAME 
		ResultSet callemethodnames = st.executeQuery("SELECT methods.methodname from methods INNER JOIN classes on methods.classname=classes.classname where methods.methodname='"+MethodTOTransformed+"'"); 
		while(callemethodnames.next()){
			calledmethodname = callemethodnames.getString("methodname"); 
			   }
		
		
		//CALLED METHOD CLASS 
		ResultSet calledmethodclasses = st.executeQuery("SELECT classes.classname from classes where classes.classname ='"+ClassTO+"'"); 
		while(calledmethodclasses.next()){
			calledmethodclass = calledmethodclasses.getString("classname"); 
			   }
		
		
		
		

		
		
		
		
				
				
				
				
		
		//System.out.println("CLASS FROM: "+ClassFROM+"        METHOD FROM       "+ MethodFROM+ "       CLASS TO       "+ ClassTO+"       Method To       "+MethodTO+"calling merthod refined id    "+ callingmethodsrefinedid+ "called method id    "+ calledmethodid); 

		methodcallsexecuted mce= new methodcallsexecuted(callingmethodsrefinedid, MethodFROMTransformed, ClassFROM, calledmethodid, MethodTOTransformed, ClassTO); 
		System.out.println(mce.toString()); 	
		if(mce.contains(methodcallsexecutedlist, mce)==false) {
			if(callingmethodsrefinedid!=null && calledmethodid!=null ) {
				String statement = "INSERT INTO `methodcallsexecuted`(`callermethodid`,  `callername`,  `callerclass`,`calleemethodid`,  `calleename`, `calleeclass`) VALUES ('"+callingmethodsrefinedid+"','" +MethodFROMTransformed+"','" +ClassFROM+"','"+calledmethodid +"','" +MethodTOTransformed+"','" +ClassTO +"')";		
				st.executeUpdate(statement);
				methodcallsexecutedlist.add(mce); 
			}
			else {
				//if the methods table does not contain a method call that is obtained from parsing the log file, then I am inserting this row within the methods table
				   //This is for METHOD FROM 
					
				
				//calculate class id FROM 
					ResultSet classidsFROM = st.executeQuery("SELECT classes.id from classes where classes.classname ='"+ClassFROM+"'"); 
					while(classidsFROM.next()){
						classFROMid = classidsFROM.getString("id"); 
						   }
					
					//calculate class classname FROM 
					ResultSet classnamesFROM = st.executeQuery("SELECT classes.classname from classes where classes.classname ='"+ClassFROM+"'"); 
					while(classnamesFROM.next()){
						ClassFROMName = classnamesFROM.getString("classname"); 
						   }
					
					
					//calculate class classname FROM 
					ResultSet paramclassids = st.executeQuery("SELECT classes.id from classes where classes.id ='"+returnFROM+"'"); 
					while(paramclassids.next()){
						classFROMid = paramclassids.getString("id"); 
						   }
					
					
				//	String MethodFROMRefined= MethodFROMTransformed.substring(0, MethodFROMTransformed.indexOf("(")); 
					String MethodFROMRefined= MethodFROMTransformed; 
					String MethodFROMAbbreviation = ClassFROM+"."+MethodFROMTransformed; 
					if(callingmethodsrefinedid==null) {
						st.executeUpdate("INSERT INTO `methods`(`methodname`,  `methodnamerefined`,`methodabbreviation`, `classid`, `classname`) VALUES ('"+MethodFROM +"','" +MethodFROMRefined+"','" +MethodFROMAbbreviation+"','" +classFROMid+"','" +ClassFROM+"')");
		    		
						//RECALCULATION PHASE: CALLING METHOD ID 
						 callingmethodsrefined = st.executeQuery("SELECT methods.id from methods INNER JOIN classes on methods.classname=classes.classname where methods.methodnamerefined='"+MethodFROMTransformed+"' and classes.classname='"+ClassFROM+"'"); 
						while(callingmethodsrefined.next()){
							callingmethodsrefinedid = callingmethodsrefined.getString("id"); 
					
						}
					
						String par= transformstring(returnFROM); 
					
						 if(par.contains("de.java_chess")) {//ignore the basic data types, only insert the parameters thaht have classes as data types 
							 
							 ResultSet ParameterClassIDs= st.executeQuery("SELECT classes.id from classes where classes.classname='"+par+"'"); 
								while(ParameterClassIDs.next()){
									 ParameterClassID = ParameterClassIDs.getString("id"); 
									   }
							 
							 
						st.executeUpdate("INSERT INTO `parameters`(`parametername`, `parametertype`, `parameterclass`,`classid`, `classname`, `methodid`, `methodname`, `isreturn`) VALUES ('"+par +"','" +par +"','"+ParameterClassID+"','"+classFROMid +"','"+ClassFROMName+"','" +callingmethodsrefinedid+"','" +MethodFROM+"','" +1+"')");
						 }
						String[] params = ExtractParams(MethodFROM); 
						 //insert parameters that were retrieved from the log file 

						for(String p: params) {
							System.out.println("HERE IS A PARAM==================================================================>"+p); 
							ResultSet ParameterClassIDs= st.executeQuery("SELECT classes.id from classes where classes.classname='"+p+"'"); 
							while(ParameterClassIDs.next()){
								 ParameterClassID = ParameterClassIDs.getString("id"); 
								   }
							
							
							if(p.contains("de.java_chess") && p!=null && p.equals("")==false && classFROMid!=null) {
								st.executeUpdate("INSERT INTO `parameters`(`parametername`, `parametertype`, `parameterclass`,`classid`, `classname`, `methodid`, `methodname`, `isreturn`) VALUES ('"+p +"','" +p +"','"+ParameterClassID+"','"+classFROMid +"','"+ClassFROMName+"','" +callingmethodsrefinedid+"','" +MethodFROM+"','" +0+"')");

							}
					}
				
					
					
					
					//METHOD TO 
					//calculate class id TO 
					ResultSet classidsTO = st.executeQuery("SELECT classes.id from classes where classes.classname ='"+ClassTO+"'"); 
					while(classidsTO.next()){
						classTOid = classidsTO.getString("id"); 
						   }
					
					//String MethodTORefined= MethodTOTransformed.substring(0, MethodTOTransformed.indexOf("(")); 
					String MethodTORefined= MethodTOTransformed;
					String MethodTOAbbreviation = ClassTO+"."+MethodTORefined; 
					if(calledmethodid==null) {
						st.executeUpdate("INSERT INTO `methods`(`methodname`,  `methodnamerefined`,`methodabbreviation`, `classid`, `classname`) VALUES ('"+MethodTO +"','" +MethodTORefined+"','" +MethodTOAbbreviation+"','" +classTOid+"','" +ClassTO+"')");

						//RECALCULATION PHASE: CALLED METHOD ID 
						 calledmethodsids= st.executeQuery("SELECT methods.id from methods INNER JOIN classes on methods.classname=classes.classname where methods.methodnamerefined='"+MethodTOTransformed+"'and classes.classname='"+ClassTO+"'"); 
						while(calledmethodsids.next()){
							calledmethodid = calledmethodsids.getString("id"); 
							   }
						
						
						
						//calculate class classname FROM 
						ResultSet classnamesTO = st.executeQuery("SELECT classes.classname from classes where classes.classname ='"+ClassTO+"'"); 
						
						while(classnamesTO.next()){
							ClassTOName = classnamesTO.getString("classname"); 
							   }
						 par= transformstring(returnTO); 
						 //insert return value within the parameters table 
						  ResultSet ParameterClassIDs = st.executeQuery("SELECT classes.id from classes where classes.classname='"+par+"'"); 
							while(ParameterClassIDs.next()){
								 ParameterClassID = ParameterClassIDs.getString("id"); 
								   }
						 if(par.contains("de.java_chess")) {//ignore the basic data types, only insert the parameters thaht have classes as data types 
								st.executeUpdate("INSERT INTO `parameters`(`parametername`, `parametertype`, `parameterclass`,`classid`, `classname`, `methodid`, `methodname`, `isreturn`) VALUES ('"+par +"','" +par +"','"+ParameterClassID+"','"+classTOid +"','"+ClassTOName+"','" +calledmethodid+"','" +MethodTO+"','" +1+"')");

						 }
						 
						 //insert parameters that were retrieved from the log file 
						 params = ExtractParams(MethodTO); 
						for(String p: params) {
							System.out.println("HERE IS A PARAM==================================================================>"+p); 
							 ParameterClassIDs= st.executeQuery("SELECT classes.id from classes where classes.classname='"+p+"'"); 
							while(ParameterClassIDs.next()){
								 ParameterClassID = ParameterClassIDs.getString("id"); 
								   }
							
							if(p.contains("de.java_chess")&& p!=null && p.equals("")==false && classTOid!=null) {
								st.executeUpdate("INSERT INTO `parameters`(`parametername`, `parametertype`, `parameterclass`,`classid`, `classname`, `methodid`, `methodname`, `isreturn`) VALUES ('"+p +"','" +p +"','"+ParameterClassID+"','"+classTOid +"','"+ClassTOName+"','" +calledmethodid+"','" +MethodTO+"','" +0+"')");

							}
						}
					
					}
				
					
					
					
					/*
					//RECALCULATION PHASE: CALLING METHOD ID 
					 callingmethodsrefined = st.executeQuery("SELECT methods.id from methods INNER JOIN classes on methods.classname=classes.classname where methods.methodnamerefined='"+MethodFROMTransformed+"' and classes.classname='"+ClassFROM+"'"); 
					while(callingmethodsrefined.next()){
						callingmethodsrefinedid = callingmethodsrefined.getString("id"); 
				
					}
					//RECALCULATION PHASE: CALLED METHOD ID 
					 calledmethodsids= st.executeQuery("SELECT methods.id from methods INNER JOIN classes on methods.classname=classes.classname where methods.methodnamerefined='"+MethodTOTransformed+"'and classes.classname='"+ClassTO+"'"); 
					while(calledmethodsids.next()){
						calledmethodid = calledmethodsids.getString("id"); 
						   }*/
					
					//insert into methodcallsexecuted table 
					String statement = "INSERT INTO `methodcallsexecuted`(`callermethodid`,  `callername`,  `callerclass`,`calleemethodid`,  `calleename`, `calleeclass`) VALUES ('"+callingmethodsrefinedid+"','" +MethodFROMTransformed+"','" +ClassFROM+"','"+calledmethodid +"','" +MethodTOTransformed+"','" +ClassTO +"')";		
					st.executeUpdate(statement);
					methodcallsexecutedlist.add(mce); 	
					
					
				//insert into methodcalls table as well 
					String statement2 = "INSERT INTO `methodcalls`(`callermethodid`,  `callername`,  `callerclass`,`calleemethodid`,  `calleename`, `calleeclass`) VALUES ('"+callingmethodsrefinedid+"','" +MethodFROMTransformed+"','" +ClassFROM+"','"+calledmethodid +"','" +MethodTOTransformed+"','" +ClassTO +"')";		
					st.executeUpdate(statement2);
				
					
					
					
			}
		}
			
		
		
		
		
		
		}	
		
	}
} catch (IOException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
}





//System.out.println("Contents of file:");
//System.out.println(stringBuffer.toString());

/*********************************************************************************************************************************************************************************/	
/*********************************************************************************************************************************************************************************/	
/*********************************************************************************************************************************************************************************/   
//CREATE REQUIREMENTS TABLE 

 file = new File("C:\\Users\\mouna\\git\\SpoonProcessor\\Requirements.txt");
 fileReader = new FileReader(file);
 bufferedReader = new BufferedReader(fileReader);
 stringBuffer = new StringBuffer();

 
try {
	

	while ((line = bufferedReader.readLine()) != null) {
		System.out.println(line);
		
		
		
	
		
		String statement = "INSERT INTO `requirements`(`requirementname`) VALUES ('"+line+"')";		
		st.executeUpdate(statement);
	
		
		
	}




	}
	
catch (IOException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
}

/*********************************************************************************************************************************************************************************/	
/*********************************************************************************************************************************************************************************/	
/*********************************************************************************************************************************************************************************/   
//CREATE TRACES TABLE 

 file = new File("C:\\Users\\mouna\\git\\SpoonProcessor\\Traces.txt");
 fileReader = new FileReader(file);
 bufferedReader = new BufferedReader(fileReader);
 stringBuffer = new StringBuffer();
 String requirement=null; 
 String method=null; 
 String gold=null; 
 String subject=null; 
 String methodid=null; 
 String classname=null; 
 String classid=null; 
 String requirementid=null; 
String calleeid=null; 
String goldprediction=null; 
String calleeidexecuted=null; 
String callerid=null; 
String callerexecutedid=null; 
 List<tracesmethods> TraceListMethods= new ArrayList<tracesmethods>();
tracesmethodscallees tmc = null; 
try {
	
	line = bufferedReader.readLine(); 
	while ((line = bufferedReader.readLine()) != null) {
		System.out.println(line);
		String[] linesplitted = line.split(","); 
		method=linesplitted[1]; 
		requirement=linesplitted[2]; 
		gold=linesplitted[4]; 
		subject=linesplitted[5]; 
		String shortmethod=method.substring(0, method.indexOf("(")); 
		  String[] parts = shortmethod.split("[$]", 2);
		shortmethod=parts[0]; 
		shortmethod=shortmethod.replaceAll("clinit", "init"); 
		shortmethod=ParseLine(line); 
		System.out.println("HERE IS THIS SHORT METHOD========>"+ shortmethod); 
		methodid=null; 
			ResultSet methodids = st.executeQuery("SELECT methods.id from methods where methods.methodabbreviation ='"+shortmethod+"'"); 
			while(methodids.next()){
				methodid = methodids.getString("id"); 
				   }
		
		
		 classname=null; 
		ResultSet classnames = st.executeQuery("SELECT methods.classname from methods where methods.methodabbreviation ='"+shortmethod+"'"); 
		while(classnames.next()){
			classname = classnames.getString("classname"); 
			   }
		classid=null; 
		ResultSet classids = st.executeQuery("SELECT methods.classid from methods where methods.methodabbreviation ='"+shortmethod+"'"); 
		while(classids.next()){
			classid = classids.getString("classid"); 
			   }
		requirementid=null; 
		ResultSet requirements = st.executeQuery("SELECT requirements.id from requirements where requirements.requirementname ='"+requirement+"'"); 
		while(requirements.next()){
			requirementid = requirements.getString("id"); 
			   }
		// Rule: if method A calls method B and method A implements requirement X, then I can just assume that method B implements requirement X as well 
		// Retrieving the calleeid
		calleeid=null; 
			ResultSet calleesparsed = st.executeQuery("SELECT methodcalls.calleemethodid from methodcalls where methodcalls.callermethodid ='"+methodid+"'"); 
			while(calleesparsed.next()){
				 calleeid = calleesparsed.getString("calleemethodid"); }
			calleeidexecuted=null; 	   
			ResultSet calleesexecuted = st.executeQuery("SELECT methodcallsexecuted.calleemethodid from methodcallsexecuted where methodcallsexecuted.callermethodid ='"+methodid+"'"); 
			while(calleesexecuted.next()){
				 calleeidexecuted = calleesexecuted.getString("calleemethodid"); 
				   }
			callerid=null; 
			ResultSet callersparsed = st.executeQuery("SELECT methodcalls.callermethodid from methodcalls where methodcalls.calleemethodid ='"+methodid+"'"); 
			while(callersparsed.next()){
				  callerid = callersparsed.getString("callermethodid"); }
			callerexecutedid=null; 	   
			ResultSet callersexecuted = st.executeQuery("SELECT methodcallsexecuted.callermethodid from methodcallsexecuted where methodcallsexecuted.calleemethodid ='"+methodid+"'"); 
			while(callersexecuted.next()){
				 callerexecutedid = callersexecuted.getString("callermethodid"); 
				   }
	
		
		//insert into tracesmethodscallees a new object: if is found in the methodcalls table, then use the value from there 
		//otherwise, use the value from the methodcallsexecuted table 
			if(calleeid!=null && requirementid!=null) {
				 tmc= new tracesmethodscallees(requirement, requirementid, shortmethod, methodid, classname, classid, gold, subject, calleeid); 
				 TracesCalleesList.add(tmc); 
			}
			else if(calleeidexecuted!=null) {
				 tmc= new tracesmethodscallees(requirement, requirementid, shortmethod, methodid, classname, classid, gold, subject, calleeidexecuted); 
				 TracesCalleesList.add(tmc); 
			}
			
			if(calleeid!=null && requirementid!=null) {
				 tmc= new tracesmethodscallees(requirement, requirementid, shortmethod, methodid, classname, classid, gold, subject, callerid); 
				 TracesCallersList.add(tmc); 
			}
			else if(calleeidexecuted!=null) {
				 tmc= new tracesmethodscallees(requirement, requirementid, shortmethod, methodid, classname, classid, gold, subject, callerexecutedid); 
				 TracesCallersList.add(tmc); 
			}
			
			
			
		tracesmethods tr= new tracesmethods(requirement, requirementid, shortmethod, methodid, classname, classid, gold, subject); 
		if(methodid!=null && requirementid!=null && classid!=null) {
			if(tr.contains(TraceListMethods, tr)==false) {
				  
				String statement = "INSERT INTO `traces`(`requirement`, `requirementid`, `method`, `fullmethod`, `methodid`,`classname`, `classid`, `gold`,  `subject`, `goldpredictioncallee`, `goldpredictioncaller`) VALUES ('"+requirement+"','" +requirementid+"','" +shortmethod+"','" +method+"','" +methodid+"','"+classname +"','" +classid+"','"+gold +"','" +subject+"','" +goldprediction+"','" +goldprediction+"')";		
				st.executeUpdate(statement);
				TraceListMethods.add(tr); 
				
				
			}
			
		}
	
		
		
		
		
	
		
		
	}
	
	
	/*String filename= "TracesCalleesList.txt"; 
	ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename));
		oos.writeObject(TracesCalleesList);
		oos.flush();
		oos.close();*/
}
catch (IOException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
}
/*********************************************************************************************************************************************************************************/	
/*********************************************************************************************************************************************************************************/	
/*********************************************************************************************************************************************************************************/   
//make prediction on the column goldprediction 
int counter=0;

for(tracesmethodscallees tc: TracesCalleesList) {
	

	System.out.println("COUNTER "+counter +"tc.gold===============================================================>"+tc.gold); 
	System.out.println("tc.callee===============================================================>"+tc.callee); 
	System.out.println("tc.requirementid===============================================================>"+tc.requirementid+   "------"+tc.callee); 

	 String query = "update traces set goldpredictioncallee = ? where methodid = ? and requirementid = ?";
     PreparedStatement pstmt = conn.prepareStatement(query); // create a statement
     pstmt.setString(1, tc.gold); // set input parameter 1
     pstmt.setString(2, tc.callee); // set input parameter 2
     pstmt.setString(3, tc.requirementid); // set input parameter 3
     pstmt.executeUpdate(); // execute update statement
	
	//PreparedStatement preparedstatement = conn.prepareStatement("update table `databasechess`.`traces` SET `traces`.`goldprediction`='"+tc.gold+"' where `traces`.`methodid`='"+tc.callee+"'"); 
	// int goldpredictions = preparedstatement.executeUpdate();
	// conn.commit();
	// preparedstatement.close();
     counter++; 
	
	
}

counter=0;
for(tracesmethodscallees tc: TracesCallersList) {
	

	System.out.println("COUNTER "+counter +"tc.gold===============================================================>"+tc.gold); 
	System.out.println("tc.callee===============================================================>"+tc.callee); 
	System.out.println("tc.requirementid===============================================================>"+tc.requirementid+   "------"+tc.callee); 

	 String query = "update traces set goldpredictioncaller = ? where methodid = ? and requirementid = ?";
     PreparedStatement pstmt = conn.prepareStatement(query); // create a statement
     pstmt.setString(1, tc.gold); // set input parameter 1
     pstmt.setString(2, tc.callee); // set input parameter 2
     pstmt.setString(3, tc.requirementid); // set input parameter 3
     pstmt.executeUpdate(); // execute update statement
	
	//PreparedStatement preparedstatement = conn.prepareStatement("update table `databasechess`.`traces` SET `traces`.`goldprediction`='"+tc.gold+"' where `traces`.`methodid`='"+tc.callee+"'"); 
	// int goldpredictions = preparedstatement.executeUpdate();
	// conn.commit();
	// preparedstatement.close();
	counter++; 
	
	
}


	/*********************************************************************************************************************************************************************************/	
	/*********************************************************************************************************************************************************************************/	
	/*********************************************************************************************************************************************************************************/   
//BUILD TABLE FOR TRACES CLASSES 

List<RequirementClassKey> RequirementClassKeys= new ArrayList<RequirementClassKey>(); 
	
try {
		file = new File("C:\\Users\\mouna\\git\\SpoonProcessor\\Traces.txt");
		fileReader = new FileReader(file);
		bufferedReader = new BufferedReader(fileReader);	
		line = bufferedReader.readLine(); 
		Hashtable<RequirementClassKey,String> GoldHashTable=new Hashtable<RequirementClassKey,String>();  
		Hashtable<RequirementClassKey,String> SubjectHashTable=new Hashtable<RequirementClassKey,String>();  
		while ((line = bufferedReader.readLine()) != null) {
			System.out.println(line);
			String[] linesplitted = line.split(","); 
			method=linesplitted[1]; 
			requirement=linesplitted[2]; 
			gold=linesplitted[4]; 
			subject=linesplitted[5]; 
			String shortmethod=method.substring(0, method.indexOf("(")); 
			  String[] parts = shortmethod.split("[$]", 2);
			shortmethod=parts[0]; 
			shortmethod=shortmethod.replaceAll("clinit", "init"); 
			
			 shortmethod=ParseLine(line); 
			 
			System.out.println("HERE IS THIS SHORT METHOD========>"+ shortmethod); 
	 String goldvalue=null; 
	 String subjectvalue=null; 
		
	
	
	classname=null; 
	ResultSet classnames = st.executeQuery("SELECT methods.classname from methods where methods.methodabbreviation ='"+shortmethod+"'"); 
	while(classnames.next()){
		classname = classnames.getString("classname"); 
		   }
	classid=null; 
	ResultSet classids = st.executeQuery("SELECT methods.classid from methods where methods.methodabbreviation ='"+shortmethod+"'"); 
	while(classids.next()){
		classid = classids.getString("classid"); 
		   }
	
	requirementid=null; 
	ResultSet requirements = st.executeQuery("SELECT requirements.id from requirements where requirements.requirementname ='"+requirement+"'"); 
	while(requirements.next()){
		requirementid = requirements.getString("id"); 
		   }	
	 
	goldvalue=null; 
	ResultSet goldvalues = st.executeQuery("SELECT traces.gold from traces where traces.requirementid ='"+requirementid+"' and traces.classid='"+classid+"'"); 
	 while(goldvalues.next()){
			goldvalue = goldvalues.getString("gold"); 
			   }
	subjectvalue=null; 
		ResultSet subjectvalues = st.executeQuery("SELECT traces.subject from traces where traces.requirementid ='"+requirementid+"' and traces.classid='"+classid+"'"); 
		while(subjectvalues.next()){
			subjectvalue = subjectvalues.getString("subject"); 
			   }
		
		//GoldSubjectValues goldsubject= new GoldSubjectValues(goldvalue, subjectvalue); 
		if(requirementid!=null && classid!=null ) {
			RequirementClassKey RequirementClassKey= new RequirementClassKey(requirementid, requirement, classid, classname, goldvalue, subjectvalue); 
			if(GoldHashTable.containsKey(RequirementClassKey)==false) {
				GoldHashTable.put(RequirementClassKey, goldvalue); 
			}
			else if((GoldHashTable.get(RequirementClassKey).equals("T")==false) &&( RequirementClassKey.getGoldflag().equals("T")==false)  ) {
				GoldHashTable.put(RequirementClassKey, goldvalue); 
			}
		
			else {
				GoldHashTable.put(RequirementClassKey, goldvalue); 
				RequirementClassKey.setGoldflag(goldvalue); 
			}
			
			if(SubjectHashTable.containsKey(RequirementClassKey)==false) {
				SubjectHashTable.put(RequirementClassKey, subjectvalue); 
			}
			
			else if((SubjectHashTable.get(RequirementClassKey).equals("T")==false) && (RequirementClassKey.getSubjectflag().equals("T")==false) ) {
				SubjectHashTable.put(RequirementClassKey, subjectvalue); 
			}
			else {
				SubjectHashTable.put(RequirementClassKey, subjectvalue); 
				RequirementClassKey.setSubjectflag(subjectvalue); 
			}
			if(RequirementClassKey.contains(RequirementClassKeys, RequirementClassKey)==false) {
				String statement2= "INSERT INTO `tracesclasses`(`requirement`, `requirementid`,  `classname`, `classid`, `gold`,  `subject`) VALUES ('"+requirement+"','" +requirementid+"','"  +classname+"','" +classid+"','"+GoldHashTable.get(RequirementClassKey) +"','" +SubjectHashTable.get(RequirementClassKey)+"')";	
				st.executeUpdate(statement2); 
				RequirementClassKeys.add(RequirementClassKey); 
			}
		
			
		}
	
		
	 
	
		
	

		
	



		}
	
	
	
	
	}
	catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	}
	
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
	
	public String ParseLine(String line) {
		System.out.println(line);
		String[] linesplitted = line.split(","); 
		String method = linesplitted[1]; 
		String requirement = linesplitted[2]; 
		String gold = linesplitted[4]; 
		String subject = linesplitted[5]; 
		System.out.println("HERE IS THIS SHORT METHOD========>"+ method); 
		
		String shortmethod=method.substring(0, method.indexOf("("));
		String regex = "(.)*(\\d)(.)*";      
		Pattern pattern = Pattern.compile(regex);
		boolean containsNumber = pattern.matcher(shortmethod).matches();
		String[] firstpart;
		String FinalMethod = null;
		if(shortmethod.contains("$") && shortmethod.matches(".*\\d+.*")) {
			 firstpart = shortmethod.split("\\$");
			String myfirstpart= firstpart[0]; 
			FinalMethod=myfirstpart; 
			if(StringUtils.isNumeric(firstpart[1])==false) {
				String[] secondpart = firstpart[1].split("\\d"); 
				System.out.println("my first part "+ myfirstpart+ "firstpart"+ firstpart[1]);
				
				String mysecondpart=secondpart[1]; 
				
				 FinalMethod=myfirstpart+mysecondpart; 
				System.out.println("FINAL RESULT:    "+FinalMethod);
			}
			
		}
		
		else if(shortmethod.contains("$") && containsNumber==false) {
			 firstpart = shortmethod.split("\\$");
			
			System.out.println("FINAL STRING:   "+firstpart[0]);
			firstpart[1]=firstpart[1].substring(firstpart[1].indexOf("."), firstpart[1].length()); 
			System.out.println("FINAL STRING:   "+firstpart[1]);
			 FinalMethod= firstpart[0]+firstpart[1]; 
			System.out.println("FINAL STRING:   "+FinalMethod);
		}
		else {
			FinalMethod=shortmethod; 
		}
		return FinalMethod; 
	}
	
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
	public String transformstring(String s) {
		s=s.replace("/", "."); 
		s=s.replace(";", ","); 
		  int endIndex = s.lastIndexOf(",");
		    if (endIndex != -1)  
		    {
		    	s = s.substring(0, endIndex); // not forgot to put check if(endIndex != -1)
		    }
		s=s.replace("Lde", "de"); 
		s=s.replace("Ljava", "java"); 
		return s; 
	}
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
	
	public String[] ExtractParams(String method) {
		String Paramlist=method.substring(method.indexOf("(")+1, method.indexOf(")")); 
		 String[] data = Paramlist.split(",");
		 return data; 
	}
	
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
	
public List<tracesmethodscallees> GetList() {
			return TracesCalleesList; 
}
}
