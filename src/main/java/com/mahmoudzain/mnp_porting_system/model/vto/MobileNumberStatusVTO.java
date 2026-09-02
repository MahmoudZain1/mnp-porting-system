package com.mahmoudzain.mnp_porting_system.model.vto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MobileNumberStatusVTO {
    private String phoneNumber;
    private String currentOperator;
    private boolean ported;
    private String activeRequestStatus;
    private Long lastPortingRequestId;
}
