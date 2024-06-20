package edu.escuelaing.arsw.ASE.app;
import java.io.*;
import java.net.URL;
import java.io.IOException;

public class ejercicio2 {

    public static void main(String[] args) throws IOException{
        URL url = new URL("http://www.google.com/");
        try {
            BufferedReader reader
                    = new BufferedReader(new InputStreamReader(url.openStream()));
            BufferedWriter write
                    = new BufferedWriter(new FileWriter("resultado.html"));


            String inputLine = null;
            while ((inputLine = reader.readLine()) != null) {
                write.write(inputLine);
                write.newLine();
            }
            System.out.println("Contenido guardado en resultado.html");
        }catch (IOException x ) {
                System.err.println("Error al leer de la URL" + x.getMessage());
            }

    }
}