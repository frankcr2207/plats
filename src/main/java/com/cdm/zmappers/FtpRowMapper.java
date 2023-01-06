package com.cdm.zmappers;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import com.cdm.entities.Ftp;

@SuppressWarnings("rawtypes")
public class FtpRowMapper implements RowMapper {
	public Ftp mapRow(ResultSet rs, int rowNum) throws SQLException {
        Ftp ftp = new Ftp();
        ftp.setIp(rs.getString("ftp"));
        ftp.setUsuario(rs.getString("usuario"));
        ftp.setClave(rs.getString("clave"));
        ftp.setPuerto(rs.getInt("puerto"));
        return ftp;
    }
}
