/*
 * package id_207612417_id_326635406;
 * 
 * import java.io.Serializable;
 * 
 * public class AnswerText implements Serializable{ private String text;
 * 
 * public AnswerText(String text) { this.text = text; }
 * 
 * public String getAnswerTextString() { return this.text; }
 * 
 * @Override public String toString() { return getAnswerTextString(); }
 * 
 * @Override public int hashCode() { return
 * (this.text.toLowerCase().hashCode()); }
 * 
 * @Override public boolean equals(Object o) { if((o != null)&&(o instanceof
 * AnswerText)) { AnswerText temp = (AnswerText) o;
 * return((temp.getAnswerTextString()!=
 * null)&&(temp.getAnswerTextString().toLowerCase().equals(this.text.toLowerCase
 * ()))); } return false; } }
 */

import java.io.Serializable;

public class AnswerText<T> implements Serializable {
	private T text;

	public AnswerText(T text) {
		this.text = text;
	}

	public T getAnswerTextString() {
		return this.text;
	}

	@Override
	public String toString() {
		return text.toString();
	}

	@Override
	public int hashCode() {
		if (this.text instanceof String) {
			return (((String) this.text).toLowerCase().hashCode());
		}
		return (this.text.hashCode());
	}

	@Override
	public boolean equals(Object o) {
		if ((o != null) && (o instanceof AnswerText)) {
			AnswerText temp = (AnswerText) o;
			if ((temp.getAnswerTextString().getClass().equals(this.text.getClass()))
					&& (temp.getAnswerTextString() != null)) {
				return (temp.getAnswerTextString().toString().toLowerCase().equals(this.text.toString().toLowerCase()));
			}
		}
		return false;
	}
}
