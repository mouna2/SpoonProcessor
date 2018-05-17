package Tables;
import java.util.List;

public class tracesmethods {
	public String requirement; 
	public String requirementid; 
	public String fullmethod; 
	public String methodid; 
	public String classname; 
	public String classid; 
	public String gold; 
	public String subject; 
	
	
	

	
	

	public tracesmethods(String requirement, String requirementid, String fullmethod, String methodid, String classname,
			String classid, String gold, String subject) {
		
		this.requirement = requirement;
		this.requirementid = requirementid;
		this.fullmethod = fullmethod;
		this.methodid = methodid;
		this.classname = classname;
		this.classid = classid;
		this.gold = gold;
		this.subject = subject;
	}

	public boolean equals(tracesmethods t) {
		if( classid.equals(t.classid) && requirementid.equals(t.requirementid)  && fullmethod.equals(t.fullmethod)   && methodid.equals(t.methodid)  && classname.equals(t.classname)
				&& classid.equals(t.classid) && gold.equals(t.gold)  && subject.equals(t.subject)) {
			return true; 
		}
	return false; 
	}
	
	public boolean contains(List<tracesmethods> TraceList, tracesmethods t) {
		for(tracesmethods tr: TraceList) {
			if(t.equals(tr)) {
				return true; 
			}
		}
		
		return false;
		
	}

	@Override
	public String toString() {
		return "tracesmethods [requirement=" + requirement + ", requirementid=" + requirementid + ", fullmethod="
				+ fullmethod + ", methodid=" + methodid + ", classname=" + classname + ", classid=" + classid
				+ ", gold=" + gold + ", subject=" + subject + "]";
	}
	
	
	
}