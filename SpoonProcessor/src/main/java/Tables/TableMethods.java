package Tables;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.ClassFactory;
import spoon.reflect.visitor.filter.TypeFilter;

public class TableMethods {
	public void methods(Statement st, ClassFactory classFactory) throws SQLException {
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
		
		
		
		
	}
}
