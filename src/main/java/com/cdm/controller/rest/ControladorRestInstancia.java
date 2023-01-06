package com.cdm.controller.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.cdm.dao.InstanciaDAO;
import com.cdm.entities.Instancia;
import com.cdm.entities.ValueCriteria;

@RestController
@CrossOrigin(origins="http://localhost:8080")
public class ControladorRestInstancia {
	
	@Autowired
	private InstanciaDAO instanciaDAO;
	
	@RequestMapping(value = "/obtInstancias", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<Instancia> obtInstancias(@RequestBody ValueCriteria vc) {
    	return instanciaDAO.getInstancias(vc.getId());
    }
	
	@RequestMapping(value = "/obtInstanciasNCPP", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<Instancia> obtInstanciasNCPP(@RequestBody ValueCriteria vc) {
    	return instanciaDAO.getInstanciasNCPP(vc.getId());
    }
	
	@RequestMapping(value = "/obtInstanciasCDM", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<Instancia> obtInstanciasCDM(@RequestBody ValueCriteria vc) {
    	return instanciaDAO.getInstanciasCDM(vc.getId());
    }
	
	@RequestMapping(value = "/obtInstanciasSecretario", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<Instancia> obtInstanciasSecretario(@RequestBody ValueCriteria vc) {
    	return instanciaDAO.getInstanciasSecretario(vc.getId());
    }
}
