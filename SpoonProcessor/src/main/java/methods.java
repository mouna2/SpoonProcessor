import java.util.List;

public class methods {
	public String methodname; 
	public String classid; 
	public String classname; 
	
	
	

	public methods(String methodname, String classid, String classname) {
		
		this.methodname = methodname;
		this.classid = classid;
		this.classname = classname;
	}

	public boolean equals(methods m) {
		if(methodname.equals(m.methodname) && classid.equals(m.classid) && classname.equals(m.classname)  ) {
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
