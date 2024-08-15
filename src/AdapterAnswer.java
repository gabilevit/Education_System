import java.io.Serializable;

public class AdapterAnswer implements Serializable{
	
	private hw3_Answer Answer;
	
	public AdapterAnswer(AnswerText answerText, boolean isCorrect){
		this.Answer = new hw3_Answer(answerText, isCorrect);
	}
	
	public AnswerText getAnswerText() {
		return Answer.hw3_getAnswerText();
	}

	public boolean getIsCorrect() {
		return Answer.hw3_getIsCorrect();
	}

	public void setAnwser(AnswerText answer) {
		Answer.hw3_setAnwser(answer);
	}

	public void setIsCorrect(boolean isCorrect) {
		Answer.hw3_getIsCorrect();
	}

	public String toStringWithoutSolution() {
		return Answer.hw3_toStringWithoutSolution();
	}
	
	public String toString() {
		return Answer.toString();
	}

	public boolean equals(Object o) {
		return Answer.equals(o);
	}

}
