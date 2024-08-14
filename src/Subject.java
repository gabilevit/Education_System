import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;

public class Subject implements Serializable {

	private Repository stock = new Repository();
	private String name;

	public Subject(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Repository getStock() {
		return this.stock;
	}

	public void setStock(Repository stock) {
		this.stock = stock;
	}

	public HashSet<Question> getQuestionsDB() {
		return this.stock.getQuestionsDB();
	}

	public LinkedHashSet<AnswerText> getAnswersText() {
		return this.stock.getAnswersText();
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Subject: " + name);
		return sb.toString();
	}

	public void addQuestionToSubject(Question question) {
		try {
			stock.addQuestionToStock(question);
		} catch (ClosedQuestionLessThen4AnswersException e) {
			e.printStackTrace();
		}
	}

	public void addAnswerToSubject(String answer) {
		stock.addAnswerToStock(answer);
	}

}
