import java.io.Serializable;

public class Answer implements Serializable {

	private AnswerText answer;
	private boolean isCorrect;

	public Answer(AnswerText answerText, boolean isCorrect) {
		setAnwser(answerText);
		setIsCorrect(isCorrect);
	}

	public AnswerText getAnswerText() {
		return this.answer;
	}

	public boolean getIsCorrect() {
		return this.isCorrect;
	}

	public void setAnwser(AnswerText answer) {
		this.answer = answer;
	}

	public void setIsCorrect(boolean isCorrect) {
		this.isCorrect = isCorrect;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(getAnswerText() + "-" + getIsCorrect());
		return sb.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (o != null && o instanceof Answer) {
			Answer temp = (Answer) o;
			return (temp.getAnswerText() != null && temp.getAnswerText() == this.answer);
		}
		return false;
	}

	public String toStringWithoutSolution() {
		return getAnswerText().toString();
	}

}
