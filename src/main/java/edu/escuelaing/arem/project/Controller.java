package edu.escuelaing.arem.project;

import java.io.IOException;

/**
 * Hello world!
 *
 */
public class Controller {
    public static void main(String[] args) throws IOException
    {
        AppServer.initialize();
        AppServer.listen();
    }
}
