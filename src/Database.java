import org.w3c.dom.CDATASection;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;

public class Database {
    private static Database instance;
    private static final String URL = "jdbc:postgresql://localhost:5432/Education";
    private static final String USER = "postgres";
    private static final String PASSWORD = "190299";
    public Connection con;
    public Statement stmt;
    public ResultSet rs;


    private void Database() throws SQLException { //constructor
        //startConnection(); //start connection with the database
        //pullDatabase(); //checks if database is not null. if null builds an empty database with ready tables.
    }

    public static Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    public void createTables() throws SQLException {
        createTable("Subject");
        createTable("AnswerText");
        createTable("Question");
        createTable("OpenQuestion");
        createTable("ClosedQuestion");
        createTable("AdapterAnswer");
        //createTable("AdapterAnswer_ClosedQuestion");
    }

    public void createTable(String tableName) throws SQLException {
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
                query.append("SubjectID INT NOT NULL,\n");
                query.append("FOREIGN KEY (SubjectID) REFERENCES Subject(SubjectID));");
                break;
            case "OpenQuestion":
                query.append("OpenQID SERIAL PRIMARY KEY,\n");
                query.append("QuestionID INT NOT NULL,\n");
                query.append("AnswerTextID INT NOT NULL,\n");
                query.append("FOREIGN KEY (QuestionID) REFERENCES Question(QuestionID),\n");
                query.append("FOREIGN KEY (AnswerTextID) REFERENCES AnswerText(AnswerTextID)\n");
                query.append(");");
                break;
            case "ClosedQuestion":
                query.append("ClosedQID SERIAL PRIMARY KEY,\n");
                query.append("QuestionID INT NOT NULL,\n");
                query.append("FOREIGN KEY (QuestionID) REFERENCES Question(QuestionID));");
                break;
            case "AdapterAnswer":
                query.append("AdapterAnswerID SERIAL PRIMARY KEY,\n");
                query.append("IsCorrect BOOLEAN NOT NULL,\n");
                query.append("AnswerTextID INT NOT NULL,\n");
                query.append("ClosedQID INT NOT NULL,\n");
                query.append("FOREIGN KEY (AnswerTextID) REFERENCES AnswerText(AnswerTextID),\n");
                query.append("FOREIGN KEY (ClosedQID) REFERENCES ClosedQuestion(ClosedQID)\n");
                query.append(");");
                break;
            /*case "AdapterAnswer_ClosedQuestion":
                query.append("AdapterAnswerID INT NOT NULL,\n");
                query.append("ClosedQID INT NOT NULL,\n");
                query.append("FOREIGN KEY (AdapterAnswerID) REFERENCES AdapterAnswer(AdapterAnswerID),\n");
                query.append("FOREIGN KEY (ClosedQID) REFERENCES ClosedQuestion(ClosedQID)\n");
                query.append(");");
                break;*/
        }
        try {
            stmt = con.createStatement();
            stmt.executeUpdate(query.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void dropTables() throws SQLException {
        dropTable("AdapterAnswer_ClosedQuestion");
        dropTable("AdapterAnswer");
        dropTable("ClosedQuestion");
        dropTable("OpenQuestion");
        dropTable("Question");
        dropTable("AnswerText");
        dropTable("Subject");
    }

    public void dropTable(String tableName) throws SQLException {
        try {
            stmt = con.createStatement();
            String q = "DROP TABLE IF EXISTS "+tableName+";";
            stmt.executeUpdate(q.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void startConnection() throws SQLException{
        con = null;
        try{
            Class.forName("org.postgresql.Driver");
            con = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void closeConnection() throws SQLException{
        try {
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean insertToSubjectTable(String name) throws SQLException {
        try {
            stmt = con.createStatement();
            String q = "INSERT INTO Subject (Name) VALUES ('"+name+"');";
            stmt.executeUpdate(q.toString());
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean insertToAnswerTextTable(String answerText, String subjectText) throws SQLException {
        try {
            stmt = con.createStatement();
            String q = "INSERT INTO AnswerText (AnswerText, SubjectID) VALUES ('"+answerText+"', "+ getSubjectID(subjectText)+");";
            stmt.executeUpdate(q.toString());
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean insertToQuestionTable(String text, String difficulty, int serialNumber, String subjectText) throws SQLException {
        try {
            stmt = con.createStatement();
            String q = "INSERT INTO Question (Text, Difficulty, SerialNumber, SubjectID) VALUES ('"+text+"', '"+difficulty+"', "+serialNumber+", "+ getSubjectID(subjectText)+");";
            stmt.executeUpdate(q.toString());
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean insertToOpenQuestionTable(String questionText, String answerText) throws SQLException {
        try {
            stmt = con.createStatement();
            String q = "INSERT INTO OpenQuestion (QuestionID, AnswerTextID) VALUES ("+ getQuestionID(questionText)+", "+getAnswerTextID(answerText)+");";
            stmt.executeUpdate(q.toString());
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean insertToClosedQuestionTable(String questionText) throws SQLException {
        try {
            stmt = con.createStatement();
            String q = "INSERT INTO ClosedQuestion (QuestionID) VALUES ("+getQuestionID(questionText)+");";
            stmt.executeUpdate(q.toString());
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean insertToAdapterAnswerTable(boolean isCorrect, String answerText, String questionText) throws SQLException {
        try {
            stmt = con.createStatement();
            String q = "INSERT INTO AdapterAnswer (IsCorrect, AnswerTextID, ClosedQID) VALUES ("+isCorrect+", "+getAnswerTextID(answerText)+", "+getClosedQuestionID(getQuestionID(questionText))+");";
            stmt.executeUpdate(q.toString());
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean insertToAdapterAnswer_ClosedQuestionTable(boolean isCorrect, String adapterAnswerText, String questionText) throws SQLException {
        try {
            stmt = con.createStatement();
            String q = "INSERT INTO AdapterAnswer_ClosedQuestion (AdapterAnswerID, ClosedQID) VALUES ("+getAdapterAnswerID(isCorrect, getAnswerTextID(adapterAnswerText))+", "+getClosedQuestionID(getQuestionID(questionText))+");";
            stmt.executeUpdate(q.toString());
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int getSubjectID(String name) throws SQLException {
        try {
            stmt = con.createStatement();
            String q = "SELECT SubjectID FROM Subject WHERE Name = '"+name+"';";
            rs = stmt.executeQuery(q.toString());
            rs.next();
            return rs.getInt("SubjectID");
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public int getAnswerTextID(String answerText) throws SQLException {
        try {
            stmt = con.createStatement();
            String q = "SELECT AnswerTextID FROM AnswerText WHERE AnswerText = '"+answerText+"';";
            rs = stmt.executeQuery(q.toString());
            rs.next();
            return rs.getInt("AnswerTextID");
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public int getQuestionID(String text) throws SQLException {
        try {
            stmt = con.createStatement();
            String q = "SELECT QuestionID FROM Question WHERE Text = '"+text+"';";
            rs = stmt.executeQuery(q.toString());
            rs.next();
            return rs.getInt("QuestionID");
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public int getOpenQuestionID(int questionID) throws SQLException {
        try {
            stmt = con.createStatement();
            String q = "SELECT OpenQID FROM OpenQuestion WHERE QuestionID = "+questionID+";";
            rs = stmt.executeQuery(q.toString());
            rs.next();
            return rs.getInt("OpenQID");
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public int getClosedQuestionID(int questionID) throws SQLException {
        try {
            stmt = con.createStatement();
            String q = "SELECT ClosedQID FROM ClosedQuestion WHERE QuestionID = "+questionID+";";
            rs = stmt.executeQuery(q.toString());
            rs.next();
            return rs.getInt("ClosedQID");
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public int getAdapterAnswerID(boolean isCorrect, int answerTextID) throws SQLException {
        try {
            stmt = con.createStatement();
            String q = "SELECT AdapterAnswerID FROM AdapterAnswer WHERE IsCorrect = "+isCorrect+" AND AnswerTextID = "+answerTextID+";";
            rs = stmt.executeQuery(q.toString());
            rs.next();
            return rs.getInt("AdapterAnswerID");
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public boolean deleteFromSubjectTable(String name) throws SQLException {
        try {
            stmt = con.createStatement();
            String q = "DELETE FROM Subject WHERE Name = '"+name+"';";
            stmt.executeUpdate(q.toString());
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteFromAnswerTextTable(String answerText) throws SQLException {
        try {
            stmt = con.createStatement();
            String q = "DELETE FROM AnswerText WHERE AnswerText = '"+answerText+"';";
            stmt.executeUpdate(q.toString());
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteFromQuestionTable(String text) throws SQLException {
        try {
            stmt = con.createStatement();
            String q = "DELETE FROM Question WHERE Text = '"+text+"';";
            stmt.executeUpdate(q.toString());
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteFromOpenQuestionTable(String questionText) throws SQLException {
        try {
            stmt = con.createStatement();
            String q = "DELETE FROM OpenQuestion WHERE QuestionID = "+getQuestionID(questionText)+";";
            stmt.executeUpdate(q.toString());
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteFromClosedQuestionTable(String questionText) throws SQLException {
        try {
            stmt = con.createStatement();
            String q = "DELETE FROM ClosedQuestion WHERE QuestionID = "+getQuestionID(questionText)+";";
            stmt.executeUpdate(q.toString());
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteFromAdapterAnswerTable(String answerText) throws SQLException {
        try {
            stmt = con.createStatement();
            String q = "DELETE FROM AdapterAnswer WHERE AnswerTextID = "+getAnswerTextID(answerText)+";";
            stmt.executeUpdate(q.toString());
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public ArrayList<AdapterAnswer> getAdapterAnswers() throws SQLException {
        ArrayList<AdapterAnswer> adapterAnswers = new ArrayList<>();
        try {
            stmt = con.createStatement();
            String q = "SELECT * FROM AdapterAnswer;";
            rs = stmt.executeQuery(q.toString());
            while (rs.next()) {
                int answerTextID = rs.getInt("AnswerTextID");
                stmt = con.createStatement();
                String q2 = "SELECT AnswerText FROM AnswerText WHERE AnswerTextID = "+answerTextID+";";
                ResultSet rs2 = stmt.executeQuery(q2.toString());
                adapterAnswers.add(new AdapterAnswer(new AnswerText(rs2.getString("AnswerText")), rs.getBoolean("IsCorrect")));
            }
            return adapterAnswers;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public HashSet<Question> getQuestions(String subjectName) throws SQLException {
        HashSet<Question> questions = new HashSet<>();
        try {
            stmt = con.createStatement();
            String q = "SELECT * FROM Question WHERE SubjectID = "+getSubjectID(subjectName)+";";
            rs = stmt.executeQuery(q.toString());
            while (rs.next()) {
                int questionID = rs.getInt("QuestionID");
                stmt = con.createStatement();
                String q2 = "SELECT text FROM Question WHERE QuestionID = "+questionID+";";
                ResultSet rs2 = stmt.executeQuery(q2.toString());
                String questionText = null;
                if (rs2.next()) {  // Move cursor to first row
                    questionText = rs2.getString("Text");
                }
                stmt = con.createStatement();
                String q3 = "SELECT * FROM OpenQuestion WHERE QuestionID = "+questionID+";";
                ResultSet rs3 = stmt.executeQuery(q3.toString());
                if (rs3.next()) {
                    stmt = con.createStatement();
                    String q4 = "SELECT AnswerText FROM AnswerText WHERE AnswerTextID = "+rs3.getInt("AnswerTextID")+";";
                    ResultSet rs4 = stmt.executeQuery(q4.toString());
                    OpenQuestion openQuestion = new OpenQuestion(questionText, eDifficulty.valueOf(rs.getString("Difficulty")));
                    if(rs4.next()){
                        openQuestion.setAnswer(new AnswerText(rs4.getString("AnswerText")));
                    }
                    questions.add(openQuestion);
                } else {
                    stmt = con.createStatement();
                    String q5 = "SELECT * FROM ClosedQuestion WHERE QuestionID = "+questionID+";";
                    ResultSet rs5 = stmt.executeQuery(q5.toString());
                    if (rs5.next()) {
                        ClosedQuestion closedQuestion = new ClosedQuestion(questionText, eDifficulty.valueOf(rs.getString("Difficulty")));
                        stmt = con.createStatement();
                        String q6 = "SELECT * FROM AdapterAnswer WHERE ClosedQID = "+rs5.getInt("ClosedQID")+";";
                        ResultSet rs6 = stmt.executeQuery(q6.toString());
                        while (rs6.next()) {
                            stmt = con.createStatement();
                            String q7 = "SELECT AnswerText FROM AnswerText WHERE AnswerTextID = "+rs6.getInt("AnswerTextID")+";";
                            ResultSet rs7 = stmt.executeQuery(q7.toString());
                            if (rs7.next()) {
                                closedQuestion.addAnswer(new AdapterAnswer(new AnswerText(rs7.getString("AnswerText")), rs6.getBoolean("IsCorrect")));
                            }
                        }
                        questions.add(closedQuestion);
                    }
                }
            }
            return questions;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public LinkedHashSet<AnswerText> getAnswersText(String subjectName) throws SQLException {
        LinkedHashSet<AnswerText> answersText = new LinkedHashSet<>();
        try {
            stmt = con.createStatement();
            String q = "SELECT * FROM AnswerText WHERE SubjectID = "+getSubjectID(subjectName)+";";
            rs = stmt.executeQuery(q.toString());
            while (rs.next()) {
                answersText.add(new AnswerText(rs.getString("AnswerText")));
            }
            return answersText;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ArrayList<Subject> getSubjects() throws SQLException {
        ArrayList<Subject> subjects = new ArrayList<>();
        try {
            stmt = con.createStatement();
            String q = "SELECT * FROM Subject;";
            rs = stmt.executeQuery(q.toString());
            while (rs.next()) {
                Subject subject = new Subject(rs.getString("Name"));
                //subject.getStock().setQuestionsDB(getQuestions(subject.getName()));
                //subject.getStock().setAnswersText(getAnswersText(subject.getName()));
                subjects.add(subject);
                
            }
            for (Subject subject : subjects) {
                subject.getStock().setQuestionsDB(getQuestions(subject.getName()));
                subject.getStock().setAnswersText(getAnswersText(subject.getName()));
            }
            return subjects;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
