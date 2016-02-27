package Domaine;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientWEB {

	private static final String PATH = "C:\\Users\\Epulapp\\Desktop\\client\\";

	private String hostname;
	private int port;
	private String file;
	private Socket socketClient;
	private ByteArrayOutputStream receivedData;
	private ByteArrayOutputStream data;

	public ClientWEB(String hostname, int port, String file) {
		this.hostname = hostname;
		this.port = port;
		this.file = file;
	}

	public ByteArrayOutputStream start() throws UnknownHostException,
			IOException {
		System.out.println("Cli - Attente de la connection " + hostname + ":"
				+ port);
		socketClient = new Socket(hostname, port);
		System.out.println("Cli - Connexion établie");

		sendRequest();

		readResponse();

		return data;
	}

	public void sendRequest() throws IOException {
		PrintWriter outWriter = null;
		outWriter = new PrintWriter(socketClient.getOutputStream());
		System.out.println("Cli - Construction de la requête");
		
		String request = "GET " + file + " HTTP/1.1\r\n";
		request += "Host:" + hostname + ":" + port + "\r\n";
		request += "Accept: text/plain, text/html, text/*\r\n";
		request += "Accept-Ranges: bytes\r\n";
		request += "Connection: close\r\n";
		request += "\r\n";
		
		outWriter.println(request);
		outWriter.flush();
		System.out.println("Cli - Requête envoyée");
	}

	public void readResponse() throws IOException {
		System.out.println("Cli - Réception des données");
		receivedData = new ByteArrayOutputStream();
		InputStream inputStream = socketClient.getInputStream();
		byte[] tableOfBytes = new byte[socketClient.getReceiveBufferSize()];
		inputStream.read(tableOfBytes);
		receivedData.write(tableOfBytes);

		String response = new String(receivedData.toByteArray());

		System.out.println( "Response : " + response);
		
		// Construction de la page à afficher
		int startOffsetInData = response.indexOf("Connection: close")
				+ "Connection: close".length();
		int endOffsetInData = response.substring(startOffsetInData,
				response.length()).length();
		data = new ByteArrayOutputStream();
		data.write(receivedData.toByteArray(), startOffsetInData,
				endOffsetInData);

		// Sauvegarde du fichier
		saveFile(data);

		socketClient.close();
	}

	private void saveFile(ByteArrayOutputStream dataFile) throws IOException {

		File file = new File(PATH + this.file);

		if (file.exists() == true) {
			file.delete();
		}

		FileOutputStream fos = new FileOutputStream(file);

		fos.write(dataFile.toByteArray());

		fos.close();
	}
}
