package Domaine;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.sql.Date;

public class ServeurWEB implements Runnable {

	private static final String PATH = "C:\\Users\\Epulapp\\Desktop\\serveur\\";

	private Socket clientSocket;
	private File file;

	public ServeurWEB(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}

	public void run() {
		start();
	}

	public void start() {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					clientSocket.getInputStream()));

			String request = reader.readLine();

			if (request == null || request.startsWith("GET") == false) {
				StringBuffer buffer = createResponse(400, "Bad Request");
				sendResponse(buffer, null);
				return;
			}

			String path = request.substring(request.indexOf(" ") + 1,
					request.indexOf(" ", request.indexOf(" ") + 1));

			file = new File(PATH + path);

			if (file.exists() == false) {
				System.out.println("SRV - Fichier inexistant");
				StringBuffer buffer = createResponse(404, "File Not Found");
				sendResponse(buffer, null);
				return;
			}

			byte[] data = readFile(file);

			StringBuffer buffer = createResponse(200, new String(data));

			sendResponse(buffer, data);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private byte[] readFile(File file) throws IOException {
		System.out.println("SRV - Lecture de la requête");
		byte[] data = new byte[(int) file.length()];
		FileInputStream fis = new FileInputStream(file);
		while (fis.read(data) != -1) {
		}
		fis.close();

		return data;
	}

	private StringBuffer createResponse(int statusCode, String content) {
		System.out.println("SRV - Création de la réponse");
		StringBuffer response = new StringBuffer();
		String contentType = "";

		int type = 0;
		if (file != null) {
			type = typeOfFile(file);
		}
		if (type == 1) {
			contentType = "text/html; charset=UTF-8";
		} else if (type == 2) {
			contentType = "image/jpeg";
		}

		if (statusCode == 400 || statusCode == 404) {
			response.append("HTTP/1.1 " + statusCode + " " + content + "\r\n");
			response.append("Content-Type: " + contentType + "\r\n");
			response.append("Connection: close \r\n");
			response.append("\r\n");
			if (statusCode == 404)
				response.append("File Not Found !");
			if (statusCode == 400)
				response.append("Bad Request !");
		}

		if (statusCode == 200) {
			response.append("HTTP/1.1 200 OK \r\n");
			response.append("Last-Modified: " + new Date(file.lastModified())
					+ "\r\n");
			response.append("Content-Length: " + file.length() + "\r\n");
			response.append("Content-Type: " + contentType + "\r\n");
			response.append("Connection: close \r\n");
			response.append("\r\n");
		}
		return response;
	}

	private void sendResponse(StringBuffer buffer, byte[] data)
			throws IOException {

		System.out.println("SRV - Envoi du résultat");

		String head = new String(buffer);

		DataOutputStream out = new DataOutputStream(
				clientSocket.getOutputStream());

		if (data != null) {
			byte[] byteArray = new byte[head.getBytes().length + data.length];
			for (int i = 0; i < byteArray.length; ++i) {
				byteArray[i] = i < head.getBytes().length ? head.getBytes()[i]
						: data[i - head.getBytes().length];
			}
			out.write(byteArray);
		} else {
			out.write(head.getBytes());
		}
		out.flush();
		out.close();
	}

	private int typeOfFile(File file) {
		/* 0 pour fichier de type inconnu */
		int num = 0;
		/* 1 pour fichier html / htm */
		if (file.getAbsolutePath().endsWith("html")
				|| file.getAbsolutePath().endsWith("htm")) {
			num = 1;
		}
		/* 2 pour image jpg / jpeg / png */
		if (file.getAbsolutePath().endsWith("jpg")
				|| file.getAbsolutePath().endsWith("jpeg")
				|| file.getAbsolutePath().endsWith("png")) {
			num = 2;
		}

		return num;
	}
}
