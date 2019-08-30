package edu.escuelaing.arem.project;

import java.io.*;
import java.lang.reflect.Method;
import java.net.*;
import java.util.HashMap;

import edu.escuelaing.arem.project.notation.Web;

public class AppServer {
    public static HashMap<String, Hanlder> ListURL = new HashMap<String, Hanlder>();

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
            String inputLine = null, pet = null;
            while((inputLine = in.readLine()) != null){
                if(inputLine.matches("(GET)+.*")) pet = inputLine.split(" ")[1];
                if (!in.ready()) break;
            }
            if(ListURL.containsKey(pet)){
                out.println("HTTP/1.1 200 OK");
				out.println("Content-Type: text/html");
				out.println();
				out.println(ListURL.get(pet).process());
            }
            out.close();
            in.close();
        }
    }

    public static void initialize() throws FileNotFoundException {
        try {
            Class cls = Class.forName("edu.escuelaing.arem.project.app.prueba");
            for (Method m : cls.getMethods()) {
                if (m.isAnnotationPresent(Web.class)) {
                    Hanlder handler =  new StaticMethodHanlder(m);
                    ListURL.put("/app/"+m.getAnnotation(Web.class).value(), handler);
                }
            }
            Method m = cls.getDeclaredMethod("hola", null);
            System.out.println(m.invoke(null,null));
            System.out.println(ListURL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
