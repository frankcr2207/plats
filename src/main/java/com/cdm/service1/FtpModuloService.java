package com.cdm.service1;

import org.springframework.web.multipart.MultipartFile;

public interface FtpModuloService {
	public void conectarFTP(Integer modulo);
	public void descargarArchivo(String ruta, String nombre);
	public void cargarArchivo(String nombreFinal, String directorio, MultipartFile archivo);
}
