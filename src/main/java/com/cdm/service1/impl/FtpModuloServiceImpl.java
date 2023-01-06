package com.cdm.service1.impl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.cdm.domain.FtpModulo;
import com.cdm.repository.FtpModuloRepository;
import com.cdm.service1.FtpModuloService;

@Service
public class FtpModuloServiceImpl implements FtpModuloService {
	
	private FTPClient ftpClient = new FTPClient();
	
	private static final String local = "E://temp_cdg/";
	
	@Autowired
	private FtpModuloRepository ftpModuloRepository;
	
	@Override
	public void conectarFTP(Integer modulo) {
		 try {
			FtpModulo ftpModulo = ftpModuloRepository.findById(modulo).get();
	        ftpClient.connect(ftpModulo.getIp(), ftpModulo.getPuerto());
	        ftpClient.login(ftpModulo.getUsuario(), ftpModulo.getClave());
	        ftpClient.enterLocalPassiveMode();
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
		} catch (IOException e) {
			throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "No se pudo conectar al servidor FTP", null);
		}
	}
	
	/**
	 * Error al descargar -> null
	 * Archivo incompleto -> false
	 * Archivo completo -> true
	 * */
	@SuppressWarnings("resource")
	@Override
	public void descargarArchivo(String carpeta, String nombre) {
		try {
			String archivoServidor = carpeta + "/" + nombre;
	        File archivo = new File(local + nombre);
	        OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(archivo));
	        
	        if(ftpClient.retrieveFile(archivoServidor, outputStream)) {
	        	Path filePath = Paths.get(local + nombre);
	            long fileSize = Files.size(filePath); 
	            if(fileSize == 0)
	            	throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Archivo no fue cargado correctamente por el usuario.", null);
	        }
	        else
	        	throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "No se pudo descargar el archivo del FTP.", null);
		} catch (FileNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "No se pudo descargar el archivo del FTP.", null);
		} catch (IOException e) {
			throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "No se pudo descargar el archivo del FTP.", null);
		}
	}
	
	@Override
	public void cargarArchivo(String nombreFinal, String directorio, MultipartFile archivo) {	
		try {
			ftpClient.changeWorkingDirectory(directorio);
            ftpClient.storeFile(nombreFinal, archivo.getInputStream());
		} catch (FileNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "No se pudo enviar el archivo al FTP.", null);
		} catch (IOException e) {
			throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "No se pudo enviar el archivo al FTP.", null);
		}
	}

}
