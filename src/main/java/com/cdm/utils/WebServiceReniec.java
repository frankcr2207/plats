package com.cdm.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class WebServiceReniec {
	public static String peticionHttpGet(String urlParaVisitar) throws Exception {
		// Esto es lo que vamos a devolver
		StringBuilder resultado = new StringBuilder();
		//System.setProperty("http.proxyHost", "172.28.206.35");
		//System.setProperty("http.proxyPort", "3128");
		// Crear un objeto de tipo URL
		URL url = new URL(urlParaVisitar);

		// Abrir la conexión e indicar que será de tipo GET
		HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
		conexion.setRequestProperty("Content-Type", "charset=utf-8");
		conexion.setRequestMethod("GET");
		// Búferes para leer
		BufferedReader rd = new BufferedReader(new InputStreamReader(conexion.getInputStream(),"UTF-8"));
		String linea;
		// Mientras el BufferedReader se pueda leer, agregar contenido a resultado
		while ((linea = rd.readLine()) != null) {
			resultado.append(linea);
		}
		// Cerrar el BufferedReader
		rd.close();
		// Regresar resultado, pero como cadena, no como StringBuilder
		return resultado.toString();
	}
    
    public static String[] obtenerDatosReniec(String dni){

        String[] array = new String[30];
        try {
        	//String url = "http://172.19.9.5/csjar/WebServiceKn.php?Id=" + dni;
			//String url = "https://csjarequipa.pj.gob.pe/csjar/WebServiceKn.php?Id=" + dni;
			String url = "http://172.28.3.11/csjar/wsreniec.php?dni=" + dni;
			String respuesta = "";
			respuesta = peticionHttpGet(url);
			
			int fin = respuesta.length(); 
			String sCadena = respuesta.substring(5,fin);
			array = sCadena.split("',");
			
			for (int i = 0; i < array.length; i++) {
				
				String letra = array[i].trim();
				String[] array3 = letra.split(":");
				String ultima = array3[array3.length-1];
				array[i] = ultima.substring(2,ultima.length());
			}
			respuesta="";
			for (int i = 17; i < 25; i++) {
				if (array[i].isEmpty()) {}
				else
					respuesta = respuesta + " " +array[i];
			}

		}
		catch(Exception e){

		}
        return array;
    }
}
