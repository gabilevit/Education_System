import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.Scanner;

public class AutomaticExam implements Examable {

	private String topic;
	private int numOfQuestions;

	Random rand = new Random();

	public AutomaticExam(String topic, int numOfQuestions) throws MoreThen10QuestionsException {
		this.topic = topic;
		this.numOfQuestions = numOfQuestions;
		if (this.numOfQuestions > 10)
			throw new MoreThen10QuestionsException();
	}

	public String getTopic() {
		return this.topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public int getNumOfQuestions() {
		return this.numOfQuestions;
	}

	public void setNumOfQuestions(int numOfQuestions) {
		this.numOfQuestions = numOfQuestions;
	}

	@Override
	public Repository createExam(Repository stock) throws IOException {

		Repository autoExam = new Repository();
		ArrayList<Question> questionsDB = new ArrayList<Question>(0);
		Iterator<Question> it1 = stock.getQuestionsDB().iterator();
		while (it1.hasNext()) {
			questionsDB.add(it1.next());
		}

		for (int i = 0; i < this.numOfQuestions; i++) {
			int randomQuestion = rand.nextInt(questionsDB.size());
			if (questionsDB.get(randomQuestion) instanceof ClosedQuestion) {
				ArrayList<Answer> answers = new ArrayList<Answer>(0);
				Iterator<Answer> it2 = ((ClosedQuestion) questionsDB.get(randomQuestion)).getAnswers().iterator();
				while (it2.hasNext()) {
					answers.add(it2.next());
				}

				int falseAnswersCount = 0;
				for (int j = 0; j < 4; j++) {
					int randomAnswer = rand.nextInt(answers.size());
					Answer answer = ((ClosedQuestion) questionsDB.get(randomQuestion)).getAnswer(randomAnswer + 1);
					if (!answer.getIsCorrect()) {
						falseAnswersCount++;
					}
					autoExam.addAnswerToSpesificQuestion(answer, randomQuestion);
					answers.remove(randomAnswer);
				}
				if (falseAnswersCount == 4) {
					autoExam.addAnswerToSpesificQuestion(new Answer(new AnswerText("No answer is correct"), true),
							randomQuestion);
				} else
					autoExam.addAnswerToSpesificQuestion(new Answer(new AnswerText("No answer is correct"), false),
							randomQuestion);
				try {
					autoExam.addQuestionToStock(questionsDB.get(randomQuestion));
				} catch (ClosedQuestionLessThen4AnswersException e) {
					e.printStackTrace();
				}
				questionsDB.remove(randomQuestion);
			} else if (questionsDB.get(randomQuestion) instanceof OpenQuestion) {
				try {
					autoExam.addQuestionToStock(questionsDB.get(randomQuestion));
				} catch (ClosedQuestionLessThen4AnswersException e) {
					e.printStackTrace();
				}
			}
		}
		return autoExam;
	}

	public void printExam(Printable p, Repository test) throws IOException {
		p.print(test, "AUTO");
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Automatic test, Topic: " + getTopic());
		return sb.toString();
	}

}
