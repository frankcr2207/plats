package com.cdm.service2.impl;

import java.util.List;

import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cdm.domain2.OrganoJurisdiccional;
import com.cdm.repository2.OrganoJurisdiccionalRepository;
import com.cdm.service2.OrganoJurisdiccionalService;

@Service
public class OrganoJurisdiccionalServiceImpl implements OrganoJurisdiccionalService{
	
	@Autowired
	private OrganoJurisdiccionalRepository organoJurisdiccionalRepository;
	
	@Override
	public List<OrganoJurisdiccional> getOrganosJurisdiccionales() {
		return organoJurisdiccionalRepository.findAll();
	}

}
