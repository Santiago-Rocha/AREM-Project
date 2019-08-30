package edu.escuelaing.arem.project;

import java.io.*;
import java.lang.reflect.Method;
import java.net.*;
import java.util.Arrays;

import edu.escuelaing.arem.project.notation.Web;

public class AppServer {
    public static void listen() throws IOException {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(35000);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 35000.");
            System.exit(1);
        }

        while (true) {
            Socket clientSocket = null;
            try {
                System.out.println("Listo para recibir ...");
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out.close();
            in.close();
        }
    }

    public static void initialize() throws FileNotFoundException {
        try {
            Class cls = Class.forName("edu.escuelaing.arem.project.app.prueba");
            for(Method m : cls.getMethods()){
                if(m.isAnnotationPresent(Web.class)){
                    System.out.println(m.getName());
                }
            }
            Method m = cls.getDeclaredMethod("hola", null);
            System.out.println(m.invoke(null,null));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
