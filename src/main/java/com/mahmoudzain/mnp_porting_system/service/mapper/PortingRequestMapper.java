package com.mahmoudzain.mnp_porting_system.service.mapper;

import com.mahmoudzain.mnp_porting_system.model.dto.CreatePortingDTO;
import com.mahmoudzain.mnp_porting_system.model.entity.PortingRequest;
import com.mahmoudzain.mnp_porting_system.model.vto.PortingRequestVTO;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE
)
public interface PortingRequestMapper {

    PortingRequest toEntity(CreatePortingDTO source);


    @Mapping(target = "donor", source = "donor.organization.code")
    @Mapping(target = "recipient", source = "recipient.organization.code")
    @Mapping(target = "status", source = "status")
    PortingRequestVTO toVTO(PortingRequest source);


    List<PortingRequestVTO> toVTOList(List<PortingRequest> requests);


}
