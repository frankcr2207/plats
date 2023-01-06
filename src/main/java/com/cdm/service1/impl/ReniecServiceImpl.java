package com.cdm.service1.impl;

import java.util.NoSuchElementException;

import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.cdm.domain.vo.ResponseReniecVO;
import com.cdm.service1.ReniecService;

@Service
public class ReniecServiceImpl implements ReniecService {

	private static final String urlReniec = "http://172.28.206.18:8080/ServiceReniec/getWsReniecOrigin?dni=";
	
	@Override
	public ResponseReniecVO consultaReniec(String dni) {
		RestTemplate restTemplate = new RestTemplate();
		((SimpleClientHttpRequestFactory)restTemplate.getRequestFactory()).setConnectTimeout(3000);
		ResponseReniecVO response = new ResponseReniecVO(); 
		try {
			response = restTemplate.getForObject(urlReniec + dni, ResponseReniecVO.class);
			if(response.getDni().equals("NN"))
				throw new NoSuchElementException("No se obtuvo resultados");
			if(response.getDni().equals("XX"))
				throw new NoSuchElementException("No se pudo obtener conexion al servicio");
		}
		catch(Exception ex) {
			throw new NoSuchElementException("No se pudo obtener conexion al servicio");
		}
		return response;
	}

}
