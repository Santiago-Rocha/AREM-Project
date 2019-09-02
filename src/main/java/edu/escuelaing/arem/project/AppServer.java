package edu.escuelaing.arem.project;

import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Method;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

import javax.imageio.ImageIO;

import edu.escuelaing.arem.project.notation.Web;
import net.sf.image4j.codec.ico.ICODecoder;
import net.sf.image4j.codec.ico.ICOEncoder;

public class AppServer {
    public static HashMap<String, Hanlder> ListURL = new HashMap<String, Hanlder>();

    public static void listen() throws IOException {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(getPort());
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
            while ((inputLine = in.readLine()) != null) {
                if (inputLine.matches("(GET)+.*"))
                    pet = inputLine.split(" ")[1];
                if (!in.ready())
                    break;
            }
            System.out.println(pet);
            pet = pet == null ? "/error.html" : pet;
            pet = pet.equals("/") ? "/index.html" : pet;
            if (pet.matches("(/app/).*")) {
                if (ListURL.containsKey(pet)) {
                    out.println("HTTP/1.1 200 OK");
                    out.println("Content-Type: text/html");
                    out.println();
                    out.println(ListURL.get(pet).process());
                }
            } else {
                if (pet.matches(".*(.html)")) {
                    StringBuffer sb = new StringBuffer();
                    System.out.println(pet);
                    try (BufferedReader reader = new BufferedReader(
                            new FileReader(System.getProperty("user.dir") + pet))) {
                        String infile = null;
                        while ((infile = reader.readLine()) != null) {
                            sb.append(infile);
                        }
                    }
                    out.println("HTTP/1.1 200 OK\r");
                    out.println("Content-Type: text/html\r");
                    out.println("\r");
                    out.println(sb.toString());
                } else if (pet.matches(".*(.png)")) {
                    out.println("HTTP/1.1 200 OK\r");
                    out.println("Content-Type: image/png\r");
                    out.println("\r");
                    BufferedImage image = ImageIO.read(new File(System.getProperty("user.dir") + pet));
                    ImageIO.write(image, "PNG", clientSocket.getOutputStream());
                } else if (pet.matches(".*(favicon.ico)")) {
                    StringBuffer sb = new StringBuffer();
                    System.out.println(pet);
                    try (BufferedReader reader = new BufferedReader(
                            new FileReader(System.getProperty("user.dir") + "/error.html"))) {
                        String infile = null;
                        while ((infile = reader.readLine()) != null) {
                            sb.append(infile);
                        }
                    }
                    out.println("HTTP/1.1 200 OK\r");
                    out.println("Content-Type: text/html\r");
                    out.println("\r");
                    out.println(sb.toString());
                }
                else {
                    StringBuffer sb = new StringBuffer();
                    System.out.println(pet);
                    try (BufferedReader reader = new BufferedReader(
                            new FileReader(System.getProperty("user.dir") + "/error.html"))) {
                        String infile = null;
                        while ((infile = reader.readLine()) != null) {
                            sb.append(infile);
                        }
                    }
                    out.println("HTTP/1.1 200 OK\r");
                    out.println("Content-Type: text/html\r");
                    out.println("\r");
                    out.println(sb.toString());
                }
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
                    Hanlder handler = new StaticMethodHanlder(m);
                    ListURL.put("/app/" + m.getAnnotation(Web.class).value(), handler);
                }
            }
            Method m = cls.getDeclaredMethod("hola", null);
            System.out.println(m.invoke(null, null));
            System.out.println(ListURL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static int getPort() {
        if (System.getenv("PORT") != null) {
            return Integer.parseInt(System.getenv("PORT"));
        }
        return 4567; // returns default port if heroku-port isn't set (i.e. on localhost)
    }
}
