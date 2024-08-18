import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;

public class SubjectRepository implements Serializable {

	private ArrayList<Subject> subjectsArrayList;
	private static SubjectRepository instance;
	Database db = new Database();

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

	public void addSubject(Subject subject, Database db) throws SQLException {
		this.subjectsArrayList.add(subject);
		db.startConnection();
		db.insertToSubjectTable(subject.getName());
		db.closeConnection();
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

	public boolean deleteSubject(Subject subject, Database db) throws SQLException {
		Iterator<Question> it = new HashSet<Question>(subject.getStock().getQuestionsDB()).iterator();
		Question temp = null;
		while (it.hasNext()) {
			temp = it.next();
			subject.getStock().deleteQuestionFromStock(temp, db);

		}
		Iterator<AnswerText> it2 = new HashSet<AnswerText>(subject.getStock().getAnswersText()).iterator();
		AnswerText temp2 = null;
		while (it2.hasNext()) {
			temp2 = it2.next();
			subject.getStock().deleteAnswerFromStock(temp2, db);
		}
		db.startConnection();
		db.deleteFromSubjectTable(subject.getName().toString());
		db.closeConnection();
		return this.subjectsArrayList.remove(subject);
	}

	public void deleteAllSubjects(Database db) throws SQLException {
		db.startConnection();
		db.dropTables();
		db.closeConnection();
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
