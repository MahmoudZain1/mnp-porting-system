package com.mahmoudzain.mnp_porting_system.repository;

import com.mahmoudzain.mnp_porting_system.model.entity.Operator;
import com.mahmoudzain.mnp_porting_system.model.enums.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OperatorRepository extends JpaRepository<Operator, Long> {

    Optional<Operator> findByOrganization(Organization organization);



}
