
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import spoon.Launcher;
import spoon.SpoonAPI;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.code.CtFieldRead;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtModule;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.ClassFactory;
import spoon.reflect.factory.Factory;
import spoon.reflect.factory.PackageFactory;
import spoon.reflect.factory.TypeFactory;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.filter.FieldAccessFilter;

public class Main {
	static List<String> classnames= new ArrayList<String>(); 
	public static void main(String[] args) {
		// Build model
		
		DBDemo dao = new DBDemo();
	   
	    
		SpoonAPI spoon = new Launcher();
    	spoon.addInputResource("C:\\Users\\mouna\\Downloads\\chessgantcode\\workspace_codeBase\\Chess");
    	spoon.getEnvironment().setAutoImports(true);
    	spoon.getEnvironment().setNoClasspath(true);
    	CtModel model = spoon.buildModel();
    	
  
    	// Interact with model
    	Factory factory = spoon.getFactory();
    	ClassFactory classFactory = factory.Class();
    	PackageFactory packagefactory = factory.Package(); 
    
for(CtType<?> clazz : classFactory.getAll()) {
    		
    		System.out.println(clazz.getSimpleName());
    		System.out.println(clazz.getPackage());
    		classnames.add(clazz.getPackage()+""+clazz.getQualifiedName()); 
    		clazz.getSuperclass();
    		
    		clazz.getSuperInterfaces();
    		
    	
   
    		
    		
    		 for(CtField<?> field : clazz.getFields()) {
    				for(CtMethod<?> method :clazz.getMethods()) {
    	    			// method.getParameters()
    	    			method.<CtFieldAccess<?>>getElements(new FieldAccessFilter(field.getReference()));
    	    		}
    		 }
    	}
    	
 	
    	for(CtType<?> clazz : classFactory.getAll()) {
    		
    		System.out.println(clazz.getSimpleName());
    		classnames.add(clazz.getQualifiedName()); 
    		clazz.getSuperclass();
    		
    		clazz.getSuperInterfaces();
    		
    	
   
    		
    		
    		 for(CtField<?> field : clazz.getFields()) {
    				for(CtMethod<?> method :clazz.getMethods()) {
    	    			// method.getParameters()
    	    			method.<CtFieldAccess<?>>getElements(new FieldAccessFilter(field.getReference()));
    	    		}
    		 }
    	}
    	
    	
    	 Classnames(); 
    	
	}
	
	public static List<String> Classnames(){
		return classnames; 
	}
}
