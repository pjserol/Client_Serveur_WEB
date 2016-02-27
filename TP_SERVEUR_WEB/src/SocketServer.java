
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import Domaine.ServeurWEB;

public class SocketServer {

	private static int PORT = 1026;

	private ServerSocket serverSocket;

	public SocketServer() {
	}

	public void start() throws IOException {
		System.out.println("SRV - Démarrage du socket serveur sur le port : "
				+ PORT);
		serverSocket = new ServerSocket(PORT);
		Socket clientSocket = null;

		while (true) {
			System.out.println("SRV - Attente des clients...");
			clientSocket = serverSocket.accept();
			System.out.println("SRV - Le client suivant est connecté : "
					+ clientSocket.getInetAddress());
			Thread thread = new Thread(new ServeurWEB(clientSocket));
			thread.start();
		}
	}

	public static void main(String[] args) {
		try {
			SocketServer srvSocket = new SocketServer();
			srvSocket.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
