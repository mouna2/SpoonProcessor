package Tables;
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
import java.util.List;
import java.util.Properties;

import mainPackage.*;
import spoon.reflect.factory.ClassFactory;

public class TableTraces {
	
	List<tracesmethods> TraceListMethods= new ArrayList<tracesmethods>();
	
	 List<tracesmethodscallees> TracesCalleesList= new ArrayList<tracesmethodscallees>();
	 List<tracesmethodscallees> TracesCallersList= new ArrayList<tracesmethodscallees>();

	 private final String userName = "root";

		/** The password for the MySQL account (or empty for anonymous) */
		private final String password = "123";
	
		
	public List<tracesmethodscallees> traces(Statement st, ClassFactory classFactory) throws FileNotFoundException, SQLException {
		connectMethod();
		File file = new File("C:\\Users\\mouna\\git\\SpoonProcessor\\Traces.txt");
		 FileReader fileReader = new FileReader(file);
		 BufferedReader bufferedReader = new BufferedReader(fileReader);
		 StringBuffer stringBuffer = new StringBuffer();
		 String requirement=null; 
		 String method=null; 
		 String gold=null; 
		 String subject=null; 
		
		  
		 
		  
		/*  
		  String statement2 = "DELETE FROM `methods` WHERE methodname ='"+"method2"+"'";		
		  st.executeUpdate(statement2);
		  
		  String statement3 = "DELETE FROM `methods` WHERE methodname ='"+"method"+"'";		
		  st.executeUpdate(statement3);
		  
		  String statement4 = "DELETE FROM `methodcalls` WHERE callername ='"+"CALLER"+"'";		
		  st.executeUpdate(statement4);
		  
		 
		 String statement10 = "ALTER TABLE `methods` AUTO_INCREMENT = 740";		
		  st.executeUpdate(statement10);
		  */
		 String  statement10 = "INSERT INTO `methods`(`methodname`, `methodnamerefined`, `methodabbreviation`, `classid`, `classname`) VALUES ('"+"method"+"','" +"method"+"','" +"method"+"','"  +100+"','"+"mmmm" +"')";		
		  st.executeUpdate(statement10);
		  statement10 = "INSERT INTO `methods`(`methodname`, `methodnamerefined`, `methodabbreviation`, `classid`, `classname`) VALUES ('"+"method2"+"','" +"method2"+"','" +"method2"+"','"  +100+"','"+"mmmm" +"')";		
		  st.executeUpdate(statement10);
		
		  statement10 = "INSERT INTO `methodcalls`(`callermethodid`, `callername`, `callerclass`, `calleemethodid`, `calleename`,`calleeclass`) VALUES ('"+741+"','" +"CALLER"+"','" +"CALLER CLASS"+"','" +742+"','" +"CALLEE METH"+"','"+"CALLEE CLASS" +"')";		
		  st.executeUpdate(statement10);
		
		
		
		String goldprediction=null; 
		String calleeidexecuted=null; 
		

		tracesmethodscallees tmc = null; 
		String line;
		
		
		try {
			
			line = bufferedReader.readLine(); 
			while ((line = bufferedReader.readLine()) != null) {
				//System.out.println(line);
				String[] linesplitted = line.split(","); 
				method=linesplitted[1]; 
				requirement=linesplitted[2]; 
				gold=linesplitted[4]; 
				subject=linesplitted[5]; 
				
				
				
			TableTracesClasses tbc= new TableTracesClasses(); 
			String shortmethod=tbc.ParseLine(line); 
			
				
				
				
				String methodid=null; 
					ResultSet methodids = st.executeQuery("SELECT methods.id from methods where methods.methodabbreviation ='"+shortmethod+"'"); 
					while(methodids.next()){
						methodid = methodids.getString("id"); 
						   }
				
					String classname=null; 
				
				ResultSet classnames = st.executeQuery("SELECT methods.classname from methods where methods.methodabbreviation ='"+shortmethod+"'"); 
				
				while(classnames.next()){
					classname = classnames.getString("classname"); 
					   }
				String classid=null; 
				ResultSet classids = st.executeQuery("SELECT methods.classid from methods where methods.methodabbreviation ='"+shortmethod+"'"); 
				while(classids.next()){
					classid = classids.getString("classid"); 
					   }
				 String requirementid=null; 
				ResultSet requirements = st.executeQuery("SELECT requirements.id from requirements where requirements.requirementname ='"+requirement+"'"); 
				while(requirements.next()){
					requirementid = requirements.getString("id"); 
					   }
				// Rule: if method A calls method B and method A implements requirement X, then I can just assume that method B implements requirement X as well 
				// Retrieving the calleeid
				String calleeid = null; 
					ResultSet calleesparsed = st.executeQuery("SELECT methodcalls.calleemethodid from methodcalls where methodcalls.callermethodid ='"+methodid+"'"); 
					while(calleesparsed.next()){
						 calleeid = calleesparsed.getString("calleemethodid"); }
					calleeidexecuted=null; 	   
					ResultSet calleesexecuted = st.executeQuery("SELECT methodcallsexecuted.calleemethodid from methodcallsexecuted where methodcallsexecuted.callermethodid ='"+methodid+"'"); 
					while(calleesexecuted.next()){
						 calleeidexecuted = calleesexecuted.getString("calleemethodid"); 
						   }
					String	callerid=null; 
					ResultSet callersparsed = st.executeQuery("SELECT methodcalls.callermethodid from methodcalls where methodcalls.calleemethodid ='"+methodid+"'"); 
					while(callersparsed.next()){
						  callerid = callersparsed.getString("callermethodid"); }
					String	callerexecutedid=null; 	   
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
					
					if(callerid!=null && requirementid!=null) {
						 tmc= new tracesmethodscallees(requirement, requirementid, shortmethod, methodid, classname, classid, gold, subject, callerid); 
						 TracesCallersList.add(tmc); 
					}
					else if(callerexecutedid!=null) {
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
			
			
				
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setTracesCallersList(TracesCallersList);
		setTracesCalleesList(TracesCalleesList);
		return TracesCalleesList;
		
	}
	public List<tracesmethods> getTraceListMethods() {
		return TraceListMethods;
	}
	public void setTraceListMethods(List<tracesmethods> traceListMethods) {
		TraceListMethods = traceListMethods;
	}
	public List<tracesmethodscallees> getTracesCalleesList() {
		return TracesCalleesList;
	}
	public void setTracesCalleesList(List<tracesmethodscallees> tracesCalleesList) {
		TracesCalleesList = tracesCalleesList;
	}
	public List<tracesmethodscallees> getTracesCallersList() {
		return TracesCallersList;
	}
	public void setTracesCallersList(List<tracesmethodscallees> tracesCallersList) {
		TracesCallersList = tracesCallersList;
	}
	public TableTraces() {
		super();
	}
	
	
	
	






public void connectMethod () throws SQLException {
	ResultSet rs = null; 
	// Connect to MySQL
	Connection conn = getConnection();
	DBDemo1 dbdemo = new DBDemo1(); 
	dbdemo.getConnection(); 
	Statement st= conn.createStatement();

	st.executeUpdate("DROP TABLE IF EXISTS traces"); 
	System.out.println("Connected to database");
	
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
		 
}


public Connection getConnection() throws SQLException {
	Connection conn = null;
	Properties connectionProps = new Properties();
	connectionProps.put("root", this.userName);
	connectionProps.put("123", this.password);
	conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/databasechess","root","123");

	return conn;
}
}
