import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Iterator;
import java.util.Scanner;

public class UserInteraction implements Observer {

	private static UserInteraction instance;

	private Scanner input = new Scanner(System.in);
	private int subIn = 0;
	SubjectRepository subjectRep = SubjectRepository.getInstance();
	Database db = Database.getInstance();

	public UserInteraction() throws FileNotFoundException, ClassNotFoundException, IOException, SQLException {
		/*try {
			this.subjectRep = readDataFromBinaryFile();
		} catch (FileNotFoundException e) {
			System.out.println("File \"Data.dat\" not found, creating new subject repository...");
			this.subjectRep = new SubjectRepository();
		} catch (IOException e) {
			System.out.println("Error reading from file \"Data.dat\", creating new subject repository...");
			this.subjectRep = new SubjectRepository();
		} catch (ClassNotFoundException e) {
			System.out.println("Error reading from file \"Data.dat\", creating new subject repository...");
			this.subjectRep = new SubjectRepository();
		}*/
		try {
			db.startConnection();
			db.createTables();
			db.closeConnection();
			//db.insertData();
			//db.readData();
		} catch (SQLException e) {
			System.out.println("Error connecting to database");
		}
	}

	public static UserInteraction getInstance() throws FileNotFoundException, ClassNotFoundException, IOException, SQLException {
		if (instance == null) {
			instance = new UserInteraction();
		}
		return instance;
	}

	public int getSubIn() {
		return this.subIn;
	}

	public void setSubIn(int subIn) {
		this.subIn = subIn;
	}

	public SubjectRepository getSubjectRep() {
		return this.subjectRep;
	}

	public void setSubjectRep(SubjectRepository subjectRep) {
		this.subjectRep = subjectRep;
	}

	public Subject getSubject() {
		return this.subjectRep.getSubject(subIn);
	}

	public void saveDataToBinaryFile() throws FileNotFoundException, IOException {
		ObjectOutputStream outFile = new ObjectOutputStream(new FileOutputStream("Data.dat"));

		outFile.writeObject(this.subjectRep);

		outFile.close();
	}

	public SubjectRepository readDataFromBinaryFile()
			throws FileNotFoundException, IOException, ClassNotFoundException {

		ObjectInputStream inFile = new ObjectInputStream(new FileInputStream("Data.dat"));

		SubjectRepository rep = (SubjectRepository) inFile.readObject();

		inFile.close();

		return rep;

	}

	public void addSubjectToData() throws SQLException {
		String subjectText;
		System.out.println("Enter subject name:");
		do {
			subjectText = input.next();
			subjectText += input.nextLine();
			if (this.subjectRep.isSubjectExistInStock(subjectText))
				System.out.println("This subject already exist in the stock");
		} while (this.subjectRep.isSubjectExistInStock(subjectText));
		this.subjectRep.addSubject(new Subject(subjectText), db);
		System.out.println("Subject added succesfuly to stock");
	}

	public void deleteSubjectFromData() throws SQLException {
		int selectedSubject;
		if (this.subjectRep.getSubjects().size() == 0) {
			System.out.println("There are no subjects in the database");
		} else {
			showSubjectsInStock();
			System.out.println("Which subject from stock you wish to delete? (pick subject number)");
			do {
				selectedSubject = input.nextInt();
				if (!this.subjectRep.findSubject(selectedSubject))
					System.out.println("Wrong input!");
			} while (!this.subjectRep.findSubject(selectedSubject));
			this.subjectRep.deleteSubject(this.subjectRep.getSubject(selectedSubject), db);
			System.out.println("Subject deleted succefuly");
		}
	}

	public int selectSubjectFromStock() throws InputMismatchException {
		int selectedSubject = -1;
		do {
			try {
				selectedSubject = input.nextInt();
				if (!this.subjectRep.findSubject(selectedSubject))
					throw new InputMismatchException();
			} catch (InputMismatchException e) {
				System.out.println("Wrong input!");
			}
		} while (!this.subjectRep.findSubject(selectedSubject));
		return selectedSubject;
	}

	public Question addQuestionWithSpecificAnswersToTheTest(int questionNumber) throws SQLException {
		Question tmpQuestion = getSubject().getStock().getQuestion(questionNumber);
		Question newQuestion = QuestionFactory.getType(eQuestionType.Closed, tmpQuestion.getQuestionText(),
				tmpQuestion.getDifficulty());
		char yesOrNo;
		do {
			System.out.println("Which answer you want to pick? (pick answer number)");
			System.out.println(((ClosedQuestion) tmpQuestion).toString());
			int answerNumber = input.nextInt();
			AdapterAnswer answer = ((ClosedQuestion) tmpQuestion).getAnswer(answerNumber);
			((ClosedQuestion) tmpQuestion).deleteAnswer(answer, db);
			((ClosedQuestion) newQuestion).addAnswer(answer);
			System.out.println("Do you want to add 1 more answer? (y/n)");
			yesOrNo = input.next().charAt(0);
		} while (yesOrNo == 'y');
		return addTwoHardCodedAnswersToQuestion(newQuestion);
	}

	public Question addTwoHardCodedAnswersToQuestion(Question newQuestion) {
		int count = 0;
		for (int i = 0; i < ((ClosedQuestion) newQuestion).getAnswers().size(); i++) {
			if (((ClosedQuestion) newQuestion).getAnswers().get(i).getIsCorrect() == true)
				count++;
		}
		if (count != 1) {
			for (int i = 0; i < ((ClosedQuestion) newQuestion).getAnswers().size(); i++) {
				if (((ClosedQuestion) newQuestion).getAnswers().get(i).getIsCorrect() == true)
					((ClosedQuestion) newQuestion).getAnswers().get(i).setIsCorrect(false);
			}
		}
		if (count >= 2) {
			((ClosedQuestion) newQuestion)
					.addAnswer(new AdapterAnswer(new AnswerText("More then one answer is correct"), true));
		} else
			((ClosedQuestion) newQuestion)
					.addAnswer(new AdapterAnswer(new AnswerText("More then one answer is correct"), false));
		if (count == 0)
			((ClosedQuestion) newQuestion).addAnswer(new AdapterAnswer(new AnswerText("No answer is correct"), true));
		else
			((ClosedQuestion) newQuestion).addAnswer(new AdapterAnswer(new AnswerText("No answer is correct"), false));
		return newQuestion;
	}

	public void deleteQuestionFromStock(int questionNumber) throws SQLException {
		Question question = getSubject().getStock().getQuestion(questionNumber);
		if (getSubject().getStock().deleteQuestionFromStock(question, db))
			System.out.println("Succes");
		else
			System.out.println("NOT succes");
	}

	public boolean deleteAnswerFromSpesificQuestion(int selectedQuestion) throws SQLException {
		Question question = getSubject().getStock().getQuestion(selectedQuestion);
		if (question instanceof ClosedQuestion) {
			System.out.println(question.toString());
			int selectedAnswerFromQuestion;
			AdapterAnswer answer;
			do {
				selectedAnswerFromQuestion = input.nextInt();
				answer = ((ClosedQuestion) question).getAnswer(selectedAnswerFromQuestion);
				if (answer == null)
					System.out.println("Wrong input!");
			} while (answer == null);
			((ClosedQuestion) question).deleteAnswer(answer, db);
			return true;
		} else
			return false;
	}

	public void addQuestionToData(eQuestionType qType) throws SQLException {
		String questionText;
		System.out.println("Whats the question?");
		do {
			questionText = input.next();
			questionText += input.nextLine();
			if (getSubject().getStock().isQuestionExistInStock(questionText))
				System.out.println("This question already exist in the stock");
		} while (getSubject().getStock().isQuestionExistInStock(questionText));
		String qDifficulty;
		System.out.println("Whats the question difficulty?");
		System.out.println("for hard type: 'Hard' / for medium type: 'Medium' / for easy type: 'Easy'");
		do {

			qDifficulty = input.next();
			qDifficulty += input.nextLine();
			if (!(qDifficulty.equals("Hard")) && !(qDifficulty.equals("Medium")) && !(qDifficulty.equals("Easy")))
				System.out.println("Wrong input!");
		} while (!(qDifficulty.equals("Hard")) && !(qDifficulty.equals("Medium")) && !(qDifficulty.equals("Easy")));
		Question question = QuestionFactory.getType(qType, questionText, eDifficulty.valueOf(qDifficulty));
		if (question instanceof OpenQuestion) {
			db.startConnection();
			db.insertToQuestionTable(questionText, qDifficulty, question.getId(), getSubject().getName());
			db.closeConnection();
			addOpenQuestionToData((OpenQuestion) question, questionText, eDifficulty.valueOf(qDifficulty));
		} else if (question instanceof ClosedQuestion) {
			db.startConnection();
			db.insertToQuestionTable(questionText, qDifficulty, question.getId(), getSubject().getName());
			db.insertToClosedQuestionTable(questionText);
			db.closeConnection();
			addClosedQuestionToData((ClosedQuestion) question, questionText, eDifficulty.valueOf(qDifficulty));
		}
		System.out.println("Question added succesfuly to stock");
	}

	public void addClosedQuestionToData(ClosedQuestion closedQuestion, String questionText, eDifficulty eDifficulty) throws SQLException {
		int answerKind;
		char ch;
		do {
			System.out.println("If you want to add answer from stock press 1");
			System.out.println("If you want to add a new answer by urself press 2");
			do {
				answerKind = input.nextInt();
				if (!(answerKind == 1) && !(answerKind == 2))
					System.out.println("Wrong input!");
			} while (!(answerKind == 1) && !(answerKind == 2));
			if (answerKind == 1) {
				showAnswersInStock();
				System.out.println("Which answer from stock you want to add? (pick answer number)");
				AnswerText answertext = selectAnswerFromStock();
				System.out.println("If you want that the answer will be correct press 't' if incorrect press 'f'");
				do {
					ch = input.next().charAt(0);
					if (!(ch == 't') && !(ch == 'f'))
						System.out.println("Wrong input!");
				} while (!(ch == 't') && !(ch == 'f'));
				if (ch == 't'){
					db.startConnection();
					db.insertToAdapterAnswerTable(true, answertext.toString());
					db.insertToAdapterAnswer_ClosedQuestionTable(true, answertext.toString(), questionText);
					db.closeConnection();
					closedQuestion.addAnswer(new AdapterAnswer(answertext, true));
				}
				else if (ch == 'f'){
					db.startConnection();
					db.insertToAdapterAnswerTable(false, answertext.toString());
					db.insertToAdapterAnswer_ClosedQuestionTable(false, answertext.toString(), questionText);
					db.closeConnection();
					closedQuestion.addAnswer(new AdapterAnswer(answertext, false));
				}
			} else if (answerKind == 2) {
				AnswerText temp = null;
				temp = addAnswerToData();
				System.out.println("If you want that the answer will be correct press 't' if incorrect press 'f'");
				do {
					ch = input.next().charAt(0);
					if (!(ch == 't') && !(ch == 'f'))
						System.out.println("Wrong input!");
				} while (!(ch == 't') && !(ch == 'f'));
				if (ch == 't'){
					db.startConnection();
					db.insertToAdapterAnswerTable(true, temp.toString());
					db.insertToAdapterAnswer_ClosedQuestionTable(true, temp.toString(), questionText);
					db.closeConnection();
					closedQuestion.addAnswer(new AdapterAnswer(temp, true));
				}
				else if (ch == 'f'){
					db.startConnection();
					db.insertToAdapterAnswerTable(false, temp.toString());
					db.insertToAdapterAnswer_ClosedQuestionTable(false, temp.toString(), questionText);
					db.closeConnection();
					closedQuestion.addAnswer(new AdapterAnswer(temp, false));
				}
			}
			do {
				System.out.println("If you want to add another answer press 'c' to continiue");
				System.out.println("If you want to stop press 's'");
				ch = input.next().charAt(0);
				if (!(ch == 'c') && !(ch == 's'))
					System.out.println("Wrong input!");
			} while (!(ch == 'c') && !(ch == 's'));
			if (ch == 's')
				try {
					getSubject().getStock().addQuestionToStock(closedQuestion);
				} catch (ClosedQuestionLessThen4AnswersException e) {
					System.out.println(e.getMessage());
					ch = 'c';
				}
		} while (ch == 'c');
	}

	public void addOpenQuestionToData(OpenQuestion openQuestion, String questionText, eDifficulty eDifficulty) throws SQLException {
		int answerKind;
		System.out.println("If you want to add answer from stock press 1");
		System.out.println("If you want to add a new answer by urself press 2");
		do {
			answerKind = input.nextInt();
			if (!(answerKind == 1) && !(answerKind == 2))
				System.out.println("Wrong input!");
		} while (!(answerKind == 1) && !(answerKind == 2));
		if (answerKind == 1) {
			System.out.println("Which answer from stock you want to add? (pick answer number)");
			showAnswersInStock();
			AnswerText answertext = selectAnswerFromStock();
			openQuestion.setAnswer(answertext);
		} else if (answerKind == 2) {
			String answerText;
			AnswerText temp = null;
			temp = addAnswerToData();
			openQuestion.setAnswer(temp);
		}
		try {
			getSubject().getStock().addQuestionToStock(openQuestion);
		} catch (ClosedQuestionLessThen4AnswersException e) {
			System.out.println(e.getMessage());
		}
		db.startConnection();
		db.insertToOpenQuestionTable(questionText, openQuestion.getAnswer().toString());
		db.closeConnection();
	}

	public int selectQuestionFromStock() {
		int selectedQuestion;
		do {
			selectedQuestion = input.nextInt();
			if (!getSubject().getStock().findQuestion(selectedQuestion))
				System.out.println("Wrong input!");
		} while (!getSubject().getStock().findQuestion(selectedQuestion));
		return selectedQuestion;
	}

	public AnswerText selectAnswerFromStock() {
		int selectedAnswer;
		AnswerText answerText;
		do {
			selectedAnswer = input.nextInt();
			answerText = getSubject().getStock().getAnswer(selectedAnswer);
			if (answerText == null)
				System.out.println("Wrong input!");
		} while (answerText == null);
		return answerText;
	}

	public void addAnswerFromStockToQuestion(AnswerText selectedAnswer, int selectedQuestion) {
		do {
			if (getSubject().getStock().getQuestion(selectedQuestion) instanceof OpenQuestion) {
				System.out.println(
						"You cant add an answer from stock to an open question, the open question already has an answer");
				System.out.println("Pick a different question number");
				selectedQuestion = selectQuestionFromStock();
			}
		} while (getSubject().getStock().getQuestion(selectedQuestion) instanceof OpenQuestion);
		System.out.println("If you want that the answer will be correct press 't' if incorrect press 'f'");
		char ch;
		do {
			ch = input.next().charAt(0);
			if (!(ch == 't') && !(ch == 'f'))
				System.out.println("Wrong input!");
		} while (!(ch == 't') && !(ch == 'f'));
		if ((ch == 't'))
			getSubject().getStock().addAnswerToSpesificQuestion(new AdapterAnswer(selectedAnswer, true),
					selectedQuestion);
		else if (((ch == 'f')))
			getSubject().getStock().addAnswerToSpesificQuestion(new AdapterAnswer(selectedAnswer, false),
					selectedQuestion);
	}

	public void showQuestionsInStock() {
		System.out.println("These are the questions in stock:");
		System.out.println(getSubject().getStock().toStringQuestionsInStock());
	}

	public void showAnswersInStock() {
		System.out.println("These are the answers in stock:");
		System.out.println(getSubject().getStock().toStringAnswersInStock());
	}

	public AnswerText addAnswerToData() throws SQLException {
		System.out.println("Please type the answer:");
		String answerText;
		AnswerText temp = null;
		while (true) {
			answerText = input.next();
			answerText += input.nextLine();
			temp = new AnswerText(answerText);
			if (!getSubject().getStock().getAnswersText().add(temp)) {
				System.out.println("This answer already exist in the stock");
			} else {
				break;
			}
		}
		db.startConnection();
		db.insertToAnswerTextTable(answerText, getSubject().getName());
		db.closeConnection();
		System.out.println("Answer added succesfuly to stock");
		return temp;
	}

	public void showSubjectsInStock() {
		System.out.println("These are the subjects in stock:");
		System.out.println(this.subjectRep.toStringSubjectsInStock());
	}

	public void showData() {
		if (getSubject().getStock().getQuestionsDB().size() == 0) {
			System.out.println("There are no questions in the database");
		} else {
			showQuestionsInStock();
		}
	}

	public void addAnswerFromStockToQuestion() {
		int questionNumber = 0;
		if (subjectRep.getSubject(subIn).getStock().getQuestionsDB().size() == 0) {
			System.out.println("There are no questions in the database");
		} else {
			if (subjectRep.getSubject(subIn).getStock().getAnswersText().size() == 0) {
				System.out.println("There are no answers in the database");
			} else {
				showAnswersInStock();
				showQuestionsInStock();
				System.out.println("Which answer from stock you want to add? (pick answer number)");
				AnswerText answertext = selectAnswerFromStock();
				System.out.println("To Which question you want to add the answer you picked? (pick question number)");
				questionNumber = selectQuestionFromStock();
				addAnswerFromStockToQuestion(answertext, questionNumber);
				System.out.println("Answer added succefuly");
			}
		}
	}

	public void deleteAnswerFromSpecificQuestion() throws SQLException {
		int questionNumber = 0;
		int counter = 0;
		Iterator<Question> it1 = subjectRep.getSubject(subIn).getStock().getQuestionsDB().iterator();
		while (it1.hasNext()) {
			if (it1.next() instanceof ClosedQuestion) {
				counter++;
			}
		}
		if (counter > 0) {
			showAnswersInStock();
			showQuestionsInStock();
			System.out.println("From which question from stock you want to delete an answer? (pick question number)");
			questionNumber = selectQuestionFromStock();
			System.out.println("which answer you want to delete? (pick answer number)");
			if (deleteAnswerFromSpesificQuestion(questionNumber))
				System.out.println("Answer deleted succefuly");
			else
				System.out.println("You cant delete an answer in open question");
		}
	}

	public void deleteQuestionFromStock() throws SQLException {
		int questionNumber = 0;
		if (subjectRep.getSubject(subIn).getStock().getQuestionsDB().size() == 0) {
			System.out.println("There are no questions in the database");
		} else {
			showQuestionsInStock();
			System.out.println("Which question from stock you wish to delete? (pick question number)");
			questionNumber = selectQuestionFromStock();
			deleteQuestionFromStock(questionNumber);
		}
	}
	
	@Override
	public void update(MenuActionCompleteListener m) {
		System.out.println("Observer said: " + m.getActionType() + " had ran successfully!");
	}

}
