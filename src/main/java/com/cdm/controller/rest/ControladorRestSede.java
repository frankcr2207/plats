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

import com.cdm.dao.SedeDAO;
import com.cdm.entities.Sede;
import com.cdm.entities.ValueCriteria;

@RestController
@CrossOrigin(origins="http://localhost:8080")
public class ControladorRestSede {
	
	@Autowired
	private SedeDAO sedeDAO;
	
	@RequestMapping(value = "/obtSedes", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<Sede> obtSedes(@RequestBody ValueCriteria vc) {
    	return sedeDAO.getSedes();
    }
}
