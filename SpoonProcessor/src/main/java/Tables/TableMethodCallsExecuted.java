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

public class TableMethodCallsExecuted {
	public void methodcallsexecuted(Statement st, ClassFactory classFactory) throws SQLException, FileNotFoundException {File file = new File("C:\\Users\\mouna\\git\\SpoonProcessor\\data.txt");
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
}
	
	
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
}
