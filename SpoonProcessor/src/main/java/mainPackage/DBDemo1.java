package mainPackage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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

import Tables.TableClasses;
import Tables.TableFieldClasses;
import Tables.TableFieldMethods;
import Tables.TableInterfaces;
import Tables.TableMethods;
import Tables.TableParameters;
import Tables.TableRequirements;
import Tables.TableSuperClasses;
import Tables.TableMethodCalls;
import Tables.TableMethodCallsExecuted;
import Tables.TableTraces;
import Tables.TableTracesClasses;
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
public class DBDemo1 {

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

  
  public DBDemo1(List<tracesmethodscallees> tracesCalleesList) {
     TracesCalleesList= new ArrayList<tracesmethodscallees>();

  }

  public DBDemo1() {
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
           "  `goldprediction` LONGTEXT NULL,\r\n" + 
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
      TableClasses classes= new TableClasses(); 
      classes.Classes(st, classFactory);
      
      
    
      /*********************************************************************************************************************************************************************************/  
        /*********************************************************************************************************************************************************************************/  
        /*********************************************************************************************************************************************************************************/
      //BUILD SUPERCLASSES TABLE 
      TableSuperClasses superclasses= new TableSuperClasses(); 
      superclasses.superclasses(st, classFactory);
      /*********************************************************************************************************************************************************************************/  
        /*********************************************************************************************************************************************************************************/  
        /*********************************************************************************************************************************************************************************/  
          
       //BUILD INTERFACES TABLE 
      TableInterfaces in = new TableInterfaces(); 
      in.interfaces(st, classFactory);
      
    
      
      /*********************************************************************************************************************************************************************************/  
        /*********************************************************************************************************************************************************************************/  
        /*********************************************************************************************************************************************************************************/      
      //BUILD METHODS TABLE 
      TableMethods meth = new TableMethods(); 
      meth.methods(st, classFactory);
        /*********************************************************************************************************************************************************************************/  
        /*********************************************************************************************************************************************************************************/  
        /*********************************************************************************************************************************************************************************/
      
      //BUILD PARAMETERS TABLE 
      TableParameters param= new TableParameters();
      param.parameters(st, classFactory);
/*********************************************************************************************************************************************************************************/  
/*********************************************************************************************************************************************************************************/  
/*********************************************************************************************************************************************************************************/
  
//BUILD FIELDS TABLE -- CLASSES
    TableFieldClasses fc= new TableFieldClasses(); 
    fc.fieldclasses(st, classFactory);
/*********************************************************************************************************************************************************************************/  
/*********************************************************************************************************************************************************************************/  
/*********************************************************************************************************************************************************************************/     
//BUILD FIELDS TABLE -- METHODS

      TableFieldMethods fm = new TableFieldMethods(); 
      fm.fieldmethods(st, classFactory);
/*********************************************************************************************************************************************************************************/  
/*********************************************************************************************************************************************************************************/  
/*********************************************************************************************************************************************************************************/     
//BUILD METHODSCALLED TABLE
      TableMethodCalls mctable = new TableMethodCalls(); 
      mctable.methodcalls(st, classFactory);
/*********************************************************************************************************************************************************************************/  
/*********************************************************************************************************************************************************************************/  
/*********************************************************************************************************************************************************************************/     
//BUILD METHODSCALLED EXECUTED TABLE
      TableMethodCallsExecuted methodsexec = new TableMethodCallsExecuted(); 
      methodsexec.methodcallsexecuted(st, classFactory);



//System.out.println("Contents of file:");
//System.out.println(stringBuffer.toString());

/*********************************************************************************************************************************************************************************/  
/*********************************************************************************************************************************************************************************/  
/*********************************************************************************************************************************************************************************/   
//CREATE REQUIREMENTS TABLE 

      TableRequirements r = new TableRequirements(); 
      r.requirements(st, classFactory);

/*********************************************************************************************************************************************************************************/  
/*********************************************************************************************************************************************************************************/  
/*********************************************************************************************************************************************************************************/   
//CREATE TRACES TABLE 
      TableTraces traces = new TableTraces(); 
      TracesCalleesList=traces.traces(st, classFactory);
     
/*********************************************************************************************************************************************************************************/  
/*********************************************************************************************************************************************************************************/  
/*********************************************************************************************************************************************************************************/   
//make prediction on the column goldprediction 

/*
      int counter=0;
      List<tracesmethodscallees> TracesCalleesList = traces.getTracesCalleesList(); 

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
      List<tracesmethodscallees> TracesCallersList = traces.getTracesCallersList(); 
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
      	
      	
      }*/
  /*********************************************************************************************************************************************************************************/  
  /*********************************************************************************************************************************************************************************/  
  /*********************************************************************************************************************************************************************************/   
  TableTracesClasses tracetable = new TableTracesClasses();
  tracetable.tracesclasses(st, classFactory);
  }
  ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////  
  

  ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////  

  

  ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////  


  ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////  

  

  

  ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////  
  
  

  ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////  


  ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////  

  

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
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////  


/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////








}