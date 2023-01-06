package com.cdm.repository;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.cdm.domain.CasillaElectronica;
import com.cdm.domain2.FechaHora;

@Repository
public interface CasillaElectronicaRepository extends JpaRepository<CasillaElectronica, Integer>{
	
	@Query("select distinct now() as fechaHora from CasillaElectronica")
	public FechaHora getFechaHoraActual();
	
	//@EntityGraph(value = "CasillaElectronica.usuarioExterno", type = EntityGraphType.FETCH)
	//public List<CasillaElectronica> getSolicitudes(List<Integer> ids);
	//public List<CasillaElectronica> findByUsuarioExternoIn(@Param("ids") List<Integer> ids);
	public List<CasillaElectronica> findByUsuarioExternoIdIn(List<Integer> ids);
	
	public List<CasillaElectronica> findByEstado(String estado);
	
	/*default List<CasillaElectronica> getSolicitudes(EntityManager em, List<Integer> ids){
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<CasillaElectronica> cq = cb.createQuery(CasillaElectronica.class);
		Root<CasillaElectronica> casillaRoot = cq.from(CasillaElectronica.class);
		
		List<Predicate> predicates = new ArrayList<>();
		
		Expression<Integer> expression = casillaRoot.get("usuarioExterno");
		Predicate predicate = expression.in(ids);
		predicates.add(predicate);
		
		Predicate global = cb.and(predicates.toArray(new Predicate[predicates.size()]));
		cq.where(global);
				
		TypedQuery<CasillaElectronica> casillas = em.createQuery(cq);
		return casillas.getResultList();
	}*/
}
