package com.cdm.zmappers;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import com.cdm.entities.Usuario;

@SuppressWarnings("rawtypes")
public class UsuarioRowMapper implements RowMapper{
	public Usuario mapRow(ResultSet rs, int rowNum) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setUser(rs.getString("c_usuario"));
        usuario.setDni(rs.getString("s_dni"));
        usuario.setNombres(rs.getString("s_nombres"));
        usuario.setPaterno(rs.getString("s_apellido_paterno"));
        usuario.setMaterno(rs.getString("s_apellido_materno"));
        usuario.setPerfil(rs.getString("idperfil"));
        usuario.setEstado(rs.getString("s_activo"));
        usuario.setCarga(rs.getString("n_carga"));
        usuario.setCorreo(rs.getString("s_correo"));
        usuario.setTelefono(rs.getString("s_telefono"));
        usuario.setEspecialidad(rs.getString("c_especialidad"));
        return usuario;
    }
}
