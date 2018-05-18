package Tables;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.ClassFactory;

public class TableSuperClasses {
	
	
	
	public TableSuperClasses() {
		super();
		// TODO Auto-generated constructor stub
	}

	public void superclasses(Statement st, ClassFactory classFactory) throws SQLException {	for(CtType<?> clazz : classFactory.getAll()) {
		int i=1; 
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
	}}
}
