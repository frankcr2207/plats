package com.cdm.service2;

public interface FtpModuloServiceDos {
	public boolean conectarFTP(Integer modulo);
	public Boolean descargarArchivo(String ruta, String nombre);
}
