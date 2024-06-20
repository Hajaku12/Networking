package edu.escuelaing.arsw.ASE.app;

import java.net.MalformedURLException;
import java.net.URL;

public class ejercicio1 {
    public static void main ( String[] args)
    {
        try{
            URL site = new URL ("http://www.google.com/");
            System.out.println( "Protocol" + site.getProtocol() );
            System.out.println( "Authority" + site.getAuthority() );
            System.out.println( "Host" + site.getHost());
            System.out.println( "Port" + site.getPort());
            System.out.println( "Path" + site.getPath());
            System.out.println( "Query" + site.getQuery());
            System.out.println( "File" + site.getFile());
            System.out.println( "Ref" + site.getRef());

        }
        catch (MalformedURLException exception){
            exception.printStackTrace();
        }
    }

}

