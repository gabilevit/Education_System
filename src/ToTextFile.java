import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ToTextFile implements Printable {

	@Override
	public void print(Repository rep, String type) throws IOException {
		LocalDateTime time = LocalDateTime.now();
		DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm");
		StringBuffer strTest = new StringBuffer("exam_" + type + "_" + time.format(format) + ".txt");
		StringBuffer strSolution = new StringBuffer("solution_" + type + "_" + time.format(format) + ".txt");
		File exam = new File(strTest.toString());
		exam.createNewFile();
		File solution = new File(strSolution.toString());
		solution.createNewFile();
		PrintWriter pwTest = new PrintWriter(exam);
		PrintWriter pwSolution = new PrintWriter(solution);
		pwTest.print(rep.toString());
		pwSolution.print(rep.toStringSolution());
		pwTest.close();
		pwSolution.close();

	}

}
