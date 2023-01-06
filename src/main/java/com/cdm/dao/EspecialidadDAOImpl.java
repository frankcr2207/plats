package com.cdm.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.cdm.entities.Especialidad;

@Repository
public class EspecialidadDAOImpl implements EspecialidadDAO {
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	@Override
	public List<Especialidad> getEspecialidades() {
		List<Especialidad> esp = jdbcTemplate.query("select c_especialidad, s_especialidad from especialidad",(result,rowNum)->new Especialidad(result.getString("c_especialidad"),
                result.getString("s_especialidad")));
		return esp;
	}

}
