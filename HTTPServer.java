package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class HTTPServer {

	private static ServerSocket server;
	private static String webpage;
		
	public static void loadWebsite() {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(new File("src/main/homepage.html")));
		} catch (FileNotFoundException e) {
			System.err.println("Could not access html file.");
			System.err.println("Message: " + e.getMessage());
			System.exit(1);
		}
	
		webpage = "";
		try {
			while (reader.ready()) {
				webpage += reader.readLine() + "\n";
			}
		} catch (IOException e) {
			System.err.println("Could not read from file.");
			System.err.println("Message: " + e.getMessage());
			System.exit(1);
		}
		
		try {
			reader.close();
		} catch (IOException e) {
			System.out.println("Could not close I/O of html file.");
			System.out.println("Message: " + e.getMessage());
		}
		
		System.out.println("Website loaded.");
	}
	
	public static void startServer() {
		try {
			server = new ServerSocket(80);
		} catch (IOException e) {
			System.err.println("Could not start server.");
			System.err.println("Reason: " + e.getMessage());
			System.exit(1);
		}
		System.out.println("Server started.");
	}
	
	public static void findClients() {
		try {
			Socket client = server.accept();
			
			String ip = client.getInetAddress().getHostAddress();
			System.out.println("Client connected: " + ip);
			
			new ClientThread(client);
		} catch (IOException e) {
			System.err.println("Could not accept client.");
			System.err.println("Reason: " + e.getMessage());
		}
	}
	
	public static void main(String[] args) {
		loadWebsite();
		startServer();
		while (true) {
			findClients();
		}
	}
	
	public static class ClientThread extends Thread {
		
		private Socket client;		
		private BufferedWriter writer;
		
		public ClientThread(Socket client) {			
			this.client = client;
			
			try {
				writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
			} catch (IOException e) {
				System.err.println("Could not get I/O from client.");
				System.err.println("Message: " + e.getMessage());
				return;
			}
			
			start();			
		}
		
		@Override
		public void run() {
			try {
				writer.write(webpage);
				writer.newLine();
				writer.close();
				client.close();
			} catch (IOException e) {
				System.err.println("Could not send webpage to" + client.getInetAddress().getHostAddress());
				System.err.println("Message: " + e.getMessage());
			}
			System.out.println("Website sent!");
		}
	}

}
