import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;

public class ManualExam implements Examable {

	private String topic;
	private int numOfQuestions;
	static Scanner input = new Scanner(System.in);
	private UserInteraction user;

	public ManualExam(String topic, int numOfQuestions, UserInteraction user) throws MoreThen10QuestionsException {
		this.topic = topic;
		this.numOfQuestions = numOfQuestions;
		this.user = user;
		if (this.numOfQuestions > 10)
			throw new MoreThen10QuestionsException();
	}

	public String getTopic() {
		return this.topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public void printExam(Printable p, Repository test) throws IOException {
		p.print(test, "MANUAL");
	}

	@Override
	public Repository createExam(Repository exam) throws IOException {
		Repository test = new Repository();
		user.showQuestionsInStock();
		int questionNumber = 0;
		HashSet<Integer> checkQ = new HashSet<Integer>(0);
		for (int i = 0; i < numOfQuestions; i++) {
			System.out.println("Choose question: \n");
			questionNumber = user.selectQuestionFromStock();
			if (checkQ.add(questionNumber)) {
				if (exam.getQuestion(questionNumber) instanceof ClosedQuestion) {
					Question temp = QuestionFactory.getType(eQuestionType.Closed, exam.getQuestion(questionNumber).getQuestionText(),
							exam.getQuestion(questionNumber).getDifficulty());
					ArrayList<AdapterAnswer> answers = ((ClosedQuestion) exam.getQuestion(questionNumber)).getAnswers();
					int counter = 1;
					int decision = -1;
					HashSet<Integer> checkA = new HashSet<Integer>(0);
					while (decision != 0) {
						Iterator<AdapterAnswer> it = answers.iterator();
						counter = 1;
						System.out.println("Choose answer to add, 0 to finish: \n");
						while (it.hasNext()) {
							System.out.println(counter + ") " + it.next().toString());
							counter++;
						}
						decision = input.nextInt();
						if ((decision > 0) && (decision <= answers.size())) {
							if (checkA.add(decision)) {
								AdapterAnswer answer = answers.get(decision - 1);
								((ClosedQuestion) temp).addAnswer(answer);
							} else {
								System.out.println("Answer already in test");
							}
						} else {
							if (decision == 0) {
								break;
							} else {
								System.out.println("wrong input!\n");
							}
						}
					}
					temp = (ClosedQuestion) user.addTwoHardCodedAnswersToQuestion(temp);
					try {
						test.addQuestionToStock(temp);
					} catch (ClosedQuestionLessThen4AnswersException e) {
						e.printStackTrace();
					}
				} else if (exam.getQuestion(questionNumber) instanceof OpenQuestion) {
					try {
						test.addQuestionToStock(exam.getQuestion(questionNumber));
					} catch (ClosedQuestionLessThen4AnswersException e) {
						e.printStackTrace();
					}
				}
			} else {
				System.out.println("Question already in test");
				i--;
			}
		}

		return test;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Manual test, Topic: " + getTopic());
		return sb.toString();
	}

}
