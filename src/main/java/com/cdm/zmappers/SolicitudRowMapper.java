package com.cdm.zmappers;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import com.cdm.entities.Solicitud;

@SuppressWarnings("rawtypes")
public class SolicitudRowMapper implements RowMapper {
	public Solicitud mapRow(ResultSet rs, int rowNum) throws SQLException {
        Solicitud solicitud= new Solicitud();
        solicitud.setId(rs.getString("n_id"));
        solicitud.setFecha(rs.getString("fecha"));
        solicitud.setCorreo(rs.getString("s_correo_electronico"));
        solicitud.setDocumento(rs.getString("s_documento"));
        solicitud.setNombres(rs.getString("s_nombres"));
        solicitud.setApellidos(rs.getString("s_apellidos"));
        solicitud.setNacimiento(rs.getString("fNacimiento"));
        solicitud.setCelular(rs.getString("s_celular"));
        solicitud.setCdm(rs.getString("s_cdm"));
        solicitud.setInstancia(rs.getString("x_nom_instancia"));
        solicitud.setExpediente(rs.getString("n_expediente"));
        solicitud.setEstado(rs.getString("s_estado"));

        return solicitud;
    }
}
