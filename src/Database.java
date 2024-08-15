import org.w3c.dom.CDATASection;

import java.sql.*;

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
        createTable("AdapterAnswer_ClosedQuestion");
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
                query.append("AnswerTextID INT,\n");
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
                query.append("FOREIGN KEY (AnswerTextID) REFERENCES AnswerText(AnswerTextID)\n");
                query.append(");");
                break;
            case "AdapterAnswer_ClosedQuestion":
                query.append("AdapterAnswerID INT NOT NULL,\n");
                query.append("ClosedQID INT NOT NULL,\n");
                query.append("FOREIGN KEY (AdapterAnswerID) REFERENCES AdapterAnswer(AdapterAnswerID),\n");
                query.append("FOREIGN KEY (ClosedQID) REFERENCES ClosedQuestion(ClosedQID)\n");
                query.append(");");
                break;
        }
        try {
            stmt = con.createStatement();
            stmt.executeUpdate(query.toString());
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

    public boolean insertToAdapterAnswerTable(boolean isCorrect, String answerText) throws SQLException {
        try {
            stmt = con.createStatement();
            String q = "INSERT INTO AdapterAnswer (IsCorrect, AnswerTextID) VALUES ("+isCorrect+", "+getAnswerTextID(answerText)+");";
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


}
