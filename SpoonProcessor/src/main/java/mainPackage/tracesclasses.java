package mainPackage;
import java.util.List;

public class tracesclasses {
	public String requirement; 
	public String requirementid; 
	public String classname; 
	public String classid; 
	public String gold; 
	public String subject; 
	
	
	

	
	

	public tracesclasses(String requirement, String requirementid,  String classname,
			String classid, String gold, String subject) {
		
		this.requirement = requirement;
		this.requirementid = requirementid;
		this.classname = classname;
		this.classid = classid;
		this.gold = gold;
		this.subject = subject;
	}

	public boolean equals(tracesclasses t) {
		if( classid.equals(t.classid) && requirementid.equals(t.requirementid)    && classname.equals(t.classname)
				&& requirement.equals(t.requirement) && gold.equals(t.gold)  && subject.equals(t.subject)) {
			return true; 
		}
	return false; 
	}
	
	public boolean contains(List<tracesclasses> TraceList, tracesclasses t) {
		for(tracesclasses tr: TraceList) {
			if(t.equals(tr)) {
				return true; 
			}
		}
		
		return false;
		
	}
}
