package edu.escuelaing.arem.project.Handlers;

import java.lang.reflect.Method;

public class StaticMethodHanlder implements Hanlder {
    Method method;

    public StaticMethodHanlder(Method method) {
        this.method = method;
    }

    public String process(Object[] params) throws Exception {
        return method.invoke(null, params).toString();
    }
}
