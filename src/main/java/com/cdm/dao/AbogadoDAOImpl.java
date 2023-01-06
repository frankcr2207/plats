package com.cdm.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import com.cdm.dto.MultaDTO;
import com.cdm.entities.Abogado;

@Repository
public class AbogadoDAOImpl implements AbogadoDAO {
	
	@Autowired
	JdbcTemplate jdbcTemplate;

	@Override
	public int saveAbogado(MultaDTO multa) {
		GeneratedKeyHolder holder = new GeneratedKeyHolder();
		jdbcTemplate.update(new PreparedStatementCreator() {
		    @Override
		    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
		        PreparedStatement statement = con.prepareStatement("insert into abogados_multa (s_dni, s_nombres, s_apellidos, s_casilla_fisica, s_casilla_electronica, s_domicilio_real, s_domicilio_procesal) VALUES (?,?,?,?,?,?,?) ", Statement.RETURN_GENERATED_KEYS);
		        statement.setString(1, multa.getDni());
		        statement.setString(2, multa.getNombres());
		        statement.setString(3, multa.getApellidos());
		        statement.setString(4, multa.getCasilla_fisica());
		        statement.setString(5, multa.getCasilla_electronica());
		        statement.setString(6, multa.getDomicilio_real());
		        statement.setString(7, multa.getDomicilio_procesal());
		        return statement;
		    }
		}, holder);

		int primaryKey = holder.getKey().intValue();
		return primaryKey;
	}

	@Override
	public int findIdAbogado(String dni) {
		String sql = "select id_abogado from abogados_multa where s_dni = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{dni}, (int.class));
	}
	
	@Override
	public int countAbogado(String dni) {
		String sql = "select count(*) from abogados_multa where s_dni = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{dni}, (int.class));
	}

	@Override
	public int updateAbogado(MultaDTO multaDTO) {
		String sql = "update abogados_multa set s_casilla_fisica = ?, s_casilla_electronica = ?, s_domicilio_real = ?, s_domicilio_procesal = ? where s_dni = ?";
		return jdbcTemplate.update(sql, multaDTO.getCasilla_fisica(), multaDTO.getCasilla_electronica(), multaDTO.getDomicilio_real(), multaDTO.getDomicilio_procesal(), multaDTO.getDni());
	}
}
