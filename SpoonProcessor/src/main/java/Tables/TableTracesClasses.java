package Tables;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Pattern;

import com.mysql.jdbc.StringUtils;

import spoon.reflect.factory.ClassFactory;

public class TableTracesClasses {
	public void tracesclasses(Statement st, ClassFactory classFactory) throws SQLException {
		List<RequirementClassKey> RequirementClassKeys= new ArrayList<RequirementClassKey>(); 
		int linenum=0; 
		String FinalMethod=null; 
		String classid = null;
		String requirementid = null;
		String goldvalue=null; 
		String classname=null; 
	try {
			File file = new File("C:\\Users\\mouna\\git\\SpoonProcessor\\Traces.txt");
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);	
			String line = bufferedReader.readLine(); 
			Hashtable<RequirementClassKey,String> GoldHashTable=new Hashtable<RequirementClassKey,String>();  
			Hashtable<RequirementClassKey,String> SubjectHashTable=new Hashtable<RequirementClassKey,String>();  
		
			while ((line = bufferedReader.readLine()) != null) {
				FinalMethod=ParseLine(line); 
		
				String[] linesplitted = line.split(","); 
				String method = linesplitted[1]; 
				String requirement = linesplitted[2]; 
				String gold = linesplitted[4]; 
				String subject = linesplitted[5]; 
			
		
		
				System.out.println("FINAL METHOD IS HERE :   "+FinalMethod);
		ResultSet classnames = st.executeQuery("SELECT methods.classname from methods where methods.methodabbreviation ='"+FinalMethod+"'"); 
		

		while(classnames.next()){
			classname = classnames.getString("classname"); 
			   }
		
		ResultSet classids = st.executeQuery("SELECT methods.classid from methods where methods.methodabbreviation ='"+FinalMethod+"'"); 
		
		while(classids.next()){
			classid = classids.getString("classid"); 
			   }
		
		
		ResultSet requirements = st.executeQuery("SELECT requirements.id from requirements where requirements.requirementname ='"+requirement+"'"); 
		

		while(requirements.next()){
			requirementid = requirements.getString("id"); 
			   }	
		 
		 
		ResultSet goldvalues = st.executeQuery("SELECT traces.gold from traces where traces.requirementid ='"+requirementid+"' and traces.classid='"+classid+"'"); 
		 while(goldvalues.next()){
				goldvalue = goldvalues.getString("gold"); 
				   }
		 String subjectvalue=null; 
			ResultSet subjectvalues = st.executeQuery("SELECT traces.subject from traces where traces.requirementid ='"+requirementid+"' and traces.classid='"+classid+"'"); 
			while(subjectvalues.next()){
				subjectvalue = subjectvalues.getString("subject"); 
				   }
			
			//GoldSubjectValues goldsubject= new GoldSubjectValues(goldvalue, subjectvalue); 
			if(requirement!=null && classid!=null) {
				
			}
			RequirementClassKey RequirementClassKey= new RequirementClassKey(requirementid, requirement, classid, classname, goldvalue, subjectvalue); 
			if(GoldHashTable.containsKey(RequirementClassKey)==false) {
				System.out.println("====================================="+linenum+"  "+requirementid+""+requirement+""+ classid+""+classname+""+goldvalue+""+subjectvalue); 
				linenum++; 
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
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
////////////////////////////////////////////////////////////////////////////////////////////////////
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
			if(StringUtils.isStrictlyNumeric(firstpart[1])==false) {
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

}
