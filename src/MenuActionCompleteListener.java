import java.util.HashSet;
import java.util.Set;

public class MenuActionCompleteListener {

	private String actionType;
	private Set<Observer> set = new HashSet<>();

	public MenuActionCompleteListener() {
			
		}

	public void setActionType(String actionType) {
		this.actionType = actionType;
	}

	public String getActionType() {
		return actionType;
	}

	// addListener
	public void attach(Observer o) {
		set.add(o);
	}

	public void complete() {
		for (Observer o : set)
			o.update(this);
	}

	public void detach(Observer o) {
		set.remove(o);
	}
}
