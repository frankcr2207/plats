package com.cdm.zmappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.cdm.entities.Audiencia;

@SuppressWarnings("rawtypes")
public class AudienciaRowMapper implements RowMapper{
	public Audiencia mapRow(ResultSet rs, int rowNum) throws SQLException {
		Audiencia aud = new Audiencia();
		aud.setExpediente(rs.getString("num_expediente"));
		aud.setInstancia(rs.getString("x_instancia"));
		aud.setTipo(rs.getString("x_audiencia"));
		aud.setDelito(rs.getString("x_delito"));
		aud.setParte1(rs.getString("x_partes_1"));
		aud.setParte2(rs.getString("x_partes_2"));
		aud.setInicio(rs.getString("h_inicio"));
		aud.setJueces(rs.getString("x_jueces"));
		aud.setEnlace(rs.getString("x_observacion"));
		return aud;
	}
}
