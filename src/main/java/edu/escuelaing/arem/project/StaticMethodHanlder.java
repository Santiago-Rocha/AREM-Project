package edu.escuelaing.arem.project;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class StaticMethodHanlder implements Hanlder {
    Method method;

    public StaticMethodHanlder(Method method) {
        this.method = method;
    }

    public String process() {
        try {
            return method.invoke(null, null).toString();
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }
}
