package com.cdm.controller;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cdm.domain.vo.ResponseRcExpedienteVO;
import com.cdm.domain.vo.ResponseRcSentenciadoVO;
import com.cdm.service1.RcExpedienteService;
import com.cdm.service1.RcSentenciadoService;

@Controller
public class ReglasConductaController {
	
	@Autowired
	private RcSentenciadoService rcSentenciadoService;
	
	@Autowired
	private RcExpedienteService rcExpedienteService;
	
	@GetMapping(value = "/getSentenciado/{documento}", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<ResponseRcSentenciadoVO> getSentenciado(@PathVariable String documento) {
		
		ResponseRcSentenciadoVO responseRcSentenciadoVO = rcSentenciadoService.getSentenciado(documento);
		return new ResponseEntity<>(responseRcSentenciadoVO, HttpStatus.OK);
		
    }
	
	@GetMapping(value = "/getExpediente/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<ResponseRcExpedienteVO> getExpediente(@PathVariable Integer id) {
		
		ResponseRcExpedienteVO responseRcExpedienteVO = rcExpedienteService.getExpediente(id);
		return new ResponseEntity<>(responseRcExpedienteVO, HttpStatus.OK);
		
    }
}
