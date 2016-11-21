package my.chat.server;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import my.chat.shared.Message;
import my.chat.shared.NewMessageProcessor;

public class ChatManager {

	private static final ChatManager INSTANCE = new ChatManager();

	private List<Message> chatMessages = new ArrayList<>();
	private Set<NewMessageProcessor> msgProcessors = new HashSet<>();

	private ChatManager() {
		// private constructor
	}

	public static ChatManager getInstance(){
		return INSTANCE;
	}

	public void reset() {
		chatMessages.clear();
		msgProcessors.clear();
	}

	public void addProcessor(NewMessageProcessor processor){
		msgProcessors.add(processor);
	}
	
	public void removeProcessor(NewMessageProcessor processor){
		msgProcessors.remove(processor);
	}
	
	public void addUserMessage(String user, String text){
		addMessage(new Message(user, text, false));
	}

	public void addServerMessage(String text){
		addMessage(new Message(null, text, true));
	}

	private void addMessage(Message msg){
		chatMessages.add(msg);
		for(NewMessageProcessor nmp : msgProcessors){
			nmp.processNewMessage(msg);
		}
	}

	public List<Message> getMessagesStartingFrom(long timestamp){
		List<Message> result = new ArrayList<>();
		for(Message msg: chatMessages){
			if(msg.getTimestamp() >= timestamp){
				result.add(msg);
			}
		}
		return result;
	}

}
