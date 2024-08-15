import java.io.Serializable;

public class hw3_Answer implements Serializable {

	private AnswerText answer;
	private boolean isCorrect;

	public hw3_Answer(AnswerText answerText, boolean isCorrect) {
		hw3_setAnwser(answerText);
		hw3_setIsCorrect(isCorrect);
	}

	public AnswerText hw3_getAnswerText() {
		return this.answer;
	}

	public boolean hw3_getIsCorrect() {
		return this.isCorrect;
	}

	public void hw3_setAnwser(AnswerText answer) {
		this.answer = answer;
	}

	public void hw3_setIsCorrect(boolean isCorrect) {
		this.isCorrect = isCorrect;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(hw3_getAnswerText() + "-" + hw3_getIsCorrect());
		return sb.toString();
	}

	public String hw3_toStringWithoutSolution() {
		return hw3_getAnswerText().toString();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o != null && o instanceof hw3_Answer) {
			hw3_Answer temp = (hw3_Answer) o;
			return (temp.hw3_getAnswerText() != null && temp.hw3_getAnswerText() == this.answer);
		}
		return false;
	}

}
