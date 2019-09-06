package edu.escuelaing.arem.project;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;

import edu.escuelaing.arem.project.Handlers.Hanlder;
import edu.escuelaing.arem.project.Handlers.StaticMethodHanlder;
import edu.escuelaing.arem.project.Sockets.AppSocket;
import edu.escuelaing.arem.project.notation.Web;
import net.sf.image4j.codec.ico.ICODecoder;
import net.sf.image4j.codec.ico.ICOEncoder;

/**
 * @author Santiago Rocha
 */

public class AppServer {
    public static HashMap<String, Hanlder> ListURL = new HashMap<String, Hanlder>();

    /**
     * Este metodo escucha y maneja las peticiones que le llegan al servidor web
     * 
     * @throws IOException
     */
    public static void listen() throws IOException {
        ServerSocket serverSocket = AppSocket.StartServerSocket();

        while (true) {
            Socket clientSocket = AppSocket.StartClientSocket(serverSocket);

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

    /**
     * Este metodo seencarga de inicializar todos los metodos de la clase prueba
     * 
     * @throws FileNotFoundException
     */
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

    /**
     * Este metodo se encarga de resolver las peticiones que solicitan una recurso
     * .png
     * 
     * @param out       Elemento de salida de datos como respuesta a las peticiones
     * @param outStream Elemento de salida de datos como respuesta a las peticiones
     *                  de imagenes
     * @param petition  Peticiones realizada al servidor
     * @throws IOException
     */
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

    /**
     * Este metodo se encarga de resolver las peticiones que solicitan una recurso
     * .html
     * 
     * @param out      Elemento de salida de datos como respuesta a las peticiones
     * @param petition Peticiones realizada al servidor
     * @throws IOException
     */
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
            out.println("\r");
            out.println(sb.toString());
        } catch (FileNotFoundException ex) {
            HtmlServer(out, "/error.html");
        }

    }

    /**
     * Este metodo se encarga de resolver las peticiones del favicon.ico
     * 
     * @param out       Elemento de salida de datos como respuesta a las peticiones
     * @param outStream Elemento de salida de datos como respuesta a las peticiones
     * @param petition  Peticiones realizada al servidor
     * @throws IOException
     */
    private static void FaviconServer(PrintWriter out, OutputStream outStream, String petition) throws IOException {
        out.println("HTTP/1.1 200 OK\r");
        out.println("Content-Type: image/vnd.microsoft.icon\r");
        out.println("\r");
        List<BufferedImage> images = ICODecoder
                .read(new File(System.getProperty("user.dir") + "/resources" + petition));
        ICOEncoder.write(images.get(0), outStream);
    }

    /**
     * Este metodo se encarga de tomar los parametros de la peticion y almacenarlos
     * en un arreglo
     * 
     * @param petition Peticiones realizada al servidor
     * @return Un arreglo de objetos que representa los parametros obtenidos en la
     *         peticion, si retorna null es que no habia parametros
     * @throws IndexOutOfBoundsException
     */
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

    /**
     * l Este metodo se encarga de resolver las peticiones a recursos dinamicos
     * 
     * @param out      Elemento de salida de datos como respuesta a las peticiones
     * @param petition Peticiones realizada al servidor
     * @throws IOException
     */
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
