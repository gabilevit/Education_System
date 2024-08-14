import java.io.Serializable;
import java.util.Objects;

public abstract class Question implements Serializable {

	private int id;
	protected static int counter;
	protected String questionText;
	protected eDifficulty difficulty;

	public Question(String questionText, eDifficulty difficulty) {
		setQuestionText(questionText);
		setDifficulty(difficulty);
		this.id = ++counter;
	}

	public int getId() {
		return this.id;
	}

	public String getQuestionText() {
		return this.questionText;
	}

	public eDifficulty getDifficulty() {
		return this.difficulty;
	}

	public void setQuestionText(String answerText) {
		this.questionText = answerText;
	}

	public void setDifficulty(eDifficulty difficulty) {
		this.difficulty = difficulty;
	}

	public void initializeCounter(int counter) {
		Question.counter = counter;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof Question) {
			Question temp = (Question) obj;
			return (temp.getQuestionText() != null
					&& temp.getQuestionText().toLowerCase().equals(this.questionText.toLowerCase()));
		}
		return false;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		if (getQuestionText() != null) {
			sb.append(getQuestionText() + "\n");
			sb.append("Question SERIAL NUMBER: " + getId() + "\n");
			sb.append("Difficulty: " + getDifficulty() + "\n");
		}
		return sb.toString();
	}
}
