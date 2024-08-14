import java.sql.*;

public class Database {
    private static final String URL = "jdbc:postgresql://localhost:5432/Education";
    private static final String USER = "your_username";
    private static final String PASSWORD = "your_password";
    public Connection con;
    public Statement stmt;
    public ResultSet rs;


    public void Database() throws SQLException { //constructor
        startConnection(); //start connection with the database
        //pullDatabase(); //checks if database is not null. if null builds an empty database with ready tables.
    }

    /*public void pullDatabase() {
        //if() { //if database is null create new ready tables

            String createSubjectTable = "CREATE TABLE IF NOT EXISTS Subject (" +
                    "subject_id SERIAL PRIMARY KEY," +
                    "subject_name VARCHAR(255) NOT NULL);";

            String createQuestionTable = "CREATE TABLE IF NOT EXISTS Question (" +
                    "question_id SERIAL PRIMARY KEY," +
                    "question_text TEXT NOT NULL," +
                    "subject_id INT," +
                    "FOREIGN KEY (subject_id) REFERENCES Subject(subject_id));";

            String createOpenQuestionTable = "CREATE TABLE IF NOT EXISTS OpenQuestion (" +
                    "open_question_id SERIAL PRIMARY KEY," +
                    "question_id INT NOT NULL," +
                    "additional_info TEXT," +
                    "FOREIGN KEY (question_id) REFERENCES Question(question_id));";

            String createClosedQuestionTable = "CREATE TABLE IF NOT EXISTS ClosedQuestion (" +
                    "closed_question_id SERIAL PRIMARY KEY," +
                    "question_id INT NOT NULL," +
                    "correct_answer VARCHAR(255)," +
                    "FOREIGN KEY (question_id) REFERENCES Question(question_id));";

            String createAnswerTable = "CREATE TABLE IF NOT EXISTS Answer (" +
                    "answer_id SERIAL PRIMARY KEY," +
                    "question_id INT NOT NULL," +
                    "answer_text TEXT NOT NULL," +
                    "is_correct BOOLEAN," +
                    "FOREIGN KEY (question_id) REFERENCES Question(question_id));";

            String createAnswerTextTable = "CREATE TABLE IF NOT EXISTS AnswerText (" +
                    "answer_text_id SERIAL PRIMARY KEY," +
                    "subject_id INT NOT NULL," +
                    "answer_text TEXT NOT NULL," +
                    "FOREIGN KEY (subject_id) REFERENCES Subject(subject_id));";

       // }
    }*/

    public void createTable(String tableName) {
        StringBuilder query = new StringBuilder("CREATE TABLE IF NOT EXISTS "+tableName+"(\n");
        switch(tableName) {
            case "Subject":
                query.append("SubjectID SERIAL PRIMARY KEY,\n");
                query.append("Name VARCHAR(255) NOT NULL);");
                break;
            case "AnswerText":
                query.append("AnswerTextID SERIAL PRIMARY KEY,\n");
                query.append("AnswerText TEXT NOT NULL,\n");
                query.append("SubjectID INT NOT NULL,\n");
                query.append("FOREIGN KEY (SubjectID) REFERENCES Subject(SubjectID));");
                break;
            case "Question":
                query.append("QuestionID SERIAL PRIMARY KEY,\n");
                query.append("Text VARCHAR(255) NOT NULL,\n");
                query.append("Difficulty VARCHAR(255) NOT NULL,\n");
                query.append("SerialNumber INT NOT NULL,\n");
                query.append("SubjectID INT,\n");
                query.append("FOREIGN KEY (SubjectID) REFERENCES Subject(SubjectID));");
                break;
            case "OpenQuestion":
                query.append("OpenQID SERIAL PRIMARY KEY,\n");
                query.append("QuestionID INT NOT NULL,\n");
                query.append("AswerTextID INT NOT NULL,\n");
                query.append("FOREIGN KEY (QuestionID) REFERENCES Question(QuestionID)),\n");
                query.append("FOREIGN KEY (AnswerTextID) REFERENCES AnswerText(AnswerTextID));");
                break;
            case "ClosedQuestion":
                query.append("ClosedQID SERIAL PRIMARY KEY,\n");
                query.append("QuestionID INT NOT NULL,\n");
                query.append("FOREIGN KEY (QuestionID) REFERENCES Question(QuestionID));");
                break;
            case "Answer":
                query.append("AnswerID SERIAL PRIMARY KEY,\n");
                query.append("IsCorrect BOOLEAN NOT NULL,\n");
                query.append("AnswerTextID INT NOT NULL,\n");
                query.append("QuestionID INT NOT NULL,\n");
                query.append("FOREIGN KEY (AnswerTextID) REFERENCES AnswerText(AnswerTextID)),\n");
                query.append("FOREIGN KEY (QuestionID) REFERENCES Question(QuestionID));");
                break;
        }
    }

    private void startConnection() throws SQLException{
        con = null;
        try{
            Class.forName("org.postgresql.Driver");
            con = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void closeConnection() {
        try {
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
