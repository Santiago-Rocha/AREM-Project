package edu.escuelaing.arem.project.Sockets;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author Santiago Rocha
 */

public class AppSocket {
    /**
     * Metodo que se encarga de iniciar el socket del cliente escuchando un socket servidor 
     * @param serverSocket Socket del servidor al que va a escuchar;
     * @return Socket del cliente prendido
     */
    public static Socket StartClientSocket(ServerSocket serverSocket) {
        try {
            Socket clientSocket = serverSocket.accept();
            System.out.println("Listo para recibir ...");
            return clientSocket;
        } catch (IOException e) {
            System.err.println("Accept failed.");
            System.exit(1);
            return null;
        }
    }

    /**
     * Metodo que se encarga de iniciar el socket del servidor
     * @return Socket del servidor prendido
     */
    public static ServerSocket StartServerSocket() {
        try {
            ServerSocket serverSocket = new ServerSocket(getPort());
            return serverSocket;
        } catch (IOException e) {
            System.err.println("Could not listen on port: 35000.");
            System.exit(1);
            return null;
        }
    }

    /**
     * Este metodo devuelve un puerto por el cual escuchara el servidor
     * @return El puerto de despliegue o 4567 si es local
     */
    private static int getPort() {
        if (System.getenv("PORT") != null) {
            return Integer.parseInt(System.getenv("PORT"));
        }
        return 4567;
    }
}