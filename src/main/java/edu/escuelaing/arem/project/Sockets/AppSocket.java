package edu.escuelaing.arem.project.Sockets;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class AppSocket {
    public static Socket StartClientSocket(ServerSocket serverSocket){
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

    public static ServerSocket StartServerSocket(){
        try {
            ServerSocket serverSocket = new ServerSocket(getPort());
            return serverSocket;
        } catch (IOException e) {
            System.err.println("Could not listen on port: 35000.");
            System.exit(1);
            return null;
        }
    }

    private static int getPort() {
        if (System.getenv("PORT") != null) {
            return Integer.parseInt(System.getenv("PORT"));
        }
        return 4567; // returns default port if heroku-port isn't set (i.e. on localhost)
    }
}