package project.network;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;
import java.util.Scanner;

@Getter
@Setter
@Builder
public class MyClient {
    private static Socket socket;
    private static ServerSocket serverSocket;

    private int port;

    public static void main(String[] args) {
//        try {
//            Socket s = new Socket("localhost", 6666);
//            DataOutputStream dout = new DataOutputStream(s.getOutputStream());
//            dout.writeUTF("Hello Server");
//            dout.flush();
//            dout.close();
//            s.close();
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//        }

        MyClient client = new MyClient(6666);
        String action = "";

        while (!Objects.equals(action, "stop")) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("-->");
            action = scanner.nextLine();


            switch (action) {
                case "stop" -> client.stopClient();
                case "send" -> client.sendToServer("Test");
                default -> System.out.println("Invalid command");
            }
        }
    }

    public void stopClient() {
        try {
            serverSocket.close();
            System.out.println("client stopped");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendToServer(String message) {
        try {
            socket = new Socket("localhost", port);
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            dataOutputStream.writeUTF(message);
            dataOutputStream.flush();
            dataOutputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
