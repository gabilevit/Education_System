import java.io.Serializable;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;

public class Repository implements Serializable {
	private HashSet<Question> questionsDB;
	private LinkedHashSet<AnswerText> answersTextDB;

	public Repository() {
		this.questionsDB = new HashSet<Question>(0);
		this.answersTextDB = new LinkedHashSet<AnswerText>(0);
	}

	public HashSet<Question> getQuestionsDB() {
		return this.questionsDB;
	}

	public void setQuestionsDB(HashSet<Question> questionsDB) {
		this.questionsDB = questionsDB;
	}

	public LinkedHashSet<AnswerText> getAnswersText() {
		return this.answersTextDB;
	}

	public void setAnswersText(LinkedHashSet<AnswerText> answersTextDB) {
		this.answersTextDB = answersTextDB;
	}

	public AnswerText getAnswer(int id) {
		Iterator<AnswerText> it = this.answersTextDB.iterator();
		AnswerText selected = null;
		for (int i = 0; i < id; i++) {
			if (!it.hasNext()) {
				return null;
			}
			selected = it.next();
		}
		return selected;
	}

	public Question getQuestion(int id) {
		return this.questionsDB.stream().filter(obj -> obj.getId() == id).findFirst().orElse(null);
	}

	public boolean findQuestion(int id) {
		if (getQuestion(id) != null)
			return true;
		return false;
	}

	public boolean isQuestionExistInStock(String questionText) {
		return this.questionsDB.contains(QuestionFactory.getType(eQuestionType.Closed, questionText, eDifficulty.Easy));
	}

	public void addQuestionToStock(Question question) throws ClosedQuestionLessThen4AnswersException {
		if (question instanceof ClosedQuestion) {
			int realNumOfAnswers = returnNumberOfRealAnswers((ClosedQuestion) question);
			if (realNumOfAnswers <= 3)
				throw new ClosedQuestionLessThen4AnswersException();
		}
		this.questionsDB.add(question);
	}

	public void initializeCounter() {
		Iterator<Question> it = this.questionsDB.iterator();
		int max = 0;
		while (it.hasNext()) {
			max = Math.max(max, it.next().getId());
		}
		QuestionFactory.getType(eQuestionType.Closed, "", eDifficulty.valueOf("Easy")).initializeCounter(max);
	}

	private int returnNumberOfRealAnswers(ClosedQuestion closedQuestion) {
		return closedQuestion.getAnswers().size();
	}

	public void addAnswerToStock(String answer) {
		this.answersTextDB.add(new AnswerText(answer));
	}

	public boolean addAnswerToSpesificQuestion(AdapterAnswer answer, int selectedQuestion) {
		if (getQuestion(selectedQuestion) instanceof ClosedQuestion)
			return ((ClosedQuestion) getQuestion(selectedQuestion)).addAnswer(answer);
		return false;
	}

	public boolean deleteQuestionFromStock(Question question, Database db) throws SQLException {
		if (question instanceof ClosedQuestion) {
			db.startConnection();
			for (int i = 0; i < ((ClosedQuestion) question).getAnswers().size(); i++) {
				db.deleteFromAdapterAnswer_ClosedQuestionTable(((ClosedQuestion) question).getAnswers().get(i).getIsCorrect() ,((ClosedQuestion) question).getAnswers().get(i).getAnswerText().toString(),
						question.getQuestionText());
				db.deleteFromAdapterAnswerTable(((ClosedQuestion) question).getAnswers().get(i).getAnswerText().toString());
			}
			db.deleteFromClosedQuestionTable(question.getQuestionText());
			db.deleteFromQuestionTable(question.getQuestionText());
			db.closeConnection();
		} else {
			db.startConnection();
			db.deleteFromOpenQuestionTable(question.getQuestionText());
			db.deleteFromQuestionTable(question.getQuestionText());
			db.closeConnection();
		}
		return this.questionsDB.remove(question);
	}

	public String toStringAnswersInStock() {
		StringBuffer sb = new StringBuffer();
		Iterator<AnswerText> it = this.answersTextDB.iterator();
		int counter = 1;
		while (it.hasNext()) {
			sb.append(counter + ")." + it.next() + "\n");
			counter++;
		}
		return sb.toString();
	}

	public String toStringQuestionsInStock() {
		StringBuffer sb = new StringBuffer();
		Iterator<Question> it = this.questionsDB.iterator();
		int counter = 1;
		Question temp = null;
		while (it.hasNext()) {
			temp = it.next();
			if (temp instanceof ClosedQuestion) {
				sb.append("[CLOSED QUESTION] -->" + counter + ")." + ((ClosedQuestion) temp).toString() + "\n");
				counter++;
			} else if (temp instanceof OpenQuestion) {
				sb.append("[OPEN QUESTION] -->" + counter + ")." + ((OpenQuestion) temp).toString() + "\n");
				counter++;
			}

		}
		return sb.toString();
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("THE TEST:\n");
		Iterator<Question> it = this.questionsDB.iterator();
		int counter = 1;
		Question temp = null;
		while (it.hasNext()) {
			temp = it.next();
			if (temp instanceof ClosedQuestion) {
				sb.append("[CLOSED QUESTION] -->" + counter + ")." + ((ClosedQuestion) temp).toStringWithoutSolution()
						+ "\n");
				counter++;
			} else if (temp instanceof OpenQuestion) {
				sb.append("[OPEN QUESTION] -->" + counter + ")." + ((OpenQuestion) temp).toStringWithoutSolution()
						+ "\n");
				counter++;
			}
		}
		return sb.toString();
	}

	public String toStringSolution() {
		StringBuffer sb = new StringBuffer();
		sb.append("THE TEST (with solution):\n");
		sb.append("THE TEST:\n");
		Iterator<Question> it = this.questionsDB.iterator();
		int counter = 1;
		Question temp = null;
		while (it.hasNext()) {
			temp = it.next();
			sb.append(counter + ") " + temp.getQuestionText() + "\nAnswers:");
			if (temp instanceof ClosedQuestion) {
				for (int j = 0; j < ((ClosedQuestion) temp).getAnswers().size(); j++) {
					sb.append(((ClosedQuestion) temp).getAnswers().get(j).toString() + "\n");
				}
				sb.append("\n");
			} else {
				sb.append("The correct answer is: " + ((OpenQuestion) temp).getAnswer().toString() + "\n\n");
			}
			counter++;
		}
		return sb.toString();
	}

}
