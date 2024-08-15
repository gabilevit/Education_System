import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;

public class ShowDataCommand implements Command{

	private UserInteraction user;
	
	public ShowDataCommand() throws FileNotFoundException, ClassNotFoundException, IOException, SQLException {
		this.user = UserInteraction.getInstance();
	}
	
	@Override
	public void execute() {
		user.showData();
	}

}
