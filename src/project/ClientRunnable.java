package project;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Scanner;
import java.util.TimeZone;

public class ClientRunnable implements Runnable{
    private Socket client_socket;
    private Deck player_hand;
    private int number_of_players;

    public ClientRunnable(Socket client_socket, int number_of_players) {
        this.client_socket = client_socket;
        this.player_hand = new Deck();
        this.number_of_players = number_of_players;
    }

    public void run() {
        writeToSocket(String.format("Hello! There are %d players competing", this.number_of_players));

    }

    public void writeToSocket(String message) {
        try {
            BufferedOutputStream bos = new BufferedOutputStream(this.client_socket.getOutputStream());
            message += "\r\n";
            bos.write(message.getBytes(StandardCharsets.UTF_8));
            bos.flush();
        } catch (IOException e) {
            //
        }
    }

    public void writeHandToSocket() {
        writeToSocket(this.player_hand.toString());
    }

    public void giveCard(Card c) {
        this.player_hand.addCard(c);
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

    private void writePageToOutputStream(File file, String date, FileInputStream fin) {
        System.out.println(date);
        long file_size = file.length();
        String file_type = "text/plain";
        BufferedOutputStream bos = null;

        try {
            bos = new BufferedOutputStream(this.client_socket.getOutputStream());
            file_type = switch (Files.probeContentType(file.toPath())) {
                case "text/html" -> "text/html";
                case "text/gif" -> "text/gif";
                case "text/png" -> "text/png";
                case "text/bmp" -> "text/bmp";
                case "text/jpeg" -> "text/jpeg";
                default -> throw new IllegalStateException("Unexpected value: " + Files.probeContentType(file.toPath()));
            };

            String content_string = String.format("Content-Length: %d\r\n", file_size);
            String content_type = String.format("Content-Type: %s\r\n", file_type);
            bos.write("HTTP/1.0 200 \r\n".getBytes(StandardCharsets.UTF_8));
            bos.write(content_string.getBytes(StandardCharsets.UTF_8));
            bos.write(content_type.getBytes(StandardCharsets.UTF_8));
            bos.write(date.getBytes(StandardCharsets.UTF_8));
            bos.write("\r\n\r\n".getBytes(StandardCharsets.UTF_8));
            bos.flush();

            int nRead = 0, buffer_size = 1024;
            boolean read_all = false;
            byte[] buffer = new byte[buffer_size];

            while (!read_all) {
                nRead = fin.read(buffer);
                if (nRead == -1) {
                    fin.close();
                    read_all = true;
                } else {
                    for (int i = 0; i < nRead; i++) {
                        bos.write(buffer[i]);
                    }
                }
            }
            bos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendResource(String requested_resource) {
        try {
            BufferedOutputStream bos = new BufferedOutputStream(this.client_socket.getOutputStream());
            FileInputStream fin = null;
            File file = null;
            String date = "Date: " + getDate();

            try {
                file = new File("./html" + requested_resource);
                fin = new FileInputStream(file);
                writePageToOutputStream(file, date, fin);
            } catch (FileNotFoundException e) {
                // Send 404
                System.out.println("File not found");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getDate() {
        SimpleDateFormat gmtFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH);
        gmtFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        String gmtString = gmtFormat.format(Calendar.getInstance().getTime());
        return gmtString;
    }
}
