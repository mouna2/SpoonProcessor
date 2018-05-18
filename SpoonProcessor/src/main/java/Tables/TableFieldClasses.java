package Tables;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;

import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.ClassFactory;

public class TableFieldClasses {
	public void fieldclasses(Statement st, ClassFactory classFactory) throws SQLException {for(CtType<?> clazz : classFactory.getAll()) {
		
		
		
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
		

	}}

}
