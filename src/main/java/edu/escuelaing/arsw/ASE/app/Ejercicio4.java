package edu.escuelaing.arsw.ASE.app;

import java.io.*;
import java.net.*;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.function.DoubleUnaryOperator;

public class Ejercicio4 {
    private static final int PORT = 45000;
    private static final int MAX_ATTEMPTS = 5;
    private static final SecureRandom random = new SecureRandom();
    private static final Map<String, DoubleUnaryOperator> FUNCTIONS = new HashMap<>();

    static {
        FUNCTIONS.put("sin", Math::sin);
        FUNCTIONS.put("cos", Math::cos);
        FUNCTIONS.put("tan", Math::tan);
    }

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server listening on port " + PORT + "...");
            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                     BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                     PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {
                    
                    System.out.println("Accepted connection from client: " + clientSocket.getInetAddress());
                    handleClientConnection(in, out);
                } catch (IOException e) {
                    System.err.println("Error handling client connection: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + PORT);
            System.exit(1);
        }
    }

    private static void handleClientConnection(BufferedReader in, PrintWriter out) throws IOException {
        String currentFunction = "cos";
        int attempts = 0;
        String inputLine;

        while ((inputLine = in.readLine()) != null && attempts < MAX_ATTEMPTS) {
            if (inputLine.equalsIgnoreCase("Bye")) {
                out.println("Goodbye!");
                break;
            }

            if (inputLine.startsWith("fun:")) {
                currentFunction = handleFunctionChange(inputLine.substring(4).trim(), out);
                attempts = 0;
            } else {
                try {
                    Double result = calculateResult(inputLine, currentFunction);
                    out.println("Respuesta: " + formatResult(result));
                    attempts = 0;
                } catch (IllegalArgumentException e) {
                    out.println("Error: " + e.getMessage());
                    attempts++;
                }
            }

            if (attempts >= MAX_ATTEMPTS) {
                out.println("Too many invalid attempts. Closing connection.");
                break;
            }

            addDelay();
        }
    }

    private static String handleFunctionChange(String newFunction, PrintWriter out) {
        if (FUNCTIONS.containsKey(newFunction)) {
            out.println("Función cambiada a: " + newFunction);
            return newFunction;
        } else {
            out.println("Función desconocida: " + newFunction + ". Manteniendo función actual.");
            return "cos";
        }
    }

    private static Double calculateResult(String input, String function) {
        Double number = parseInput(input);
        if (number == null) {
            throw new IllegalArgumentException("Entrada no válida: " + input);
        }
        return FUNCTIONS.get(function).applyAsDouble(number);
    }

    private static Double parseInput(String input) {
        input = input.trim().toLowerCase().replace("π", String.valueOf(Math.PI));

        try {
            if (input.contains("/")) {
                String[] parts = input.split("/");
                if (parts.length != 2) throw new IllegalArgumentException("Invalid division format");
                return Double.parseDouble(parts[0]) / Double.parseDouble(parts[1]);
            } else if (input.contains("*")) {
                String[] parts = input.split("\\*");
                if (parts.length != 2) throw new IllegalArgumentException("Invalid multiplication format");
                return Double.parseDouble(parts[0]) * Double.parseDouble(parts[1]);
            } else {
                return Double.parseDouble(input);
            }
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static String formatResult(Double result) {
        return Math.abs(result) < 1e-10 ? "0.0" : String.format("%.10f", result);
    }

    private static void addDelay() {
        try {
            Thread.sleep(random.nextInt(100) + 50);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}