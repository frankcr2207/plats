package com.cdm.zmappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.cdm.entities.Archivo;

@SuppressWarnings("rawtypes")
public class ArchivoRowMapper implements RowMapper{
	public Archivo mapRow(ResultSet rs, int rowNum) throws SQLException {
        Archivo archivo = new Archivo();
        archivo.setNombre(rs.getString("nombre"));
        archivo.setUbicacion(rs.getString("ubicacion"));
        archivo.setTemporal(rs.getString("temporal"));
        return archivo;
    }
}
