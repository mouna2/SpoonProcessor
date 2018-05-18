package Tables;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.ClassFactory;
import spoon.reflect.reference.CtTypeReference;

public class TableParameters {
	public void parameters(Statement st, ClassFactory classFactory) throws SQLException {for(CtType<?> clazz : classFactory.getAll()) {
		
		System.out.println(clazz.getSimpleName());
		System.out.println(clazz.getPackage());
		String fullname= clazz.getPackage()+""+clazz.getQualifiedName(); 
		String MethodReferenced=null; 
		String MethodName=null; 
		String parameter=null; 
	    String ClassName=null; 
	    String classid=null; 
		String parameterclass=null; 
		String paramclassid=null; 
				
		 //for(CtField<?> field : clazz.getFields()) {
				for(CtMethod<?> method :clazz.getMethods()) {
	    			List<CtParameter<?>> params = method.getParameters(); 
				
	    			
	    			
	    		
	    	
	    			for( CtParameter<?> myparam :params) {
	    				boolean flag2=false; 
	    				
	    				ResultSet classnames = st.executeQuery("SELECT classes.classname from classes INNER JOIN methods ON classes.id=methods.classid where methods.methodname='"+method.getSignature().toString()+"' "); 
	    				
    					while(classnames.next()){
    						 ClassName =classnames.getString("classname"); 
    					
    			   		   }
    					
    					ResultSet classids = st.executeQuery("SELECT classes.id from classes INNER JOIN methods ON classes.id=methods.classid where methods.methodname='"+method.getSignature().toString()+"' "); 
	    				
    					while(classids.next()){
    						 classid =classids.getString("id"); 
    					
    			   		   }
    					
	    					ResultSet methods = st.executeQuery("SELECT methods.id from methods INNER JOIN classes ON classes.id=methods.classid where methods.methodname='"+method.getSignature().toString()+"' and classes.id='"+classid+"'"); 
	    				
	    					while(methods.next()){
	    						MethodReferenced =methods.getString("id"); 
	    					
	    			   		   }
	    				
	    					ResultSet paramclassids = st.executeQuery("SELECT classes.id from classes where classes.classname='"+myparam.getType()+"'"); 
    	    				
	    					while(paramclassids.next()){
	    						flag2=true; 
	    						paramclassid =paramclassids.getString("id"); 
	    					
	    			   		   }
	    			
	    				
	    					
	    					
	    				
	    				//	if(field.toString().contains("java.awt")==false && field.toString().contains("javax")==false) {
	    						System.out.println("HERE IS A PARAMETER: "+ myparam);
	    						if(MethodReferenced==null) {
	    							System.out.println("HERE IS NULL PARAMETER: "+myparam+"method referenced======>"+MethodReferenced);
	    						}
	    						if(MethodReferenced!=null && flag2==true)
	    		    			st.executeUpdate("INSERT INTO `parameters`(`parametername`, `parametertype`, `parameterclass`,`classid`, `classname`, `methodid`, `methodname`, `isreturn`) VALUES ('"+myparam +"','" +myparam.getType() +"','"+paramclassid+"','"+classid +"','"+ClassName+"','" +MethodReferenced+"','" +method.getSignature().toString()+"','" +0+"')");

	    				//	}
	    				
	    				
	    			}
	    			
	    		
	    			/*List<CtStatement> bodystatements = methodbody.getStatements(); 
	    			//List<CtReturn> returnstatement = methodbody.getElements(new TypeFilter<>(CtReturn.class)); 
	    		
	    				List<CtReturn> returnstatement = methodbody.getElements(new TypeFilter<>(CtReturn.class)); 
	    				for(CtReturn ret: returnstatement) {
	    					System.out.println("HERE IS RETURN: "+ret.getReturnedExpression().getType());
	    					ret.getReturnedExpression().getType(); 
	    				
	    			}*/
	    			boolean flag=false; 
	    			CtTypeReference<?> MethodType = method.getType();  
 	    			System.out.println("METHOD TYPE  "+ MethodType);
 	    			ResultSet classnames = st.executeQuery("SELECT classes.classname from classes INNER JOIN methods ON classes.id=methods.classid where methods.methodname='"+method.getSignature().toString()+"' "); 
    				
					while(classnames.next()){
						 ClassName =classnames.getString("classname"); 
					
			   		   }
					
					ResultSet classids = st.executeQuery("SELECT classes.id from classes INNER JOIN methods ON classes.id=methods.classid where methods.methodname='"+method.getSignature().toString()+"' "); 
    				
					while(classids.next()){
						 classid =classids.getString("id"); 
					
			   		   }
					
    					ResultSet methods = st.executeQuery("SELECT methods.id from methods INNER JOIN classes ON classes.id=methods.classid where methods.methodname='"+method.getSignature().toString()+"' and classes.id='"+classid+"'"); 
    				
    					while(methods.next()){
    						MethodReferenced =methods.getString("id"); 
    					
    			   		   }
    				
    					
    					
    					ResultSet parameterclasses = st.executeQuery("SELECT classes.id from classes where classes.classname='"+MethodType+"'"); 
	    				
    					while(parameterclasses.next()){
    						parameterclass =parameterclasses.getString("id"); 
    						flag=true; 
    					
    			   		   }
 	    			
	    			if(MethodReferenced!=null && flag==true)
		    			st.executeUpdate("INSERT INTO `parameters`(`parametername`, `parametertype`, `parameterclass`,`classid`, `classname`, `methodid`, `methodname`, `isreturn`) VALUES ('"+MethodType +"','" +MethodType+"','" +parameterclass +"','" +classid +"','"+ClassName+"','" +MethodReferenced+"','" +method.getSignature().toString()+"','" +1+"')");

	    		
	    		}
		 //}
	}}
}
