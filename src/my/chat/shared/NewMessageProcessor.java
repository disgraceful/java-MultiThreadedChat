package my.chat.shared;

@FunctionalInterface
public interface NewMessageProcessor {
	void processNewMessage(Message msg);
}
