package edu.escuelaing.arem.project.Handlers;

import java.lang.reflect.Method;

/**
 * @author Santiago Rocha
 */

public class StaticMethodHanlder implements Hanlder {
    Method method;

    /**
     * Constructor de la clase
     * 
     * @param method Representa el metodo que se debe invocar
     */
    public StaticMethodHanlder(Method method) {
        this.method = method;
    }

    @Override
    public String process(Object[] params) throws Exception {
        return method.invoke(null, params).toString();
    }
}
