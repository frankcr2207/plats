package com.cdm.utils;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.Random;

import javax.imageio.ImageIO;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.cdm.entities.Correo;
import com.cdm.entities.Persona;
import com.cdm.entities.ValueCriteria;
import com.cdm.utils.SmtpMailSender;

import sun.misc.BASE64Decoder;

@RestController
@CrossOrigin(origins = "*", methods= {RequestMethod.GET,RequestMethod.POST})
public class WebServiceCaptcha {
	
	@Autowired
    private SmtpMailSender smtpMailSender;

	@RequestMapping(value = "/verifyReCaptcha", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public synchronized boolean verifyReCaptcha(@RequestBody ValueCriteria vc) {
	    boolean flag = true; 
		try {
	    	//System.out.println(vc.getId());
	    	String secretKey="6LcxObcaAAAAAMhrA7GAUFeOLj1UDq78Py-5_VD2";
	    	String url = "https://www.google.com/recaptcha/api/siteverify",
	                params = "secret=" + secretKey + "&response=" + vc.getId();

	        HttpURLConnection http = (HttpURLConnection) new URL(url).openConnection();
	        http.setDoOutput(true);
	        http.setRequestMethod("POST");
	        http.setRequestProperty("Content-Type",
	                "application/x-www-form-urlencoded; charset=UTF-8");
	        OutputStream out = http.getOutputStream();
	        out.write(params.getBytes("UTF-8"));
	        out.flush();
	        out.close();

	        InputStream res = http.getInputStream();
	        BufferedReader rd = new BufferedReader(new InputStreamReader(res, "UTF-8"));

	        StringBuilder sb = new StringBuilder();
	        int cp;
	        while ((cp = rd.read()) != -1) {
	            sb.append((char) cp);
	        }
	        JSONObject json = new JSONObject(sb.toString());
	        res.close();
	        String estado = json.get("success").toString();
	        System.out.println(json.toString());
	        if(estado.equals("false"))
	        	flag = false;
	        System.out.println(flag);

	    } catch (Exception e) {
	        e.getMessage();
	    }
	    return flag;
	}
	
	@RequestMapping(value = "/verifyReCaptcha2", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public synchronized boolean verifyReCaptcha2(@RequestBody ValueCriteria vc) {
	    boolean flag = true; 
		try {
	    	//System.out.println(vc.getId());
	    	String secretKey="6LeYObcaAAAAAOo_SICWVx_hCY6Xt6YTqvfASN4H";
	    	String url = "https://www.google.com/recaptcha/api/siteverify",
	                params = "secret=" + secretKey + "&response=" + vc.getId();

	        HttpURLConnection http = (HttpURLConnection) new URL(url).openConnection();
	        http.setDoOutput(true);
	        http.setRequestMethod("POST");
	        http.setRequestProperty("Content-Type",
	                "application/x-www-form-urlencoded; charset=UTF-8");
	        OutputStream out = http.getOutputStream();
	        out.write(params.getBytes("UTF-8"));
	        out.flush();
	        out.close();

	        InputStream res = http.getInputStream();
	        BufferedReader rd = new BufferedReader(new InputStreamReader(res, "UTF-8"));

	        StringBuilder sb = new StringBuilder();
	        int cp;
	        while ((cp = rd.read()) != -1) {
	            sb.append((char) cp);
	        }
	        JSONObject json = new JSONObject(sb.toString());
	        res.close();
	        String estado = json.get("success").toString();
	        System.out.println(json.toString());
	        if(estado.equals("false"))
	        	flag = false;
	        System.out.println(flag);

	    } catch (Exception e) {
	        e.getMessage();
	    }
	    return flag;
	}
	
	@RequestMapping(value = "/enviaNotificacion", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public boolean enviaNotificacion(@RequestBody Correo correo) {
	    boolean flag = false; 
	    int a = 1;
		try {
        	if(smtpMailSender.sendNotificacion(correo))
        		flag = true;
        	else
        		flag= false;
        		
	    } catch (Exception e) {
	        flag = false;
	    }
	    return flag;
	}
	
	
}
