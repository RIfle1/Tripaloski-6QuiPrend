package project.network;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


@Setter
@Getter
public class MyServer {
    private Socket socket;
    private ServerSocket serverSocket;
    private int port;

    public MyServer(int port) {
        this.port = port;
    }

    public void startServer() {
        try {
            System.out.println("Starting server...");
            serverSocket = new ServerSocket(port);
            System.out.println("Server started");
        } catch (IOException e) {
            System.out.println("Error starting server / Port already in use");
        }
    }

    public void stopServer() {
        try {
            serverSocket.close();
            System.out.println("Server stopped");
        } catch (IOException e) {
            System.out.println("Error stopping server / Server does not exist");
        }
    }

    public String retrieve() {
        try {
            socket = serverSocket.accept();
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            String message = dataInputStream.readUTF();
            System.out.println("Received From (" + socket.getInetAddress().getHostAddress() + ") :" + message);

            return message;
        } catch (IOException e) {
            return "Error retrieving data from client";
        }
    }

    public void send(String message, String ip, int port) {
        try {
            System.out.println("Sending: " + message + " to " + ip + ":" + port);
            socket = new Socket(ip, port);
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            dataOutputStream.writeUTF(message);
            dataOutputStream.flush();
            dataOutputStream.close();
        } catch (IOException e) {
            System.out.println("Error sending data to client");
        }
    }

}