package com.cdm.controller.rest;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cdm.domain2.AuxilioJudicial;
import com.cdm.domain2.vo.ResponseAuxilioJudicialVO;
import com.cdm.service2.AuxilioJudicialService;

@RestController
@RequestMapping("/auxiliojudicial")
@CrossOrigin("*")
public class AuxilioJudicialControllerRest {
	
	@Autowired
	private AuxilioJudicialService auxilioJudicialService;
	
	@GetMapping(value = "/getAuxiliosJudicialesssssss", produces = {"application/json"})
	public List<ResponseAuxilioJudicialVO> getAuxiliosJudiciales(@RequestParam String sede){
		return null;
	}
	
	@GetMapping(value = "/getAuxilioJudicialssss", produces = {"application/json"})
	public ResponseAuxilioJudicialVO getAuxiliosJudiciales(@RequestParam Integer id){
		return auxilioJudicialService.getAuxilioJudicial("usuario", id);
	}
	
	@GetMapping(value = "/descargarArchivo", produces = {"application/json"})
	public boolean descargarArchivo(@RequestParam Integer id){
	//	return auxilioJudicialService.descargarArchivo(id);
		return false;
	}
}