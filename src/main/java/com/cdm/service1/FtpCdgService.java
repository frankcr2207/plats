package com.cdm.service1;

public interface FtpCdgService {
	public boolean conectarFTP(String sede);
	public boolean cargarArchivo(String archivoFinal);
}
