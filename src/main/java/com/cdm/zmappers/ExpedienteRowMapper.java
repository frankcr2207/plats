package com.cdm.zmappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.cdm.entities.Expediente;

@SuppressWarnings("rawtypes")
public class ExpedienteRowMapper implements RowMapper{
	public Expediente mapRow(ResultSet rs, int rowNum) throws SQLException {
		Expediente expediente = new Expediente();
		expediente.setId_correlativo(rs.getString("id_correlativo"));
		expediente.setId_solicitud(rs.getString("id_solicitud_expedientes"));
		expediente.setNumero(rs.getString("n_expediente"));
		expediente.setAnio(rs.getString("n_anio"));
		expediente.setCuaderno(rs.getString("n_cuaderno"));
		expediente.setId_instancia(rs.getString("id_instancia"));
		expediente.setInstancia(rs.getString("x_nom_instancia"));
		expediente.setNombre_parte(rs.getString("s_nombre_parte"));
		expediente.setNombre_secretario(rs.getString("s_nombre_secretario"));
        return expediente;
    }
}

