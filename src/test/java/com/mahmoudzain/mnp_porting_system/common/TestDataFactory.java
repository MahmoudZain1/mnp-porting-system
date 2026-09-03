package com.mahmoudzain.mnp_porting_system.common;

import com.mahmoudzain.mnp_porting_system.model.dto.CreatePortingDTO;
import com.mahmoudzain.mnp_porting_system.model.dto.RejectPortingDTO;
import com.mahmoudzain.mnp_porting_system.model.entity.MobileNumberRange;
import com.mahmoudzain.mnp_porting_system.model.entity.Operator;
import com.mahmoudzain.mnp_porting_system.model.entity.PortingRequest;
import com.mahmoudzain.mnp_porting_system.model.enums.Organization;
import com.mahmoudzain.mnp_porting_system.model.enums.PortingRequestStatus;
import com.mahmoudzain.mnp_porting_system.model.vto.PortingRequestVTO;

import java.time.LocalDateTime;

public class TestDataFactory {

    public static final String VODAFONE_NUMBER = "01012345678";
    public static final String ORANGE_NUMBER = "01212345678";
    public static final String ETISALAT_NUMBER = "01112345678";
    public static final String INVALID_NUMBER = "01512345678";



    public static Operator createVodafoneOperator() {
        return Operator.builder()
                .id(1L)
                .organization(Organization.VODAFONE)
                .displayName("Vodafone")
                .active(true)
                .build();
    }

    public static Operator createOrangeOperator() {
        return Operator.builder()
                .id(2L)
                .organization(Organization.ORANGE)
                .displayName("Orange")
                .active(true)
                .build();
    }


    public static Operator createEtisalatOperator() {
        return Operator.builder()
                .id(3L)
                .organization(Organization.ETISALAT)
                .displayName("Etisalat")
                .active(true)
                .build();
    }

    public static MobileNumberRange createVodafoneRange() {
        return MobileNumberRange.builder()
                .id(1L)
                .prefix("010")
                .operator(createVodafoneOperator())
                .build();
    }

    public static CreatePortingDTO createSampleCreateDTO() {
        return new CreatePortingDTO(VODAFONE_NUMBER);
    }

    public static RejectPortingDTO createSampleRejectDTO() {
        return new RejectPortingDTO("Subscriber information mismatch");
    }


    public static PortingRequest createPendingPortingRequest() {
        return PortingRequest.builder()
                .id(100L)
                .phoneNumber(VODAFONE_NUMBER)
                .donor(createVodafoneOperator())
                .recipient(createOrangeOperator())
                .status(PortingRequestStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public static PortingRequestVTO createSamplePortingRequestVTO() {
        return PortingRequestVTO.builder()
                .id(100L)
                .phoneNumber(VODAFONE_NUMBER)
                .donor("vodafone")
                .recipient("orange")
                .status("PENDING")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }


}
