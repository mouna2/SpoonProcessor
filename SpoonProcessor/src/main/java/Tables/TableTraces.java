package Tables;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import spoon.reflect.factory.ClassFactory;

public class TableTraces {
	 List<tracesmethods> TraceListMethods= new ArrayList<tracesmethods>();

	public List<tracesmethodscallees> traces(Statement st, ClassFactory classFactory) throws FileNotFoundException, SQLException {
		File file = new File("C:\\Users\\mouna\\git\\SpoonProcessor\\Traces.txt");
		 FileReader fileReader = new FileReader(file);
		 BufferedReader bufferedReader = new BufferedReader(fileReader);
		 StringBuffer stringBuffer = new StringBuffer();
		 String requirement=null; 
		 String method=null; 
		 String gold=null; 
		 String subject=null; 
		
		
		
		
		
		String goldprediction=null; 
		String calleeidexecuted=null; 
		List<tracesmethodscallees> TracesCalleesList= new ArrayList<tracesmethodscallees>();
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
				
				
				
				ResultSet classnames = st.executeQuery("SELECT methods.classname from methods where methods.methodabbreviation ='"+shortmethod+"'"); 
				String classname=null; 
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
					String calleeid=null; 
					ResultSet calleesparsed = st.executeQuery("SELECT methodcalls.calleemethodid from methodcalls where methodcalls.callermethodid ='"+methodid+"'"); 
					while(calleesparsed.next()){
						 calleeid = calleesparsed.getString("calleemethodid"); 
						   }
					 calleeidexecuted=null; 
					ResultSet calleesexecuted = st.executeQuery("SELECT methodcallsexecuted.calleemethodid from methodcallsexecuted where methodcallsexecuted.callermethodid ='"+methodid+"'"); 
					while(calleesexecuted.next()){
						 calleeidexecuted = calleesexecuted.getString("calleemethodid"); 
						   }
				
				
				//insert into tracesmethodscallees a new object: if is found in the methodcalls table, then use the value from there 
				//otherwise, use the value from the methodcallsexecuted table 
					
					if(calleeid!=null) {
						 tmc= new tracesmethodscallees(requirement, requirementid, shortmethod, methodid, classname, classid, gold, subject, calleeid); 
						 TracesCalleesList.add(tmc); 
					}
					else if(calleeidexecuted!=null) {
						 tmc= new tracesmethodscallees(requirement, requirementid, shortmethod, methodid, classname, classid, gold, subject, calleeidexecuted); 
						 TracesCalleesList.add(tmc); 
					}
				tracesmethods tr= new tracesmethods(requirement, requirementid, shortmethod, methodid, classname, classid, gold, subject); 
				//System.out.println("====================="+requirementid+ "   "+ requirement +"    "+ shortmethod+ "           "+ methodid +"   "+ classname +"    "+classid+"   "+gold+ "   "+subject);
				if(methodid!=null) {
					if(tr.contains(TraceListMethods, tr)==false ) {
						  
						String statement = "INSERT INTO `traces`(`requirement`, `requirementid`, `method`, `fullmethod`, `methodid`,`classname`, `classid`, `gold`,  `subject`, `goldprediction`) VALUES ('"+requirement+"','" +requirementid+"','" +shortmethod+"','" +method+"','" +methodid+"','"+classname +"','" +classid+"','"+gold +"','" +subject+"','" +goldprediction+"')";		
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
		return TracesCalleesList;
		
	}
}
