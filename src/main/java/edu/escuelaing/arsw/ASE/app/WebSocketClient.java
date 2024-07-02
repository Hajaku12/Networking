package edu.escuelaing.arsw.ASE.app;

import java.io.*;
import java.net.*;

public class WebSocketClient {
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 45000;

    public static void main(String[] args) {
        try (
            Socket socket = new Socket(HOST, PORT);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in))
        ) {
            System.out.println("Connected to server. Enter numbers (or 'exit' to quit):");
            String userInput;

            while ((userInput = stdIn.readLine()) != null) {
                if ("exit".equalsIgnoreCase(userInput)) {
                    break;
                }

                out.println(userInput);
                String serverResponse = in.readLine();
                System.out.println("Server response: " + serverResponse);
            }
        } catch (UnknownHostException e) {
            System.err.println("Unknown host: " + HOST);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to: " + HOST);
            System.exit(1);
        }
    }
}