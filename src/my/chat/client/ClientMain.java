package my.chat.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Date;

import my.chat.shared.Constants;
import my.chat.shared.Message;
import my.chat.shared.NewMessageProcessor;

public class ClientMain implements Constants{

	public static void main(String[] args) {

		System.out.println("starting client");

		ServerThread serverThread = new ServerThread(new MsgProcessor(), "user1");
		serverThread.start();

		System.out.println("server thread started");

		String[] toSend = {"first", "these", "are", "test", "messages"};

		for(String text : toSend){
			serverThread.sendMessage(text);

			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		System.exit(0);
	}


	private static class MsgProcessor implements NewMessageProcessor, Constants{

		@Override
		public void processNewMessage(Message msg) {
			String output = new StringBuilder().append("> ")
					.append(DATE_FORMATTER.format(new Date(msg.getTimestamp()))).append(" ")
					.append(msg.isServerMessage() ? "[server]" : msg.getUser())
					.append(": ").append(msg.getText()).toString();
			System.out.println(output);
		}

	}

	private static class ServerThread extends Thread implements Constants{

		private Socket socket;
		private final NewMessageProcessor msgProcessor;
		private final String userName;
		private ObjectInputStream in;
		private ObjectOutputStream out;

		public ServerThread(NewMessageProcessor msgProcessor, String userName) {
			super("client.thread");
			setDaemon(true);
			this.userName = userName;
			this.msgProcessor = msgProcessor;
			try{
				socket = new Socket(SERVER_HOST, SERVER_PORT);
				out = new ObjectOutputStream(socket.getOutputStream());
				in = new ObjectInputStream(socket.getInputStream());
			}catch(IOException e){
				throw new IllegalStateException(e);
			}

			System.out.println(getName() + ": initialized");
		}

		@Override
		public void run() {
			System.out.println(getName() + ": started");

			while(true){
				try {
					Object inObj = in.readObject();
					if(inObj != null && inObj instanceof Message){
						Message msg = (Message) inObj;
						//System.out.println(getName() + " -> received: " + msg.getText());
						msgProcessor.processNewMessage(msg);
					}
				} catch (IOException | ClassNotFoundException e) {
					e.printStackTrace();
					break;
				}
			}
		}

		public void sendMessage(String text){
			String toSend = text == null ? null : text.trim();
			if(toSend == null || toSend.isEmpty()){
				return;
			}
			try {
				//System.out.println(getName() + "-> sending: " + text);
				out.writeObject(new Message(userName, text, false));
				out.flush();
			}catch(IOException e){
				e.printStackTrace();
			}
		}
	}

}
