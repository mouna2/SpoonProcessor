package Tables;
import java.util.List;

public class tracesmethodscallees {
	public String requirement; 
	public String requirementid; 
	public String fullmethod; 
	public String methodid; 
	public String classname; 
	public String classid; 
	public String gold; 
	public String subject; 
	public String callee; 
	
	

	
	

	public tracesmethodscallees(String requirement, String requirementid, String fullmethod, String methodid, String classname,
			String classid, String gold, String subject, String callee) {
		
		this.requirement = requirement;
		this.requirementid = requirementid;
		this.fullmethod = fullmethod;
		this.methodid = methodid;
		this.classname = classname;
		this.classid = classid;
		this.gold = gold;
		this.subject = subject;
		this.callee=callee; 
	}

	public boolean equals(tracesmethodscallees t) {
		if( classid.equals(t.classid) && requirementid.equals(t.requirementid)  && fullmethod.equals(t.fullmethod)   && methodid.equals(t.methodid)  && classname.equals(t.classname)
				&& classid.equals(t.classid) && gold.equals(t.gold)  && subject.equals(t.subject) && callee.equals(t.callee)) {
			return true; 
		}
	return false; 
	}
	
	public boolean contains(List<tracesmethodscallees> TraceList, tracesmethodscallees t) {
		for(tracesmethodscallees tr: TraceList) {
			if(t.equals(tr)) {
				return true; 
			}
		}
		
		return false;
		
	}
}
