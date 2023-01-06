package com.cdm.service2.impl;

import java.io.BufferedOutputStream;
import java.io.File;
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
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import com.cdm.domain2.AuxilioJudicialArchivo;
import com.cdm.domain2.FtpModulo;
import com.cdm.repository2.FtpModuloRepositoryDos;
import com.cdm.service2.FtpModuloServiceDos;

@Service
public class FtpModuloServiceImplDos implements FtpModuloServiceDos {
	
	private FTPClient ftpClient = new FTPClient();
	
	private static final String local = "E://temp_cdg/";
	
	@Autowired
	private FtpModuloRepositoryDos ftpModuloRepository;
	
	@Override
	public boolean conectarFTP(Integer modulo) {
		 try {
			FtpModulo ftpModulo = ftpModuloRepository.getOne(modulo);
	        ftpClient.connect(ftpModulo.getIp(), ftpModulo.getPuerto());
	        ftpClient.login(ftpModulo.getUsuario(), ftpModulo.getClave());
	        ftpClient.enterLocalPassiveMode();
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	/**
	 * Error al descargar -> null
	 * Archivo incompleto -> false
	 * Archivo completo -> true
	 * */
	@SuppressWarnings("resource")
	@Override
	public Boolean descargarArchivo(String carpeta, String nombre) {
		try {
			String archivoServidor = carpeta + "/" + nombre;
	        File archivo = new File(local + nombre);
	        OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(archivo));
	        
	        if(ftpClient.retrieveFile(archivoServidor, outputStream)) {
	        	Path filePath = Paths.get(local + nombre);
	            long fileSize = Files.size(filePath); 
	            if(fileSize == 0)
	            	return false;
	            else
	            	return true;
	        }
	        else
	        	return null;
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
			return null;
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return null;
		}
	}

}
