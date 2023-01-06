package com.cdm.service2.impl;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.cdm.domain.SedeUsuario;
import com.cdm.domain2.SalvaguardiaArchivo;
import com.cdm.domain2.FechaHora;
import com.cdm.domain2.Salvaguardia;
import com.cdm.domain2.vo.RequestSalvaguardiaVO;
import com.cdm.domain2.vo.ResponseArchivoVO;
import com.cdm.domain2.vo.ResponseSalvaguardiaVO;
import com.cdm.mapper.SalvaguardiaMapperService;
import com.cdm.repository.SedeUsuarioRepository;
import com.cdm.repository2.ArchivoSalvaguardiaRepository;
import com.cdm.repository2.SalvaguardiaRepository;
import com.cdm.service1.FtpCdgService;
import com.cdm.service2.FtpModuloServiceDos;
import com.cdm.service2.SalvaguardiaService;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfReader;

@Service
public class SalvaguardiaServiceImpl implements SalvaguardiaService{

	private static final Integer modulo = 7;
	
	private static final String local = "E://temp_cdg/";
	
	@Autowired
	private SalvaguardiaRepository salvaguardiaRepository;
	
	@Autowired
	private ArchivoSalvaguardiaRepository archivoSalvaguardiaRepository;
	
	@Autowired
	private SalvaguardiaMapperService salvaguardiaMapperService;
	
	@Autowired
	private SedeUsuarioRepository sedeUsuarioRepository;
	
	@Autowired
	private FtpModuloServiceDos ftpModuloService;
	
	@Autowired
	private FtpCdgService ftpCdgService;
	
	@Override
	public List<ResponseSalvaguardiaVO> getSalvaguardias(String usuario, String inicio, String fin) {
		List<SedeUsuario> sedesUsuario = this.sedeUsuarioRepository.findByUsuario(usuario);		
		List<String> sedes = sedesUsuario.stream().map(SedeUsuario::getSede).collect(Collectors.toList());
		List<Salvaguardia> salvaguardias = new ArrayList<>(); 
		
		if(Objects.nonNull(inicio) && Objects.nonNull(fin)) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
			LocalDateTime fecInicio = LocalDateTime.parse(inicio, formatter);
			LocalDateTime fecFin = LocalDateTime.parse(fin, formatter);
			salvaguardias = this.salvaguardiaRepository.findByFechaSistemaBetweenAndSedeIn(fecInicio, fecFin, sedes);
		}
		else {
			List<String> estados = Arrays.asList("P","T");
			salvaguardias = this.salvaguardiaRepository.findByEstadoInAndSedeIn(estados, sedes);
		}
		return salvaguardiaMapperService.convertir_a_VO(salvaguardias);
	}
	
	@Transactional
	@Override
	public ResponseSalvaguardiaVO getSalvaguardia(String usuario, Integer id) {
		Salvaguardia salvaguardia = this.salvaguardiaRepository.findById(id).get();
		if(salvaguardia.getEstado().equals("P")) {
			salvaguardia.setEstado("T");
			salvaguardia.setUsuario(usuario);
			return salvaguardiaMapperService.convertir_a_VO(this.salvaguardiaRepository.saveAndFlush(salvaguardia));
		}
		else
			return salvaguardiaMapperService.convertir_a_VO(salvaguardia);
	}

	@Override
	public ResponseArchivoVO descargarArchivoSalvaguardia(Integer tipo, Integer id) {
		String carpeta, nombre;
		ResponseArchivoVO responseArchivoVO = new ResponseArchivoVO();
		if(tipo == 1) {
			Salvaguardia salvaguardia = salvaguardiaRepository.findById(id).get();
			carpeta = salvaguardia.getCarpeta();
			nombre = salvaguardia.getArchivo();
		}
		else {
			SalvaguardiaArchivo archivoSalvaguradia = archivoSalvaguardiaRepository.findById(id).get();
			carpeta = archivoSalvaguradia.getCarpeta();
			nombre = archivoSalvaguradia.getNombre();
		}
		
		if(ftpModuloService.conectarFTP(modulo)) {
			Boolean estado = ftpModuloService.descargarArchivo(carpeta, nombre);
			if(estado == null)
				throw new RuntimeException("No se pudo descargar el archivo del FTP.");
			else if(!estado)
				throw new RuntimeException("Archivo no fue cargado correctamente por el usuario.");
			else if(estado)
				responseArchivoVO.setNombre(nombre);
		}
		else
			throw new RuntimeException("No se pudo conectar al servidor FTP");
		
		return responseArchivoVO;
	}

	@Transactional
	@Override
	public void updateSalvaguardia(RequestSalvaguardiaVO requestSalvaguardiaVO) throws Exception {
		
		LocalDateTime ahora = salvaguardiaRepository.getFechaHoraActual().getFechaHora();
		
		Salvaguardia salvaguardia = salvaguardiaRepository.findById(requestSalvaguardiaVO.getIdSalvaguardia()).get();
		
		if(salvaguardia.getEstado().equals("A"))
			throw new RuntimeException("El registro ya fue atendido, actualice la lista.");
		
		if(requestSalvaguardiaVO.getTipoRespuesta().equals("A")) {
			Integer entero = Integer.parseInt(requestSalvaguardiaVO.getCodigo());
			requestSalvaguardiaVO.setExpediente(String.format("%05d", Integer.parseInt(requestSalvaguardiaVO.getExpediente())));
			
			DateTimeFormatter formato = DateTimeFormatter.ofPattern("ddMMyyyy-HHmmss");
			
			String archivoFinal = String.format("%010d", entero) + "-" + requestSalvaguardiaVO.getAnio()
				+ "-EXP-" + requestSalvaguardiaVO.getOrgano() + "-" + requestSalvaguardiaVO.getEspecialidad()  
				+ "-" + ahora.format(formato) + ".pdf";
			
			this.descargarArchivoGeneralSalvaguardia(salvaguardia, archivoFinal, null);
			
			if(!ftpCdgService.conectarFTP(salvaguardia.getSede()))
				throw new RuntimeException("No se pudo conectar al FTP local");
			
			if(!ftpCdgService.cargarArchivo(archivoFinal))
				throw new RuntimeException("No se pudo enviar el archivo al FTP local");
			
			salvaguardia.setCodigo(archivoFinal);
			salvaguardia.setExpediente(requestSalvaguardiaVO.getExpediente() + "-" + 
				requestSalvaguardiaVO.getAnio() + "-0-" + salvaguardia.getSede() + "-" + requestSalvaguardiaVO.getOrgano() + "-" + 
					requestSalvaguardiaVO.getEspecialidad() );
		}

		salvaguardia.setEstado(requestSalvaguardiaVO.getTipoRespuesta());
		salvaguardia.setObservacion(requestSalvaguardiaVO.getTextoRespuesta().toUpperCase());
		salvaguardia.setFechaAtencion(ahora);
		salvaguardia.setIp(getClientIp());
		salvaguardiaRepository.save(salvaguardia);		
	}

	@Override
	public void liberarSalvaguardia(RequestSalvaguardiaVO requestSalvaguardiaVO) {
		Salvaguardia salvaguardia = salvaguardiaRepository.findById(requestSalvaguardiaVO.getIdSalvaguardia()).get();
		salvaguardia.setEstado("P");
		salvaguardia.setUsuario(null);
		salvaguardiaRepository.save(salvaguardia);
	}

	@Override
	public FechaHora getFechaHoraActual() {
		return salvaguardiaRepository.getFechaHoraActual();
	}
	
	public void unirPdf(List<InputStream> listaPdf, String archivoFinal) throws Exception{
		OutputStream outputStream =  new FileOutputStream(local + archivoFinal);
		Document document = new Document();
		PdfCopy copy = new PdfCopy(document, outputStream);
		document.open();
		PdfReader reader;
		int n;
		Iterator<InputStream> pdfIterator = listaPdf.iterator();
		while(pdfIterator.hasNext()) {
			reader = new PdfReader(pdfIterator.next());
			n = reader.getNumberOfPages();
			for(int page = 0; page < n;) {
				copy.addPage(copy.getImportedPage(reader, ++page));
			}
			copy.freeReader(reader);
			reader.close();
		}
		outputStream.flush();
        document.close();
        outputStream.close();
    }

	private String getClientIp() {
        HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest(); 
        return request.getRemoteAddr();
    }

	@Override
	public ResponseArchivoVO descargarArchivoGeneralSalvaguardia(Salvaguardia salvaguardia, String archivoFinal, Integer id) throws Exception {
		
		ResponseArchivoVO responseArchivoVO = new ResponseArchivoVO();
		
		if(Objects.isNull(salvaguardia))
			salvaguardia = salvaguardiaRepository.findById(id).get();
		if(Objects.isNull(archivoFinal))
			archivoFinal = "PLATS_salvaguardia_" + id + ".pdf";
	
		List<InputStream> listaPdf = new ArrayList<InputStream>();
		responseArchivoVO = descargarArchivoSalvaguardia(1, salvaguardia.getIdSalvaguardia());
		listaPdf.add(new FileInputStream(local + responseArchivoVO.getNombre()));
		for(SalvaguardiaArchivo archivoSalvaguardia : salvaguardia.getArchivosSalvaguardia()) {
			responseArchivoVO = descargarArchivoSalvaguardia(2, archivoSalvaguardia.getId());
			listaPdf.add(new FileInputStream(local + responseArchivoVO.getNombre()));
		}
		this.unirPdf(listaPdf, archivoFinal);
		responseArchivoVO.setNombre(archivoFinal);
		return responseArchivoVO;
	}
}
