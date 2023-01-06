package com.cdm.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.cdm.entities.Instancia;

@Repository
public class InstanciaDAOImpl implements InstanciaDAO {
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	@Override
	public List<Instancia> getInstancias(String sede) {
		List<Instancia> instancias = jdbcTemplate.query("select c_instancia, x_nom_instancia from instancia where c_sede = ?", new Object[]{sede}, (result, rowNum) -> new Instancia(result.getString("c_instancia"),
            result.getString("x_nom_instancia")));
		return instancias;
	}
	
	@Override
	public List<Instancia> getInstanciasNCPP(String sede) {
		List<Instancia> instancias = jdbcTemplate.query("select c_instancia, x_nom_instancia from instancia where c_sede = ? AND s_penal = 'S' ORDER BY s_tipo, x_nom_instancia ASC", new Object[]{sede}, (result, rowNum) -> new Instancia(result.getString("c_instancia"),
            result.getString("x_nom_instancia")));
		return instancias;
	}
	
	@Override
	public List<Instancia> getInstanciasCDM(String cdm) {
		List<Instancia> instancias = jdbcTemplate.query("SELECT * FROM instancia WHERE n_id_cdm = ?", new Object[]{cdm}, (result, rowNum) -> new Instancia(result.getString("c_instancia"),
            result.getString("x_nom_instancia")));
		return instancias;
	}
	
	@Override
	public List<Instancia> getInstanciasSecretario(String secretario) {
		List<Instancia> instancias = jdbcTemplate.query("SELECT i.c_instancia, i.x_nom_instancia FROM r_usuario_instancia r INNER JOIN instancia i ON i.c_instancia = r.c_instancia WHERE r.c_usuario = ?", new Object[]{secretario}, (result, rowNum) -> new Instancia(result.getString("c_instancia"),
            result.getString("x_nom_instancia")));
		return instancias;
	}
}
