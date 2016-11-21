package my.chat.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import my.chat.shared.Constants;
import my.chat.shared.Message;
import my.chat.shared.NewMessageProcessor;

public class ServerMain implements Constants {

	public static void main(String[] args) {
		ChatManager.getInstance().reset();
		ServerSocket serverSocket = null;
		int clientThreadCount = 0;

		try {
			serverSocket = new ServerSocket(SERVER_PORT);
			System.out.println("chat server started @ " + serverSocket.getLocalSocketAddress());
			while (true) {
				new ClientThread(serverSocket.accept(), "thread." + (++clientThreadCount)).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("chat server stopped");
	}

	private static class ClientThread extends Thread implements NewMessageProcessor, Constants {

		private final Socket socket;
		private ObjectInputStream in;
		private ObjectOutputStream out;

		private String userName = null;
		private boolean isUserSignedIn = false;

		public ClientThread(Socket socket, String threadName) {
			super(threadName);
			ChatManager.getInstance().addProcessor(this);
			this.socket = socket;
			try {
				in = new ObjectInputStream(socket.getInputStream());
				out = new ObjectOutputStream(socket.getOutputStream());

				sendMessage(new Message(null, "Welcome, please sign in.", true));

			} catch (IOException e) {
				e.printStackTrace();
				System.out.println(getName() + ": cannot I/O client socket.");
			}
			System.out.println(getName() + ": initialized");
		}

		@Override
		public void run() {
			System.out.println(getName() + ": running");

			if(in == null || out == null){
				System.out.println(getName() + ": cannot I/O client socket. terminating thread.");
				return;
			}

			while (true) {
				try {
					Object inObj = in.readObject();
					if (inObj != null && inObj instanceof Message) {
						Message msg = (Message) inObj;
						System.out.println(getName()+ "-> received: " + msg.getText());
						if (isUserSignedIn) {
							ChatManager.getInstance().addUserMessage(userName, msg.getText());

						} else if (msg.getUser() != null && !msg.getUser().isEmpty()) {
							userName = msg.getUser();
							isUserSignedIn = true;

							List<Message> recentMessages = ChatManager.getInstance().getMessagesStartingFrom(System.currentTimeMillis() - RECENT_SHIFT);
							for(Message recent : recentMessages){
								sendMessage(recent);
							}
							ChatManager.getInstance().addServerMessage("User '" + userName + "' signed in");
						}
					}
				} catch (IOException | ClassNotFoundException e) {
					//e.printStackTrace();
					System.out.println(getName() + ": cannot I/O client socket. terminating thread.");
					break;
				}
			}

			signOut();
		}

		@Override
		public void processNewMessage(Message msg) {
			if (!isUserSignedIn) {
				return;
			}
			sendMessage(msg);
		}

		private void sendMessage(Message msg) {
			try {
				System.out.println(getName() + " -> sending: " + msg.getText());
				out.writeObject(msg);
				out.flush();
			}catch(IOException e){
				System.out.println(getName() + ": cannot I/O client socket. terminating thread.");
				signOut();
			}
		}
		
		private void signOut(){
			ChatManager.getInstance().removeProcessor(this);
			ChatManager.getInstance().addServerMessage("User '" + userName + "' signed out");
			try{
				socket.close();
			}catch(IOException e){
				
			}
		}
	}

}
