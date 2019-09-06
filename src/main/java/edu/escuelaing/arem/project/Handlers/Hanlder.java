package edu.escuelaing.arem.project.Handlers;

/**
 * @author Santiago Rocha
 */

public interface Hanlder {
    /**
     * Este metodo se encarga de invocar un metodo con sus parametros
     * 
     * @param params parametros que el metodo utiliza
     * @return La respuesta del metodo
     * @throws Exception
     */
    public String process(Object[] params) throws Exception;
}
