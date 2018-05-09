import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

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
		   		"  `childclassid` INT NULL,\r\n" + 
		   		"  `childclassname` LONGTEXT NULL,\r\n" + 
		   		"  PRIMARY KEY (`id`),\r\n" + 
		   		"  INDEX `superclassid_idx` (`superclassid` ASC),\r\n" + 
		   		"  INDEX `childclassid_idx` (`childclassid` ASC),\r\n" + 
		   		"  CONSTRAINT `superclassid`\r\n" + 
		   		"    FOREIGN KEY (`superclassid`)\r\n" + 
		   		"    REFERENCES `databasechess`.`classes` (`id`)\r\n" + 
		   		"    ON DELETE NO ACTION\r\n" + 
		   		"    ON UPDATE NO ACTION,\r\n" + 
		   		"  CONSTRAINT `childclassid`\r\n" + 
		   		"    FOREIGN KEY (`childclassid`)\r\n" + 
		   		"    REFERENCES `databasechess`.`classes` (`id`)\r\n" + 
		   		"    ON DELETE NO ACTION\r\n" + 
		   		"    ON UPDATE NO ACTION);"); 
		   
		   st.executeUpdate("CREATE TABLE `databasechess`.`interfaces` (\r\n" + 
		   		"  `id` INT NOT NULL AUTO_INCREMENT,\r\n" + 	   	
		   		"  `interfaceid` INT NULL,\r\n" + 
		   		"  `interfacename` LONGTEXT NULL,\r\n" + 
		   		"  `classid` INT NULL,\r\n" + 
		   		"  `classname` LONGTEXT NULL,\r\n" +	   		
		   		"  PRIMARY KEY (`id`),\r\n" + 
		   		"  UNIQUE INDEX `id_UNIQUE` (`id` ASC),\r\n" + 
		   		"  INDEX `interfaceid_idx` (`interfaceid` ASC),\r\n" + 
		   		"  INDEX `classid_idx` (`classid` ASC),\r\n" + 
		   		"  CONSTRAINT `interfaceid`\r\n" + 
		   		"    FOREIGN KEY (`interfaceid`)\r\n" + 
		   		"    REFERENCES `databasechess`.`classes` (`id`)\r\n" + 
		   		"    ON DELETE NO ACTION\r\n" + 
		   		"    ON UPDATE NO ACTION,\r\n" + 
		   		"  CONSTRAINT `classid`\r\n" + 
		   		"    FOREIGN KEY (`classid`)\r\n" + 
		   		"    REFERENCES `databasechess`.`classes` (`id`)\r\n" + 
		   		"    ON DELETE NO ACTION\r\n" + 
		   		"    ON UPDATE NO ACTION);"); 
		   
		   st.executeUpdate("CREATE TABLE `databasechess`.`methods` (\r\n" + 
		   		"  `id` INT NOT NULL AUTO_INCREMENT,\r\n" + 
		   		"  `methodname` LONGTEXT NULL,\r\n" + 
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
		   		"  `classid` INT NULL,\r\n" + 
		   		"  `classname` LONGTEXT NULL,\r\n" + 
		   		"  PRIMARY KEY (`id`),\r\n" + 
		   		"  INDEX `classid_idx` (`classid` ASC),\r\n" + 
		   		"  CONSTRAINT `classid4`\r\n" + 
		   		"    FOREIGN KEY (`classid`)\r\n" + 
		   		"    REFERENCES `databasechess`.`classes` (`id`)\r\n" + 
		   		"    ON DELETE NO ACTION\r\n" + 
		   		"    ON UPDATE NO ACTION);"); 
		   

		   
		   st.executeUpdate("CREATE TABLE `databasechess`.`fieldmethods` (\r\n" + 
		   		"  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,\r\n" + 
		   		"  `fieldaccess` VARCHAR(200) NULL,\r\n" + 
		   		"  `classname` VARCHAR(200) NULL,\r\n" + 
		   		"  `classid` INT NULL,\r\n" + 
		   		"  `methodname` VARCHAR(200) NULL,\r\n" + 
		   		"  `methodid` INT NULL,\r\n" + 
		   		"  PRIMARY KEY (`id`),\r\n" + 
		   		"  UNIQUE INDEX `id_UNIQUE` (`id` ASC),\r\n" + 
		   		"  INDEX `classid_idx` (`classid` ASC),\r\n" + 
		   		"  INDEX `methodid_idx` (`methodid` ASC),\r\n" + 		
		   		"  CONSTRAINT `classid5`\r\n" + 
		   		"    FOREIGN KEY (`classid`)\r\n" + 
		   		"    REFERENCES `databasechess`.`classes` (`id`)\r\n" + 
		   		"    ON DELETE NO ACTION\r\n" + 
		   		"    ON UPDATE NO ACTION,\r\n" + 
		   		"  CONSTRAINT `methodid2`\r\n" + 
		   		"    FOREIGN KEY (`methodid`)\r\n" + 
		   		"    REFERENCES `databasechess`.`methods` (`id`)\r\n" + 
		   		"    ON DELETE NO ACTION\r\n" + 
		   		"    ON UPDATE NO ACTION);"); 
		   

		   st.executeUpdate("CREATE TABLE `databasechess`.`methodcalls` (\r\n" + 
		   		"  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,\r\n" + 
		   		"  `methodcalledid` INT NULL,\r\n" + 
		   		"  `methodcalledname` LONGTEXT NULL,\r\n" + 
		   		"  `methodcalledclass` LONGTEXT NULL,\r\n" + 
		   		"  `callingmethodid` INT NULL,\r\n" + 
		   		"  `callingmethodname` LONGTEXT NULL,\r\n" + 
		   		"  `callingmethodclass` LONGTEXT NULL,\r\n" + 
		   		"  PRIMARY KEY (`id`),\r\n" + 
		   		"  UNIQUE INDEX `id_UNIQUE` (`id` ASC),\r\n" + 
		   		"  INDEX `methodcalledid_idx` (`methodcalledid` ASC),\r\n" + 
		   		"  INDEX `callingmethodid_idx` (`callingmethodid` ASC),\r\n" + 
		   		"  CONSTRAINT `methodcalledid`\r\n" + 
		   		"    FOREIGN KEY (`methodcalledid`)\r\n" + 
		   		"    REFERENCES `databasechess`.`methods` (`id`)\r\n" + 
		   		"    ON DELETE NO ACTION\r\n" + 
		   		"    ON UPDATE NO ACTION,\r\n" + 
		   		"  CONSTRAINT `callingmethodid`\r\n" + 
		   		"    FOREIGN KEY (`callingmethodid`)\r\n" + 
		   		"    REFERENCES `databasechess`.`methods` (`id`)\r\n" + 
		   		"    ON DELETE NO ACTION\r\n" + 
		   		"    ON UPDATE NO ACTION);"); 
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
    			st.executeUpdate("INSERT INTO `superclasses`(`superclassid`, `superclassname`, `childclassid`, `childclassname`) VALUES ('"+superclassQuery +"','" +superclassQueryName+"','" +childclassQuery+"','" +childclassQueryName+"')");
    			
    		
    		
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
    		
    		
    		String myinterfaceid = null;
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
						myinterfaceid= interfacesclasses.getString("id"); 
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
					
	    			st.executeUpdate("INSERT INTO `interfaces`(`interfaceid`,`interfacename`,`classid`, `classname`) VALUES ('"+myinterfaceid +"','" +myinterfacename+"','" +myclassid+"','" +myclassname+"')");
				}
				
			}
			

    	}
    	
    
    	
    	/*********************************************************************************************************************************************************************************/	
        /*********************************************************************************************************************************************************************************/	
        /*********************************************************************************************************************************************************************************/	  	
    	//BUILD METHODS TABLE 
    	
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
					//st.executeUpdate("INSERT INTO `fields`(`fieldname`) VALUES ('"+field+"');");
					System.out.println("FULL CONSTRUCTOR NAME BEFORE:"+FullConstructorName);
					//24 is the size of the string "de.java_chess.javaChess."
						FullConstructorName=FullConstructorName.substring(24, FullConstructorName.length()); 
						//FullConstructorName="-init-"+FullConstructorName.substring(FullConstructorName.lastIndexOf('('));  
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
					
					
						
							System.out.println(FullClassName);
			    			st.executeUpdate("INSERT INTO `methods`(`methodname`, `classid`, `classname`) VALUES ('"+FullConstructorName +"','" +myclassid+"','" +myclassname+"')");

						}
			 
			 
			 
			for(CtMethod<?> method: methods) {
				 
				 
				String FullMethodName=method.getSignature().toString(); 
				//st.executeUpdate("INSERT INTO `fields`(`fieldname`) VALUES ('"+field+"');");
			//	System.out.println(FullClassName);
				
					
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
		    			st.executeUpdate("INSERT INTO `methods`(`methodname`, `classid`, `classname`) VALUES ('"+FullMethodName +"','" +myclassid+"','" +myclassname+"')");

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
    		
    		
    		 //for(CtField<?> field : clazz.getFields()) {
    				for(CtMethod<?> method :clazz.getMethods()) {
    	    			List<CtParameter<?>> params = method.getParameters(); 
    				
    	    		
    	    			
    	    		
    	    	
    	    			for( CtParameter<?> myparam :params) {
    	    				
    	    				
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
    	    				
    	    					
    	    			
    	    				
    	    					
    	    					
    	    				
    	    				//	if(field.toString().contains("java.awt")==false && field.toString().contains("javax")==false) {
    	    						System.out.println("HERE IS A PARAMETER: "+ myparam);
    	    						if(MethodReferenced==null) {
    	    							System.out.println("HERE IS NULL PARAMETER: "+myparam+"method referenced======>"+MethodReferenced);
    	    						}
    	    						if(MethodReferenced!=null)
    	    		    			st.executeUpdate("INSERT INTO `parameters`(`parametername`, `classid`, `classname`, `methodid`, `methodname`, `isreturn`) VALUES ('"+myparam +"','" +classid +"','"+ClassName+"','" +MethodReferenced+"','" +method.getSignature().toString()+"','" +0+"')");

    	    				//	}
    	    				
    	    				
    	    			}
    	    			
    	    		
    	    			/*List<CtStatement> bodystatements = methodbody.getStatements(); 
    	    			//List<CtReturn> returnstatement = methodbody.getElements(new TypeFilter<>(CtReturn.class)); 
    	    		
    	    				List<CtReturn> returnstatement = methodbody.getElements(new TypeFilter<>(CtReturn.class)); 
    	    				for(CtReturn ret: returnstatement) {
    	    					System.out.println("HERE IS RETURN: "+ret.getReturnedExpression().getType());
    	    					ret.getReturnedExpression().getType(); 
    	    				
    	    			}*/
    	    			
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
	    				
	    					
     	    			
    	    			if(MethodReferenced!=null)
    		    			st.executeUpdate("INSERT INTO `parameters`(`parametername`, `classid`, `classname`, `methodid`, `methodname`, `isreturn`) VALUES ('"+MethodType +"','" +classid +"','"+ClassName+"','" +MethodReferenced+"','" +method.getSignature().toString()+"','" +1+"')");

    	    		
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

//ALTERNATIVE: Collection<CtFieldReference<?>> fields = clazz.getAllFields(); 
	Collection<CtField<?>> fields = clazz.getFields(); 
	String FullClassName= clazz.getPackage()+"."+clazz.getSimpleName(); 
	
//ALTERNATIVE: 	for(CtFieldReference<?> field: fields) {	
	for(CtField<?> field: fields) {
		
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
			
		//	if(field.toString().contains("java.awt")==false && field.toString().contains("javax")==false) {
    			st.executeUpdate("INSERT INTO `fieldclasses`(`fieldname`, `classid`,  `classname`) VALUES ('"+field.getSimpleName() +"','" +myclass+"','" +myclassname+"')");

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
	String FullClassName= clazz.getPackage()+"."+clazz.getSimpleName();
	List<fieldmethod> FieldMethodsList= new ArrayList<fieldmethod>(); 
	
	
	for(CtMethod<?> method :clazz.getMethods()) {
		List<CtFieldAccess> list = method.getElements(new TypeFilter<>(CtFieldAccess.class)); 
		for(CtFieldAccess fieldaccess: list) {
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
			fieldmethod myfield= new fieldmethod(FieldName, myclassname, myclass, MethodName, Methodid); 
		
			
				if(myfield.contains(FieldMethodsList, myfield)==false && FieldName!=null) {
					st.executeUpdate("INSERT INTO `fieldmethods`(`fieldaccess`,  `classname`,  `classid`,  `methodname`, `methodid`) VALUES ('"+FieldName +"','" +myclassname+"','" +myclass+"','" +MethodName+"','" +Methodid+"')");
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
			//CALLING METHOD ID 
			ResultSet callingmethodsrefined = st.executeQuery("SELECT methods.id from methods INNER JOIN classes on methods.classname=classes.classname where methods.methodname='"+calledmethod.getExecutable().getSignature().toString()+"'"); 
			while(callingmethodsrefined.next()){
				callingmethodsrefinedid = callingmethodsrefined.getString("id"); 
	   		   }
			 
			//CALLING METHOD NAME 
			ResultSet callingmethodsrefinednames = st.executeQuery("SELECT methods.methodname from methods INNER JOIN classes on methods.classname=classes.classname where methods.methodname='"+calledmethod.getExecutable().getSignature().toString()+"'"); 
			while(callingmethodsrefinednames.next()){
				callingmethodsrefinedname = callingmethodsrefinednames.getString("methodname"); 
	   		   }
			
			
			//CALLING METHOD CLASS 
			ResultSet callingmethodsclasses = st.executeQuery("SELECT classes.classname from methods INNER JOIN classes on methods.classname=classes.classname where methods.methodname='"+calledmethod.getExecutable().getSignature().toString()+"'"); 
			while(callingmethodsclasses.next()){
				callingmethodclass = callingmethodsclasses.getString("classname"); 
	   		   }
			
			
			//CALLED METHOD ID 
			ResultSet calledmethodsids= st.executeQuery("SELECT methods.id from methods INNER JOIN classes on methods.classname=classes.classname where methods.methodname='"+method.getSignature().toString()+"'"); 
			while(calledmethodsids.next()){
				calledmethodid = calledmethodsids.getString("id"); 
	   		   }
			 
			//CALLED METHOD NAME 
			ResultSet callemethodnames = st.executeQuery("SELECT methods.methodname from methods INNER JOIN classes on methods.classname=classes.classname where methods.methodname='"+method.getSignature().toString()+"'"); 
			while(callemethodnames.next()){
				calledmethodname = callemethodnames.getString("methodname"); 
	   		   }
			
			
			//CALLED METHOD CLASS 
			ResultSet calledmethodclasses = st.executeQuery("SELECT classes.classname from methods INNER JOIN classes on methods.classname=classes.classname where methods.methodname='"+method.getSignature().toString()+"'"); 
			while(calledmethodclasses.next()){
				calledmethodclass = calledmethodclasses.getString("classname"); 
	   		   }
			
			
			System.out.println("CALLED METHOD "+calledmethodname+ "\tCLASS2: "+calledmethodclass+"\tCALLINGMETHOD: "+callingmethodsrefinedname+"CALLING MENTHOD CLASS"+callingmethodclass);

		    
			
			
			methodcalls methodcall= new methodcalls(calledmethodid, calledmethodname, calledmethodclass, callingmethodsrefinedid, callingmethodsrefinedname, callingmethodclass); 
		
			
			if(methodcall.contains(methodcallsList, methodcall)==false && callingmethodsrefinedname!=null && callingmethodsrefinedid!=null && callingmethodclass!=null && calledmethodclass!=null && calledmethodname!=null && calledmethodid!=null) {
				String statement = "INSERT INTO `methodcalls`(`methodcalledid`,  `methodcalledname`,  `methodcalledclass`,`callingmethodid`,  `callingmethodname`, `callingmethodclass`) VALUES ('"+calledmethodid +"','" +calledmethodname+"','" +calledmethodclass+"','" +callingmethodsrefinedid+"','" +callingmethodsrefinedname+"','" +callingmethodclass+"')";
				
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
File file = new File("C:\\Users\\mouna\\git\\ParseFile\\ParseFile\\src\\data");
FileReader fileReader = new FileReader(file);
BufferedReader bufferedReader = new BufferedReader(fileReader);
StringBuffer stringBuffer = new StringBuffer();
String line;
try {
	while ((line = bufferedReader.readLine()) != null) {
		String methodsCalling= line.substring(1, line.indexOf("---")); 	
		String ClassFROM=methodsCalling.substring(0, methodsCalling.lastIndexOf("."));
		String MethodFROM=methodsCalling.substring(methodsCalling.lastIndexOf(".")+1, methodsCalling.indexOf(")")+1);
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
		//CALLING METHOD ID 
		ResultSet callingmethodsrefined = st.executeQuery("SELECT methods.id from methods INNER JOIN classes on methods.classname=classes.classname where methods.methodname='"+MethodFROM+"' and classes.classname='"+ClassFROM+"'"); 
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
		
		
	
		System.out.println("CLASS FROM: "+ClassFROM+"        METHOD FROM       "+ MethodFROM+ "       CLASS TO       "+ ClassTO+"       Method To       "+MethodTO+"calling merthod refined id    "+ callingmethodsrefinedid+ "called method id    "+ calledmethodid); 
	
	//	if(callingmethodsrefinedid!=null && callingmethodclass!=null && calledmethodclass!=null && calledmethodname!=null && calledmethodid!=null) {
			String statement = "INSERT INTO `methodcallsexecuted`(`methodcalledid`,  `methodcalledname`,  `methodcalledclass`,`callingmethodid`,  `callingmethodname`, `callingmethodclass`) VALUES ('"+calledmethodid +"','" +MethodTO+"','" +ClassTO+"','" +callingmethodsrefinedid+"','" +MethodFROM+"','" +ClassFROM+"')";
			
			st.executeUpdate(statement);
	//	}
		
	}
} catch (IOException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
}





//System.out.println("Contents of file:");
//System.out.println(stringBuffer.toString());


	}
}