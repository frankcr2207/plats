package com.cdm.service2.impl;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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
import com.cdm.domain2.AuxilioJudicialArchivo;
import com.cdm.domain2.AuxilioJudicial;
import com.cdm.domain2.FechaHora;
import com.cdm.domain2.vo.RequestAuxilioJudicialVO;
import com.cdm.domain2.vo.ResponseArchivoVO;
import com.cdm.domain2.vo.ResponseAuxilioJudicialVO;
import com.cdm.mapper.AuxilioJudicialMapperService;
import com.cdm.repository.SedeUsuarioRepository;
import com.cdm.repository2.ArchivoAuxJudRepository;
import com.cdm.repository2.AuxilioJudicialRepository;
import com.cdm.service1.FtpCdgService;
import com.cdm.service2.AuxilioJudicialService;
import com.cdm.service2.FtpModuloServiceDos;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfReader;

@Transactional
@Service
public class AuxilioJudicialServiceImpl implements AuxilioJudicialService{
	
	private static final Integer modulo = 6;
	
	private static final String local = "E://temp_cdg/";
	
	@Autowired
	private AuxilioJudicialRepository auxilioJudicialRepository;
	
	@Autowired
	private ArchivoAuxJudRepository archivoAuxJudRepository;
	
	@Autowired
	private SedeUsuarioRepository sedeUsuarioRepository;
	
	@Autowired
	private AuxilioJudicialMapperService auxilioJudicialMapperService;
	
	@Autowired
	private FtpModuloServiceDos ftpModuloService;
	
	@Autowired
	private FtpCdgService ftpCdgService;
	
	@Override
	public List<ResponseAuxilioJudicialVO> getAuxiliosJudiciales(String usuario, String inicio, String fin) {
		List<SedeUsuario> sedeUsuarioS = this.sedeUsuarioRepository.findByUsuario(usuario);		
		List<String> sedes = sedeUsuarioS.stream().map(SedeUsuario::getSede).collect(Collectors.toList());
		List<AuxilioJudicial> auxilioJudicialS = new ArrayList<>();
		
		if(Objects.nonNull(inicio) && Objects.nonNull(fin)) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
			LocalDateTime fecInicio = LocalDateTime.parse(inicio, formatter);
			LocalDateTime fecFin = LocalDateTime.parse(fin, formatter);
			auxilioJudicialS = auxilioJudicialRepository.findByFechaSistemaBetweenAndSedeIn(fecInicio, fecFin, sedes);
		}
		else {
			List<String> estados = Arrays.asList("P","T");
			auxilioJudicialS = auxilioJudicialRepository.findByEstadoInAndSedeIn(estados, sedes);
		}
		return auxilioJudicialMapperService.convertirAVO(auxilioJudicialS);
	}

	@Override
	public ResponseAuxilioJudicialVO getAuxilioJudicial(String usuario, Integer id) {
		AuxilioJudicial auxilioJudicial = auxilioJudicialRepository.findById(id).orElse(null);
		if(auxilioJudicial.getEstado().equals("P")) {
			auxilioJudicial.setEstado("T");
			auxilioJudicial.setUsuario(usuario);
			return auxilioJudicialMapperService.convertirAVO(auxilioJudicialRepository.saveAndFlush(auxilioJudicial));
		}
		else
			return auxilioJudicialMapperService.convertirAVO(auxilioJudicial);
	}
	
	@Override
	public void updateAuxilioJudicial(RequestAuxilioJudicialVO requestAuxilioJudicialVO) {
		List<InputStream> listaPdf = new ArrayList<InputStream>();
		Integer entero = Integer.parseInt(requestAuxilioJudicialVO.getCodigo());
		requestAuxilioJudicialVO.setCodigo(String.format("%010d", entero));
		if(requestAuxilioJudicialVO.getTipoRespuesta().equals("A")) {
			DateTimeFormatter formato = DateTimeFormatter.ofPattern("ddMMyyyy-HHmmss");
			ResponseArchivoVO responseArchivoVO = descargarArchivoAuxJud(1, requestAuxilioJudicialVO.getIdAuxilioJudicial());
			AuxilioJudicial auxilioJudicial = auxilioJudicialRepository.getOne(requestAuxilioJudicialVO.getIdAuxilioJudicial());
			String archivoFinal = requestAuxilioJudicialVO.getCodigo() + "-" + requestAuxilioJudicialVO.getNomenclatura()
				+ "-" + LocalDateTime.now().format(formato) + ".pdf";
			try {
				listaPdf.add(new FileInputStream(local + responseArchivoVO.getNombre()));
				for(AuxilioJudicialArchivo archivoAuxJud : auxilioJudicial.getArchivoAuxJudS()) {
					responseArchivoVO = descargarArchivoAuxJud(2, archivoAuxJud.getId());
					listaPdf.add(new FileInputStream(local + responseArchivoVO.getNombre()));
				}
				this.unirPdf(listaPdf, archivoFinal);
			} catch (Exception e) {
				e.printStackTrace();
			}
			this.ftpCdgService.conectarFTP(auxilioJudicial.getSede());
			this.ftpCdgService.cargarArchivo(archivoFinal);
		}
		
		AuxilioJudicial auxilioJudicial = auxilioJudicialRepository.getOne(requestAuxilioJudicialVO.getIdAuxilioJudicial());
		auxilioJudicial.setEstado(requestAuxilioJudicialVO.getTipoRespuesta());
		auxilioJudicial.setObservacion(requestAuxilioJudicialVO.getTextoRespuesta());
		if(requestAuxilioJudicialVO.getTipoRespuesta().equals("A"))
			auxilioJudicial.setCodigo(requestAuxilioJudicialVO.getCodigo() + "-" + requestAuxilioJudicialVO.getNomenclatura());
		auxilioJudicial.setFechaAtencion(LocalDateTime.now());
		auxilioJudicial.setIp(getClientIp());
		auxilioJudicialRepository.save(auxilioJudicial);
	}

	@Override
	public ResponseArchivoVO descargarArchivoAuxJud(Integer tipo, Integer id) {
		String carpeta, nombre;
		ResponseArchivoVO responseArchivoVO = new ResponseArchivoVO();
		if(tipo == 1) {
			AuxilioJudicial  archivoJudicial = auxilioJudicialRepository.findById(id).orElse(null);
			carpeta = archivoJudicial.getCarpeta();
			nombre = archivoJudicial.getArchivo();
		}
		else {
			AuxilioJudicialArchivo archivoAuxJud = archivoAuxJudRepository.findById(id).orElse(null);
			carpeta = archivoAuxJud.getCarpeta();
			nombre = archivoAuxJud.getNombre();
		}
		
		if(ftpModuloService.conectarFTP(modulo)) {
			Boolean estado = ftpModuloService.descargarArchivo(carpeta, nombre);
			if(estado == null)
				responseArchivoVO.setError("No se pudo descargar el archivo del FTP.");
			else if(!estado)
				responseArchivoVO.setError("Archivo no fue cargado correctamente por el usuario.");
			else if(estado)
				responseArchivoVO.setNombre(nombre);
		}
		else
			responseArchivoVO.setError("No se pudo conectar al servidor FTP");
		
		return responseArchivoVO;
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
	public void liberarAuxilioJudicial(RequestAuxilioJudicialVO requestAuxilioJudicialVO) {
		AuxilioJudicial auxilioJudicial = auxilioJudicialRepository.getOne(requestAuxilioJudicialVO.getIdAuxilioJudicial());
		auxilioJudicial.setEstado("P");
		auxilioJudicial.setUsuario(null);
		auxilioJudicialRepository.save(auxilioJudicial);
	}

	@Override
	public FechaHora getFechaActual() {
		return this.auxilioJudicialRepository.getFechaHoraActual();
	}
}
