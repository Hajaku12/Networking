package edu.escuelaing.arsw.ASE.app;

import java.net.*;
import java.io.*;
import java.nio.file.*;
import java.util.Base64;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Ejercicio5 {
    private static final int PORT = 45000;
    private static final String BASE_PATH = "src/main/resources/";
    private static final String DEFAULT_FILE = BASE_PATH + "index.html";
    private static final Logger LOGGER = Logger.getLogger(Ejercicio5.class.getName());
    private static final int MAX_THREADS = 10;

    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(MAX_THREADS);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            LOGGER.info("Server started on port " + PORT);

            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    executor.execute(() -> handleClient(clientSocket));
                } catch (IOException e) {
                    LOGGER.log(Level.SEVERE, "Error accepting client connection", e);
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Could not listen on port: " + PORT, e);
            System.exit(1);
        } finally {
            executor.shutdown();
        }
    }

    private static void handleClient(Socket clientSocket) {
        try (
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
        ) {
            String request = readRequest(in);
            String requestPath = extractRequestPath(request);
            LOGGER.info("Received request: " + requestPath);

            if (requestPath.startsWith("/search")) {
                handleSearchRequest(out, requestPath);
            } else {
                serveDefaultPage(out);
            }
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Error handling client request", e);
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "Error closing client socket", e);
            }
        }
    }

    private static String readRequest(BufferedReader in) throws IOException {
        StringBuilder request = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null && !line.isEmpty()) {
            request.append(line).append("\n");
        }
        return request.toString();
    }

    private static String extractRequestPath(String request) {
        String[] requestLines = request.split("\n");
        String[] requestParts = requestLines[0].split(" ");
        return requestParts.length > 1 ? requestParts[1] : "/";
    }

    private static void handleSearchRequest(PrintWriter out, String requestPath) throws IOException {
        String fileName = requestPath.split("=")[1];
        String filePath = BASE_PATH + fileName;

        if (isImageFile(fileName)) {
            serveImage(out, filePath);
        } else {
            serveTextFile(out, filePath);
        }
    }

    private static boolean isImageFile(String fileName) {
        return fileName.endsWith(".png") || fileName.endsWith(".jpg") || fileName.endsWith(".jpeg");
    }

    private static void serveImage(PrintWriter out, String filePath) throws IOException {
        byte[] imageData = Files.readAllBytes(Paths.get(filePath));
        String base64Image = Base64.getEncoder().encodeToString(imageData);

        String htmlResponse = "<!DOCTYPE html>\r\n"
                + "<html>\r\n"
                + "    <head>\r\n"
                + "        <title>Image</title>\r\n"
                + "    </head>\r\n"
                + "    <body>\r\n"
                + "         <center><img src=\"data:image/jpeg;base64," + base64Image + "\" alt=\"image\"></center>\r\n"
                + "    </body>\r\n"
                + "</html>";

        sendResponse(out, "text/html", htmlResponse);
    }

    private static void serveTextFile(PrintWriter out, String filePath) throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(filePath)));
        sendResponse(out, getContentType(filePath), content);
    }

    private static void serveDefaultPage(PrintWriter out) throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(DEFAULT_FILE)));
        sendResponse(out, getContentType(DEFAULT_FILE), content);
    }

    private static void sendResponse(PrintWriter out, String contentType, String content) {
        out.println("HTTP/1.1 200 OK");
        out.println("Content-Type: " + contentType);
        out.println();
        out.println(content);
    }

    private static String getContentType(String fileName) {
        if (fileName.endsWith(".html")) return "text/html";
        if (fileName.endsWith(".js")) return "application/javascript";
        if (fileName.endsWith(".css")) return "text/css";
        if (fileName.endsWith(".txt")) return "text/plain";
        if (fileName.endsWith(".png")) return "image/png";
        if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) return "image/jpeg";
        return "text/plain";
    }
}