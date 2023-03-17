package com.cdm.repository2;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cdm.domain2.SunarpSolicitud;

@Repository
public interface SunarpSolicitudRepository extends JpaRepository<SunarpSolicitud, Integer>{
	
	List<SunarpSolicitud> findByEstadoInAndIdSedeIn(List<String> estados, List<String> sede);
	
	List<SunarpSolicitud> findByEstadoIn(List<String> estados);
	
	default List<SunarpSolicitud> getSolicitudes(EntityManager em, List<String> estados, List<String> sedes){
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<SunarpSolicitud> cq = cb.createQuery(SunarpSolicitud.class);
		Root<SunarpSolicitud> casillaRoot = cq.from(SunarpSolicitud.class);
		
		List<Predicate> predicates = new ArrayList<>();
		
		Expression<Integer> expression = casillaRoot.get("estado");
		Predicate predicate = expression.in(estados);
		predicates.add(predicate);
		
		if(!sedes.isEmpty()) {
			expression = casillaRoot.get("sede");
			predicate = expression.in(sedes);
			predicates.add(predicate);
		}
		
		Predicate global = cb.and(predicates.toArray(new Predicate[predicates.size()]));
		cq.where(global);
				
		TypedQuery<SunarpSolicitud> casillas = em.createQuery(cq);
		return casillas.getResultList();
	}
}
