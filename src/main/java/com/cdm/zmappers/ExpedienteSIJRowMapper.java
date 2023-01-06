package com.cdm.zmappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.cdm.entities.Expediente;

@SuppressWarnings("rawtypes")
public class ExpedienteSIJRowMapper implements RowMapper{
	public Expediente mapRow(ResultSet rs, int rowNum) throws SQLException {
		Expediente expediente = new Expediente();
		expediente.setN_unico(rs.getString("n_unico"));
		expediente.setX_formato(rs.getString("x_formato"));
        return expediente;
    }
}
