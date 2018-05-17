package Tables;
import java.util.List;

public class methodcallsexecuted {
	public String callerid=null; 
	public String callername=null; 
	public String callerclass=null; 
	public String calleeid=null; 
	public String calleename=null; 
	public String calleeclass=null;
	
	
	public methodcallsexecuted(String callerid, String callername, String callerclass, String calleeid,
			String calleename, String calleeclass) {

		this.callerid = callerid;
		this.callername = callername;
		this.callerclass = callerclass;
		this.calleeid = calleeid;
		this.calleename = calleename;
		this.calleeclass = calleeclass;
	}
	
	
	public boolean equals(methodcallsexecuted mc) {
		if(  callername.equals(mc.callername) && callerclass.equals(mc.callerclass)   && calleename.equals(mc.calleename) && calleeclass.equals(mc.calleeclass) ) {
			return true; 
		}
	return false; 
	}
	
	public boolean contains(List<methodcallsexecuted> MethodCallsList, methodcallsexecuted f) {
		for(methodcallsexecuted mc: MethodCallsList) {
			if(mc.equals(f)) {
				return true; 
			}
		}
		
		return false;
		
	}


	@Override
	public String toString() {
		return "methodcallsexecuted [callerid=" + callerid + ", callername=" + callername + ", callerclass="
				+ callerclass + ", calleeid=" + calleeid + ", calleename=" + calleename + ", calleeclass=" + calleeclass
				+ "]";
	}
	
}
