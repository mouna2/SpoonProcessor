package Tables;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.ClassFactory;
import spoon.reflect.visitor.filter.TypeFilter;

public class TableFieldMethods {
	public void fieldmethods(Statement st, ClassFactory classFactory) throws SQLException {for(CtType<?> clazz : classFactory.getAll()) {
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


		

	}   	}
}
