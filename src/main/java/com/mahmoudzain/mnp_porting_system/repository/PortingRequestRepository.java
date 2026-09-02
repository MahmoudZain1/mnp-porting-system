package com.mahmoudzain.mnp_porting_system.repository;

import com.mahmoudzain.mnp_porting_system.model.entity.Operator;
import com.mahmoudzain.mnp_porting_system.model.entity.PortingRequest;
import com.mahmoudzain.mnp_porting_system.model.enums.PortingRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PortingRequestRepository extends JpaRepository<PortingRequest, Long> {


    @Query("""
          select case when count(pr) > 0 then true else false end from PortingRequest pr
           where pr.phoneNumber = :phoneNumber and pr.status = :status   
         """)
    boolean existsByPhoneNumberAndStatus(@Param("phoneNumber") String phoneNumber,
                                         @Param("status") PortingRequestStatus status);


    @Query("""
    select pr from PortingRequest pr
    where pr.donor = :operator 
    or pr.recipient = :operator
    or pr.status = :status
    order by pr.createdAt desc
            """)
    List<PortingRequest> findVisibleRequests(@Param("operator") Operator operator,
                                             @Param("status") PortingRequestStatus status);


    @Query("""
    select pr from PortingRequest pr
    where pr.phoneNumber = :phoneNumber
    order by pr.createdAt desc
    limit 1
           """)
    Optional<PortingRequest> findLatestRequestByPhoneNumber(@Param("phoneNumber") String phoneNumber);


    @Query("""
    select pr from PortingRequest pr
    where pr.status = :status and pr.createdAt < :cutoffTime
          """)
    List<PortingRequest> findByStatusAndCreatedAtBefore(@Param("status") PortingRequestStatus status, @Param("cutoffTime") LocalDateTime cutoffTime);
}
