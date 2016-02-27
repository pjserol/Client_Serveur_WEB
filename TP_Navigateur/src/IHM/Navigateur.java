package IHM;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JTextField;

import Domaine.ClientWEB;

public class Navigateur {

	private JFrame frame;
	private JTextField txfUrl;
	private JEditorPane edpData;
	private String urlTest = "localhost:1026/test.html";

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Navigateur navigateur = new Navigateur();
					navigateur.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Navigateur() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 800, 600);
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		txfUrl = new JTextField();
		txfUrl.setBounds(10, 11, 400, 20);
		frame.getContentPane().add(txfUrl);
		txfUrl.setColumns(10);
		txfUrl.setText(urlTest);

		edpData = new JEditorPane();
		edpData.setBounds(10, 42, 760, 500);
		frame.getContentPane().add(edpData);

		JButton btnSend = new JButton("Actualiser");
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				afficherPage();
			}
		});
		btnSend.setBounds(450, 10, 103, 23);

		txfUrl.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent evt) {
				if (evt.getKeyCode() == KeyEvent.VK_ENTER || evt.getKeyCode() == KeyEvent.VK_F5) {
					afficherPage();
				}
			}
		});

		frame.getContentPane().add(btnSend);
	}
	
	public void afficherPage(){
		
		if (txfUrl.getText().equals("")){
			return;
		}
		
		String hostname = txfUrl.getText().substring(0,
				txfUrl.getText().indexOf(":"));
		int port = Integer.parseInt(txfUrl.getText().substring(
				txfUrl.getText().indexOf(":") + 1,
				txfUrl.getText().indexOf("/")));
		String file = txfUrl.getText().substring(
				txfUrl.getText().indexOf("/") + 1,
				txfUrl.getText().length());
		
		ClientWEB cli = new ClientWEB(hostname, port,
				file);

		try {
			ByteArrayOutputStream data = cli.start();

			if (file.endsWith("html")) {
				edpData.setContentType("text/html");
				edpData.setText(data.toString());
			}

		} catch (IOException e1) {
			edpData.setText("Erreur : " + e1.getMessage());

		}
	}
}
