package Tables;
import java.util.List;

public class methodcalls {
	public String methodcalledid; 
	public String methodcalledname; 
	public String menthodcalledclass; 
	public String callingmethodid; 
	public String callingmethodname; 
	public String callingmethodclass;
	
	
	public methodcalls(String methodcalledid, String methodcalledname, String menthodcalledclass, String callingmethodid,
			String callingmethodname, String callingmethodclass) {

		this.methodcalledid = methodcalledid;
		this.methodcalledname = methodcalledname;
		this.menthodcalledclass = menthodcalledclass;
		this.callingmethodid = callingmethodid;
		this.callingmethodname = callingmethodname;
		this.callingmethodclass = callingmethodclass;
	}
	
	
	public boolean equals(methodcalls mc) {
		if(methodcalledid.equals(mc.methodcalledid) &&  methodcalledname.equals(mc.methodcalledname) && menthodcalledclass.equals(mc.menthodcalledclass) && callingmethodid.equals(mc.callingmethodid)  && callingmethodname.equals(mc.callingmethodname) && callingmethodclass.equals(mc.callingmethodclass) ) {
			return true; 
		}
	return false; 
	}
	
	public boolean contains(List<methodcalls> MethodCallsList, methodcalls f) {
		for(methodcalls mc: MethodCallsList) {
			if(mc.equals(f)) {
				return true; 
			}
		}
		
		return false;
		
	}
	
}
