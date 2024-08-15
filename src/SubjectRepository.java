import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class SubjectRepository implements Serializable {

	private ArrayList<Subject> subjectsArrayList;
	private static SubjectRepository instance;

	public SubjectRepository() {
		subjectsArrayList = new ArrayList<>();
	}
	
    public static SubjectRepository getInstance() {
        if (instance == null) {
            instance = new SubjectRepository();
        }
        return instance;
    }

	public ArrayList<Subject> getSubjects() {
		return this.subjectsArrayList;
	}

	public void setSubjects(ArrayList<Subject> subjectsArrayList) {
		this.subjectsArrayList = subjectsArrayList;
	}

	public Subject getSubject(int id) {
		return this.subjectsArrayList.get(id - 1);
	}

	public void addSubject(Subject subject) {
		this.subjectsArrayList.add(subject);
	}

	public boolean findSubject(int id) {
		try {
			if (this.subjectsArrayList.get(id - 1) != null)
				return true;
			return false;
		} catch (IndexOutOfBoundsException e) {
			return false;
		}
	}

	public boolean isSubjectExistInStock(String subjectName) {
		for (int i = 0; i < subjectsArrayList.size(); i++) {
			if (subjectsArrayList.get(i) == null)
				return false;
			if (subjectName.equalsIgnoreCase(subjectsArrayList.get(i).getName()))
				return true;
		}
		return false;
	}

	public boolean deleteSubject(Subject subject) {
		return this.subjectsArrayList.remove(subject);
	}

	public String toStringSubjectsInStock() {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < getSubjects().size(); i++) {
			if (getSubjects().get(i) != null) {
				sb.append((i + 1) + ")." + getSubjects().get(i).getName() + "\n");
			}
		}
		return sb.toString();
	}
}
