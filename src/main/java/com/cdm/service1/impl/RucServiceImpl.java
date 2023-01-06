package com.cdm.service1.impl;

import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.cdm.domain.vo.ResponseRucVO;
import com.cdm.service1.RucService;

@Service
public class RucServiceImpl implements RucService {

	@Override
	public ResponseRucVO consultaRuc(String ruc) {
		ResponseRucVO responseRucDTO = new ResponseRucVO();
		
		RestTemplate restTemplate = new RestTemplate();
		try{
			responseRucDTO = restTemplate.getForObject("http://api.apis.net.pe/v1/ruc?numero=" + ruc, ResponseRucVO.class);
		}
		catch(Exception ex) {
			throw new NoSuchElementException("No se encontró registro con el RUC ingresado.");
		}
		return responseRucDTO;
	}

}
