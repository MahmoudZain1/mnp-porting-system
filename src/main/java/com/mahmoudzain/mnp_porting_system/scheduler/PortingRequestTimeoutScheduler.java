package com.mahmoudzain.mnp_porting_system.scheduler;

import com.mahmoudzain.mnp_porting_system.service.api.PortingRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PortingRequestTimeoutScheduler {

    private final PortingRequestService portingRequestService;

    @Scheduled(fixedDelay = 30000)
    public void processExpiredPortingRequests() {
         portingRequestService.cancelExpiredRequests();
    }

}
