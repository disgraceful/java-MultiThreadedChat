package my.chat.shared;

import java.io.Serializable;

public class Message implements Serializable{

	private static final long serialVersionUID = -3121702471461442313L;

	private String user;
	private String text;
	private long timestamp;
	private boolean isServerMessage;

	public Message() {
		// default constructor for de-serialization
		this(null, null, false);
	}
	public Message(String user, String text, boolean isServerMessage){
		this.user = user;
		this.text = text;
		this.isServerMessage = isServerMessage;
		this.timestamp = System.currentTimeMillis();
	}

	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	public boolean isServerMessage() {
		return isServerMessage;
	}
	public void setServerMessage(boolean isServerMessage) {
		this.isServerMessage = isServerMessage;
	}



}
