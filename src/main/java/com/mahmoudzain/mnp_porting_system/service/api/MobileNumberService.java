package com.mahmoudzain.mnp_porting_system.service.api;

import com.mahmoudzain.mnp_porting_system.model.enums.Organization;
import com.mahmoudzain.mnp_porting_system.model.vto.MobileNumberStatusVTO;

public interface MobileNumberService {
    MobileNumberStatusVTO getMobileNumberStatus(String phoneNumber, Organization organization);
}