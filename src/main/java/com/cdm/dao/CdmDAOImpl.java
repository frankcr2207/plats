package com.cdm.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.cdm.entities.Cdm;

@Repository
public class CdmDAOImpl implements CdmDAO {
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	@Override
	public List<Cdm> getCdms() {
		List<Cdm> cdms = jdbcTemplate.query("select * from cdm", (result, rowNum) -> new Cdm(result.getInt("n_id_cdm"),
				result.getString("s_cdm")));
		return cdms;
	}
	
	public List<Cdm> getCdmAsistente(String asistente) {
		List<Cdm> cdms = jdbcTemplate.query("SELECT c.n_id_cdm, c.s_cdm FROM r_usuario_cdm r INNER JOIN cdm c ON c.n_id_cdm = r.n_id_cdm WHERE r.c_usuario = ?", new Object[]{asistente}, (result, rowNum) -> new Cdm(result.getInt("n_id_cdm"),
				result.getString("s_cdm")));
		return cdms;
	}
}
