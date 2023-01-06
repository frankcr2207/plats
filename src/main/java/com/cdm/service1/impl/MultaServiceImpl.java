package com.cdm.service1.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

import javax.transaction.Transactional;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.cdm.domain.Multa;
import com.cdm.domain.vo.RequestGestionMultaVO;
import com.cdm.domain.vo.RequestNuevaMultaVO;
import com.cdm.domain.vo.ResponseConsultaDocumento;
import com.cdm.domain.vo.ResponseMultaVO;
import com.cdm.domain.vo.ResponseReniecVO;
import com.cdm.domain.vo.ResponseRucVO;
import com.cdm.exception.BussinesException;
import com.cdm.exception.ErrorCode;
import com.cdm.mapper.MultaMapperService;
import com.cdm.repository.EspecialidadRepository;
import com.cdm.repository.InstanciaRepository;
import com.cdm.repository.MultaRepository;
import com.cdm.repository.SedeRepository;
import com.cdm.repository.UsuarioInternoRepository;
import com.cdm.service1.MultaService;
import com.cdm.service1.ReniecService;
import com.cdm.service1.RucService;
import com.cdm.utils.Constantes;

@Service
public class MultaServiceImpl implements MultaService {
	
	private final static String DNI = "DNI";
	
	private final static String RUC = "RUC";
	
	private MultaRepository multaRepository;
	
	private MultaMapperService multaMapperService;
	
	private ReniecService reniecService;
	
	private RucService rucService;
	
	private SedeRepository sedeRepository;
	
	private InstanciaRepository instanciaRepository;
	
	private EspecialidadRepository especialidadRepository;
	
	private UsuarioInternoRepository usuarioInternoRepository;
	
	public MultaServiceImpl(MultaRepository multaRepository, MultaMapperService multaMapperService,
			ReniecService reniecService, RucService rucService, SedeRepository sedeRepository, InstanciaRepository instanciaRepository,
			EspecialidadRepository especialidadRepository, UsuarioInternoRepository usuarioInternoRepository) {
			this.multaMapperService = multaMapperService;
			this.multaRepository = multaRepository;
			this.reniecService = reniecService;
			this.rucService = rucService;
			this.sedeRepository = sedeRepository;
			this.instanciaRepository = instanciaRepository;
			this.especialidadRepository = especialidadRepository;
			this.usuarioInternoRepository = usuarioInternoRepository;
		}
		
		@Override
		public List<ResponseMultaVO> getMultasSJ(String parametro) {
			List<Multa> multas = new ArrayList<>(); 
			if(Objects.isNull(parametro))
				multas = this.multaRepository.findByEstado(Constantes.multa_estado_pendiente);
			else
				multas = this.multaRepository.findByNombresContainingOrApellidosContaining(parametro, parametro);
				
			return this.multaMapperService.convertir_a_VO(multas);
		}

		@Override
		public ResponseMultaVO getMulta(Integer id) {
			Multa multa = this.multaRepository.findById(id).get();
			return this.multaMapperService.convertir_a_VO(multa);
		}

		@Override
		public List<ResponseMultaVO> getMultasSec(String usuario, String parametro) {
			List<Multa> multas = new ArrayList<>();
			if(Objects.isNull(parametro)) {
				multas = this.multaRepository.findBySecretarioUsuario(usuario);
			}
			else {
				multas = this.multaRepository.findBySecretarioUsuarioAndNombresContainingOrApellidosContaining(usuario, parametro, parametro);
			}
			
			return this.multaMapperService.convertir_a_VO(multas);
		}

		@Transactional
		@Override
		public ResponseMultaVO guardarMulta(RequestNuevaMultaVO requestNuevaMultaVO, String usuario) {
			
			if(!this.multaRepository.findByDocumentoAndNumExpAndAnioExpAndIncExpAndNumResolucion
				(requestNuevaMultaVO.getDocumento(), requestNuevaMultaVO.getNumExp(), requestNuevaMultaVO.getAnioExp(), 
						requestNuevaMultaVO.getIncExp(), requestNuevaMultaVO.getNumResolucion()).isEmpty())
				throw new BussinesException(ErrorCode.BUSS_ERR_081.getMensaje());
			
			
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
			LocalDateTime dateTime = LocalDateTime.parse(requestNuevaMultaVO.getFecResolucion() + " 00:00", formatter);
			String expediente = requestNuevaMultaVO.getNumExp() + "-" + requestNuevaMultaVO.getAnioExp()
			+ "-" + requestNuevaMultaVO.getIncExp();
			
			Multa multa = this.multaMapperService.convertir_a_Entidad(requestNuevaMultaVO);
			multa.setEstado(Constantes.multa_estado_pendiente);
			multa.setSecretario(this.usuarioInternoRepository.findById(usuario).get());
			multa.setExpediente(expediente);
			multa.setSede(this.sedeRepository.findById(requestNuevaMultaVO.getSede()).get());
			multa.setInstancia(this.instanciaRepository.findById(requestNuevaMultaVO.getInstancia()).get());
			multa.setEspecialidad(this.especialidadRepository.findById(requestNuevaMultaVO.getEspecialidad()).get());
			multa.setFecResolucion(dateTime);
			multa.setFecSistema(this.multaRepository.getFechaHoraActual().getFechaHora());
			if(requestNuevaMultaVO.getTipoMultado().equals(Constantes.multa_multado_natural))
				multa.setRazonSocial(requestNuevaMultaVO.getNombres() + " " + requestNuevaMultaVO.getApellidos());
			return this.multaMapperService.convertir_a_VO(this.multaRepository.saveAndFlush(multa));
		}

		@Override
		public ResponseConsultaDocumento consultaServicios(String servicio, String documento) {
			ResponseConsultaDocumento response = new ResponseConsultaDocumento();
			if(servicio.equals(Constantes.DNI)) {
				ResponseReniecVO reniec = this.reniecService.consultaReniec(documento);
				response.setDocumento(reniec.getDni());
				response.setNombres(reniec.getNombres());
				response.setApellidos(reniec.getPaterno() + " " + reniec.getMaterno());
				response.setRazonSocial("-");
				response.setDomReal("");
				response.setTipoMultado(Constantes.multa_multado_natural);
			}
			else if(servicio.equals(Constantes.RUC)) {
				ResponseRucVO ruc = this.rucService.consultaRuc(documento);
				response.setDocumento(ruc.getNumeroDocumento());
				response.setRazonSocial(ruc.getNombre());
				response.setApellidos("-");
				response.setNombres("-");
				response.setDomReal(ruc.getDireccion());
				response.setTipoMultado(Constantes.multa_multado_juridica);
			}
			return response;
		}

		@Override
		//@Scheduled(cron = "*/10 * * * * *")
		public void testALertas() {
			System.out.println(new Date());
		}

		@Override
		public ResponseMultaVO guardarGestionMulta(RequestGestionMultaVO requestGestionMultaVO, String usuario) {
			Multa multa = this.multaRepository.findById(requestGestionMultaVO.getId()).get();
			
			if(!multa.getEstado().equals(Constantes.multa_estado_pendiente)) 
				throw new BussinesException(ErrorCode.BUSS_ERR_082.toString());
			
			multa.setEstado(requestGestionMultaVO.getEstado());
			multa.setObservacion(requestGestionMultaVO.getObservacion());
			multa.setServJudiciales(this.usuarioInternoRepository.findById(usuario).get());
			multa.setFecGestion(this.multaRepository.getFechaHoraActual().getFechaHora());
			return this.multaMapperService.convertir_a_VO(multa);
		}

}
