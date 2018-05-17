package Tables;
import java.util.List;

public class RequirementClassKey {
	String requirement_id; 
	String requirement; 
	String ClassName;
	String ClassName_id; 
	String goldflag; 
	String subjectflag;
	public String getRequirement_id() {
		return requirement_id;
	}
	public void setRequirement_id(String requirement_id) {
		this.requirement_id = requirement_id;
	}
	public String getRequirement() {
		return requirement;
	}
	public void setRequirement(String requirement) {
		this.requirement = requirement;
	}
	public String getClassName() {
		return ClassName;
	}
	public void setClassName(String className) {
		ClassName = className;
	}
	public String getClassName_id() {
		return ClassName_id;
	}
	public void setClassName_id(String className_id) {
		ClassName_id = className_id;
	}
	public String isGoldflag() {
		return goldflag;
	}
	public void setGoldflag(String goldflag) {
		this.goldflag = goldflag;
	}
	public String isSubjectflag() {
		return subjectflag;
	}
	public void setSubjectflag(String subjectflag) {
		this.subjectflag = subjectflag;
	}
	
	
	
	
	public String getGoldflag() {
		return goldflag;
	}
	public String getSubjectflag() {
		return subjectflag;
	}
	public RequirementClassKey(String requirement_id, String requirement, String className, String className_id,
			String goldflag, String subjectflag) {
		super();
		this.requirement_id = requirement_id;
		this.requirement = requirement;
		ClassName = className;
		ClassName_id = className_id;
		this.goldflag = goldflag;
		this.subjectflag = subjectflag;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ClassName == null) ? 0 : ClassName.hashCode());
		result = prime * result + ((ClassName_id == null) ? 0 : ClassName_id.hashCode());
		result = prime * result + (goldflag != null ? 1231 : 1237);
		result = prime * result + ((requirement == null) ? 0 : requirement.hashCode());
		result = prime * result + ((requirement_id == null) ? 0 : requirement_id.hashCode());
		result = prime * result + (subjectflag != null ? 1231 : 1237);
		return result;
	}
	
	
	
	public boolean equals(RequirementClassKey requirementclasskey) {
		if(requirement_id.equals(requirementclasskey.requirement_id) && requirement.equals(requirementclasskey.requirement) && ClassName.equals(requirementclasskey.ClassName)	
				&& ClassName_id.equals(requirementclasskey.ClassName_id) && goldflag==this.goldflag && subjectflag==this.subjectflag) {
			return true; 
		}
		return false; 
	} 
	public boolean contains(List<RequirementClassKey> RequirementClassKeys, RequirementClassKey t) {
		for(RequirementClassKey tr: RequirementClassKeys) {
			if(t.equals(tr)) {
				return true; 
			}
		}
		
		return false;
		
	}
	
}
