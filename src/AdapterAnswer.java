import java.io.Serializable;

public class AdapterAnswer implements Serializable{

	private AnswerText answer;
	private boolean isCorrect;
	
	public AdapterAnswer(AnswerText answerText, boolean isCorrect){
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

	public String toStringWithoutSolution() {
		return getAnswerText().toString();
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(getAnswerText() + "-" + getIsCorrect());
		return sb.toString();
	}

	public boolean equals(Object o) {
		return (o != null && o instanceof AdapterAnswer && ((AdapterAnswer) o).getAnswerText().equals(this.answer));
	}

}
