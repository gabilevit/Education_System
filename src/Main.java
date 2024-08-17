import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.InputMismatchException;
import java.util.Iterator;
import java.util.Scanner;

public class Main {
	static Scanner input = new Scanner(System.in);

	public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException {
		UserInteraction user = UserInteraction.getInstance();
		subjectMenu(user);
	}

	private static void subjectMenu(UserInteraction user)
            throws FileNotFoundException, IOException, InputMismatchException, ClassNotFoundException, SQLException {
		
		int value = -1;
		MenuActionCompleteListener m = new MenuActionCompleteListener();
		m.attach(user);
		
		do {

			System.out.println("THIS IS THE MENU SUBJECT");
			System.out.println("*******************************************");
			System.out.println("Press 1 to show the subjects and pick one");
			System.out.println("Press 2 to add a subject");
			System.out.println("Press 3 to delete a subject");
			System.out.println("Press 0 to exit and save the data");

			try {
				value = input.nextInt();
			} catch (InputMismatchException e) {
				System.out.println("enter a number");
				input.next();
			}

			switch (value) {
			case 1:
				if (user.getSubjectRep().getSubjects().size() == 0) {
					System.out.println("There are no subjects in repository, please create a new one");
					subjectMenu(user);
				} else {
					user.showSubjectsInStock();
					System.out.println("Which subject from stock you want to pick? (pick subject number)");
					value = user.selectSubjectFromStock();
					user.setSubIn(value);
					if (user.getSubject().getQuestionsDB().size() > 0) {
						user.getSubject().getStock().initializeCounter();
					}
				}
				mainMenu(user, m);
				break;
			case 2:
				user.addSubjectToData();
				break;
			case 3:
				user.deleteSubjectFromData();
				break;
			case 0:
				user.saveDataToBinaryFile();
				System.out.println("Bye bye");
				break;
			default:
				break;
			}
		} while (value != 0 && value != 1);

	}

	private static void mainMenu(UserInteraction user, MenuActionCompleteListener m) throws IOException, InputMismatchException, ClassNotFoundException {
		int value = -1;
		do {
			try {
				System.out.println("Welcome to the system of tests");
				System.out.println("MENU:");
				System.out.println("*******************************************");
				System.out.println("Press 1 to show the whole data ");
				System.out.println("Press 2 to add answer to the data ");
				System.out.println("Press 3 to add answer from the data to the question ");
				System.out.println("Press 4 to add closed question to the data ");
				System.out.println("Press 5 to add open question to the data ");
				System.out.println("Press 6 to delete an answer from spesific question ");
				System.out.println("Press 7 to delete question from data ");
				System.out.println("Press 8 to create an exam ");
				System.out.println("Press 9 to return to the subjects menu ");
				System.out.println("Press 0 to exit the program and save the data");

				value = input.nextInt();
				Command cmd;

				switch (value) {
				case 1:
					cmd = new ShowDataCommand();
					m.setActionType("Show Data");
					cmd.execute();
					m.complete();
					break;
				case 2:
					cmd = new AddAnswerToDataCommand();
					m.setActionType("Add Answer");
					cmd.execute();
					m.complete();
					break;
				case 3:
					user.addAnswerFromStockToQuestion();
					break;
				case 4:
					cmd = new AddQuestionToDataCommand(eQuestionType.Closed);
					m.setActionType("Add Closed Question");
					cmd.execute();
					m.complete();
					break;
				case 5:
					cmd = new AddQuestionToDataCommand(eQuestionType.Open);
					m.setActionType("Add Open Question");
					cmd.execute();
					m.complete();
					break;
				case 6:
					user.deleteAnswerFromSpecificQuestion();
					break;
				case 7:
					user.deleteQuestionFromStock();
					break;
				case 8:
					createExam(user);
					break;
				case 9:
					subjectMenu(user);
					break;
				case 0:
					user.saveDataToBinaryFile();
					System.out.println("Bye bye");
					break;
				}
			} catch (InputMismatchException | SQLException e) {
				System.out.println("INPUT MISMATCH, ABORTING...");
				input.next();
			}
		} while (value != 0);
	}

	private static void createExam(UserInteraction user) throws IOException {
		System.out.println("These are the questions in stock: ");
		user.showQuestionsInStock();
		System.out.println("How many questions you want in the test?");
		int numOfQuestions;
		do {
			numOfQuestions = input.nextInt();
			if ((user.getSubject().getStock().getQuestionsDB().size() < numOfQuestions) || (0 >= numOfQuestions))
				System.out.println("Amount is too big/less than 0, try again");
		} while ((user.getSubject().getStock().getQuestionsDB().size() < numOfQuestions) || (0 >= numOfQuestions));
		System.out.println("If you want to do the exam manualy press 1");
		System.out.println("If you want to do the exam automatic press 2");
		int examType = 0;
		do {
			try {
				examType = input.nextInt();
			} catch (InputMismatchException e) {
				System.out.println("Enter a number");
				input.next();
			}
			if (examType > 2 || examType < 1)
				System.out.println("Wrong input");
		} while (examType > 2 || examType == 0);

		if (examType == 1) {
			ManualExam manualExam;
			try {
				manualExam = new ManualExam(user.getSubject().getName(), numOfQuestions, user);
				Repository theManualExam = manualExam.createExam(user.getSubject().getStock());
				manualExam.printExam(new ToTextFile(), theManualExam);
				System.out.println("Manual exam created succesfully!");
			} catch (MoreThen10QuestionsException e) {
				System.out.println(e.getMessage());
			}
		} else if (examType == 2) {
			AutomaticExam automaticExam;
			try {
				automaticExam = new AutomaticExam(user.getSubject().getName(), numOfQuestions);
				Repository theAutoExam = automaticExam.createExam(user.getSubject().getStock());
				automaticExam.printExam(new ToTextFile(), theAutoExam);
				System.out.println("Automatic exam created succesfully!");
			} catch (MoreThen10QuestionsException e) {
				System.out.println(e.getMessage());
			}
		}
	}
}
