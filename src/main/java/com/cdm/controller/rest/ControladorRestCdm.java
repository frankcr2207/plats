package com.cdm.controller.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.cdm.dao.CdmDAO;
import com.cdm.domain.Sede;
import com.cdm.domain.vo.ResponseCdgDocumentoVO;
import com.cdm.domain.vo.ResponseInstanciaVO;
import com.cdm.domain.vo.ResponseSedeVO;
import com.cdm.entities.Cdm;
import com.cdm.service1.CdgDocumentoService;
import com.cdm.service1.InstanciaService;
import com.cdm.service1.SedeService;

@RestController
@CrossOrigin(origins="http://localhost:8080")
public class ControladorRestCdm {
	
	@Autowired
	private CdmDAO cdmDAO;
	
	@Autowired
	private CdgDocumentoService cdgDocumentoService;
	
	@Autowired
	private SedeService sedeService;
	
	@Autowired
	private InstanciaService instanciaService;
	
	@RequestMapping(value = "/obtCDM", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<Cdm> obtCDM(@RequestBody String id) {
    	return cdmDAO.getCdms();
    }
	
	@RequestMapping(value = "/obtCDMAsistente", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<Cdm> obtCDMAsistente(@RequestBody String id) {
    	return cdmDAO.getCdmAsistente(id);
    }
	@RequestMapping(value = "/obtCdgDocumentoss", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<ResponseCdgDocumentoVO> obtCdgDocumentos() {
    	return cdgDocumentoService.getCdgDocumentos("JCOAQUIRACDG", "0401", "", "", "");
    }
	
	@RequestMapping(value = "/obtCdgDocumento/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseCdgDocumentoVO obtCdgDocumento(@PathVariable Integer id) {
    	return cdgDocumentoService.getCdgDocumento(id);
    }
	
	@RequestMapping(value = "/obtSede", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseSedeVO obtSede() {
    	return sedeService.getSede("0401");
    }
	
	@RequestMapping(value = "/obtInstancia", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseInstanciaVO obtInstancia() {
    	return instanciaService.getInstancia("I01");
    }
}
