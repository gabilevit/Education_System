public class OpenQuestion extends Question {

	private AnswerText answer;

	public OpenQuestion(String questionText, eDifficulty difficulty, AnswerText answer) {
		super(questionText, difficulty);
		this.answer = answer;
	}

	public AnswerText getAnswer() {
		return this.answer;
	}

	public boolean addAnswer(AnswerText inputAnswer) {
		if (!isExistAnswer(inputAnswer) && getAnswer() == null) {
			this.answer = inputAnswer;
			return true;
		}
		return false;
	}

	public boolean isExistAnswer(AnswerText openAnswer) {
		if (getAnswer().equals(openAnswer))
			return true;
		else
			return false;
	}

	@Override
	public String toString() {
		return (super.toString() + "\t" + "The answer: " + answer + "\n");
	}

	public String toStringWithoutSolution() {
		return (super.toString() + "\n");
	}

}
