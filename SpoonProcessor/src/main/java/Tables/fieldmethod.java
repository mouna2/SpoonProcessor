package Tables;
import java.util.List;

public class fieldmethod {
	String fieldaccess; 
	String classname; 
	String classid; 
	String methodname; 
	String methodid;

	public fieldmethod(String fieldName, String myclassname, String myclass, String methodName2, String methodid2) {
		// TODO Auto-generated constructor stub
		
		this.fieldaccess = fieldName;
		this.classname = myclassname;
		this.classid = myclass;
		this.methodname = methodName2;
		this.methodid = methodid2;
	}

	public boolean equals(fieldmethod f) {
		if(fieldaccess.equals(f.fieldaccess) && classname.equals(f.classname) && classid.equals(f.classid)  && methodname.equals(f.methodname) && methodid.equals(f.methodid) ) {
			return true; 
		}
	return false; 
	}
	
	public boolean contains(List<fieldmethod> FieldMethodList, fieldmethod f) {
		for(fieldmethod field: FieldMethodList) {
			if(field.equals(f)) {
				return true; 
			}
		}
		
		return false;
		
	}
	
}
