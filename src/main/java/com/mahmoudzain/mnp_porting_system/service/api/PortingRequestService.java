package com.mahmoudzain.mnp_porting_system.service.api;

import com.mahmoudzain.mnp_porting_system.model.dto.CreatePortingDTO;
import com.mahmoudzain.mnp_porting_system.model.dto.RejectPortingDTO;
import com.mahmoudzain.mnp_porting_system.model.enums.Organization;
import com.mahmoudzain.mnp_porting_system.model.vto.MobileNumberStatusVTO;
import com.mahmoudzain.mnp_porting_system.model.vto.PortingRequestVTO;

import java.util.List;

public interface PortingRequestService {
    PortingRequestVTO createPortingRequest(CreatePortingDTO request , Organization organization);
    List<PortingRequestVTO> getPortingRequests(Organization organization);
    PortingRequestVTO getPortingRequestById(Long requestId, Organization organization);
    PortingRequestVTO acceptPortingRequest(Long requestId, Organization organization);
    PortingRequestVTO rejectPortingRequest(Long requestId, RejectPortingDTO rejectDto, Organization organization);
    int cancelExpiredRequests();
}
