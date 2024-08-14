import java.util.ArrayList;

public class ClosedQuestion extends Question {

	private ArrayList<Answer> answers;

	public ClosedQuestion(String questionText, eDifficulty difficulty) {
		super(questionText, difficulty);
		this.answers = new ArrayList<Answer>(10);
	}

	public ArrayList<Answer> getAnswers() {
		return this.answers;
	}

	public boolean addAnswer(Answer answer) {
		if (!isExistAnswer(answer) && answers.size() < 10) {
			answers.add(answer);
		} else {
			System.out.println("Answer list has already 10 answers, cannot add another");
		}
		return false;
	}

	private boolean isExistAnswer(Answer answer) {
		for (int i = 0; i < answers.size(); i++) {
			if (answers.contains(answer))
				return true;
		}
		return false;
	}

	public Answer getAnswer(int id) {
		if (id > answers.size()) {
			return null;
		}
		return this.answers.get(id - 1);
	}

	public void deleteAnswer(Answer answer) {
		answers.remove(answer);
	}

	private StringBuffer printAnswersList(boolean flag) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < answers.size(); i++) {
			if (answers.get(i) != null) {
				if (flag)
					sb.append("\t" + (i + 1) + ")." + answers.get(i).toString() + "\n");
				else
					sb.append("\t" + (i + 1) + ")." + answers.get(i).toStringWithoutSolution() + "\n");
			}
		}
		return sb;
	}

	@Override
	public String toString() {
		return (super.toString() + "\n" + printAnswersList(true));
	}

	public String toStringWithoutSolution() {
		return (super.toString() + "\n" + printAnswersList(false));
	}

}
