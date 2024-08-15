public class QuestionFactory {
	public static Question getType(eQuestionType qType, String questionText, eDifficulty difficulty) {
		if (qType == null) {
			return null;
		} else if (qType == eQuestionType.Open) {
			return new OpenQuestion(questionText, difficulty);
		} else if (qType == eQuestionType.Closed) {
			return new ClosedQuestion(questionText, difficulty);
		}
		return null;
	}
}
