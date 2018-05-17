package Tables;
import java.util.List;

public class methods {
	public String methodname; 
	public String methodabbreviation; 
	public String classid; 
	public String classname; 
	
	
	

	public methods(String methodabbreviation,  String classid, String classname) {
		
	
		this.methodabbreviation=methodabbreviation; 
		this.classid = classid;
		this.classname = classname;
	}

	

	public boolean equals(methods m) {
		if( methodabbreviation.equals(m.methodabbreviation) && classid.equals(m.classid) && classname.equals(m.classname)  ) {
			return true; 
		}
	return false; 
	}
	
	public boolean contains(List<methods> MethodList, methods m) {
		for(methods meth: MethodList) {
			if(meth.equals(m)) {
				return true; 
			}
		}
		
		return false;
		
	}
}
