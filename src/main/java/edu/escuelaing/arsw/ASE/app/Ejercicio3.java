package edu.escuelaing.arsw.ASE.app;

import java.io.*;
import java.net.*;
import java.security.SecureRandom;

public class Ejercicio3 {
    private static final int PORT = 45000;
    private static final int MAX_ATTEMPTS = 5;
    private static final SecureRandom random = new SecureRandom();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started on port " + PORT);
            
            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                     PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                     BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
                    
                    System.out.println("New client connected: " + clientSocket.getInetAddress());
                    processClientRequests(in, out);
                } catch (IOException e) {
                    System.err.println("Error handling client connection: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + PORT);
            System.exit(1);
        }
    }

    private static void processClientRequests(BufferedReader in, PrintWriter out) throws IOException {
        String inputLine;
        int attempts = 0;

        while ((inputLine = in.readLine()) != null && attempts < MAX_ATTEMPTS) {
            try {
                double number = validateAndParseInput(inputLine);
                double result = Math.pow(number, 2);
                out.println("Respuesta " + String.format("%.2f", result));
                attempts = 0; // Reset attempts on successful processing
            } catch (NumberFormatException e) {
                out.println("Error: Invalid input. Please enter a valid number.");
                attempts++;
            } catch (IllegalArgumentException e) {
                out.println("Error: " + e.getMessage());
                attempts++;
            }

            if (attempts >= MAX_ATTEMPTS) {
                out.println("Too many invalid attempts. Closing connection.");
                break;
            }
        }
    }

    private static double validateAndParseInput(String input) {
        if (input == null || input.trim().isEmpty()) {
            throw new IllegalArgumentException("Input cannot be empty.");
        }

        double number = Double.parseDouble(input);

        if (Double.isInfinite(number) || Double.isNaN(number)) {
            throw new IllegalArgumentException("Input is not a valid number.");
        }

        // Add a small delay to prevent rapid-fire requests
        try {
            Thread.sleep(random.nextInt(100) + 50);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return number;
    }
}