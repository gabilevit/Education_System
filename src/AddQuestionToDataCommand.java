import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;

public class AddQuestionToDataCommand implements Command{

	private UserInteraction user;
	private eQuestionType qType;
	
	public AddQuestionToDataCommand(eQuestionType qType) throws FileNotFoundException, ClassNotFoundException, IOException, SQLException {
		this.user = UserInteraction.getInstance();
		this.qType = qType;
	}
	
	@Override
	public void execute() throws SQLException {
		user.addQuestionToData(this.qType);
	}
}
