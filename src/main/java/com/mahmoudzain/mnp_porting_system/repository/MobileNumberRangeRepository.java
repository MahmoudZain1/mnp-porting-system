package com.mahmoudzain.mnp_porting_system.repository;

import com.mahmoudzain.mnp_porting_system.model.entity.MobileNumberRange;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MobileNumberRangeRepository extends JpaRepository<MobileNumberRange, Long> {
    Optional<MobileNumberRange> findByPrefix(String prefix);
}
