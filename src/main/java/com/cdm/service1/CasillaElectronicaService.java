package com.cdm.service1;

import java.util.List;

import com.cdm.domain.vo.RequestCasillaElectronicaVO;
import com.cdm.domain.vo.ResponseCasillaElectronicaVO;
import com.cdm.domain2.vo.RequestAuxilioJudicialVO;
import com.cdm.domain2.vo.ResponseArchivoVO;

public interface CasillaElectronicaService {
	public List<ResponseCasillaElectronicaVO> getSolicitudes();
	public List<ResponseCasillaElectronicaVO> getSolicitudesPorUsuario(String parametro);
	public ResponseCasillaElectronicaVO getSolicitud(Integer id);
	String descargarArchivoCasillaElectronica(Integer tipo, Integer id);
	void updateCasillaElectronica(RequestCasillaElectronicaVO requestAuxilioJudicialVO);
}
