package com.cdm.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.cdm.dto.MultaDTO;
import com.cdm.utils.UtilIP;

@Repository
public class MultaDAOImpl implements MultaDAO {
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	@Override
	public int saveMulta(MultaDTO m, int abogado, String usuario) {
		String sql = "insert into multa (n_expediente, c_sede, c_instancia, c_especialidad, n_resolucion, f_resolucion, n_monto, s_consentida, "+
				"s_documento, s_nombres, s_apellidos, s_contacto, s_dom_real, s_dom_proc, s_casilla_fisica, s_casilla_elec, s_estado, c_usuario, s_ip, f_registro) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,'P',?,?,NOW())";
		UtilIP ip = new UtilIP(); 
		return jdbcTemplate.update(sql, m.getExpediente(), m.getSede(), m.getInstancia(), m.getEspecialidad(), m.getResolucion(), m.getF_resolucion(), m.getMonto(), m.getConsentida(),
				m.getDni(), m.getNombres(), m.getApellidos(), m.getContacto(), m.getDomicilio_real(), m.getDomicilio_procesal(),
				m.getCasilla_fisica(), m.getCasilla_electronica(), usuario, ip.getClientIp());
	}

	@Override
	public List<?> getAllMultas(String usuario) {
		String sql = "select *, date_format(m.f_registro, '%d-%m-%Y %r') as registro from multa m where m.c_usuario = ? order by m.f_registro desc";
		return jdbcTemplate.queryForList(sql, usuario);
	}

	@Override
	public List<?> searchMultas(String parametro) {
		String sql = "select *, date_format(m.f_registro, '%d-%m-%Y %r') as registro from multa m where m.s_nombres like ? or m.s_apellidos like ? order by m.f_registro desc";
		return jdbcTemplate.queryForList(sql, "%" + parametro + "%", "%" + parametro + "%");
	}

	@Override
	public List<?> getAllMultasCoor() {
		String sql = "select *, date_format(m.f_registro, '%d-%m-%Y %r') as registro, concat(m.s_nombres, ' ', m.s_apellidos) as nombresCompletos from multa m where m.s_estado = 'P' order by m.f_registro asc";
		return jdbcTemplate.queryForList(sql);
	}

	@Override
	public List<?> findMulta(int id) {
		String sql = "select *, date_format(m.f_registro, '%d-%m-%Y %r') as registro, date_format(m.f_resolucion, '%d-%m-%Y') as resolucion, date_format(m.f_gestion, '%d-%m-%Y %r') as tramitado, concat(u.s_nombres, ' ', u.s_apellido_paterno, ' ', u.s_apellido_materno) as usuarioCsjar from multa m "+
				"inner join sede s on s.c_sede = m.c_sede inner join instancia i on i.c_instancia = m.c_instancia " +
				"inner join especialidad e on e.c_especialidad = m.c_especialidad inner join usuarios u on u.c_usuario = m.c_usuario where m.id_multa = ?";
		return jdbcTemplate.queryForList(sql, id);
	}

	@Override
	public int saveTramite(int id, String accion, String tramite, String usuario) {
		String sql = "update multa set s_estado = ?, s_observacion = ?, c_usuario_sj = ?, f_gestion = now() where id_multa = ?";
		return jdbcTemplate.update(sql, accion, tramite, usuario, id);
	}

	@Override
	public List<?> searchMultasCoor(String parametro) {
		String sql = "select *, date_format(m.f_registro, '%d-%m-%Y %r') as registro, concat(m.s_nombres, ' ', m.s_apellidos) as nombresCompletos from multa m where m.s_nombres like ? or m.s_apellidos like ? or m.n_expediente = ? order by m.f_registro desc";
		return jdbcTemplate.queryForList(sql, "%" + parametro + "%", "%" + parametro + "%", "%" + parametro + "%");
	}
	
}
