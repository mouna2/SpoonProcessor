package Tables;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.ClassFactory;
import spoon.reflect.visitor.filter.TypeFilter;

public class TableMethodCalls {
	public void methodcalls(Statement st, ClassFactory classFactory) throws SQLException {List<methodcalls> methodcallsList = new ArrayList<methodcalls>(); 
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
				String paramclassid=null; 
				//CALLING METHOD ID 
				ResultSet callingmethodsrefined = st.executeQuery("SELECT methods.id from methods INNER JOIN classes on methods.classname=classes.classname where methods.methodname='"+calledmethod.getExecutable().getSignature().toString()+"' and classes.classname='"+  clazz.getQualifiedName() +"'"); 
				while(callingmethodsrefined.next()){
					callingmethodsrefinedid = callingmethodsrefined.getString("id"); 
		   		   }
				 
				//CALLING METHOD NAME 
				ResultSet callingmethodsrefinednames = st.executeQuery("SELECT methods.methodname from methods INNER JOIN classes on methods.classname=classes.classname where methods.methodname='"+calledmethod.getExecutable().getSignature().toString()+"' and classes.classname='"+  clazz.getQualifiedName() +"'"); 
				while(callingmethodsrefinednames.next()){
					callingmethodsrefinedname = callingmethodsrefinednames.getString("methodname"); 
		   		   }
				
				
				//CALLING METHOD CLASS 
				ResultSet callingmethodsclasses = st.executeQuery("SELECT classes.classname from methods INNER JOIN classes on methods.classname=classes.classname where methods.methodname='"+calledmethod.getExecutable().getSignature().toString()+"' and classes.classname='"+  clazz.getQualifiedName() +"'"); 
				while(callingmethodsclasses.next()){
					callingmethodclass = callingmethodsclasses.getString("classname"); 
		   		   }
				
				
				//CALLED METHOD ID 
				ResultSet calledmethodsids= st.executeQuery("SELECT methods.id from methods INNER JOIN classes on methods.classname=classes.classname where methods.methodname='"+method.getSignature().toString()+"' and classes.classname='"+  clazz.getQualifiedName() +"'"); 
				while(calledmethodsids.next()){
					calledmethodid = calledmethodsids.getString("id"); 
		   		   }
				 
				//CALLED METHOD NAME 
				ResultSet callemethodnames = st.executeQuery("SELECT methods.methodname from methods INNER JOIN classes on methods.classname=classes.classname where methods.methodname='"+method.getSignature().toString()+"' and classes.classname='"+  clazz.getQualifiedName() +"'"); 
				while(callemethodnames.next()){
					calledmethodname = callemethodnames.getString("methodname"); 
		   		   }
				
				
				//CALLED METHOD CLASS 
				ResultSet calledmethodclasses = st.executeQuery("SELECT classes.classname from methods INNER JOIN classes on methods.classname=classes.classname where methods.methodname='"+method.getSignature().toString()+"' and classes.classname='"+  clazz.getQualifiedName() +"'"); 
				while(calledmethodclasses.next()){
					calledmethodclass = calledmethodclasses.getString("classname"); 
		   		   }
				
				
				//System.out.println("CALLED METHOD "+calledmethodname+ "\tCLASS2: "+calledmethodclass+"\tCALLINGMETHOD: "+callingmethodsrefinedname+"CALLING MENTHOD CLASS"+callingmethodclass);

			    
				
				
				methodcalls methodcall= new methodcalls(calledmethodid, calledmethodname, calledmethodclass, callingmethodsrefinedid, callingmethodsrefinedname, callingmethodclass); 
				
				
				if(methodcall.contains(methodcallsList, methodcall)==false && callingmethodsrefinedname!=null && callingmethodsrefinedid!=null && callingmethodclass!=null && calledmethodclass!=null && calledmethodname!=null && calledmethodid!=null) {
					String statement = "INSERT INTO `methodcalls`(`callermethodid`,  `callername`,  `callerclass`,`calleemethodid`,  `calleename`, `calleeclass`) VALUES ('"+calledmethodid +"','" +calledmethodname+"','" +calledmethodclass+"','" +callingmethodsrefinedid+"','" +callingmethodsrefinedname+"','" +callingmethodclass+"')";
					
					st.executeUpdate(statement);
					methodcallsList.add(methodcall); 
				}
			
				
				
				
				
				
				
				
				

			
			}
		}


		

	}       	}
}
