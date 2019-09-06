package edu.escuelaing.arem.project;

import java.io.IOException;

/**
 * @author Santiago Rocha
 */

public class Controller {
    public static void main(String[] args) throws IOException {
        AppServer.initialize();
        AppServer.listen();
    }
}
