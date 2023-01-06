package com.cdm.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.cdm.entities.Sede;

@Repository
public class SedeDAOImpl implements SedeDAO {
	
	@Autowired
	JdbcTemplate jdbcTemplate;

	@Override
	public List<Sede> getSedes() {
		List<Sede> sedes = jdbcTemplate.query("select c_sede, s_sede from sede",(result,rowNum)->new Sede(result.getString("c_sede"),
                result.getString("s_sede")));
		return sedes;
	}

}
