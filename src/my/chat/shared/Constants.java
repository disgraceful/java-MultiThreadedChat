package my.chat.shared;

import java.text.SimpleDateFormat;

public interface Constants {
	String SERVER_HOST = "127.0.0.1";
	int SERVER_PORT = 2222;
	long RECENT_SHIFT = 2 * 60 * 1000;
	SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("hh:mm:ss");


}
