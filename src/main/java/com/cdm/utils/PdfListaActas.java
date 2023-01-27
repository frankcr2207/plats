package com.cdm.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cdm.domain.vo.RequestActaVO;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class PdfListaActas {
	
	private static Logger logger = LoggerFactory.getLogger(PdfListaActas.class);
	
	public static final String IMG = "./src/main/resources/static/img/logoCSJAR2.png";

	public static ByteArrayInputStream pdfListaActas(String usuario, LocalDateTime inicio, LocalDateTime fin, List<RequestActaVO> requestActaVOS) throws MalformedURLException, IOException {
		Document document = new Document();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		
		try {
			
			PdfWriter.getInstance(document, out);
			document.open();
			
			Image img = Image.getInstance(IMG);
			img.scalePercent(50);
		    document.add(img);
		    document.add(Chunk.NEWLINE);
		    
			// Add Text to PDF file ->
			Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.BLACK);
			Paragraph titulo = new Paragraph("LISTA DE ACTAS", fontTitle);
			titulo.setAlignment(Element.ALIGN_CENTER);
			document.add(titulo);
			document.add(Chunk.NEWLINE);
			
			titulo = new Paragraph("Esp. de audio: ".concat(usuario), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, BaseColor.BLACK));
			titulo.setAlignment(Element.ALIGN_LEFT);
			document.add(titulo);
			document.add(Chunk.NEWLINE);
			
			titulo = new Paragraph("Desde: ".concat(inicio.format(formatter)).concat(" hasta ").concat(fin.format(formatter)), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, BaseColor.BLACK));
			titulo.setAlignment(Element.ALIGN_LEFT);
			document.add(titulo);
			document.add(Chunk.NEWLINE);
			
			PdfPTable table = new PdfPTable(6);
			table.setWidthPercentage(100);
			table.setWidths(new int[]{2,10,10,20,30,8});
			// Add PDF Table Header ->
			Stream.of("#", "F. AUDIENCIA", "F CREACIÓN", "EXPEDIENTE", "INSTANCIA", "ESTADO").forEach(headerTitle ->           {
				PdfPCell header = new PdfPCell();
				header.setBackgroundColor(BaseColor.LIGHT_GRAY);
				header.setHorizontalAlignment(Element.ALIGN_CENTER);
				header.setBorderWidth(1);
				header.setPhrase(new Phrase(headerTitle,  FontFactory.getFont(FontFactory.HELVETICA_BOLD, 6, BaseColor.BLACK)));
				table.addCell(header);
			});
			
			Font font = FontFactory.getFont(FontFactory.HELVETICA, 6, BaseColor.BLACK);
			
			for (RequestActaVO requestActaVO : requestActaVOS) {
				
				PdfPCell cellOrden = new PdfPCell(new Phrase(requestActaVO.getOrden().toString(), font));
				cellOrden.setPaddingLeft(4);
				cellOrden.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cellOrden.setHorizontalAlignment(Element.ALIGN_CENTER);
				table.addCell(cellOrden);
				
				PdfPCell cellFecAudiencia = new PdfPCell(new Phrase(requestActaVO.getFecAudiencia().toString(), font));
				cellFecAudiencia.setPaddingLeft(4);
				cellFecAudiencia.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cellFecAudiencia.setHorizontalAlignment(Element.ALIGN_CENTER);
				table.addCell(cellFecAudiencia);
				
				PdfPCell cellFecCreacion = new PdfPCell(new Phrase(requestActaVO.getFecCreacion(), font));
				cellFecCreacion.setPaddingLeft(4);
				cellFecCreacion.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cellFecCreacion.setHorizontalAlignment(Element.ALIGN_CENTER);
				table.addCell(cellFecCreacion);
				
				PdfPCell cellExpediente = new PdfPCell(new Phrase(requestActaVO.getExpediente(), font));
				cellExpediente.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cellExpediente.setHorizontalAlignment(Element.ALIGN_CENTER);
				cellExpediente.setPaddingRight(4);
				table.addCell(cellExpediente);
				
				PdfPCell cellInstancia = new PdfPCell(new Phrase(requestActaVO.getInstancia(), font));
				cellInstancia.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cellInstancia.setHorizontalAlignment(Element.ALIGN_CENTER);
				cellInstancia.setPaddingRight(4);
				table.addCell(cellInstancia);
				
				PdfPCell cellEstado = new PdfPCell(new Phrase(requestActaVO.getEstado(), font));
				cellEstado.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cellEstado.setHorizontalAlignment(Element.ALIGN_CENTER);
				cellEstado.setPaddingRight(4);
				table.addCell(cellEstado);
			}
			document.add(table);
			
			document.close();
		} catch (DocumentException e) {
			logger.error(e.toString());
		}
		
		return new ByteArrayInputStream(out.toByteArray());
	}

}
