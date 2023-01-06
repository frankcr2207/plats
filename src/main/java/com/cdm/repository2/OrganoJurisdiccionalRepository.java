package com.cdm.repository2;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cdm.domain2.OrganoJurisdiccional;

@Repository
public interface OrganoJurisdiccionalRepository extends JpaRepository<OrganoJurisdiccional, String>{

}
