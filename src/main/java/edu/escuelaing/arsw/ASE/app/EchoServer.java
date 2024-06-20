package edu.escuelaing.arsw.ASE.app;

import java.net.*;
import java.io.*;

import static java.lang.Math.*;

public class EchoServer {
    public static void main(String[] args) throws IOException {

        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(35000);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 35000.");
            System.exit(1);
        }

        Socket clientSocket = null;
        try {
            clientSocket = serverSocket.accept();
        } catch (IOException e) {
            System.err.println("Accept failed.");
            System.exit(1);
        }
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(
                clientSocket.getInputStream()));
        String inputLine, outputLine;
        String check = "cos";
        while ((inputLine = in.readLine()) != null) {
            if (inputLine.equals("fun:sin")){
                check = "sin";
            }
            else if(inputLine.equals("fun:tan")){
                check = "tan";
            }else{
                double temp = 0;
                if (check.equals("cos") || inputLine.contains("π")){
                    try {
                        String replace = inputLine.replace("π",String.valueOf(Math.PI));
                        temp = Double.parseDouble(replace);
                        System.out.println("Coseno: " + cos(temp));
                    } catch (NumberFormatException e) {
                        e.getStackTrace();
                    }
                }else if(check.equals("tan")){
                    try {
                        String replace = inputLine.replace("π",String.valueOf(Math.PI));
                        temp = Double.parseDouble(replace);
                        System.out.println("Tangente: " + tan(temp));
                    } catch (NumberFormatException e) {
                        e.getStackTrace();
                    }
                }else if(check.equals("sin")){
                    try {
                        String replace = inputLine.replace("π",String.valueOf(Math.PI));
                        temp = Double.parseDouble(replace);
                        System.out.println("Seno: " + sin(temp));
                    } catch (NumberFormatException e) {
                        e.getStackTrace();
                    }
                }
            }
            System.out.println("Mensaje:" + inputLine);
            outputLine = "Respuesta" + inputLine ;
            out.println(outputLine);
            System.out.println("Check " + outputLine);
            if (outputLine.equals("Respuestas: Bye."))
                break;
        }
        out.close();
        in.close();
        clientSocket.close();
        serverSocket.close();
    }

    /**
     * Calcular el coseno
     * @param num
     * @return
     */
    public static double cos (double num){
        return Math.cos(num);
    }


    /**
     * Calcular el seno
     * @param num
     * @return
     */
    public static double sin(double num){
        return Math.sin(num);
    }
    /**
     * Calcular el tangente
     * @param num
     * @return
     */
    public static double tan (double num){
        return Math.tan(num);
    }
}
