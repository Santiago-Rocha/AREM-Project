package edu.escuelaing.arem.project.app;

import edu.escuelaing.arem.project.notation.Web;

public class prueba {

	@Web("prueba")
	public static String hola() {
		return "Hola amigos";
	}

	@Web("suma")
	public static String suma(String num1, String num2) {
		return Integer.toString(Integer.parseInt(num1)+Integer.parseInt(num2));
	}
}
