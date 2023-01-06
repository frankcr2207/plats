package com.cdm.service1.impl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cdm.domain2.FtpCdg;
import com.cdm.repository2.FtpCdgRepository;
import com.cdm.service1.FtpCdgService;

@Service
public class FtpCdgServiceImpl implements FtpCdgService {
	
	private static final String local = "E://temp_cdg/";
	
	private FTPClient ftpClient;
	
	private FtpCdgRepository ftpCdgRepository;
	
	public FtpCdgServiceImpl(FtpCdgRepository ftpCdgRepository) {
		this.ftpCdgRepository = ftpCdgRepository; 
	}
	
	@Override
	public boolean conectarFTP(String sede) {
        try {
        	ftpClient = new FTPClient();
        	FtpCdg ftpCdg = ftpCdgRepository.findById(sede).get();
        	ftpClient.connect(ftpCdg.getIp(), ftpCdg.getPuerto());
            ftpClient.login(ftpCdg.getUsuario(), ftpCdg.getClave());
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        	ftpClient.changeWorkingDirectory("/");
        	ftpClient.enterLocalPassiveMode();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean cargarArchivo(String archivoFinal) {	
		try {
			FileInputStream fis = new FileInputStream(local + archivoFinal);
            return ftpClient.storeFile(archivoFinal, fis);
		} catch (FileNotFoundException e) {
			return false;
		} catch (IOException e) {
			return false;
		}
	}

}
