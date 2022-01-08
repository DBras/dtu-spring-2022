package project;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class ClientRunnable implements Runnable{
    private Socket client_socket;

    public ClientRunnable(Socket client_socket) {
        this.client_socket = client_socket;
    }

    public void run() {
        String requested_resouce = getRequest();
        if (requested_resouce.equals("/LUK")) {
            System.exit(0);
        }
        else if (requested_resouce.equals("/")) {
            requested_resouce = "/index.html";
        }
    }

    private String getRequest() {
        try {
            Scanner server_in = new Scanner(this.client_socket.getInputStream());
            String text_line = "", first_line = server_in.nextLine();

            do {
                text_line = server_in.nextLine();
            } while(!text_line.equals(""));

            String requested_resource = first_line.split(" ")[1];
            return requested_resource;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
