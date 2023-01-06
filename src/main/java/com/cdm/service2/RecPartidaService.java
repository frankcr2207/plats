package com.cdm.service2;

import java.time.LocalDateTime;
import java.util.List;

import com.cdm.domain2.RecPartida;
import com.cdm.domain2.vo.ResponseRecPartidaVO;

public interface RecPartidaService {
	List<ResponseRecPartidaVO> getRecPartidas(String usuario);
	List<ResponseRecPartidaVO> findRecPartidas(String usuario, LocalDateTime inicio, LocalDateTime fin);
	ResponseRecPartidaVO getRecPartida(Integer id);
}
