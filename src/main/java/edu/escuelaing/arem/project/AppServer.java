package edu.escuelaing.arem.project;

import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Method;
import java.net.*;
import java.util.HashMap;
import java.util.List;

import javax.imageio.IIOException;
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
                DinamicResourcesServer(out, pet);
            } else {
                if (pet.matches(".*(.html)"))
                    HtmlServer(out, pet);

                else if (pet.matches(".*(.png)"))
                    ImagesServer(out, clientSocket.getOutputStream(), pet);

                else if (pet.matches(".*(favicon.ico)"))
                    FaviconServer(out, clientSocket.getOutputStream(), pet);

                else
                    HtmlServer(out, "/error.html");

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
            System.out.println(m.invoke(null, new Object[] {}));
            System.out.println(ListURL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static int getPort() {
        if (System.getenv("PORT") != null) {
            return Integer.parseInt(System.getenv("PORT"));
        }
        return 4567; // returns default port if heroku-port isn't set (i.e. on localhost)
    }

    private static void ImagesServer(PrintWriter out, OutputStream outStream, String petition) throws IOException {
        try {
            BufferedImage image = ImageIO.read(new File(System.getProperty("user.dir") + "/resources" + petition));
            out.println("HTTP/1.1 200 OK\r");
            out.println("Content-Type: image/png\r");
            out.println("\r");
            ImageIO.write(image, "PNG", outStream);
        } catch (IIOException ex) {
            HtmlServer(out, "/error.html");
        }

    }

    private static void HtmlServer(PrintWriter out, String petition) throws IOException {
        StringBuffer sb = new StringBuffer();
        try (BufferedReader reader = new BufferedReader(
                new FileReader(System.getProperty("user.dir") + "/resources" + petition))) {
            String infile = null;
            while ((infile = reader.readLine()) != null) {
                sb.append(infile);
            }
            out.println("HTTP/1.1 200 OK\r");
            out.println("Content-Type: text/html\r");
            out.println("");
            out.println(sb.toString());
        } catch (FileNotFoundException ex) {
            HtmlServer(out, "/error.html");
        }

    }

    private static void FaviconServer(PrintWriter out, OutputStream outStream, String petition) throws IOException {
        out.println("HTTP/1.1 200 OK\r");
        out.println("Content-Type: image/vnd.microsoft.icon\r");
        out.println("\r");
        List<BufferedImage> images = ICODecoder
                .read(new File(System.getProperty("user.dir") + "/resources" + petition));
        ICOEncoder.write(images.get(0), outStream);
    }

    private static Object[] extractParams(String pet) throws IndexOutOfBoundsException {
        Object[] params = null;
        if (pet.matches("[/app/]+[a-z]+[?]+[a-z,=,&,0-9]+")) {
            String[] preParams = pet.split("\\?")[1].split("&");
            params = new Object[preParams.length];
            for (int i = 0; i < preParams.length; i++) {
                params[i] = preParams[i].split("=")[1];
            }
        }
        return params;
    }

    private static void DinamicResourcesServer(PrintWriter out, String petition) throws IOException {
        try {
            Object[] params = extractParams(petition);
            if (petition.matches(".+[?]+.+"))
                petition = petition.subSequence(0, petition.indexOf("?")).toString();
            if (ListURL.containsKey(petition)) {

                String result = ListURL.get(petition).process(params);
                out.println("HTTP/1.1 200 OK\r");
                out.println("Content-Type: text/html\r");
                out.println("\r");
                out.println(result);

            } else {
                HtmlServer(out, "/error.html");
            }
        } catch (Exception e) {
            HtmlServer(out, "/error2.html");
        }
    }
}
