import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.net.ServerSocket;
import java.net.Socket;

public class DiscountServer {

	private static LinkedList<Server> threads = new LinkedList<>();

	public static void main(String[] args) {
		DiscountServer server = new DiscountServer();

		server.start(6000);
	}

	private void start(int port) {
		try {
			ServerSocket serverSocket = new ServerSocket(port);
			while (true) {
				Socket socket = serverSocket.accept();
				Server thread = new Server(socket);
				threads.add(thread);
				thread.start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void updateAllStores() throws IOException {
        for (Server thread : threads) {
            if (thread.isAlive() && thread.onMapPage) {
            	thread.sendMessage("104");
            }
        }
    }

}
