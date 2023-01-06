package com.cdm.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.VerticalAlignment;


@Controller
public class ControladorUtilitarios {
	
	private static final String UPLOADFILE = "E://temp_utilitarios/";
	
	@RequestMapping(value="/foliarArchivo", method = RequestMethod.POST)
	public @ResponseBody Map<String,Object> enviarRespuestaCDG(HttpServletResponse response, @RequestPart(value="archivo") MultipartFile archivo) throws IOException {
		Map<String,Object> map = new HashMap<String,Object>();

		if(!archivo.isEmpty()) {
			byte[] bytes = archivo.getBytes();
			Path path = Paths.get(UPLOADFILE + "TEMP_" + archivo.getOriginalFilename());
			Files.write(path, bytes);

			PdfDocument pdfDoc = new PdfDocument(new PdfReader(UPLOADFILE + "TEMP_" + archivo.getOriginalFilename()), 
					new PdfWriter(UPLOADFILE + "PLATS_" + archivo.getOriginalFilename()));
		    Document doc = new Document(pdfDoc);

		        int numberOfPages = pdfDoc.getNumberOfPages();
		        for (int i = 1; i <= numberOfPages; i++) {

		            // Write aligned text to the specified by parameters point
		        	doc.setFontSize(12);
		        	doc.setBold();
		            doc.showTextAligned(new Paragraph(String.format("%s de %s", i, numberOfPages)),
		                    559, 806, i, TextAlignment.RIGHT, VerticalAlignment.TOP, 0);
		        }

		        doc.close();
		}

		return map;
	}
}
