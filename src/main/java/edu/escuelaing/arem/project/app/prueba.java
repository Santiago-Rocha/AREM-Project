package edu.escuelaing.arem.project.app;

import edu.escuelaing.arem.project.notation.Web;

public class prueba {

	/**
	 * Retorna una cadena saludando al usuario
	 * @return Cadena con saludo
	 */
	@Web("prueba")
	public static String hola() {
		return "Hola amigos";
	}

	/**
	 * Metodo que suma dos numeros
	 * @param num1 El primero numero a sumar
	 * @param num2 El segundo numero a sumar
	 * @return resultado de la suma
	 */
	@Web("suma")
	public static String suma(String num1, String num2) {
		return "La suma es "+Integer.toString(Integer.parseInt(num1)+Integer.parseInt(num2));
	}
}
